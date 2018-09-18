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

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.triggerable.BaseTriggeredActivityManager;
import com.i4one.base.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class TriggeredEventResponseManager extends BaseTriggeredActivityManager<EventResponseRecord, EventResponse, Event> implements EventResponseManager
{
	private EventResponseManager eventResponseManager;

	@Override
	public User getUser(EventResponse item)
	{
		return item.getUser(false);
	}

	@Override
	public RecordTypeDelegator<?> getAttachee(EventResponse eventResponse)
	{
		return eventResponse.getEvent();
	}

	@Override
	public EventResponseManager getImplementationManager()
	{
		return getEventResponseManager();
	}

	@Override
	protected void processAttached(ReturnType<EventResponse> retEventResponse, String methodName)
	{
		// We only process the triggers if the event was successfully processed for the first time
		//
		EventResponse pre = retEventResponse.getPre();
		EventResponse post = retEventResponse.getPost();

		if (
			(post.exists() && pre.equals(post)) 	||	// Previously played
			(!post.exists())				// Failed/Expired
		)
		{
			getLogger().debug("Triggers not processed for " + retEventResponse.getPost().getEvent() + " and user " + retEventResponse.getPost().getUser(false));
		}
		else
		{
			super.processAttached(retEventResponse, methodName);
		}

	}

	public EventResponseManager getEventResponseManager()
	{
		return eventResponseManager;
	}

	@Autowired
	@Qualifier("promotion.CachedEventResponseManager")
	public void setEventResponseManager(EventResponseManager eventResponseManager)
	{
		this.eventResponseManager = eventResponseManager;
	}
}
