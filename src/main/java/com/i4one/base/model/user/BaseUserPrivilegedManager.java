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
package com.i4one.base.model.user;

import com.i4one.base.dao.UserRecordType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BasePrivilegedManager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.TopLevelReport;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for checking a user's access rights to manager methods. The typical implementation
 * allows users to access their own records but not others. If access is not granted, the super
 * class admin check is invoked instead.
 * 
 * @author Hamid Badiozamani
 */
public abstract class BaseUserPrivilegedManager<U extends UserRecordType, T extends RecordTypeDelegator<U>> extends BasePrivilegedManager<U, T> implements PrivilegedManager<U, T>
{
	protected User getUser()
	{
		return getRequestState().getUser();
	}

	/**
	 * Allows subclasses to override the default read check. If this method
	 * returns true, no further checks are performed and the user is allowed
	 * read access. Otherwise, the default behavior is assumed.
	 * 
	 * @param item The item being written to
	 * @param user The user currently logged in
	 * @param methodName The method of this feature being accessed
	 * 
	 * @return True if the user is allowed to read the item, false otherwise
	 */
	protected boolean checkReadInternal(T item, User user, String methodName)
	{
		return false;
	}

	protected void checkRead(T item, User user, String methodName)
	{
		if ( getUser().exists() )
		{
			if ( checkReadInternal(item, user, methodName) || getUser().equals(user) )
			{
				// Either checkReadInteral gave the ok or the users were equal 
				//
			}
			else
			{
				try
				{
					// Not equal users, check the admin just to make sure the admin
					// isn't logged in as a user
					//
					super.checkRead(getClient(item), methodName);
				}
				catch (Exception e)
				{
					throw new Errors(getInterfaceName() + "." + methodName, new ErrorMessage("msg." + getInterfaceName() + "." + methodName + ".denied", "User $user does not have read privileges for $item feature $featureName.", new Object[] { "user", getUser(), "item", item, "featureName", getFeatureName() }, e));
				}
			}
		}
		else
		{
			//throw new Errors(getInterfaceName() + "." + methodName, new ErrorMessage("msg." + getInterfaceName() + "." + methodName + ".denied", "You must be logged in.", new Object[] { "user", getUser(), "item", item, "featureName", getFeatureName() }));
			super.checkRead(getClient(item), methodName);
		}
	}

	/**
	 * Allows subclasses to override the default write check. If this method
	 * returns true, no further checks are performed and the user is allowed
	 * write access. Otherwise, the default behavior is assumed.
	 * 
	 * @param item The item being written to
	 * @param user The user currently logged in
	 * @param methodName The method of this feature being accessed
	 * 
	 * @return True if the user is allowed to write the item, false otherwise
	 */
	protected boolean checkWriteInternal(T item, User user, String methodName)
	{
		return false;
	}

	protected void checkWrite(T item, User user, String methodName)
	{
		User loggedIn = getUser();
		if ( loggedIn.exists() )
		{
			if ( checkWriteInternal(item, user, methodName) || loggedIn.equals(user) )
			{
				// Either checkWriteInteral gave the ok or the users were equal 
				//
			}
			else
			{
				try
				{
					// Not equal users, check the admin just to make sure the admin
					// isn't logged in
					//
					super.checkWrite(getClient(item), methodName);
				}
				catch (Exception e)
				{
					throw new Errors(getInterfaceName() + "." + methodName, new ErrorMessage("msg." + getInterfaceName() + "." + methodName + ".denied", "User $user does not have write privileges for $item feature $featureName.", new Object[] { "user", loggedIn, "item", item, "featureName", getFeatureName() }, e));
				}
			}
			// else we let the user through w/o checking the admin since they are their own record's owner
		}
		else
		{
			//throw new Errors(getInterfaceName() + "." + methodName, new ErrorMessage("msg." + getInterfaceName() + "." + methodName + ".denied", "You must be logged in.", new Object[] { "user", getUser(), "item", item, "featureName", getFeatureName() }));
			getLogger().trace("No user (" +  loggedIn + ") logged in for state " + System.identityHashCode(getRequestState()) + ", checking admin level write privilege");

			super.checkWrite(getClient(item), methodName);
		}
	}

	@Override
	public void checkRead(SingleClient client, String methodName)
	{
		throw new UnsupportedOperationException("Not supported, please use checkRead(item, user, method).");
	}

	@Override
	public void checkWrite(SingleClient client, String methodName)
	{
		throw new UnsupportedOperationException("Not supported, please use checkWrite(item, user, method).");
	}

	protected void checkAdminRead(SingleClient singleClient, String methodName)
	{
		// Needed for Settings objects that aren't the same as the item being managed
		// but are rather the manager's settings
		//
		super.checkRead(singleClient, methodName);
	}

	protected void checkAdminWrite(SingleClient singleClient, String methodName)
	{
		// Needed for Settings objects that aren't the same as the item being managed
		// but are rather the manager's settings
		//
		super.checkWrite(singleClient, methodName);
	}

	@Override
	public T getById(int id)
	{
		T retVal = super.getById(id);
		if ( retVal.exists() )
		{
			checkRead(retVal, getUser(retVal), "getById");
		}

		return retVal;
	}

	@Override
	public void loadById(T item, int id)
	{
		super.loadById(item, id);
		if ( item.exists())
		{
			checkRead(item, getUser(item), "loadById");
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		checkWrite(item, getUser(item), "create");
		return getImplementationManager().create(item);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		checkWrite(item, getUser(item), "update");
		return getImplementationManager().update(item);
	}

	@Transactional(readOnly = false)
	@Override
	public T remove(T item)
	{
		checkWrite(item, getUser(item), "remove");
		return getImplementationManager().remove(item);
	}

	@Override
	public TopLevelReport getReport(T item, TopLevelReport report, PaginationFilter pagination)
	{
		checkRead(item, getUser(item), "report");
		return getImplementationManager().getReport(item, report, pagination);
	}

	public abstract User getUser(T item);
}