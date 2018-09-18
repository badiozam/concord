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
package com.i4one.promotion.model.event;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.BaseTransactionalActivityManager;
import com.i4one.base.model.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("promotion.EventResponseManager")
public class TransactionalEventResponseManager extends BaseTransactionalActivityManager<EventResponseRecord, EventResponse, Event> implements EventResponseManager
{
	private EventResponseManager eventResponseManager;

	@Override
	public EventResponseManager getImplementationManager()
	{
		return getEventResponseManager();
	}

	protected void processEventTransaction(ReturnType<EventResponse> retEventResponse)
	{
		EventResponse pre = retEventResponse.getPre();
		EventResponse post = retEventResponse.getPost();

		Transaction t = newTransaction(post.getUser());

		if ( post.exists() && pre.equals(post))
		{
			// Previously played
			//
			t.setStatus(Transaction.FAILED);
		}

		// The message is responsible for displaying the proper previously played status
		//
		setTransactionDescr(t, "msg.eventResponseManager.create.xaction.descr", "eventResponse", post);

		// See if we can locate a user balance update transaction and set us
		// as the the parent transaction
		//
		t = createTransaction(retEventResponse, t);

		// Set our master transaction record since we've created a new transaction
		//
		retEventResponse.put("transaction", t);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<EventResponse> create(EventResponse eventResponse)
	{
		ReturnType<EventResponse> retVal = getEventResponseManager().create(eventResponse);

		processEventTransaction(retVal);

		return retVal;
	}

	public EventResponseManager getEventResponseManager()
	{
		return eventResponseManager;
	}

	@Autowired
	@Qualifier("promotion.InstantWinningEventResponseManager")
	public void setEventResponseManager(EventResponseManager eventResponseManager)
	{
		this.eventResponseManager = eventResponseManager;
	}
}
