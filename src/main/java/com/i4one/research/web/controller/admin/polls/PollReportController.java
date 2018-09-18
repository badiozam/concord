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
package com.i4one.research.web.controller.admin.polls;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
import com.i4one.research.model.poll.Poll;
import com.i4one.research.model.poll.PollManager;
import com.i4one.research.model.poll.PollResponse;
import com.i4one.research.model.poll.PollResponseManager;
import com.i4one.research.model.poll.PollResultsManager;
import com.i4one.research.model.poll.reports.PollResultsReport;
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
public class PollReportController extends BaseAdminReportViewController
{
	private PollManager pollManager;
	private PollResponseManager pollResponseManager;
	private PollResultsManager pollResultsManager;

	public Model usage(@ModelAttribute("reportSettings") ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Poll poll = getPollManager().getById(reportSettings.getId());

		Model model = initRequest(request, poll);

		return initResponse(model, response, reportSettings);
	}

	@RequestMapping(value = "**/research/admin/polls/report")
	public Model pollUsage(@ModelAttribute("reportSettings") ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Poll poll = getPollManager().getById(reportSettings.getId());
		Model model = initRequest(request, reportSettings, poll);

		SingleClient client = model.getSingleClient();
		PollResultsReport pollResultsReport = new PollResultsReport(poll, client.getCalendar());

		PollResponse template = new PollResponse();
		template.setPoll(poll);

		return classificationReport(model,
			reportSettings,
			pollResultsReport,
			() -> { return getPollResponseManager().getReport(template, pollResultsReport, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/research/admin/polls/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute("reportSettings") ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		pollUsage(reportSettings, request, response);
	}

	public PollResponseManager getPollResponseManager()
	{
		return pollResponseManager;
	}

	@Autowired
	public void setPollResponseManager(PollResponseManager pollResponseManager)
	{
		this.pollResponseManager = pollResponseManager;
	}

	public PollResultsManager getPollResultsManager()
	{
		return pollResultsManager;
	}

	@Autowired
	public void setPollResultsManager(PollResultsManager pollResultsManager)
	{
		this.pollResultsManager = pollResultsManager;
	}

	public PollManager getPollManager()
	{
		return pollManager;
	}

	@Autowired
	public void setPollManager(PollManager pollManager)
	{
		this.pollManager = pollManager;
	}

}
