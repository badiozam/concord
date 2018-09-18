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
import com.i4one.base.model.client.reports.UserLoginClassification;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.userlogin.UserLogin;
import com.i4one.base.model.userlogin.UserLoginManager;
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
public class MemberLoginReportController extends BaseAdminReportViewController
{
	private UserLoginManager userLoginManager;
	private UserManager userManager;

	@ModelAttribute
	public UserLoginReportSettings getModelAttribute(HttpServletRequest request)
	{
		UserLoginReportSettings retVal = new UserLoginReportSettings(ClientInterceptor.getSingleClient(request));

		return retVal;
	}

	@RequestMapping(value = "**/admin/members/reports/userlogin", method = RequestMethod.GET )
	public Model userLoginClassificationReport(@ModelAttribute("reportSettings") UserLoginReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		return userLoginClassificationReportRange(reportSettings, null, request, response);
	}

	@RequestMapping(value = "**/admin/members/reports/userlogin", method = RequestMethod.POST )
	public Model userLoginClassificationReportRange(@ModelAttribute("reportSettings") UserLoginReportSettings reportSettings,
		BindingResult result,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, reportSettings);
		SingleClient client = model.getSingleClient();

		UserLogin sample = new UserLogin();
		sample.setClient(client);

		UserLoginClassification userClassification = new UserLoginClassification(client.getCalendar());
		return classificationReport(model,
			reportSettings,
			userClassification,
			() -> { return getUserLoginManager().getReport(sample, userClassification, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/admin/members/reports/userlogin", produces = "text/csv", method = RequestMethod.GET )
	public void userLoginReportCSV(@ModelAttribute("reportSettings") UserLoginReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		userLoginClassificationReportRange(reportSettings, null, request, response);
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

	public UserLoginManager getUserLoginManager()
	{
		return userLoginManager;
	}

	@Autowired
	public void setUserLoginManager(UserLoginManager userLoginManager)
	{
		this.userLoginManager = userLoginManager;
	}

}
