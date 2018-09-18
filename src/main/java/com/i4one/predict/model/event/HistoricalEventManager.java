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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.adminhistory.AdminHistory;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.categorizable.BaseCategorizableHistoricalManager;
import com.i4one.base.model.manager.categorizable.CategorizableHistoricalManager;
import com.i4one.base.model.manager.categorizable.CategorizablePrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("predict.EventManager")
public class HistoricalEventManager extends BaseCategorizableHistoricalManager<EventRecord, Event> implements CategorizableHistoricalManager<EventRecord, Event>, EventManager
{
	private CategorizablePrivilegedManager<EventRecord, Event> privilegedEventManager;

	@Override
	public Set<Event> getAllActualized(SingleClient client, int starttm, PaginationFilter daoConfigurer)
	{
		return getEventManager().getAllActualized(client, starttm, daoConfigurer);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Event> setActualOutcome(Event event)
	{
		ReturnType<Event> retVal = getEventManager().setActualOutcome(event);

		AdminHistory adminHistory = newAdminHistory(getImplementationManager().emptyInstance(), event, "setActualOutcome");
		setHistoryChainOwnership(retVal, adminHistory);
		
		retVal.put(ATTR_ADMINHISTORY, adminHistory);

		return retVal;
	}

	public EventManager getEventManager()
	{
		return (EventManager) getPrivilegedEventManager();
	}

	public CategorizablePrivilegedManager<EventRecord, Event> getPrivilegedEventManager()
	{
		return privilegedEventManager;
	}

	@Autowired
	public <P extends EventManager & CategorizablePrivilegedManager<EventRecord, Event>>
	 void setPrivilegedEventManager(P privilegedEventManager)
	{
		this.privilegedEventManager = privilegedEventManager;
	}

	@Override
	public CategorizablePrivilegedManager<EventRecord, Event> getImplementationManager()
	{
		return getPrivilegedEventManager();
	}
}
