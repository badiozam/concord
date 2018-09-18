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

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.ActivityReportSettings;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.promotion.model.event.Event;
import com.i4one.promotion.model.event.EventManager;
import com.i4one.promotion.model.event.EventResponse;
import com.i4one.promotion.model.event.EventResponseManager;
import com.i4one.promotion.model.event.reports.EventUsageReport;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EventReportController extends BaseAdminReportViewController
{
	private EventManager eventManager;
	private EventResponseManager eventResponseManager;

	@RequestMapping(value = "**/promotion/admin/events/report")
	public Model usage(@ModelAttribute ActivityReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int eventid,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Event event = getEventManager().getById(eventid);

		Model model = initRequest(request, event);
		getLogger().debug("Got request for report for event with id " + eventid);

		EventUsageReport eventUsageReport = new EventUsageReport(event, model.getSingleClient().getCalendar());

		EventResponse sample = new EventResponse();
		sample.setActionItem(event);

		return report(model, reportSettings, eventUsageReport, (report) -> { return getEventResponseManager().getReport(sample, report, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/promotion/admin/events/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ActivityReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int id,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, id, request, response);
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
}
