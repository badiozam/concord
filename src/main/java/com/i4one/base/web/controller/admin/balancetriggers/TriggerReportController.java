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
package com.i4one.base.web.controller.admin.balancetriggers;

import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import com.i4one.base.model.balancetrigger.UserBalanceTriggerManager;
import com.i4one.base.model.balancetrigger.reports.BalanceTriggerResultsReport;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.ActivityReportSettings;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.interceptor.ClientInterceptor;
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
public class TriggerReportController extends BaseAdminReportViewController
{
	private UserBalanceTriggerManager userBalanceTriggerManager;

	@ModelAttribute
	public ActivityReportSettings getModelAttribute(HttpServletRequest request)
	{
		ActivityReportSettings retVal = new ActivityReportSettings(ClientInterceptor.getSingleClient(request));

		return retVal;
	}

	@RequestMapping(value = "**/base/admin/balancetriggers/report")
	public Model usage(@ModelAttribute("reportSettings") ActivityReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		BalanceTrigger trigger = getBalanceTriggerManager().getById(reportSettings.getId());
		Model model = initRequest(request, reportSettings, trigger);

		SingleClient client = model.getSingleClient();
		BalanceTriggerResultsReport resultsReport = new BalanceTriggerResultsReport(trigger, client.getCalendar(), reportSettings);

		UserBalanceTrigger template = new UserBalanceTrigger();
		template.setBalanceTrigger(trigger);

		return classificationReport(model,
			reportSettings,
			resultsReport,
			() -> { return getUserBalanceTriggerManager().getReport(template, resultsReport, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/base/admin/balancetriggers/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute("reportSettings") ActivityReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, request, response);
	}

	public UserBalanceTriggerManager getUserBalanceTriggerManager()
	{
		return userBalanceTriggerManager;
	}

	@Autowired
	public void setUserBalanceTriggerManager(UserBalanceTriggerManager userBalanceTriggerManager)
	{
		this.userBalanceTriggerManager = userBalanceTriggerManager;
	}

}
