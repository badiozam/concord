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

import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.predict.model.event.Event;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("predict.EventOutcomeManager")
public class HistoricalEventOutcomeManager extends BaseHistoricalManager<EventOutcomeRecord, EventOutcome> implements EventOutcomeManager
{
	private PrivilegedManager<EventOutcomeRecord, EventOutcome> privilegedEventOutcomeManager;

	@Override
	public Set<EventOutcome> getEventOutcomes(Event e, PaginationFilter pagination)
	{
		return getEventOutcomeManager().getEventOutcomes(e, pagination);
	}


	@Override
	public int getUsageCount(EventOutcome eventOutcome)
	{
		return getEventOutcomeManager().getUsageCount(eventOutcome);
	}

	public EventOutcomeManager getEventOutcomeManager()
	{
		return (EventOutcomeManager) getPrivilegedEventOutcomeManager();
	}

	public PrivilegedManager<EventOutcomeRecord, EventOutcome> getPrivilegedEventOutcomeManager()
	{
		return privilegedEventOutcomeManager;
	}

	@Autowired
	public <P extends EventOutcomeManager & PrivilegedManager<EventOutcomeRecord, EventOutcome>>
	 void setPrivilegedEventOutcomeManager(P privilegedEventOutcomeManager)
	{
		this.privilegedEventOutcomeManager = privilegedEventOutcomeManager;
	}

	@Override
	public PrivilegedManager<EventOutcomeRecord, EventOutcome> getImplementationManager()
	{
		return getPrivilegedEventOutcomeManager();
	}

}
