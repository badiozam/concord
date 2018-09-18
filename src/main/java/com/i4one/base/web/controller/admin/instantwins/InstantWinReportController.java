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
package com.i4one.base.web.controller.admin.instantwins;

import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.instantwin.reports.InstantWinUsageReport;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class InstantWinReportController extends BaseAdminReportViewController
{
	@RequestMapping(value = "**/base/admin/instantwins/report")
	public Model usage(@ModelAttribute ReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int instantWinid,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		InstantWin instantWin = getInstantWinManager().getById(instantWinid);

		Model model = initRequest(request, instantWin);
		getLogger().debug("Got request for report for instantWin with id " + instantWinid);

		InstantWinUsageReport instantWinUsageReport = new InstantWinUsageReport(instantWin, model.getSingleClient().getCalendar());

		return report(model, reportSettings, instantWinUsageReport, (report) -> { return getInstantWinManager().getReport(instantWin, report, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/base/admin/instantwins/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ReportSettings reportSettings,
		@RequestParam(value = "id", required = true) int id,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, id, request, response);
	}

}
