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
package com.i4one.predict.web.controller.admin.events;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.base.model.category.CategorySelectStringifier;
import com.i4one.base.web.interceptor.ClientInterceptor;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.category.EventCategoryRecord;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventManager;
import com.i4one.predict.model.event.EventRecord;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.eventoutcome.EventOutcomeManager;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import com.i4one.predict.model.term.TermManager;
import com.i4one.predict.model.term.TermSettings;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EventCrudController extends BaseTerminableSiteGroupTypeCrudController<EventRecord, Event>
{
	private CategoryManager<EventCategoryRecord, EventCategory> eventCategoryManager;

	private TermManager termManager;
	private EventManager eventManager;
	private EventOutcomeManager eventOutcomeManager;
	private EventPredictionManager eventPredictionManager;

	@ModelAttribute("event")
	public WebModelEvent initEvent(HttpServletRequest request)
	{
		WebModelEvent event = new WebModelEvent();

		SingleClient client = ClientInterceptor.getSingleClient(request);
		event.setPostsBySeconds(event.getEndTimeSeconds() + 86400);

		// Default to the current client
		//
		event.setClient(client);

		// Default to uncategorized
		//
		event.setCategory(new EventCategory());

		TermSettings settings = getTermManager().getSettings(client);

		// Defaults as set in the settings page
		//
		event.setMinBid(settings.getDefaultMinBid());
		event.setMaxBid(settings.getDefaultMaxBid());

		event.setBalanceExpenses(settings.getDefaultExpenses());

		return event;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Event modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelEvent )
		{
			WebModelEvent event = (WebModelEvent)modelAttribute;
			SingleClient client = model.getSingleClient();

			// Get all of the outcomes from the manager and add them, this might very well be an empty list
			// if the event is brand new
			//
			Set<EventOutcome> eventOutcomes = getEventOutcomeManager().getEventOutcomes(event, SimplePaginationFilter.NONE);
			event.setPossibleOutcomes(eventOutcomes);
			model.put("outcomes", toSelectMapping(eventOutcomes, EventOutcome::getDescr, model.getLanguage()));

			// Same thign for categories
			//
			Set<EventCategory> eventCategories = getEventCategoryManager().getAllCategories(new ClientPagination(model.getSingleClient(), SimplePaginationFilter.NONE));
			model.put("categories", toSelectMapping(eventCategories, new CategorySelectStringifier(client), model.getLanguage()));

			boolean isRequestGet = request.getMethod().equals("GET");
			if ( !event.exists() && isRequestGet )
			{
				Calendar cal = model.getRequest().getCalendar();

				// Starts next month
				//
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
				event.setStartTimeSeconds(Utils.getStartOfDay(cal));

				// Ends the month after
				//
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
				event.setEndTimeSeconds(Utils.getEndOfDay(cal));

				// Closes 24 hours before it ends
				//
				event.setClosesBySeconds(event.getEndTimeSeconds() - 86400);

				// Posts the day after it ends
				//
				cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
				event.setPostsBySeconds(Utils.getEndOfDay(cal));
			}
		}

		return model;
	}

	@RequestMapping(value = { "**/predict/admin/events/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer eventId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(eventId, request, response);
	}

	@RequestMapping(value = { "**/predict/admin/events/update", "**/predict/admin/events/setactualoutcome" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("event") WebModelEvent event,
					@RequestParam(value = "id", required = false) Integer eventId,
					HttpServletRequest request, HttpServletResponse response)
	{
		return createUpdateImpl(event, eventId, request, response);
	}

	@RequestMapping(value = "**/predict/admin/events/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("event") WebModelEvent event, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(event, result, request, response);
	}

	@RequestMapping(value = "**/predict/admin/events/setactualoutcome", method = RequestMethod.POST)
	public Model setActualOutcome(@ModelAttribute("event") WebModelEvent event, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, event);

		try
		{
			getEventManager().setActualOutcome(event);

			// Right now we actualize the outcome right away since the number of players is
			// small but this will eventually have to be turned into its own task
			//
			getEventPredictionManager().actualizeOutcomeForAll(event.getActualOutcome());
		}
		catch (Errors errors)
		{
			fail(model, "msg.predict.admin.events.update.failure", result, errors);
		}

		return initResponse(model, response, event);
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

	public EventOutcomeManager getEventOutcomeManager()
	{
		return eventOutcomeManager;
	}

	@Autowired
	public void setEventOutcomeManager(EventOutcomeManager eventOutcomeManager)
	{
		this.eventOutcomeManager = eventOutcomeManager;
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

	public CategoryManager<EventCategoryRecord, EventCategory> getEventCategoryManager()
	{
		return eventCategoryManager;
	}

	@Autowired
	public void setEventCategoryManager(CategoryManager<EventCategoryRecord, EventCategory> eventCategoryManager)
	{
		this.eventCategoryManager = eventCategoryManager;
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

	@Override
	protected String getMessageRoot()
	{
		return "msg.predict.admin.events";
	}

	@Override
	protected Manager<EventRecord, Event> getManager()
	{
		return getEventManager();
	}
}
