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
package com.i4one.promotion.web.controller.admin.events.settings;

import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.base.web.interceptor.ClientInterceptor;
import com.i4one.promotion.model.event.EventManager;
import com.i4one.promotion.model.event.EventSettings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EventSettingsController extends BaseAdminViewController
{
	private EventManager eventManager;
	
	@ModelAttribute("settings")
	public EventSettings initSettings(HttpServletRequest request)
	{
		return getEventManager().getSettings(ClientInterceptor.getSingleClient(request));
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model retVal = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof EventSettings )
		{
			EventSettings settings = (EventSettings)modelAttribute;

			settings.setClient(retVal.getSingleClient());
		}

		return retVal;
	}

	@RequestMapping(value = { "**/promotion/admin/events/settings/index" }, method = RequestMethod.GET)
	public Model viewSettings(@ModelAttribute("settings") EventSettings settings,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		return initResponse(model, response, settings);
	}

	@RequestMapping(value = { "**/promotion/admin/events/settings/index" }, method = RequestMethod.POST)
	public Model updateSettings(@ModelAttribute("settings") EventSettings settings, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, settings);

		try
		{
			// This has to happen since the settings object is not a ClientType
			//
			SingleClient client = model.getSingleClient();
			settings.setClient(client);

			getEventManager().updateSettings(settings);

			success(model, "msg.promotion.admin.settings.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.promotion.admin.settings.failure", result, errors);
		}

		return initResponse(model, response, settings);
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

}
