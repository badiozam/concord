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
package com.i4one.predict.web.controller.user.events;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.qualifier.SimpleComparisonQualifier;
import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseCategorizableListingController;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.category.EventCategoryManager;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventManager;
import com.i4one.predict.model.eventoutcome.EventOutcomeManager;
import com.i4one.predict.model.eventprediction.EventPrediction;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import com.i4one.predict.model.term.Term;
import com.i4one.predict.model.term.TermManager;
import com.i4one.predict.web.interceptor.PredictCategoriesModelInterceptor;
import java.util.Collections;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EventViewController extends BaseCategorizableListingController<Event, EventCategory>
{
	private TermManager termManager;
	private EventManager eventManager;
	private EventOutcomeManager eventOutcomeManager;
	private EventCategoryManager eventCategoryManager;

	private EventPredictionManager eventPredictionManager;

	@RequestMapping(value = "**/predict/user/events/index", method = RequestMethod.GET)
	public Model listAllEvents(@RequestParam(value = "categoryid", defaultValue = "0") int categoryid, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, null);
		EventCategory category = viewListingImpl(categoryid, model, request, response);

		CategoryPagination pagination = new CategoryPagination(category, model.getTimeInSeconds(),
			new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));

		if ( category.exists() )
		{
			// Only display the live events
			//
			Set<Event> events = getEventManager().getLive(pagination);
			model.put("events", events);

			getLogger().debug("We have " + events.size() + " events in category " + categoryid);
			setTitle(model, category.getTitle());
		}
		else
		{
			model.put("events", Collections.EMPTY_SET);
			addMessageToModel(model, Model.TITLE, "msg.predict.user.index.title");
		}

		SingleClient client = model.getSingleClient();
		Term currTerm = getTermManager().getLiveTerm(model.getSingleClient());

		// Gather recently closed predictions for display, but don't display
		// posts from the previous term.
		//
		int oldestPostTime = Utils.currentTimeSeconds() - (86400 * 4);
		oldestPostTime = oldestPostTime < currTerm.getStartTimeSeconds() ? currTerm.getStartTimeSeconds() : oldestPostTime;

		pagination.setOrderBy("postedtm DESC");
		pagination.setQualifier("postedTime", new SimpleComparisonQualifier("postedtm", ">=", oldestPostTime));

		Set<Event> closedEvents = getEventManager().getAllActualized(client, currTerm.getStartTimeSeconds(), pagination);
		model.put("closedEvents", closedEvents);

		// Finally gather the user's pending bids in this category, these
		// are converted to WebModelEventPredictions to allow for front-end
		// logic involving timestamps to be executed.
		//
		pagination.setOrderBy(new EventPrediction().getDelegate().getFullTableName() + ".ser DESC");
		pagination.setQualifier("postedTime", null);
		Set<EventPrediction> pendingBids = getEventPredictionManager().getPendingPredictionsByUser(model.getUser(), pagination);

		model.put("pendingBids", pendingBids);

		return initResponse(model, response, null);
	}

	@RequestMapping(value = "**/predict/user/events/pastevents")
	public Model actualizedEvents(@ModelAttribute("pagination") CategoryPagination pagination, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, pagination);

		SingleClient client = model.getSingleClient();
		Term currTerm = getTermManager().getLiveTerm(client);

		// Force the sort order to end time
		//
		pagination.setOrderBy("endtm DESC");

		Set<Event> allEvents = getEventManager().getAllActualized(client, currTerm.getStartTimeSeconds(), pagination);

		model.put("events", allEvents);

		return initResponse(model, response, pagination);
	}

	@Override
	public CategoryManager<?, EventCategory> getCategoryManager()
	{
		return getEventCategoryManager();
	}

	@Override
	protected Set<EventCategory> loadCategories(Model model)
	{
		// We load the categories from the model since we have an interceptor that handles
		// filtering out live categories with at least one item in them
		//
		return (Set<EventCategory>) model.get(PredictCategoriesModelInterceptor.EVENT_CATEGORIES);
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

	public EventOutcomeManager getEventOutcomeManager()
	{
		return eventOutcomeManager;
	}

	@Autowired
	public void setEventOutcomeManager(EventOutcomeManager eventOutcomeManager)
	{
		this.eventOutcomeManager = eventOutcomeManager;
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

	public EventCategoryManager getEventCategoryManager()
	{
		return eventCategoryManager;
	}

	@Autowired
	public void setEventCategoryManager(EventCategoryManager eventCategoryManager)
	{
		this.eventCategoryManager = eventCategoryManager;
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
