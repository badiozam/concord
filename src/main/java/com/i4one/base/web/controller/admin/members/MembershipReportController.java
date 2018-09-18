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

import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.client.reports.UserMembershipClassification;
import com.i4one.base.model.client.reports.UserMembershipZipClassification;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.model.user.UserManager;
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
public class MembershipReportController extends BaseAdminReportViewController
{
	private SingleClientManager singleClientManager;
	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;
	private UserManager userManager;

	@ModelAttribute
	public MemberReportSettings getModelAttribute(HttpServletRequest request)
	{
		MemberReportSettings retVal = new MemberReportSettings(ClientInterceptor.getSingleClient(request));

		return retVal;
	}

	@RequestMapping(value = "**/admin/members/reports/membership", method = RequestMethod.GET )
	public Model membershipClassificationReport(@ModelAttribute("reportSettings") MemberReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		return membershipClassificationReportRange(reportSettings, null, request, response);
	}

	@RequestMapping(value = "**/admin/members/reports/membership", method = RequestMethod.POST )
	public Model membershipClassificationReportRange(@ModelAttribute("reportSettings") MemberReportSettings reportSettings,
		BindingResult result,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, reportSettings);
		SingleClient client = model.getSingleClient();

		UserBalance userBalance = new UserBalance();
		userBalance.setBalance(getBalanceManager().getDefaultBalance(client));

		UserMembershipClassification userClassification = new UserMembershipClassification(client.getCalendar());
		return classificationReport(model,
			reportSettings,
			userClassification,
			() -> { return getUserBalanceManager().getReport(userBalance, userClassification, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/admin/members/reports/postalranking", method = RequestMethod.GET )
	public Model zipClassificationReport(@ModelAttribute("reportSettings") MemberReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		return zipClassificationReportRange(reportSettings, null, request, response);
	}

	@RequestMapping(value = "**/admin/members/reports/postalranking", method = RequestMethod.POST )
	public Model zipClassificationReportRange(@ModelAttribute("reportSettings") MemberReportSettings reportSettings,
		BindingResult result,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, reportSettings);
		SingleClient client = model.getSingleClient();

		UserBalance userBalance = new UserBalance();
		userBalance.setBalance(getBalanceManager().getDefaultBalance(client));

		UserMembershipZipClassification userClassification = new UserMembershipZipClassification(3);
		return classificationReport(model,
			reportSettings,
			userClassification,
			() -> { return getUserBalanceManager().getReport(userBalance, userClassification, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/admin/members/reports/membership", produces = "text/csv", method = RequestMethod.GET )
	public void membershipReportCSV(@ModelAttribute("reportSettings") MemberReportSettings reportSettings,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, reportSettings);
		SingleClient client = model.getSingleClient();

		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);
		reportSettings.setCSVExportUsageType(new MemberCSVExportUsageType(client));

		membershipClassificationReportRange(reportSettings, null, request, response);
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

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

}
