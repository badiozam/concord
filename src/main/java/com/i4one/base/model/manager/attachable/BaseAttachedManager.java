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
package com.i4one.base.model.manager.attachable;

import com.i4one.base.dao.RecordType;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.BaseDelegatingManager;
import com.i4one.base.model.manager.DelegatingManager;
import com.i4one.base.web.RequestState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseAttachedManager<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseDelegatingManager<U, T> implements DelegatingManager<U,T>,AttachedManager<U,T>
{
	private RequestState requestState;

	@Override
	public RecordTypeDelegator<?> getAttachee(T item)
	{
		return emptyInstance();
	}

	protected abstract void processAttached(ReturnType<T> processedItem, String methodName);

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		ReturnType<T> retVal = getImplementationManager().create(item);

		try
		{
			processAttached(retVal, "create");
		}
		catch (Errors errors)
		{
			// The underlying implementation may assign a serial number
			// if it succeeds, this method would of course roll that back
			// but this method's caller would see a non-existent serial
			// number. As such, we force the serial number back to 0 on
			// failure.
			//
			item.setSer(0);
			throw errors;
		}

		return retVal;
	}


	@Transactional(readOnly = false)
        @Override
	public ReturnType<T> update(T item)
	{
		ReturnType<T> retVal = getImplementationManager().update(item);

		processAttached(retVal, "update");

		return retVal;
	}

	@Override
	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	@Override
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}
}
