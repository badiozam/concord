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
package com.i4one.base.model.manager;

import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.SingleClientType;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseClientTypePrivilegedManager<U extends ClientRecordType, T extends SingleClientType<U>> extends BasePrivilegedManager<U,T> implements PrivilegedManager<U, T>
{
	@Override
	public T getById(int ser)
	{
		T retVal = super.getById(ser);

		if ( retVal.belongsTo(getRequestState().getSingleClient()))
		{
			return retVal;
		}
		else
		{
			return emptyInstance();
		}
	}

	@Override
	public void loadById(T item, int ser)
	{
		super.loadById(item, ser);

		if ( !item.belongsTo(getRequestState().getSingleClient()))
		{
			item.setDelegate(emptyInstance().getDelegate());
		}
	}
}
