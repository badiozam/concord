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
import java.util.Calendar;
import java.util.function.Supplier;

/**
 * Hour of Day break down report.
 * 
 * @author Hamid Badiozamani
 */
@JsonTypeName("hod")
public class HourOfDayReport extends BaseCalendarDemo implements Demo
{
	private int hourOfDay;

	public HourOfDayReport()
	{
		super();

		initInternal(0);
	}

	public HourOfDayReport(int hourOfDay, Supplier<Calendar> calSupplier)
	{
		super(calSupplier);

		initInternal(hourOfDay);
	}

	private void initInternal(int hourOfDay)
	{
		if ( hourOfDay < 0 || hourOfDay > 23 )
		{
			throw new IllegalArgumentException("Invalid hour of day " + hourOfDay);
		}

		this.hourOfDay = hourOfDay;

		initTitle();
	}

	private void initTitle()
	{
		newDisplayName(String.valueOf(hourOfDay));
		setTitle(getDisplayName());
	}

	@Override
	public boolean isEligible(ReportUsageType item)
	{
		Calendar cal = (Calendar) getCalendar().clone();
		cal.setTimeInMillis( item.getTimeStampSeconds() * 1000l);
		
		return cal.get(Calendar.HOUR_OF_DAY) == hourOfDay;
	}

	@JsonProperty("_h")
	public int getHourOfDay()
	{
		return hourOfDay;
	}

	@JsonProperty("_h")
	public void setHourOfDay(int hourOfDay)
	{
		initInternal(hourOfDay);
	}

	@Override
	public HourOfDayReport cloneReport()
	{
		return new HourOfDayReport(hourOfDay, getCalendarSupplier());
	}

	@Override
	protected boolean isExclusiveToInternal(ReportType right)
	{
		if ( right instanceof HourOfDayReport )
		{
			// If we have a different hour of day, then none of our data will be 
			// in the incoming's set
			//
			return getHourOfDay() != ((HourOfDayReport)right).getHourOfDay();
		}
		else
		{
			return false;
		}
	}
}
