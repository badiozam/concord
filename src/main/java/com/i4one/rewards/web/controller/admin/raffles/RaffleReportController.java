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
package com.i4one.rewards.web.controller.admin.raffles;

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
import com.i4one.rewards.model.raffle.Raffle;
import com.i4one.rewards.model.raffle.RaffleEntry;
import com.i4one.rewards.model.raffle.RaffleEntryManager;
import com.i4one.rewards.model.raffle.RaffleManager;
import com.i4one.rewards.model.raffle.reports.RaffleUsageReport;
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
public class RaffleReportController extends BaseAdminReportViewController
{
	private RaffleManager raffleManager;
	private RaffleEntryManager raffleEntryManager;

	@RequestMapping(value = "**/rewards/admin/raffles/report")
	public Model usage(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Raffle raffle = getRaffleManager().getById(reportSettings.getId());

		Model model = initRequest(request, raffle);

		getLogger().debug("Got request for report for raffle with id " + reportSettings.getId());

		RaffleUsageReport raffleReport = new RaffleUsageReport(raffle, model.getSingleClient().getCalendar());
		RaffleEntry sample = new RaffleEntry();
		sample.setRaffle(raffle);

		return report(model, reportSettings, raffleReport, (report) -> { return getRaffleEntryManager().getReport(sample, report, reportSettings.getPagination()); }, response );
	}

	@RequestMapping(value = "**/rewards/admin/raffles/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, request, response);
	}

	public RaffleEntryManager getRaffleEntryManager()
	{
		return raffleEntryManager;
	}

	@Autowired
	public void setRaffleEntryManager(RaffleEntryManager raffleEntryManager)
	{
		this.raffleEntryManager = raffleEntryManager;
	}

	public RaffleManager getRaffleManager()
	{
		return raffleManager;
	}

	@Autowired
	public void setRaffleManager(RaffleManager raffleManager)
	{
		this.raffleManager = raffleManager;
	}
}
