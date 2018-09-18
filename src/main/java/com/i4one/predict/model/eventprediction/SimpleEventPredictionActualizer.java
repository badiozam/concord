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
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.balanceexpense.BalanceExpenseManager;
import com.i4one.base.model.manager.HistoricalManager;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.web.RequestState;
import com.i4one.predict.model.term.Term;
import com.i4one.predict.model.term.TermManager;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEventPredictionActualizer extends BaseLoggable implements EventPredictionActualizer
{
	private RequestState requestState;

	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;

	private TermManager termManager;
	private BalanceExpenseManager balanceExpenseManager;
	private EventPredictionManager eventPredictionManager;

	public void init()
	{
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<EventPrediction> actualize(EventPrediction ep, EventOutcome actualOutcome)
	{
		getLogger().debug("Actualizing outcome for " + ep + " using outcome " + actualOutcome);

		// We don't want to create any historical recordings since this is being done by the system
		// for a very large set of users and the records are kept for each individual user in their
		// transaction logs
		//
		userBalanceManager.init(Utils.makeMap(HistoricalManager.ATTR_RECORDHISTORYENABLED, false));

		if ( ep.exists() &&
			ep.getPostedTimeSeconds() == 0 &&
			ep.getEvent().exists() &&
			ep.getEvent().equals(actualOutcome.getEvent()) )
		{
			Term currTerm = getTermManager().getLiveTerm(ep.getUser().getClient());

			// Potentially return a given amount into this term's balance
			//
			ep.setTerm(currTerm);

			int incAmount = 0;
			if ( ep.getEventOutcome().equals(actualOutcome) )
			{
				// They got it right! Set the pay out based on what's recorded
				//
				incAmount = round(ep.getQuantity() * ep.getPayout() );
				ep.setCorrect(true);
			}
			else
			{
				ep.setCorrect(false);
			}

			// We may increment their account by 0 just so there's a transaction record and also
			// because we want to make sure that the item was posted
			//

			Set<BalanceExpense> expenses = getBalanceExpenseManager().getAllExpensesByFeature(ep.getEvent(false));
			List<ReturnType<UserBalance>> updatedBalances = new ArrayList<>(expenses.size());

			int currTime = Utils.currentTimeSeconds();
			for ( BalanceExpense expense : expenses )
			{
				ReturnType<UserBalance> balRetVal = getUserBalanceManager().increment(
					new UserBalance(ep.getUser(), expense.getBalance(), currTime),
					incAmount * expense.getAmount());
				if ( !balRetVal.getPost().exists() )
				{
					// NSF, shouldn't really happen here since we're incrementing the balance
					//
					throw new Errors("eventPredictionManager.actualizeOutcome", new ErrorMessage("msg.predict.eventPredictionManager.actualizeOutcome.error", "There was a problem with crediting your account, please try again later",
						new Object[] { "userBalance", balRetVal.getPost(), "item", ep }, null));
				}
				else
				{
					updatedBalances.add(balRetVal);
				}
			}

			//ep.setPostedTimeSeconds(ep.getEvent().getPostsBySeconds());
			ep.setPostedTimeSeconds(getRequestState().getRequest().getTimeInSeconds());
			ReturnType<EventPrediction> retVal = getEventPredictionManager().update(ep);

			retVal.addChain(getUserBalanceManager(), "update", updatedBalances);

			return retVal;
		}
		else
		{
			getLogger().debug("Nothing to be done here");

			// Nothing done because either:
			//
			// a) the event prediction item doesn't exist, or
			// b) The event prediction has already been posted, or
			// c) the outcome given doesn't belong to the event given
			//
			ReturnType<EventPrediction> retVal = new ReturnType<>();
			retVal.setPre(ep);
			retVal.setPost(ep);

			return retVal;
		}
	}

	protected Balance getPredictionBalance(EventPrediction ep)
	{
		Term term = getTermManager().getLiveTerm(ep.getUser().getClient());

		return getBalanceManager().getBalance(term);
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
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

	public EventPredictionManager getEventPredictionManager()
	{
		return eventPredictionManager;
	}

	@Autowired
	public void setEventPredictionManager(EventPredictionManager eventPredictionManager)
	{
		this.eventPredictionManager = eventPredictionManager;
	}

	public BalanceExpenseManager getBalanceExpenseManager()
	{
		return balanceExpenseManager;
	}

	@Autowired
	public void setBalanceExpenseManager(BalanceExpenseManager balanceExpenseManager)
	{
		this.balanceExpenseManager = balanceExpenseManager;
	}

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}
}
