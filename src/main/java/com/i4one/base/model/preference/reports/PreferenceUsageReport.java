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
package com.i4one.base.model.preference.reports;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.i4one.base.dao.UsageRecordType;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.BaseActivityReport;
import com.i4one.base.model.report.ReportUsageType;
import com.i4one.base.model.preference.Preference;
import com.i4one.base.model.preference.UserPreferenceRecord;
import java.util.Calendar;

/**
 * @author Hamid Badiozamani
 */
@JsonTypeName("b.pu")
public class PreferenceUsageReport extends BaseActivityReport<Preference> implements ActivityReport<Preference>
{
	public PreferenceUsageReport(Preference item, Calendar cal)
	{
		super(item, cal);
	}

	@Override
	public String getSchemaName()
	{
		return "base";
	}

	@Override
	public ReportUsageType getUsage(UsageRecordType usageRecord)
	{
		if ( usageRecord instanceof UserPreferenceRecord )
		{
			return new PreferenceUsage((UserPreferenceRecord) usageRecord);
		}
		else
		{
			throw new IllegalArgumentException(usageRecord + " is not of type UserPreferenceRecord");
		}
	}

	@Override
	public PreferenceUsageReport cloneReport()
	{
		return new PreferenceUsageReport(getItem(), getEndCalendar());
	}
}
