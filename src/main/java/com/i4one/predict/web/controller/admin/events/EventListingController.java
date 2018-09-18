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

import com.i4one.base.model.category.CategoryPagination;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.TerminableListingPagination;
import com.i4one.base.web.controller.admin.BaseTerminableExpendableListingController;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventManager;
import com.i4one.predict.model.event.EventRecord;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EventListingController extends BaseTerminableExpendableListingController<EventRecord, Event>
{
	private EventManager eventManager;
	private EventPredictionManager eventPredictionManager;

	@RequestMapping(value = { "**/predict/admin/events/listing", "**/predict/admin/events/index" })
	public Model viewListing(@ModelAttribute("listing") TerminableListingPagination<Event> listing, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		return viewListingImpl(listing, result, request, response);
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

	@Override
	protected TerminablePagination getItemPagination(Model model, TerminableListingPagination<Event> listing)
	{
		SingleClient client = model.getSingleClient();
		return new CategoryPagination(new EventCategory(),
			model.getTimeInSeconds(),
			new ClientPagination(client, listing.getPagination()));
	}

	@Override
	protected TerminableManager<EventRecord, Event> getManager()
	{
		return getEventManager();
	}

	@Override
	protected ActivityManager<?, ?, Event> getActivityManager()
	{
		return getEventPredictionManager();
	}

}
