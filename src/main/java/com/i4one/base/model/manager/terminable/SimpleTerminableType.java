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
import com.i4one.base.dao.terminable.TerminableRecordType;
import com.i4one.base.model.BaseRecordTypeDelegatorForwarder;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;

/**
 * A terminable implementation. This class uses the delegate of the incoming forward
 * in order to access the relevant start/end times.
 * 
 * @author Hamid Badiozamani
 * @param <U> The underlying terminable record type
 * @param <T> The incoming type to add terminable functionality to
 */
public class SimpleTerminableType<U extends TerminableRecordType, T extends RecordTypeDelegator<U>> extends BaseRecordTypeDelegatorForwarder<U, T> implements TerminableRecordTypeDelegator<U>
{
	public SimpleTerminableType(T forward)
	{
		super(forward);
	}
	
	@Override
	protected Errors validateInternal()
	{
		Errors retVal = super.validateInternal();

		if (getStartTimeSeconds() > getEndTimeSeconds() )
		{
			retVal.addError("startTimeSeconds", new ErrorMessage("startTimeSeconds", "msg.base.Terminable.timeMismatch", "The start time ($item.startTimeSeconds) cannot be after the end time ($item.endTimeSeconds).", new Object[]{"item", this}));
		}

		return retVal;
	}

	@Override
	public boolean isValidDuring(TerminableRecordTypeDelegator right)
	{
		//return getStartTimeSeconds() <= right.getStartTimeSeconds() && getEndTimeSeconds() >= right.getEndTimeSeconds();
		return isValidDuring(right.getStartTimeSeconds(), right.getEndTimeSeconds());
	}

	@Override
	public boolean isValidDuring(int startTimeSeconds, int endTimeSeconds)
	{
		return getStartTimeSeconds() <= startTimeSeconds && getEndTimeSeconds() >= endTimeSeconds;
	}

	@Override
	public boolean isValidAt(int seconds)
	{
		return isValidDuring(seconds, seconds);
	}

	@Override
	public boolean isLive(int asOf)
	{
		return isValidDuring(asOf, asOf);
	}

	@Override
	public boolean isPast(int asOf)
	{
		return getEndTimeSeconds() < asOf;
	}

	@Override
	public boolean isFuture(int asOf)
	{
		return getStartTimeSeconds() > asOf;
	}

	@Override
	public int getRemainingLifeSeconds()
	{
		int currTime = Utils.currentTimeSeconds();

		if ( currTime < getStartTimeSeconds() )
		{
			return getUsefulLifeSeconds();
		}
		else
		{
			int retVal = getEndTimeSeconds() - currTime;

			return retVal > 0 ? retVal : 0;
		}
	}

	@Override
	public int getUsefulLifeSeconds()
	{
		int retVal = getEndTimeSeconds() - getStartTimeSeconds();

		return retVal > 0 ? retVal : 0;
	}

	@Override
	public int getStartTimeSeconds()
	{
		return getForward().getDelegate().getStarttm();
	}

	@Override
	public void setStartTimeSeconds(int startTime)
	{
		getForward().getDelegate().setStarttm(startTime);
	}

	@Override
	public int getEndTimeSeconds()
	{
		return getForward().getDelegate().getEndtm();
	}

	@Override
	public void setEndTimeSeconds(int endTime)
	{
		// We go directly for the delegate since the forward's methods
		// reflect back to us and would cause an infinite loop
		//
		getForward().getDelegate().setEndtm(endTime);
	}
}