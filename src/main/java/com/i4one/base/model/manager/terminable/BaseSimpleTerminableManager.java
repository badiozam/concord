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

import com.i4one.base.dao.Dao;
import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.dao.terminable.TerminableClientRecordTypeDao;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseSimpleClientTypeManager;
import com.i4one.base.web.RequestState;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseSimpleTerminableManager<U extends TerminableClientRecordType, T extends TerminableClientType<U>> extends BaseSimpleClientTypeManager<U, T> implements TerminableManager<U,T>
{
	private RequestState requestState;

	@Override
	public Set<T> getLive(TerminablePagination pagination)
	{
		List<U> delegates = getDao().getLive(pagination);
		return convertDelegates(delegates);
	}

	@Override
	public Set<T> getByRange(TerminablePagination pagination)
	{
		List<U> delegates = getDao().getByRange(pagination);
		return convertDelegates(delegates);
	}

	@Override
	protected T cloneInternal(T item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		T retVal = super.cloneInternal(item);

		long currTimeMillis = getRequestState().getRequest().getTimeInMillis();
		SingleClient client = getRequestState().getSingleClient();

		Calendar startCal = client.getCalendar();
		startCal.setTimeInMillis(currTimeMillis);

		// The start of the day today + a year from now
		//
		startCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR) + 1);
		startCal.set(Calendar.HOUR, startCal.getActualMinimum(Calendar.HOUR));
		startCal.set(Calendar.MINUTE, startCal.getActualMinimum(Calendar.MINUTE));
		startCal.set(Calendar.SECOND, startCal.getActualMinimum(Calendar.SECOND));
		startCal.set(Calendar.AM_PM, startCal.getActualMinimum(Calendar.AM_PM));
		int startTime = (int)(startCal.getTimeInMillis() / 1000);

		// End time is calculated off of the duration the prize is live
		//
		int endTime = startTime + (retVal.getEndTimeSeconds() - retVal.getStartTimeSeconds());

		retVal.setStartTimeSeconds(startTime);
		retVal.setEndTimeSeconds(endTime);

		return retVal;
	}

	@Override
	public TerminableClientRecordTypeDao<U> getDao()
	{
		Dao<U> baseDao = super.getDao();
		if (baseDao instanceof TerminableClientRecordTypeDao)
		{
			TerminableClientRecordTypeDao<U> terminableDao = (TerminableClientRecordTypeDao<U>) baseDao;
			return terminableDao;
		}
		else
		{
			return null;
		}
	}

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}
}