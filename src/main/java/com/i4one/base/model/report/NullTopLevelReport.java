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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.i4one.base.dao.UsageRecordType;
import java.util.Calendar;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
@JsonTypeName("ntl")
public class NullTopLevelReport extends BaseTopLevelReport implements TopLevelReport
{
	public NullTopLevelReport()
	{
		super();

		initInternal();
	}

	public NullTopLevelReport(Calendar cal)
	{
		super(cal);

		initInternal();
	}

	private void initInternal()
	{
		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(0);

		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(1000);

		setStartCalendar(startCal);
		setEndCalendar(endCal);
	}

	@JsonProperty("s")
	@Override
	public Set<AggregateReportType> getJSONSubReports()
	{
		// We don't bypass the deeper levels for this report because we
		// want to load templates off of it
		//
		return getSubReports();
	}

	@Override
	public ReportUsageType getUsage(UsageRecordType usageRecord)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public NullTopLevelReport cloneReport()
	{
		return new NullTopLevelReport(getEndCalendar());
	}
}
