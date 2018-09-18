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
package com.i4one.predict.model.jobs;

import com.i4one.base.core.Utils;
import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.jobs.BaseQuartzJob;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventManager;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.eventoutcome.EventOutcomeManager;
import java.util.Set;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Hamid Badiozamani
 */
@DisallowConcurrentExecution 
public class EventOutcomeStatsJob extends BaseQuartzJob
{
	private EventManager eventManager;
	private EventOutcomeManager eventOutcomeManager;
	private SingleClientManager clientManager;

	public EventOutcomeStatsJob()
	{
		super();
	}

	@Override
	protected void doExecuteInternal(JobExecutionContext jec) throws JobExecutionException
	{
		int currentTimeSeconds = Utils.currentTimeSeconds();

		getLogger().debug("Executing " + this + " at " + currentTimeSeconds);
		getClientManager().getAllClients(SingleClient.getRoot(), SimplePaginationFilter.NONE).stream().forEach((currClient) ->
		{
			updateAllEvents(jec, currentTimeSeconds, currClient);
		});
		getLogger().debug("Done with " + this);
	}

	protected void updateAllEvents(JobExecutionContext jec, int currentTimeSeconds, SingleClient client)
	{
		Set<Event> liveEvents = getEventManager().getLive(new CategoryPagination(new EventCategory(),
			currentTimeSeconds, new ClientPagination(client, SimplePaginationFilter.NONE)));

		liveEvents.stream().forEach((currEvent) ->
		{
			updateEvent(jec, currentTimeSeconds, currEvent);
		});
	}

	protected void updateEvent(JobExecutionContext jec, int currTime, Event event)
	{
		getLogger().debug("Updating " + event.getTitle() );

		Set<EventOutcome> eventOutcomes = getEventOutcomeManager().getEventOutcomes(event, SimplePaginationFilter.NONE);
		int totalCount = 0;

		// Once through to count how much each outcome has been used
		//
		for ( EventOutcome eventOutcome : eventOutcomes )
		{
			int usageCount = getEventOutcomeManager().getUsageCount(eventOutcome);
			eventOutcome.setUsageCount(usageCount);
			eventOutcome.setUpdateTimeSeconds(currTime);

			totalCount += usageCount;
			getEventOutcomeManager().update(eventOutcome);
		}

		getLogger().debug("Total count for " + event.getTitle() + " is " + totalCount + " updating outcomes");

		// A second time through to adjust the likelihoods
		//
		for (EventOutcome eventOutcome : eventOutcomes )
		{
			if ( eventOutcome.getBaseline() > 0.0f )
			{
				// The formula we use here averages the baseline likelihood with the
				// usage likelihood. This is a simple average at this point, but a
				// better approach might be a weighted average according to recency
				// of each prediction. For example:
				//
				//	weightedTotalSum = SUM( secondsElapsed since start for any outcome) / total EVENT usage count
				//	weightedOutcomeSum = SUM( secondsElapsed since start for the specific outcome) / total OUTCOME usage count
				//
				//	weightedLikelihood = weightedOutcomeSum / weightedTotalSum
				//
				//
				float simpleAverage = (eventOutcome.getUsageCount() + (eventOutcome.getBaseline() * (totalCount+1))) / 2;
				simpleAverage /= (totalCount + 1); 

				getLogger().debug(eventOutcome.getDescr() + " has usage " + eventOutcome.getUsageCount() + " and baseline " + eventOutcome.getBaseline() + " for a simple average of " + simpleAverage);

				eventOutcome.setLikelihood(simpleAverage);
			}
			else // if (eventOutcome.getBaseline() <= 0.0f)
			{
				eventOutcome.setLikelihood(0.0f);
			}
			getEventOutcomeManager().update(eventOutcome);
		}

		getLogger().debug("Event " + event.getTitle() + " has " + totalCount + " total predictions");
	}

	public SingleClientManager getClientManager()
	{
		return clientManager;
	}

	@Autowired
	@Qualifier("base.ReadOnlySingleClientManager")
	public void setClientManager(SingleClientManager clientManager)
	{
		this.clientManager = clientManager;
	}

	public EventManager getEventManager()
	{
		return eventManager;
	}

	@Autowired
	@Qualifier("predict.CachedEventManager")
	public void setEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
	}

	public EventOutcomeManager getEventOutcomeManager()
	{
		return eventOutcomeManager;
	}

	@Autowired
	@Qualifier("predict.CachedEventOutcomeManager")
	public void setEventOutcomeManager(EventOutcomeManager eventOutcomeManager)
	{
		this.eventOutcomeManager = eventOutcomeManager;
	}
}
