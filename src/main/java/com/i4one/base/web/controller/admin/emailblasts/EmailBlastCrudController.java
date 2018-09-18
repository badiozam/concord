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
package com.i4one.base.web.controller.admin.emailblasts;

import com.i4one.base.core.Utils;
import com.i4one.base.model.client.ClientOptionManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.emailblast.EmailBlastManager;
import com.i4one.base.model.emailblast.EmailBlastRecord;
import com.i4one.base.web.controller.admin.BaseClientTypeCrudController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
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
public class EmailBlastCrudController extends BaseClientTypeCrudController<EmailBlastRecord, EmailBlast>
{
	private ClientOptionManager clientOptionManager;
	private EmailBlastManager emailBlastManager;

	private static final String VALID_SCHEDULES = "base.emailBlastManager.validSchedules";

	@Override
	public Model initRequest(HttpServletRequest request, EmailBlast modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelEmailBlast )
		{
			WebModelEmailBlast emailblast = (WebModelEmailBlast)modelAttribute;
			SingleClient client = model.getSingleClient();

			// The types of schedules. We expect a client option in the form of possible
			// cron schedules and their corresponding messages. For example:
			//
			// 0 0 0 * * *:msg.base.emailblast.validSchedule.daily
			// 0 0 0 * * Mon-Fri:msg.base.emailblast.validSchedule.weekdays
			// 0 0 0 * * Sun-Sat:msg.base.emailblast.validSchedule.weekends
			// 0 0 0 * * Mon:msg.base.emailblasts.validSchedule.mon
			// 0 0 0 * * Tue:msg.base.emailblasts.validSchedule.tue
			// 0 0 0 * * Wed:msg.base.emailblasts.validSchedule.wed
			// 0 0 0 * * Thu:msg.base.emailblasts.validSchedule.thu
			// 0 0 0 * * Fri:msg.base.emailblasts.validSchedule.fri
			// 0 0 0 * * Sat:msg.base.emailblasts.validSchedule.sat
			// 0 0 0 * * Sun:msg.base.emailblasts.validSchedule.sun
			//
			Map<String, String> validScheduleTypes = new LinkedHashMap<>();
			String validSchedules = getClientOptionManager().getOptionValue(client, VALID_SCHEDULES);
			getLogger().debug("ValidSchedules = {}", validSchedules);
			for (String schedules : validSchedules.split("\n"))
			{
				String[] scheduleNameVal = schedules.split(":");
				if ( scheduleNameVal.length > 1 && !Utils.isEmpty(scheduleNameVal[1]) )
				{
					getLogger().debug("Adding {} => {}", scheduleNameVal[0], scheduleNameVal[1]);
					validScheduleTypes.put(scheduleNameVal[0], model.buildMessage(scheduleNameVal[1]));
				}
				else
				{
					getLogger().debug("Skipping addition of {} with length of split at {}", schedules, scheduleNameVal.length);
				}
			}

			// We add the "other" type just to allow a custom schedule
			//
			validScheduleTypes.put("--", model.buildMessage("msg.base.emailblast.validSchedule.other"));
			model.put("validScheduleTypes", validScheduleTypes);
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.emailblasts";
	}

	@Override
	protected Manager<EmailBlastRecord, EmailBlast> getManager()
	{
		return getEmailBlastManager();
	}

	@RequestMapping(value = "**/base/admin/emailblasts/preview", method = RequestMethod.GET)
	public Model preview(@ModelAttribute("emailblast") WebModelEmailBlast emailblast,
					@RequestParam(value = "id", required = false) Integer id,
					HttpServletRequest request, HttpServletResponse response)
	{
		if ( id != null )
		{
			emailblast.setSer(id);
			emailblast.loadedVersion();
		}

		Model model = this.initRequest(request, emailblast);

		return this.initResponse(model, response, emailblast);
	}

	@RequestMapping(value = { "**/base/admin/emailblasts/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("emailblast") WebModelEmailBlast emailblast,
					@RequestParam(value = "id", required = false) Integer emailblastId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model =  createUpdateImpl(emailblast, emailblastId, request, response);

		if ( !emailblast.exists() )
		{
			// By default we schedule the e-mail a week in advance
			//
			Calendar cal = model.getSingleClient().getCalendar();
			cal.setTimeInMillis(model.getTimeInMillis());
			cal.add(Calendar.WEEK_OF_YEAR, 1);

			emailblast.setMatureTimeSeconds(Utils.getStartOfDay(cal));
			//emailblast.setTitle(model.buildIMessage("base.emailBlastManager.defaultTitle"));
			//emailblast.setBody(model.buildIMessage("base.emailBlastManager.defaultBody"));
		}

		return model;
	}

	@RequestMapping(value = "**/base/admin/emailblasts/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("emailblast") WebModelEmailBlast emailblast, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(emailblast, result, request, response);
	}

	@RequestMapping(value = { "**/base/admin/emailblasts/remove" }, method = RequestMethod.GET)
	public ModelAndView remove(@RequestParam(value="id") Integer id, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return removeImpl(id, request, response);
	}

	@Override
	protected String getRemoveRedirURL(Model model, EmailBlast item)
	{
		return "index.html?display=" + (item.isPast(model.getTimeInSeconds()) ? "past" : item.isLive(model.getTimeInSeconds()) ? "live" : "future");
	}

	@RequestMapping(value = { "**/base/admin/emailblasts/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer emailblastId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(emailblastId, request, response);
	}

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}

	public ClientOptionManager getClientOptionManager()
	{
		return clientOptionManager;
	}

	@Autowired
	public void setClientOptionManager(ClientOptionManager clientOptionManager)
	{
		this.clientOptionManager = clientOptionManager;
	}

}