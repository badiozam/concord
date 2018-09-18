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
package com.i4one.rewards.web.controller.admin.shoppings;

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
import com.i4one.rewards.model.WinnerCSVExportUsageType;
import com.i4one.rewards.model.shopping.Shopping;
import com.i4one.rewards.model.shopping.ShoppingManager;
import com.i4one.rewards.model.shopping.ShoppingPurchase;
import com.i4one.rewards.model.shopping.ShoppingPurchaseManager;
import com.i4one.rewards.model.shopping.reports.ShoppingUsageReport;
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
public class ShoppingReportController extends BaseAdminReportViewController
{
	private ShoppingManager shoppingManager;
	private ShoppingPurchaseManager shoppingPurchaseManager;

	@RequestMapping(value = "**/rewards/admin/shoppings/report")
	public Model usage(@ModelAttribute ReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int shoppingid,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Shopping shopping = getShoppingManager().getById(shoppingid);

		Model model = initRequest(request, shopping);

		getLogger().debug("Got request for report for shopping with id " + shoppingid);

		ShoppingUsageReport shoppingReport = new ShoppingUsageReport(shopping, model.getSingleClient().getCalendar());
		ShoppingPurchase sample = new ShoppingPurchase();
		sample.setShopping(shopping);

		return report(model, reportSettings, shoppingReport, (report) -> { return getShoppingPurchaseManager().getReport(sample, report, reportSettings.getPagination()); }, response );
	}

	@RequestMapping(value = "**/rewards/admin/shoppings/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int id,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);
		reportSettings.setCSVExportUsageType(new WinnerCSVExportUsageType());

		usage(reportSettings, id, request, response);
	}

	public ShoppingPurchaseManager getShoppingPurchaseManager()
	{
		return shoppingPurchaseManager;
	}

	@Autowired
	public void setShoppingPurchaseManager(ShoppingPurchaseManager shoppingPurchaseManager)
	{
		this.shoppingPurchaseManager = shoppingPurchaseManager;
	}

	public ShoppingManager getShoppingManager()
	{
		return shoppingManager;
	}

	@Autowired
	public void setShoppingManager(ShoppingManager shoppingManager)
	{
		this.shoppingManager = shoppingManager;
	}
}
