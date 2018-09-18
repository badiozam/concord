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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.BaseTransactionalActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.user.User;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.term.Term;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("predict.EventPredictionManager")
public class TransactionalEventPredictionManager extends BaseTransactionalActivityManager<EventPredictionRecord, EventPrediction, Event> implements EventPredictionManager
{
	private EventPredictionManager eventPredictionManager;

	@Transactional(readOnly = false)
	@Override
	public void actualizeOutcomeForAll(EventOutcome actualOutcome)
	{
		getEventPredictionManager().actualizeOutcomeForAll(actualOutcome);
	}

	@Override
	public Set<EventPrediction> getAllPredictions(Event event, User user, PaginationFilter pagination)
	{
		return getEventPredictionManager().getAllPredictions(event, user, pagination);
	}

	@Override
	public Set<EventPrediction> getAllPredictionsByUser(User user, Term term, PaginationFilter pagination)
	{
		return getEventPredictionManager().getAllPredictionsByUser(user, term, pagination);
	}

	@Override
	public Set<EventPrediction> getPendingPredictionsByUser(User user, PaginationFilter pagination)
	{
		return getEventPredictionManager().getPendingPredictionsByUser(user, pagination);
	}

	@Override
	public Set<EventPrediction> getPostedPredictionsByUser(User user, PaginationFilter pagination)
	{
		return getEventPredictionManager().getPostedPredictionsByUser(user, pagination);
	}

	public EventPredictionRecordDao getEventPredictionRecordDao()
	{
		return (EventPredictionRecordDao) getDao();
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<EventPrediction> create(EventPrediction ep)
	{
		ep.setTimeStampSeconds(getRequestState().getRequest().getTimeInSeconds());
		ReturnType<EventPrediction> retVal = getEventPredictionManager().create(ep);

		if ( ep.exists() )
		{
			// This is our transaction that reflects the creation of a new prediction record
			//
			Transaction t = newTransaction(ep.getUser());

			setTransactionDescr(t, "msg.eventPredictionManager.predict.xaction.descr", "eventPrediction", ep);

			t = createTransaction(retVal, t);

			retVal.put("transaction", t);
		}

		return retVal;
	}

	public EventPredictionManager getEventPredictionManager()
	{
		return eventPredictionManager;
	}

	@Autowired
	public void setEventPredictionManager(EventPredictionManager expensiveEventPredictionManager)
	{
		this.eventPredictionManager = expensiveEventPredictionManager;
	}

	@Override
	public EventPredictionActualizer getEventPredictionActualizer()
	{
		return getEventPredictionManager().getEventPredictionActualizer();
	}

	@Override
	public void setEventPredictionActualizer(EventPredictionActualizer eventPredictionActualizer)
	{
		// Ignore, since the delegate will have one autowired
		//
	}

	@Override
	public EventPredictionManager getImplementationManager()
	{
		return getEventPredictionManager();
	}
}
