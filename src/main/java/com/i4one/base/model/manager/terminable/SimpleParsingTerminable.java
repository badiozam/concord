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
import com.i4one.base.model.ClientType;
import com.i4one.base.model.client.SingleClient;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Supplier;

/**
 * @author Hamid Badiozamani
 */
public class SimpleParsingTerminable extends SimpleTerminable implements ParsingTerminable
{
	private Locale parseLocale;
	private TimeZone timeZone;
	private boolean dateOnly;

	private final Supplier<SingleClient> clientSupplier;
	private final ClientType parent;

	public SimpleParsingTerminable(ClientType parent)
	{
		this.parent = parent;
		clientSupplier = null;

		parseLocale = null;
		timeZone = null;
		dateOnly = false;
	}

	public SimpleParsingTerminable(Supplier<SingleClient> clientSupplier)
	{
		parent = null;
		this.clientSupplier = clientSupplier;

		parseLocale = null;
		timeZone = null;
		dateOnly = false;
	}

	public SimpleParsingTerminable(Locale locale, TimeZone timeZone)
	{
		parent = null;
		clientSupplier = null;

		this.parseLocale = locale;
		this.timeZone = timeZone;
		dateOnly = false;
	}

	@Override
	public Date getStartTime()
	{
		return Utils.toDate(getStartTimeSeconds());
	}

	@Override
	public String getStartTimeString()
	{
		return toDateString(getStartTimeSeconds());
	}

	@Override
	public void setStartTimeString(String startTimeStr) throws ParseException
	{
		int seconds = parseToSeconds(startTimeStr);
		setStartTimeSeconds(seconds);
	}

	@Override
	public void setStartTimeSeconds(int seconds)
	{
		if ( isDateOnly() && seconds > 0 )
		{
			Calendar cal = Calendar.getInstance(getTimeZone(), getParseLocale());
			cal.setTimeInMillis(seconds * 1000l);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);

			super.setStartTimeSeconds( (int)(cal.getTimeInMillis() / 1000l));
		}
		else
		{
			super.setStartTimeSeconds(seconds);
		}
	}

	@Override
	public String getEndTimeString()
	{
		return toDateString(getEndTimeSeconds());
	}

	@Override
	public Date getEndTime()
	{
		return Utils.toDate(getEndTimeSeconds());
	}

	@Override
	public void setEndTimeString(String endTimeStr) throws ParseException
	{
		setEndTimeSeconds(parseToSeconds(endTimeStr));
	}

	@Override
	public void setEndTimeSeconds(int seconds)
	{
		if ( isDateOnly() && seconds > 0 )
		{
			Calendar cal = Calendar.getInstance(getTimeZone(), getParseLocale());
			cal.setTimeInMillis(seconds * 1000l);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);

			super.setEndTimeSeconds( (int)(cal.getTimeInMillis() / 1000l));
		}
		else
		{
			super.setEndTimeSeconds(seconds);
		}
	}

	@Override
	public Locale getParseLocale()
	{
		if ( parseLocale == null )
		{
			if ( parent == null )
			{
				return clientSupplier.get().getLocale();
			}
			else
			{
				return parent.getClient().getLocale();
			}
		}
		else
		{
			return parseLocale;
		}
	}

	@Override
	public void setParseLocale(Locale parseLocale)
	{
		this.parseLocale = parseLocale;
	}

	public TimeZone getTimeZone()
	{
		if ( timeZone == null )
		{
			if ( parent == null )
			{
				return clientSupplier.get().getTimeZone();
			}
			else
			{
				return parent.getClient().getTimeZone();
			}
		}
		else
		{
			return timeZone;
		}
	}

	public void setTimeZone(TimeZone timeZone)
	{
		this.timeZone = timeZone;
	}

	public String toDateString(int seconds)
	{
		getLogger().trace("Parsing {} using time zone {}", seconds, getTimeZone());
		return getDateFormat(getParseLocale(), getTimeZone()).format(Utils.toDate(seconds));
	}

	public static String toDateString(int seconds, Locale locale, TimeZone timeZone)
	{
		return getDateFormat(locale, timeZone).format(Utils.toDate(seconds));
	}

	public DateFormat getDateFormat()
	{
		return getDateFormat(getParseLocale(), getTimeZone());
	}

	public static DateFormat getDateFormat(Locale locale, TimeZone timeZone)
	{
		DateFormat retVal = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		retVal.setTimeZone(timeZone);

		return retVal;
	}

	public int parseToSeconds(String timeStr) throws ParseException
	{
		return parseToSeconds(timeStr, getParseLocale(), getTimeZone());
	}

	public static int parseToSeconds(String timeStr, Locale locale, TimeZone timeZone) throws ParseException
	{
		DateFormat formatter = getDateFormat(locale, timeZone);

		Date parsedDate = formatter.parse(timeStr);
		return (int)(parsedDate.getTime() / 1000L);
	}

	public boolean isDateOnly()
	{
		return dateOnly;
	}

	public void setDateOnly(boolean dateOnly)
	{
		this.dateOnly = dateOnly;
	}

}