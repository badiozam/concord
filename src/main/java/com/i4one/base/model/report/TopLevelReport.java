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

import com.i4one.base.dao.UsageRecordType;
import java.util.Calendar;

/**
 * A top level report has no parent and contains a set of AggregateReports.
 * 
 * @author Hamid Badiozamani
 */
public interface TopLevelReport extends DataReportType
{
	/**
	 * Converts the input usage record into the appropriate model object
	 * for this report. A null return value indicates that the conversion
	 * is to be delegated to each of the sub-reports. As such no eligibility
	 * check will be performed, and the corresponding tally will not be
	 * incremented.
	 * 
	 * @param usageRecord The usage record to convert
	 * 
	 * @return The model object representing the usage record
	 */
	public ReportUsageType getUsage(UsageRecordType usageRecord);

	/**
	 * Add a usage record for processing to this report. This method attempts
	 * to convert the incoming record to a T type using the getUsage(..) method.
	 * If it is unsuccessful, the record is delegated to all sub-reports.
	 * 
	 * @param usage The usage record to process
	 */
	public void process(UsageRecordType usage);

	/**
	 * Get the end time calendar instance currently in use by this report.
	 * The time of the calendar reflects the relevant cutoff point for the report.
	 * 
	 * @return The calendar currently in use
	 */
	public Calendar getEndCalendar();

	/**
	 * Set the end calendar instance to use by this report. The time of the calendar
	 * reflects the relevant cutoff point for the report as well as any age
	 * or other time-sensitive demographic calculations.
	 * 
	 * @param calendar The new calendar to use
	 */
	public void setEndCalendar(Calendar calendar);

	/**
	 * Get the start calendar instance currently in use by this report. The time of the calendar
	 * reflects the relevant starting point for the report.
	 * 
	 * @return The calendar currently in use
	 */
	public Calendar getStartCalendar();

	/**
	 * Set the start calendar instance to use by this report. The time of the calendar
	 * reflects the relevant starting point for the report as well as any age
	 * or other time-sensitive demographic calculations.
	 * 
	 * @param calendar The new calendar to use
	 */
	public void setStartCalendar(Calendar calendar);

	/**
	 * Whether this report can be cached or not. Cached reports can be persisted and
	 * retrieved for later use. Otherwise, the report has to be run anew each time
	 * it is accessed.
	 * 
	 * @return True if the report is cacheable, false otherwise
	 */
	public boolean isCacheable();

	/**
	 * Whether to allow caching for this report or not.
	 * 
	 * @param cacheable True if the report can be retrieved/stored to a
	 * 	persistent store, false otherwise
	 */
	public void setCacheable(boolean cacheable);

	/**
	 * The last serial number processed.
	 * 
	 * @return The last serial number processed.
	 */
	public int getLastSer();
}
