/*
 * MIT License
 * 
 * Copyright (c) 2018 i4one Interactive, LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.i4one.predict.model.eventprediction;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.eventoutcome.EventOutcomeManager;
import com.i4one.predict.model.term.Term;
import com.i4one.predict.model.term.TermManager;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEventPredictionManager extends BaseActivityManager<EventPredictionRecord, EventPrediction, Event> implements EventPredictionManager
{
	private TermManager termManager;
	private EventPredictionActualizer eventPredictionActualizer;
	private EventOutcomeManager eventOutcomeManager;

	@Override
	public void init()
	{
		super.init();

		getLogger().debug("Initializing " + this);
	}

	@Override
	protected int getActivityTimeStampSeconds(EventPrediction item)
	{
		if ( item.exists() )
		{
			// Don't overwrite the update time that the user input
			// originally when actualizing a prediction
			//
			return item.getTimeStampSeconds();
		}
		else
		{
			return super.getActivityTimeStampSeconds(item);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<EventPrediction> create(EventPrediction ep)
	{
		int currTime = ep.getTimeStampSeconds();
		if ( !ep.getEventOutcome().exists() )
		{
			throw new Errors("eventPrediction.create", new ErrorMessage("msg.predict.EventPrediction.noSelection", "Please select an outcome from one of the choices", new Object[]{"item", this}));
		}
		else if (ep.getEventOutcome().getLikelihood() <= 0.0f )
		{
			throw new Errors("eventPrediction.create", new ErrorMessage("msg.predict.EventPrediction.expiredEventOutcome", "The selected outcome is no longer available", new Object[]{"item", this}));
		}
		else if ( !ep.getEvent().isPlayable(currTime) )
		{
			throw new Errors("eventPredictionManager.create", new ErrorMessage("msg.predict.eventPredictionManager.eventClosed", "Sorry, bidding on this event has closed", new Object[] { "item", ep }, null));
		}
		else if ( ep.getEvent().getActualOutcome().exists() )
		{
			throw new Errors("eventPredictionManager.create", new ErrorMessage("msg.predict.eventPredictionManager.actualized", "Sorry, this event's outcome has already been determined to be '#IString($item.event.actualOutcome.descr)'", new Object[] { "item", ep }, null));
		}
		else
		{
			SingleClient client = ep.getEvent().getClient();
			Term currTerm = getTermManager().getLiveTerm(client);

			PaginationFilter mostRecentPagination = new SimplePaginationFilter();
			mostRecentPagination.setOrderBy("ser DESC");
			mostRecentPagination.setCurrentPage(0);
			mostRecentPagination.setPerPage(1);

			// Don't allow rapid voting, a default delay is built in which can be circumvented
			// if we really want to at run time by setting the client option to 30
			//
			int cooldown = Utils.defaultIfNaN(client.getOptionValue("predict.cooldown"), 10);
			Set<EventPrediction> lastPredictions = this.getAllPredictions(ep.getEvent(false), ep.getUser(false), mostRecentPagination);
			if ( !currTerm.exists() )
			{
				throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg.predict.eventPredictionManager.predict.noliveterm", "The current prediction term has expired.", new Object[] { "item", ep, "term", currTerm }, null));
			}
			else if ( !lastPredictions.isEmpty() && (currTime - lastPredictions.iterator().next().getTimeStampSeconds() < cooldown ) )
			{
				throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg.predict.eventPredictionManager.predict.oncooldown", "Whoa there! You just made a prediction into this game within the last $cooldownSeconds seconds. Before making another Prediction quickly, take a moment to consider your strategy.", new Object[] { "item", ep, "cooldownSeconds", cooldown }, null));
			}
			else
			{
				// Deduct a given amount from the user's default predictionBalance (might be changed later for custom balances)
				//
				//ep.setBalance(getPredictionBalance(ep));
				ep.setTerm(currTerm);
		
				// We lock the user balance of the user for use as a semaphore to prevent outside updates while
				// we check the maximums
				//
	
				int currentHoldings = getCurrentHoldings(ep);
				if ( currentHoldings + ep.getQuantity() > ep.getEvent().getMaxBid() )
				{
					throw new Errors("quantity", new ErrorMessage("msg.predict.eventPredictionManager.predict.overmax", "You have reached the maximum bid amount allowable for this event: your current investment in this event is $currentHoldings and by bidding $item.quantity more you would exceed the maximum of $item.event.maxBid", new Object[] { "item", ep, "currentHoldings", currentHoldings }, null));
				}
				else
				{
					// Record the prediction with current payout based on the likelihood of the
					// given outcome (overwriting any pre-existing payout value passed in). If this
					// formula is changed, the Player rankings algorithm needs to be updated as
					// well since it assumes the formula for payout rather than retrieve the value.
					//
					// Note that the latest event outcome percentage is what will get recorded.
					//
					EventOutcome eventOutcome = getEventOutcomeManager().getById(ep.getEventOutcome(false).getSer());
					ep.setPayout(1 / eventOutcome.getLikelihood());
		
					// By using create we ensure that an existent record would throw an exception which
					// protects against changing a bid after the fact
					//
					ReturnType<EventPrediction> retVal = super.create(ep);
		
					return retVal;
				}
			}
		}
	}

	private int getCurrentHoldings(EventPrediction ep)
	{
		int currentHoldings = 0;

		currentHoldings = getAllPredictions(ep.getEvent(false), ep.getUser(false), SimplePaginationFilter.NONE ).stream()
			.map((currPred) -> currPred.getQuantity())
			.reduce(currentHoldings, Integer::sum);

		return currentHoldings;
	}

	@Override
	public List<ReturnType<EventPrediction>> revert(Event parent, PaginationFilter pagination)
	{
		if ( parent.getActualOutcome(false).exists() )
		{
			throw new Errors(getInterfaceName() + ".revert", new ErrorMessage("msg.predict.eventPredictionManager.revert.actualized", "This event has already been actualized and cannot be reverted.", new Object[] { "item", parent }, null));
		}
		else
		{
			return super.revert(parent, pagination);
		}
	}

	@Override
	public Set<EventPrediction> getAllPredictions(Event event, User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAll(event.getSer(), user.getSer(), pagination));
	}

	@Override
	public Set<EventPrediction> getAllPredictionsByUser(User user, Term term, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAllEventPredictionsByUser(user.getSer(), term.getSer(), pagination));
	}

	@Override
	public Set<EventPrediction> getPendingPredictionsByUser(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getPendingEventPredictionsByUser(user.getSer(), pagination));
	}

	@Override
	public Set<EventPrediction> getPostedPredictionsByUser(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getPostedEventPredictionsByUser(user.getSer()));
	}

	@Transactional(readOnly = false)
	@Override
	public void actualizeOutcomeForAll(EventOutcome actualOutcome)
	{
		if ( actualOutcome.exists() && actualOutcome.getEvent().exists() )
		{
			getLogger().debug("Actualizing all outcomes for " + actualOutcome);

			// Go through all of the predictions and credit members
			//
			List<EventPredictionRecord> allPreds = getDao().getByItem(actualOutcome.getEvent().getSer(), SimplePaginationFilter.NONE);
			allPreds.stream().forEach( (currRecord) ->
			{
				EventPrediction currPred = new EventPrediction(currRecord);
				getEventPredictionActualizer().actualize(currPred, actualOutcome);
			});
		}
		else
		{
			getLogger().debug("Can't actualize for actual outcome = " + actualOutcome);
		}
	}

	public TermManager getTermManager()
	{
		return termManager;
	}

	@Autowired
	public void setTermManager(TermManager termManager)
	{
		this.termManager = termManager;
	}

	@Override
	public EventPredictionActualizer getEventPredictionActualizer()
	{
		return eventPredictionActualizer;
	}

	@Override
	@Autowired
	public void setEventPredictionActualizer(EventPredictionActualizer eventPredictionActualizer)
	{
		this.eventPredictionActualizer = eventPredictionActualizer;
	}

	public EventOutcomeManager getEventOutcomeManager()
	{
		return eventOutcomeManager;
	}

	@Autowired
	public void setEventOutcomeManager(EventOutcomeManager eventOutcomeManager)
	{
		this.eventOutcomeManager = eventOutcomeManager;
	}

	/*
	protected Balance getPredictionBalance(EventPrediction ep)
	{
		Term term = getTermManager().getLiveTerm(ep.getUser().getClient());

		return getBalanceManager().getBalance(term);
	}
	*/

	@Override
	public EventPredictionRecordDao getDao()
	{
		return (EventPredictionRecordDao) super.getDao();
	}

	@Override
	public EventPrediction emptyInstance()
	{
		return new EventPrediction();
	}

}
