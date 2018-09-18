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
package com.i4one.base.web.controller.admin.preferences;

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminReportViewController;
import com.i4one.base.web.controller.admin.ReportSettings;
import com.i4one.base.model.preference.Preference;
import com.i4one.base.model.preference.PreferenceManager;
import com.i4one.base.model.preference.UserPreference;
import com.i4one.base.model.preference.UserPreferenceManager;
import com.i4one.base.model.preference.reports.PreferenceUsageReport;
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
public class PreferenceReportController extends BaseAdminReportViewController
{
	private PreferenceManager preferenceManager;
	private UserPreferenceManager userPreferenceManager;

	@RequestMapping(value = "**/base/admin/preferences/report")
	public Model usage(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Preference preference = getPreferenceManager().getById(reportSettings.getId());

		Model model = initRequest(request, preference);

		getLogger().debug("Got request for report for preference with id " + reportSettings.getId());

		PreferenceUsageReport preferenceReport = new PreferenceUsageReport(preference, model.getSingleClient().getCalendar());
		UserPreference sample = new UserPreference();
		sample.setPreference(preference);

		return report(model, reportSettings, preferenceReport, (report) -> { return getUserPreferenceManager().getReport(sample, report, reportSettings.getPagination()); }, response);
	}

	@RequestMapping(value = "**/base/admin/preferences/report", produces = "text/csv")
	public void usageCSV(@ModelAttribute ReportSettings reportSettings,
		HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		reportSettings.setShowRawData(true);
		reportSettings.setCsv(true);

		usage(reportSettings, request, response);
	}

	public UserPreferenceManager getUserPreferenceManager()
	{
		return userPreferenceManager;
	}

	@Autowired
	public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager)
	{
		this.userPreferenceManager = userPreferenceManager;
	}

	public PreferenceManager getPreferenceManager()
	{
		return preferenceManager;
	}

	@Autowired
	public void setPreferenceManager(PreferenceManager preferenceManager)
	{
		this.preferenceManager = preferenceManager;
	}
}
