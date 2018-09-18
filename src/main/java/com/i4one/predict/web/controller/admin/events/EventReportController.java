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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.ActivityReportSettings;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.interceptor.ClientInterceptor;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventManager;
import com.i4one.predict.model.event.reports.EventResultsReport;
import com.i4one.predict.model.eventprediction.EventPrediction;
import com.i4one.predict.model.eventprediction.EventPredictionManager;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EventReportController extends BaseAdminReportViewController
{
	private EventManager eventManager;
	private EventPredictionManager eventPredictionManager;

	@ModelAttribute
	public ActivityReportSettings getModelAttribute(HttpServletRequest request)
	{
		ActivityReportSettings retVal = new ActivityReportSettings(ClientInterceptor.getSingleClient(request));

		return retVal;
	}

	@RequestMapping(value = "**/predict/admin/events/report")
	public Model eventUsage(@ModelAttribute("reportSettings") ActivityReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Event event = getEventManager().getById(reportSettings.getId());
		Model model = initRequest(request, reportSettings, event);

		SingleClient client = model.getSingleClient();
		EventResultsReport eventResultsReport = new EventResultsReport(event, client.getCalendar(), reportSettings);

		EventPrediction template = new EventPrediction();
		template.setEvent(event);

		return classificationReport(model,
			reportSettings,
			eventResultsReport,
			() -> { return getEventPredictionManager().getReport(template, eventResultsReport, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/predict/admin/events/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute("reportSettings") ActivityReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		eventUsage(reportSettings, request, response);
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
