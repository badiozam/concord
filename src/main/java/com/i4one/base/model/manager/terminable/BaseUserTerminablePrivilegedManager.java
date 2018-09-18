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


import com.i4one.base.dao.terminable.UserTerminableClientRecordType;
import com.i4one.base.dao.terminable.TerminableClientRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.user.BaseUserPrivilegedManager;
import com.i4one.base.model.user.User;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseUserTerminablePrivilegedManager<U extends UserTerminableClientRecordType, T extends UserTerminableClientType<U>> extends BaseUserPrivilegedManager<U,T> implements TerminablePrivilegedManager<U,T>
{

	@Override
	public abstract TerminableManager<U,T> getImplementationManager();

	@Override
	public ReturnType<T> clone(T item)
	{
		checkWrite(item, getUser(item), "clone");
		return getImplementationManager().clone(item);
	}

	@Override
	public Set<T> getLive(TerminablePagination pagination)
	{
		Set<T> retVal = getImplementationManager().getLive(pagination);

		for ( T item : retVal )
		{
			// Items that are intended for a given user should be checked,
			// public messages don't need to be.
			//
			User user = getUser(item);
			if ( user.exists() )
			{
				checkRead(item, user, "getLive");
			}
		}

		return retVal;
	}

	@Override
	public Set<T> getByRange(TerminablePagination pagination)
	{
		Set<T> retVal = getImplementationManager().getByRange(pagination);

		for ( T item : retVal )
		{
			User user = getUser(item);
			if ( user.exists() )
			{
				this.checkRead(item, getUser(item), "getByRange");
			}
		}

		return retVal;
	}

	@Override
	public TerminableClientRecordTypeDao<U> getDao()
	{
		return getImplementationManager().getDao();
	}
}
