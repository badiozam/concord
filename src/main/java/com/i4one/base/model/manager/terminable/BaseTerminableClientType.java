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
package com.i4one.base.model.manager.terminable;

import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SingleClient;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Any object that is valid during a certain period of time
 *
 * @author Hamid Badiozamani
 * 
 * @param <U> The underlying terminable client record type
 */
public abstract class BaseTerminableClientType<U extends TerminableClientRecordType> extends BaseSingleClientType<U> implements TerminableClientType<U>
{
	private final transient SimpleTerminableClientType<U, TerminableClientType<U>> terminableSingleClientType;

	public BaseTerminableClientType(U delegate)
	{
		super(delegate);

		terminableSingleClientType = new SimpleTerminableClientType(this);
	}

	@Override
	protected void init()
	{
		super.init();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal.merge(terminableSingleClientType.validate());
		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		terminableSingleClientType.setOverrides();
	}
	
	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		terminableSingleClientType.actualizeRelations();
	}

	@Override
	public boolean isValidDuring(TerminableRecordTypeDelegator another)
	{
		return terminableSingleClientType.isValidDuring(another);
	}

	@Override
	public boolean isValidDuring(int startTimeSeconds, int endTimeSeconds)
	{
		return terminableSingleClientType.isValidDuring(startTimeSeconds, endTimeSeconds);
	}

	@Override
	public boolean isValidAt(int seconds)
	{
		return terminableSingleClientType.isValidAt(seconds);
	}

	@Override
	public boolean isLive(int asOf)
	{
		return terminableSingleClientType.isLive(asOf);
	}

	@Override
	public boolean isPast(int asOf)
	{
		return terminableSingleClientType.isPast(asOf);
	}

	@Override
	public boolean isFuture(int asOf)
	{
		return terminableSingleClientType.isFuture(asOf);
	}

	@Override
	public int getRemainingLifeSeconds()
	{
		return terminableSingleClientType.getRemainingLifeSeconds();
	}

	@Override
	public int getUsefulLifeSeconds()
	{
		return terminableSingleClientType.getUsefulLifeSeconds();
	}

	@Override
	public Date getStartTime()
	{
		return terminableSingleClientType.getStartTime();
	}

	@Override
	public String getStartTimeString()
	{
		return terminableSingleClientType.getStartTimeString();
	}

	@Override
	public void setStartTimeString(String startTimeStr) throws ParseException
	{
		terminableSingleClientType.setStartTimeString(startTimeStr);
	}

	@Override
	public int getStartTimeSeconds()
	{
		return terminableSingleClientType.getStartTimeSeconds();
	}

	@Override
	public void setStartTimeSeconds(int startTime)
	{
		terminableSingleClientType.setStartTimeSeconds(startTime);
	}

	@Override
	public Date getEndTime()
	{
		return terminableSingleClientType.getEndTime();
	}

	@Override
	public String getEndTimeString()
	{
		return terminableSingleClientType.getEndTimeString();
	}

	@Override
	public void setEndTimeString(String endTimeStr) throws ParseException
	{
		terminableSingleClientType.setEndTimeString(endTimeStr);
	}

	@Override
	public int getEndTimeSeconds()
	{
		return terminableSingleClientType.getEndTimeSeconds();
	}

	@Override
	public void setEndTimeSeconds(int endTime)
	{
		terminableSingleClientType.setEndTimeSeconds(endTime);
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@Override
	public Locale getParseLocale()
	{
		return terminableSingleClientType.getParseLocale();
	}

	@Override
	public void setParseLocale(Locale locale)
	{
		terminableSingleClientType.setParseLocale(locale);
	}
}
