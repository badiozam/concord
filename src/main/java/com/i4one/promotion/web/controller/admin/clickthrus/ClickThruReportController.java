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
package com.i4one.promotion.web.controller.admin.clickthrus;

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.ActivityReportSettings;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.promotion.model.clickthru.ClickThru;
import com.i4one.promotion.model.clickthru.ClickThruManager;
import com.i4one.promotion.model.clickthru.ClickThruResponse;
import com.i4one.promotion.model.clickthru.ClickThruResponseManager;
import com.i4one.promotion.model.clickthru.reports.ClickThruUsageReport;
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
public class ClickThruReportController extends BaseAdminReportViewController
{
	private ClickThruManager clickThruManager;
	private ClickThruResponseManager clickThruResponseManager;

	@RequestMapping(value = "**/promotion/admin/clickthrus/report")
	public Model usage(@ModelAttribute ActivityReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int clickThruid,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		ClickThru clickThru = getClickThruManager().getById(clickThruid);

		Model model = initRequest(request, clickThru);
		getLogger().debug("Got request for report for clickThru with id " + clickThruid);

		ClickThruUsageReport clickThruUsageReport = new ClickThruUsageReport(clickThru, model.getSingleClient().getCalendar());
		ClickThruResponse sample = new ClickThruResponse();
		sample.setClickThru(clickThru);

		return report(model, reportSettings, clickThruUsageReport, (report) -> { return getClickThruResponseManager().getReport(sample, report, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/promotion/admin/clickthrus/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ActivityReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int id,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, id, request, response);
	}

	public ClickThruResponseManager getClickThruResponseManager()
	{
		return clickThruResponseManager;
	}

	@Autowired
	public void setClickThruResponseManager(ClickThruResponseManager clickThruResponseManager)
	{
		this.clickThruResponseManager = clickThruResponseManager;
	}

	public ClickThruManager getClickThruManager()
	{
		return clickThruManager;
	}

	@Autowired
	public void setClickThruManager(ClickThruManager clickThruManager)
	{
		this.clickThruManager = clickThruManager;
	}
}
