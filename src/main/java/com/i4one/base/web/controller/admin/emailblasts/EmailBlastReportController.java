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

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.emailblast.EmailBlastManager;
import com.i4one.base.model.emailblast.EmailBlastResponse;
import com.i4one.base.model.emailblast.EmailBlastResponseManager;
import com.i4one.base.model.emailblast.reports.EmailBlastUsageReport;
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
public class EmailBlastReportController extends BaseAdminReportViewController
{
	private EmailBlastManager emailBlastManager;
	private EmailBlastResponseManager emailBlastResponseManager;

	@RequestMapping(value = "**/base/admin/emailblasts/report")
	public Model usage(@ModelAttribute ReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int emailBlastid,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		EmailBlast emailBlast = getEmailBlastManager().getById(emailBlastid);

		Model model = initRequest(request, emailBlast);
		getLogger().debug("Got request for report for email blast with id " + emailBlastid);

		EmailBlastUsageReport emailBlastUsageReport = new EmailBlastUsageReport(emailBlast, model.getSingleClient().getCalendar());
		EmailBlastResponse sample = new EmailBlastResponse();
		sample.setEmailBlast(emailBlast);

		return report(model, reportSettings, emailBlastUsageReport, (report) -> { return getEmailBlastResponseManager().getReport(sample, report, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/promotion/admin/emailblasts/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int id,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, id, request, response);
	}

	public EmailBlastResponseManager getEmailBlastResponseManager()
	{
		return emailBlastResponseManager;
	}

	@Autowired
	public void setEmailBlastResponseManager(EmailBlastResponseManager emailBlastResponseManager)
	{
		this.emailBlastResponseManager = emailBlastResponseManager;
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

}
