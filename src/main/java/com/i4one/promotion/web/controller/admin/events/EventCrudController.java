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
package com.i4one.promotion.web.controller.admin.events;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.base.model.category.CategorySelectStringifier;
import com.i4one.promotion.model.event.Event;
import com.i4one.promotion.model.event.EventManager;
import com.i4one.promotion.model.event.EventRecord;
import com.i4one.promotion.model.event.category.EventCategory;
import com.i4one.promotion.model.event.category.EventCategoryManager;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
	private EventManager eventManager;
	private EventCategoryManager eventCategoryManager;

	@Override
	public Model initRequest(HttpServletRequest request, Event modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelEvent )
		{
			WebModelEvent event = (WebModelEvent)modelAttribute;
			SingleClient client = model.getSingleClient();

			Set<EventCategory> eventCategories = getEventCategoryManager().getAllCategories(new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));
			model.put("categories", toSelectMapping(eventCategories, new CategorySelectStringifier(client), model.getLanguage()));
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.promotion.admin.events";
	}

	@Override
	protected Manager<EventRecord, Event> getManager()
	{
		return getEventManager();
	}

	@RequestMapping(value = { "**/promotion/admin/events/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("event") WebModelEvent event,
					@RequestParam(value = "id", required = false) Integer eventId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model =  createUpdateImpl(event, eventId, request, response);

		if ( !event.exists() )
		{
			event.setIntro(model.buildIMessage("promotion.eventManager.defaultIntro"));
		}

		return model;
	}

	@RequestMapping(value = "**/promotion/admin/events/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("event") WebModelEvent event, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(event, result, request, response);
	}

	@RequestMapping(value = { "**/promotion/admin/events/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer eventId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(eventId, request, response);
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

}
