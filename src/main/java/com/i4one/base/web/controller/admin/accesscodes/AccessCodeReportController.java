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
package com.i4one.base.web.controller.admin.accesscodes;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
import com.i4one.base.model.accesscode.AccessCode;
import com.i4one.base.model.accesscode.AccessCodeManager;
import com.i4one.base.model.accesscode.AccessCodeResponse;
import com.i4one.base.model.accesscode.AccessCodeResponseManager;
import com.i4one.base.model.accesscode.reports.AccessCodeResultsReport;
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
public class AccessCodeReportController extends BaseAdminReportViewController
{
	private AccessCodeManager accessCodeManager;
	private AccessCodeResponseManager accessCodeResponseManager;

	@RequestMapping(value = "**/base/admin/accesscodes/report")
	public Model usage(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		AccessCode code = getAccessCodeManager().getById(reportSettings.getId());
		Model model = initRequest(request, reportSettings, code);

		SingleClient client = model.getSingleClient();
		AccessCodeResultsReport resultsReport = new AccessCodeResultsReport(code, client.getCalendar());

		AccessCodeResponse template = new AccessCodeResponse();
		template.setAccessCode(code);

		return classificationReport(model,
			reportSettings,
			resultsReport,
			() -> { return getAccessCodeResponseManager().getReport(template, resultsReport, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/base/admin/accesscodes/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, request, response);
	}

	public AccessCodeResponseManager getAccessCodeResponseManager()
	{
		return accessCodeResponseManager;
	}

	@Autowired
	public void setAccessCodeResponseManager(AccessCodeResponseManager accessCodeResponseManager)
	{
		this.accessCodeResponseManager = accessCodeResponseManager;
	}

	public AccessCodeManager getAccessCodeManager()
	{
		return accessCodeManager;
	}

	@Autowired
	public void setAccessCodeManager(AccessCodeManager accessCodeManager)
	{
		this.accessCodeManager = accessCodeManager;
	}
}
