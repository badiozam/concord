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
package com.i4one.base.web.controller.admin.friendref;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.friendref.FriendRef;
import com.i4one.base.model.friendref.FriendRefManager;
import com.i4one.base.model.friendref.reports.FriendRefSentReport;
import com.i4one.base.model.friendref.reports.FriendRefSignUpReport;
import com.i4one.base.model.user.User;
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
public class FriendRefReportController extends BaseAdminReportViewController
{
	private FriendRefManager friendRefManager;

	@RequestMapping(value = "**/admin/friendrefs/reports/referrals")
	public Model referrals(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);

		SingleClient client = model.getSingleClient();

		User user = new User();
		user.setClient(client);

		FriendRef friendRef = new FriendRef();
		friendRef.setUser(user);
		friendRef.setClient(client);

		FriendRefSentReport friendRefReport = new FriendRefSentReport(client.getCalendar());

		return report(model, reportSettings, friendRefReport, (report) -> { return getFriendRefManager().getReport(friendRef, report, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/admin/friendrefs/reports/referrals", produces = "text/csv")
	public void referralsCSV(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		referrals(reportSettings, request, response);
	}

	@RequestMapping(value = "**/admin/friendrefs/reports/signups")
	public Model signups(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);

		SingleClient client = model.getSingleClient();

		User user = new User();
		user.setClient(client);

		FriendRef friendRef = new FriendRef();
		friendRef.setUser(user);
		friendRef.setClient(client);

		FriendRefSignUpReport friendRefReport = new FriendRefSignUpReport(client.getCalendar());
		return report(model, reportSettings, friendRefReport, (report) -> { return getFriendRefManager().getReport(friendRef, report, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/admin/friendrefs/reports/signups", produces = "text/csv")
	public void signupsCSV(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		signups(reportSettings, request, response);
	}

	public FriendRefManager getFriendRefManager()
	{
		return friendRefManager;
	}

	@Autowired
	public void setFriendRefManager(FriendRefManager friendRefManager)
	{
		this.friendRefManager = friendRefManager;
	}
}
