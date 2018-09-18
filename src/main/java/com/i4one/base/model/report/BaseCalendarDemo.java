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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Calendar;
import java.util.function.Supplier;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseCalendarDemo extends BaseDemo implements Demo
{
	private transient Supplier<Calendar> calendarSupplier;

	public BaseCalendarDemo()
	{
		super();

		initInternal(() -> { return Calendar.getInstance(); });
	}

	public BaseCalendarDemo(Supplier<Calendar> calSupplier)
	{
		super();

		initInternal(calSupplier);
	}

	private void initInternal(Supplier<Calendar> calSupplier)
	{
		this.calendarSupplier = calSupplier;
	}

	@JsonIgnore
	@Override
	public String getDisplayName()
	{
		// These are overridden to ignore JSON serialization for a
		// slight space/time performance boost
		//
		return super.getDisplayName();
	}

	@JsonIgnore
	@Override
	public void setDisplayName(String displayName)
	{
		super.setDisplayName(displayName);
	}

	@JsonIgnore
	public Calendar getCalendar()
	{
		return calendarSupplier.get();
	}

	@JsonIgnore
	public Supplier<Calendar> getCalendarSupplier()
	{
		return calendarSupplier;
	}

	@JsonIgnore
	public void setCalendarSupplier(Supplier<Calendar> calendarSupplier)
	{
		if ( isProcessingStarted() )
		{
			throw new IllegalArgumentException("Can't set calendar supplier after processing has started.");
		}
		else
		{
			this.calendarSupplier = calendarSupplier;
		}
	}

}
