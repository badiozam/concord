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
import com.i4one.base.model.user.reports.UserLastLoginReport;
import com.i4one.base.model.userlogin.UserLoginManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
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
public class MemberReportController extends BaseAdminReportViewController
{
	private UserLoginManager userLoginManager;
	private SingleClientManager singleClientManager;
	private UserManager userManager;

	@RequestMapping(value = "**/admin/members/reports/lastlogin")
	public Model lastLoginReport(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);

		SingleClient client = model.getSingleClient();

		User user = new User();
		user.setClient(client);
		
		UserLastLoginReport userReport = new UserLastLoginReport(client.getCalendar());
		return report(model, reportSettings, userReport, (report) -> { return getUserManager().getReport(user, report, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/admin/members/reports/lastlogin", produces = "text/csv")
	public void lastLoginReportCSV(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		lastLoginReport(reportSettings, request, response);
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
