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
package com.i4one.base.model.report;

import com.i4one.base.model.manager.Manager;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface ReportManager extends Manager<ReportRecord, Report>
{
	/**
	 * Get all reports that share a specific title. This is useful for trending.
	 * 
	 * @param title The title of the reports
	 * 
	 * @return A (potentially empty) set of all reports with unique start/end times
	 */
	public Set<Report> getReportsByTitle(String title);

	/**
	 * Look up a report for the given start/end times and with a given title.
	 * 
	 * @param title The title of the report
	 * @param starttm The start time of the report
	 * @param endtm The end time of the report
	 * 
	 * @return The report as loaded from the database or an empty Report object
	 */
	public Report getReport(String title, int starttm, int endtm);

	/**
	 * Look up a sub report with the given title and parent.
	 * 
	 * @param title The title of the report
	 * @param parent The immediate parent of the report
	 * 
	 * @return The report as loaded from the database or an empty Report object
	 */
	public Report getSubReport(String title, Report parent);

	/**
	 * Save a top level report to be loaded at a later time.
	 * 
	 * @param topLevelReport The report to load
	 */
	public void saveReport(TopLevelReport topLevelReport);

	/**
	 * Load a previously saved top-level report.
	 * 
	 * @param topLevelReport The report to load
	 * 
	 * @return The loaded report or a NullTopLevelReport if none was found
	 */
	public TopLevelReport loadReport(TopLevelReport topLevelReport);

	/**
	 * Load a subreport of a given report
	 * 
	 * @param parent The parent report which matches its id to that of the database
	 * @param titleChain The titlechain of the report to lookup
	 * 
	 * @return The report with the given title chain or a NullTopLevelReport if not found
	 */
	public DataReportType loadSubReport(TopLevelReport parent, String titleChain);

	/**
	 * Populate a given empty report with the default demographics of interest
	 * for the given client.
	 * 
	 * @param report The report to populate
	 */
	public void populateReport(TopLevelReport report);
}
