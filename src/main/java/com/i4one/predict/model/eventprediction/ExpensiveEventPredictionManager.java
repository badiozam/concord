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
import com.i4one.base.model.manager.expendable.BaseExpensiveManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.term.Term;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class ExpensiveEventPredictionManager extends BaseExpensiveManager<EventPredictionRecord, EventPrediction, Event> implements EventPredictionManager
{
	private EventPredictionManager eventPredictionManager;

	@Override
	protected void processAttached(ReturnType<EventPrediction> processedItem, String method)
	{
		// We only deduct for creation. Normally, this would be unnecessary but for
		// predictions, we update the response entry after an event has been actualized
		// and as such we don't want to process the expenses again
		//
		if ( method.equals("create"))
		{
			super.processAttached(processedItem, method);
		}
	}

	@Override
	public Event getAttachee(EventPrediction item)
	{
		return item.getEvent();
	}

	@Override
	public EventPredictionManager getImplementationManager()
	{
		return getEventPredictionManager();
	}

	@Override
	public User getUser(EventPrediction item)
	{
		return item.getUser(false);
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

	@Override
	public void actualizeOutcomeForAll(EventOutcome actualOutcome)
	{
		getEventPredictionManager().actualizeOutcomeForAll(actualOutcome);
	}

	@Override
	public EventPredictionActualizer getEventPredictionActualizer()
	{
		return getEventPredictionManager().getEventPredictionActualizer();
	}

	@Override
	public void setEventPredictionActualizer(EventPredictionActualizer eventPredictionActualizer)
	{
		getEventPredictionManager().setEventPredictionActualizer(eventPredictionActualizer);
	}

	public EventPredictionManager getEventPredictionManager()
	{
		return eventPredictionManager;
	}

	@Autowired
	public void setEventPredictionManager(EventPredictionManager simpleEventPredictionManager)
	{
		this.eventPredictionManager = simpleEventPredictionManager;
	}
}
