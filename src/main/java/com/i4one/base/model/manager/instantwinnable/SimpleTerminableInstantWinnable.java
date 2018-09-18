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
package com.i4one.base.model.manager.instantwinnable;

import com.i4one.base.model.manager.terminable.TerminableClientType;
import com.i4one.base.model.manager.terminable.TerminableRecordTypeDelegator;
import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.instantwin.TerminableExclusiveInstantWin;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * @author Hamid Badiozamani
 */
public class SimpleTerminableInstantWinnable<U extends TerminableClientRecordType, T extends TerminableClientType<U>> extends SimpleInstantWinnable<U,T> implements TerminableInstantWinnableClientType<U, T>
{
	public SimpleTerminableInstantWinnable(T forward)
	{
		super(forward);
	}

	@Override
	protected ExclusiveInstantWin<T> newExclusiveInstantWin()
	{
		return new TerminableExclusiveInstantWin<>(getForward());
	}

	@Override
	protected Errors validateInstantWin(InstantWin instantWin)
	{
		Errors retVal = super.validateInstantWin(instantWin);

		// Make sure the trigger's clients belong to this item's
		//
		if ( !this.isValidDuring(instantWin) )
		{
			retVal.addError(new ErrorMessage("ser", "msg.base.TerminableInstantWinning.exclusiveInstantWins.timeMismatch", "The start/end times do not fall within the same start/end times as the instant win $instantWin", new Object[]{"item", this, "instantWin", instantWin}));
		}

		return retVal;

	}

	@Override
	public void setOverridesInternal()
	{
		super.setOverridesInternal();
	}

	@Override
	public int getEndTimeSeconds()
	{
		return getForward().getEndTimeSeconds();
	}

	@Override
	public int getRemainingLifeSeconds()
	{
		return getForward().getRemainingLifeSeconds();
	}

	@Override
	public int getStartTimeSeconds()
	{
		return getForward().getStartTimeSeconds();
	}

	@Override
	public int getUsefulLifeSeconds()
	{
		return getForward().getUsefulLifeSeconds();
	}

	@Override
	public boolean isValidDuring(TerminableRecordTypeDelegator right)
	{
		return getForward().isValidDuring(right);
	}

	@Override
	public boolean isValidDuring(int startTimeSeconds, int endTimeSeconds)
	{
		return getForward().isValidDuring(startTimeSeconds, endTimeSeconds);
	}

	@Override
	public boolean isValidAt(int seconds)
	{
		return getForward().isValidAt(seconds);
	}

	@Override
	public boolean isLive(int asOf)
	{
		return getForward().isLive(asOf);
	}

	@Override
	public boolean isPast(int asOf)
	{
		return getForward().isPast(asOf);
	}

	@Override
	public boolean isFuture(int asOf)
	{
		return getForward().isFuture(asOf);
	}

	@Override
	public void setEndTimeSeconds(int endTime)
	{
		getForward().setEndTimeSeconds(endTime);
	}

	@Override
	public void setStartTimeSeconds(int startTime)
	{
		getForward().setStartTimeSeconds(startTime);
	}

	@Override
	public String getStartTimeString()
	{
		return getForward().getStartTimeString();
	}

	@Override
	public Date getStartTime()
	{
		return getForward().getStartTime();
	}

	@Override
	public void setStartTimeString(String startTimeStr) throws ParseException
	{
		getForward().setStartTimeString(startTimeStr);
	}

	@Override
	public String getEndTimeString()
	{
		return getForward().getEndTimeString();
	}

	@Override
	public Date getEndTime()
	{
		return getForward().getEndTime();
	}

	@Override
	public void setEndTimeString(String endTimeStr) throws ParseException
	{
		getForward().setEndTimeString(endTimeStr);
	}

	@Override
	public Locale getParseLocale()
	{
		return getForward().getParseLocale();
	}

	@Override
	public void setParseLocale(Locale locale)
	{
		getForward().setParseLocale(locale);
	}
}