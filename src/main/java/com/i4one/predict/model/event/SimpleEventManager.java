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
package com.i4one.predict.model.event;

import com.i4one.base.core.Utils;
import static com.i4one.base.core.Utils.currentDateTime;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.categorizable.BaseSimpleCategorizableManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.eventoutcome.EventOutcomeManager;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import com.i4one.predict.model.term.TermManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class retrieves prediction events for a given term
 *
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEventManager extends BaseSimpleCategorizableManager<EventRecord, Event, EventCategory> implements EventManager
{
	private TermManager termManager;
	private EventOutcomeManager eventOutcomeManager;
	private EventPredictionManager eventPredictionManager;

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Event> clone(Event item)
	{
		try
		{
			if ( item.exists() )
			{
				Set<EventOutcome> eventOutcomes = getEventOutcomeManager().getEventOutcomes(item, SimplePaginationFilter.NONE);
	
				String currTimeStamp = "" + currentDateTime();
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				Event event = new Event();
				event.copyFrom(item);
	
				event.setSer(0);
				event.setTitle(workingTitle);
				event.setStartTimeSeconds(Utils.currentTimeSeconds() + (86400 * 365));
				event.setEndTimeSeconds(Utils.currentTimeSeconds() + (86400 * 366));
				event.setClosesBySeconds(Utils.currentTimeSeconds() + (86400 * 366) - 60 * 60);
				event.setPostsBySeconds(Utils.currentTimeSeconds() + (86400 * 367));

				event.getDelegate().setPostedtm(null);
				event.setActualOutcome(new EventOutcome());
	
				ReturnType retVal = create(event);

				// Remove any default outcomes since we can't be certain of what
				// effect it would have on baseline probabilities and whether those
				// will actually add up to 100%. Also, this method should only
				// duplicate an existing item and not make outside modifications
				//
				for (EventOutcome currOutcome : event.getPossibleOutcomes() )
				{
					getEventOutcomeManager().remove(currOutcome);
				}
	
				List<ReturnType<EventOutcome>> createdOutcomes = new ArrayList<>();
				for ( EventOutcome eventOutcome : eventOutcomes )
				{
					eventOutcome.setSer(0);
					eventOutcome.setEvent(event);
	
					ReturnType<EventOutcome> createdOutcome = getEventOutcomeManager().create(eventOutcome);
					createdOutcomes.add(createdOutcome);
				}
	
				retVal.addChain(getEventOutcomeManager(), "create", new ReturnType<>(createdOutcomes));
				return retVal;
			}
			else
			{
				throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.dne", "You are attempting to clone a non-existent item: $item", new Object[] { "item", item }));
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
		{
			Errors errors = new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".clone.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));

			throw errors;
		}
	}

	protected ReturnType<Event> updateInternal(EventRecord lockedRecord, Event item)
	{
		if ( getEventPredictionManager().hasActivity(item) )
		{
			boolean noRestrictedFields = true;

			noRestrictedFields &= lockedRecord.getBrief().equals(item.getBrief());
			noRestrictedFields &= lockedRecord.getDescr().equals(item.getDescr());
			noRestrictedFields &= lockedRecord.getPromo().equals(item.getPromo());
			noRestrictedFields &= lockedRecord.getTitle().equals(item.getTitle());
			noRestrictedFields &= lockedRecord.getReference().equals(item.getReference());

			if ( !noRestrictedFields )
			{
				getLogger().debug("Old {} vs new {}", lockedRecord, item);
				throw new Errors(getInterfaceName() + ".remove", new ErrorMessage("msg." + getInterfaceName() + ".update.hasusage", "This item can no longer be updated because it has activity", new Object[] { "item", item}));
			}
		}

		return super.updateInternal(lockedRecord, item);
	}

	@Transactional(readOnly = false)
	@Override
	public Event remove(Event item)
	{
		if ( getEventPredictionManager().hasActivity(item) )
		{
			Errors errors = new Errors(getInterfaceName() + ".remove", new ErrorMessage("msg." + getInterfaceName() + ".remove.hasusage", "This item can no longer be removed because it has activity", new Object[] { "item", item}));

			throw errors;
		}
		else
		{
			return super.remove(item);
		}
	}

	@Override
	public Set<Event> getAllActualized(SingleClient client, int starttm, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAllActualized(starttm, new ClientPagination(client, pagination)));
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Event> setActualOutcome(Event event)
	{
		Event dbEvent = new Event();
		dbEvent.setSer(event.getSer());
		dbEvent.loadedVersion();

		if ( event.exists() && dbEvent.exists() && !dbEvent.getActualOutcome().exists() )
		{
			getLogger().debug("Actual outcome: " + event.getActualOutcome());

			dbEvent.setActualOutcome(event.getActualOutcome());
			getLogger().debug("Updating event " + dbEvent);

			return update(dbEvent);
		}
		else
		{
			// Already actualized this event or the event doesn't exist
			//
			if ( event.exists() && dbEvent.exists() && dbEvent.getActualOutcome().exists() )
			{
				throw new Errors(getInterfaceName() + ".setActualOutcome", new ErrorMessage("msg." + getInterfaceName() + ".setActualOutcome.collision", "The event '#IString($item.title)' has already been actualized", new Object[] { "item", event }));
			}
			else
			{
				throw new Errors(getInterfaceName() + ".setActualOutcome", new ErrorMessage("msg." + getInterfaceName() + ".setActualOutcome.dne", "The event '$item' does not exist", new Object[] { "item", event }));
			}
		}
	}

	@Override
	public EventRecordDao getDao()
	{
		return (EventRecordDao) super.getDao();
	}

	@Override
	public Event emptyInstance()
	{
		return new Event();
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

	public EventOutcomeManager getEventOutcomeManager()
	{
		return eventOutcomeManager;
	}

	@Autowired
	public void setEventOutcomeManager(EventOutcomeManager eventOutcomeManager)
	{
		this.eventOutcomeManager = eventOutcomeManager;
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

	@Override
	protected Event initModelObject(Event item)
	{
		Event retVal = super.initModelObject(item);

		retVal.setPossibleOutcomes(getEventOutcomeManager().getEventOutcomes(item, SimplePaginationFilter.NONE));

		return retVal;
	}
}
