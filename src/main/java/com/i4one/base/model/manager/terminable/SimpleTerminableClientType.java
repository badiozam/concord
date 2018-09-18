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

import com.i4one.base.core.Utils;
import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.client.Client;
import com.i4one.base.model.client.SingleClient;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * @author Hamid Badiozamani
 * 
 * @param <U> The underlying terminable client record type
 * @param <T> The incoming client type to add terminable functionality to
 */
public class SimpleTerminableClientType<U extends TerminableClientRecordType, T extends SingleClientType<U>> extends SimpleTerminableType<U,T> implements TerminableClientType<U>
{
	private final transient SimpleParsingTerminable parsingTerminable;

	public SimpleTerminableClientType(T forward)
	{
		super(forward);

		parsingTerminable = new SimpleParsingTerminable(this);
	}

	@Override
	public Date getStartTime()
	{
		return Utils.toDate(getStartTimeSeconds());
	}

	@Override
	public String getStartTimeString()
	{
		return parsingTerminable.toDateString(getStartTimeSeconds());
	}

	@Override
	public void setStartTimeString(String startTimeStr) throws ParseException
	{
		setStartTimeSeconds(parsingTerminable.parseToSeconds(startTimeStr));
	}

	@Override
	public String getEndTimeString()
	{
		return parsingTerminable.toDateString(getEndTimeSeconds());
	}

	@Override
	public Date getEndTime()
	{
		return Utils.toDate(getEndTimeSeconds());
	}

	@Override
	public void setEndTimeString(String endTimeStr) throws ParseException
	{
		setEndTimeSeconds(parsingTerminable.parseToSeconds(endTimeStr));
	}

	@Override
	public SingleClient getClient()
	{
		return getForward().getClient();
	}

	@Override
	public void setClient(SingleClient client)
	{
		getForward().setClient(client);
	}

	@Override
	public boolean belongsTo(Client client)
	{
		return getForward().belongsTo(client);
	}

	@Override
	public Locale getParseLocale()
	{
		return parsingTerminable.getParseLocale();
		/*
		if ( locale != null )
		{
			return locale;
		}
		else
		{
			return getForward().getClient().getLocale();
		}
		*/
	}

	@Override
	public void setParseLocale(Locale locale)
	{
		parsingTerminable.setParseLocale(locale);
		//this.locale = locale;
	}
}