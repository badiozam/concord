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
import com.i4one.base.model.RecordTypeDelegator;
import java.util.Calendar;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseActivityReport<T extends RecordTypeDelegator<?>> extends BaseTopLevelReport implements ActivityReport<T>
{
	private transient T item;

	/*
	public BaseActivityReport()
	{
		// We need a default constructor because we need to serialize/deserialize
		// these. This has ramifications as the item can now be a null reference
		//
		super();

		item = null;
		initInternal();
	}
	*/

	public BaseActivityReport(T item, Calendar cal)
	{
		super(cal);

		this.item = item;
		initInternal();
	}

	private void initInternal()
	{
		setStartTimeSeconds(0);

		Calendar calCopy = (Calendar) getEndCalendar().clone();
		calCopy.setTimeInMillis(System.currentTimeMillis());
		calCopy.set(Calendar.HOUR, 23);
		calCopy.set(Calendar.MINUTE, 59);
		calCopy.set(Calendar.SECOND, 59);

		int endOfDay = (int)(calCopy.getTimeInMillis() / 1000);

		setEndTimeSeconds(endOfDay);

		setCacheable(false);
		initTitle();
	}

	private void initTitle()
	{
		if ( item != null )
		{
			setTitle(super.getTitle() + ":" + getItem().uniqueKey());
		}
		else
		{
			setTitle(super.getTitle());
		}

	}

	@JsonIgnore
	@Override
	public T getItem()
	{
		if ( item != null )
		{
			item.loadedVersion();
		}

		return item;
	}

	@JsonIgnore
	@Override
	public void setItem(T item)
	{
		this.item = item;

		if (item != null )
		{
			item.loadedVersion();
		}
		initTitle();
	}
}
