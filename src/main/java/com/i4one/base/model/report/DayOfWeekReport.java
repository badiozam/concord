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
 * @author Hamid Badiozamani
 */
@JsonTypeName("dow")
public class DayOfWeekReport extends BaseCalendarDemo implements Demo
{
	private int dayOfWeek;

	public DayOfWeekReport()
	{
		super();

		initInternal(0);
	}

	public DayOfWeekReport(int dayOfWeek, Supplier<Calendar> calSupplier)
	{
		super(calSupplier);

		initInternal(dayOfWeek);
	}

	private void initInternal(int dayOfWeek)
	{
		if ( dayOfWeek < Calendar.SUNDAY && dayOfWeek > Calendar.SATURDAY )
		{
			throw new IllegalArgumentException("Invalid day of week " + dayOfWeek + " must be between Sun(" + Calendar.SUNDAY + ") and Sat(" + Calendar.SATURDAY + ")");
		}

		this.dayOfWeek = dayOfWeek;

		initTitle();
	}

	private void initTitle()
	{
		newDisplayName(String.valueOf(dayOfWeek));
		setTitle(getDisplayName());
	}

	@Override
	public boolean isEligible(ReportUsageType item)
	{
		Calendar cal = (Calendar) getCalendar().clone();
		cal.setTimeInMillis( item.getTimeStampSeconds() * 1000l);

		//boolean retVal = cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
		//if ( !retVal ) { getLogger().debug("Item " + item.getDelegate().getSer() + ":" + item.getUser().getUsername() + " with timestamp " + cal.getTimeInMillis() + " does not fall into " + dayOfWeek + " it falls under " + cal.get(Calendar.DAY_OF_WEEK)); }
		
		return cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
	}

	@JsonProperty("_d")
	public int getDayOfWeek()
	{
		return dayOfWeek;
	}

	@JsonProperty("_d")
	public void setDayOfWeek(int dayOfWeek)
	{
		initInternal(dayOfWeek);
	}

	@Override
	public DayOfWeekReport cloneReport()
	{
		return new DayOfWeekReport(dayOfWeek, getCalendarSupplier());
	}

	@Override
	protected boolean isExclusiveToInternal(ReportType right)
	{
		if ( right instanceof DayOfWeekReport )
		{
			// If we have a different hour of day, then none of our data will be 
			// in the incoming's set
			//
			return getDayOfWeek() != ((DayOfWeekReport)right).getDayOfWeek();
		}
		else
		{
			return false;
		}
	}
}
