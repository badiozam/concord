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
package com.i4one.base.model.report.classifier.classifications;

import com.i4one.base.model.TemporalType;
import com.i4one.base.model.report.classifier.BaseSubclassification;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.Subclassification;
import java.util.Calendar;

/**
 * @author Hamid Badiozamani
 */
public class DayOfWeekSubclassification<T extends TemporalType> extends BaseSubclassification<T> implements Subclassification<T>
{
	private final Calendar calendar;
	private final int dayOfWeek;

	public DayOfWeekSubclassification(Classification<T, DayOfWeekSubclassification<T>> parent, Calendar cal, int dayOfWeek)
	{
		super(parent, String.valueOf(dayOfWeek));

		this.calendar = cal;

		if ( dayOfWeek < Calendar.SUNDAY && dayOfWeek > Calendar.SATURDAY )
		{
			throw new IllegalArgumentException("Invalid day of week " + dayOfWeek + " must be between Sun(" + Calendar.SUNDAY + ") and Sat(" + Calendar.SATURDAY + ")");
		}
		this.dayOfWeek = dayOfWeek;
	}

	@Override
	public boolean belongs(TemporalType item)
	{
		Calendar cal = (Calendar) getCalendar().clone();
		cal.setTimeInMillis( item.getTimeStampSeconds() * 1000l);

		return cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
	}

	public Calendar getCalendar()
	{
		return calendar;
	}

}
