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
package com.i4one.promotion.web.controller.user.events;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseCategorizableListingController;
import com.i4one.promotion.model.event.Event;
import com.i4one.promotion.model.event.EventManager;
import com.i4one.promotion.model.event.EventResponse;
import com.i4one.promotion.model.event.EventResponseManager;
import com.i4one.promotion.model.event.category.EventCategory;
import com.i4one.promotion.model.event.category.EventCategoryManager;
import com.i4one.promotion.web.interceptor.PromotionCategoriesModelInterceptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EventResponseFormController extends BaseCategorizableListingController<Event, EventCategory>
{
	private EventManager eventManager;
	private EventCategoryManager eventCategoryManager;
	private EventResponseManager eventResponseManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof EventResponse )
		{
		}

		return model;
	}

	@Override
	protected Set<EventCategory> loadCategories(Model model)
	{
		return (Set<EventCategory>) model.get(PromotionCategoriesModelInterceptor.EVENT_CATEGORIES);
	}

	@RequestMapping("**/promotion/user/events/index")
	public ModelAndView listAllEvents(@RequestParam(value = "categoryid", defaultValue = "0") int categoryid, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, null);

		EventCategory category = viewListingImpl(categoryid, model, request, response);

		// Only display the live events
		//
		Set<Event> liveEvents = getEventManager().getLive(new CategoryPagination(category, model.getTimeInSeconds(),
			new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE)));

		User user = model.getUser();

		// Go through all of the available events and load the status of any the 
		// user may have participated in
		//
		Map<Event, EventResponse> events = new LinkedHashMap<>();
		liveEvents.forEach((event) ->
		{
			events.put(event, getEventResponseManager().getActivity(event, user));
		});

		getLogger().debug("We have " + events.size() + " events in category " + categoryid);

		model.put("events", events);
		addMessageToModel(model, Model.TITLE, "msg.promotion.user.events.index.title");

		// We have more than one or we have no events, in either case we can have the view
		// determine the outcome
		//
		ModelAndView retVal = new ModelAndView();
		retVal.addAllObjects(initResponse(model, response, null));

		return retVal;
	}

	@RequestMapping(value = "**/promotion/user/events/{id}")
	public void processEvent(@PathVariable("id") int id, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException
	{
		Model model = initRequest(request, null);

		User user = model.getUser();

		EventResponse eventResponse = new EventResponse();
		eventResponse.getEvent().setSer(id);
		eventResponse.setUser(user);


		try
		{
			if ( eventResponse.getEvent().exists() )
			{
				ReturnType<EventResponse> processedClickThru = getEventResponseManager().create(eventResponse);

				// Regardless of status, we send to the programmed URL
				//
				if ( !Utils.isEmpty(processedClickThru.getPost().getEvent().getSponsorURL()) )
				{
					response.sendRedirect(processedClickThru.getPost().getEvent().getSponsorURL());
				}
				else
				{
					// Avoid inifinite redirects
					//
					response.sendRedirect(model.getBaseURL());
				}
			}
			else
			{
				getLogger().debug("No live events found matching " + eventResponse.getEvent());
				response.sendRedirect(model.getBaseURL());
			}
		}
		catch (Errors errors)
		{
			getLogger().warn("processEvent failed", errors);
			response.sendRedirect(model.getBaseURL());
		}
	}

	public EventResponseManager getEventResponseManager()
	{
		return eventResponseManager;
	}

	@Autowired
	public void setEventResponseManager(EventResponseManager eventResponseManager)
	{
		this.eventResponseManager = eventResponseManager;
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

	public EventCategoryManager getEventCategoryManager()
	{
		return eventCategoryManager;
	}

	@Autowired
	public void setEventCategoryManager(EventCategoryManager eventCategoryManager)
	{
		this.eventCategoryManager = eventCategoryManager;
	}

	@Override
	public CategoryManager<?, EventCategory> getCategoryManager()
	{
		return getEventCategoryManager();
	}
}
