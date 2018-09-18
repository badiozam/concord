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
package com.i4one.base.web.controller.admin.members;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.user.reports.UserCreationClassification;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.interceptor.ClientInterceptor;
import java.io.IOException;
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
public class MemberRegistrationReportController extends BaseAdminReportViewController
{
	private SingleClientManager singleClientManager;
	private UserManager userManager;

	@ModelAttribute
	public MemberReportSettings getModelAttribute(HttpServletRequest request)
	{
		MemberReportSettings retVal = new MemberReportSettings(ClientInterceptor.getSingleClient(request));

		return retVal;
	}

	@RequestMapping(value = "**/admin/members/reports/registration", method = RequestMethod.GET )
	public Model registrationClassificationReport(@ModelAttribute("reportSettings") MemberReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		return registrationClassificationReportRange(reportSettings, null, request, response);
	}

	@RequestMapping(value = "**/admin/members/reports/registration", method = RequestMethod.POST )
	public Model registrationClassificationReportRange(@ModelAttribute("reportSettings") MemberReportSettings reportSettings,
		BindingResult result,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, reportSettings);
		SingleClient client = model.getSingleClient();

		User user = new User();
		user.setClient(client);

		UserCreationClassification userClassification = new UserCreationClassification(client.getCalendar());
		return classificationReport(model,
			reportSettings,
			userClassification,
			() -> { return getUserManager().getReport(user, userClassification, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/admin/members/reports/registration", produces = "text/csv", method = RequestMethod.GET )
	public void registrationReportCSV(@ModelAttribute("reportSettings") MemberReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, reportSettings);
		SingleClient client = model.getSingleClient();

		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);
		reportSettings.setCSVExportUsageType(new MemberCSVExportUsageType(client));

		registrationClassificationReportRange(reportSettings, null, request, response);
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	public SingleClientManager getSingleClientManager()
	{
		return singleClientManager;
	}

	@Autowired
	public void setSingleClientManager(SingleClientManager singleClientManager)
	{
		this.singleClientManager = singleClientManager;
	}
}
