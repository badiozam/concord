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
package com.i4one.predict.web.controller.admin.events.targets;

import com.i4one.base.model.manager.targetable.BaseActivityTargetListResolver;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventManager;
import com.i4one.predict.model.eventprediction.EventPrediction;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import com.i4one.predict.model.eventprediction.EventPredictionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class EventPredictionActivityTargetListResolver extends BaseActivityTargetListResolver<EventPredictionRecord, EventPrediction, Event, EventPredictionTarget>
{
	private EventManager eventManager;
	private EventPredictionManager eventPredictionManager;

	@Override
	protected EventPredictionTarget emptyInstance(String key)
	{
		return new EventPredictionTarget(key);
	}

	@Override
	protected EventManager getParentManager()
	{
		return getEventManager();
	}

	@Override
	protected ActivityManager<EventPredictionRecord, EventPrediction, Event> getActivityManager()
	{
		return getEventPredictionManager();
	}

	public EventManager getEventManager()
	{
		return eventManager;
	}

	@Autowired
	public void setEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
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
