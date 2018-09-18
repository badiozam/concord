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
package com.i4one.predict.model.eventoutcome;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleEventOutcomeManager extends BaseSimpleManager<EventOutcomeRecord, EventOutcome> implements EventOutcomeManager
{
	private EventPredictionManager eventPredictionManager;

	@Override
	public Set<EventOutcome> getEventOutcomes(Event e, PaginationFilter pagination)
	{
		return getEventOutcomesInternal(e, pagination);
	}

	@Override
	public int getUsageCount(EventOutcome e)
	{
		if ( e.exists() )
		{
			return getDao().getUsageCount(e.getSer());
		}
		else
		{
			return 0;
		}
	}

	// Avoid being overwritten because we need this to be the most recent from the database
	//
	private Set<EventOutcome> getEventOutcomesInternal(Event e, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getEventOutcomes(e.getSer(), pagination));
	}

	/**
	 * Checks to ensure that the likelihood values of a given event are between
	 * 0 and 100
	 * 
	 * @param e The event whose likelihoods we're to check
	 * @return True if the outcomes are valid, false otherwise
	 */
	protected boolean isValidOutcomes(Event e)
	{
		Set<EventOutcome> outcomes = getEventOutcomesInternal(e, SimplePaginationFilter.NONE);

		float total = 0.0f;
		total = outcomes.stream().map((outcome) -> outcome.getBaseline()).reduce(total, (accumulator, _item) -> accumulator + _item);

		getLogger().debug("Event " + e.getTitle() + " now adds up to a total likelihood of " + total);
		return ( total >= 0.0f && total <= 1.0f );
	}

	@Override
	@Transactional(readOnly = false)
	public ReturnType<EventOutcome> create(EventOutcome outcome)
	{
		// Default the likelihood to the baseline since there's no data to fluctuate it
		//
		outcome.setLikelihood(outcome.getBaseline());
		ReturnType<EventOutcome> retVal = super.create(outcome);

		if ( !isValidOutcomes(outcome.getEvent() ) )
		{
			// Rolling back
			//
			outcome.setSer(0);

			throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".create.invalid", "Creating this outcome will result in an invalid total likelihood (over 100% or less than 0%). Please adjust other outcomes first before creating this one.", new Object[] {}));
		}

		return retVal;
	}

	@Override
	@Transactional(readOnly = false)
	public ReturnType<EventOutcome> update(EventOutcome outcome)
	{
		ReturnType<EventOutcome> retVal = super.update(outcome);

		if ( !isValidOutcomes(outcome.getEvent()))
		{
			throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName()  + ".update.invalid", "Updating this outcome's likelihood will result in an invalid total likelihood (over 100% or less than 0%). Please adjust other outcomes first before updating this one.", new Object[] { }));
		}

		return retVal;
	}

	@Override
	protected ReturnType<EventOutcome> updateInternal(EventOutcomeRecord lockedRecord, EventOutcome outcome)
	{
		if ( (!lockedRecord.getDescr().equals(outcome.getDescr())
			|| lockedRecord.getBaseline() != outcome.getBaseline())
			&& getEventPredictionManager().hasActivity(outcome.getEvent(false)) )
		{
			throw new Errors(getInterfaceName() + ".remove", new ErrorMessage("msg." + getInterfaceName() + ".update.hasusage", "This item can no longer be updated because it has activity", new Object[] { "item", outcome }));
		}
		else
		{
			return super.updateInternal(lockedRecord, outcome);
		}
	}

	@Override
	public EventOutcomeRecordDao getDao()
	{
		return (EventOutcomeRecordDao) super.getDao();
	}

	@Override
	public EventOutcome emptyInstance()
	{
		return new EventOutcome();
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

}
