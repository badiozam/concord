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
package com.i4one.base.model.manager.activity;

import com.i4one.base.dao.activity.ActivityRecordType;
import com.i4one.base.dao.activity.ActivityRecordTypeDao;
import com.i4one.base.model.ActivityType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.BaseUserPrivilegedManager;
import com.i4one.base.model.user.User;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseActivityPrivilegedManager<U extends ActivityRecordType, T extends ActivityType<U, V>, V extends RecordTypeDelegator<?>> extends BaseUserPrivilegedManager<U, T> implements PrivilegedManager<U, T>, ActivityManager<U, T, V>
{
	@Override
	public abstract ActivityManager<U, T, V> getImplementationManager();

	@Override
	public List<ReturnType<T>> revert(V parent, PaginationFilter pagination)
	{
		checkAdminWrite(getRequestState().getSingleClient(), "revert");
		return getImplementationManager().revert(parent, pagination);
	}

	@Override
	public T getActivity(V parent, User user)
	{
		T retVal = getImplementationManager().getActivity(parent, user);

		// Make sure the incoming user had access rights to look up this response
		//
		checkRead(retVal, getUser(retVal), "get");

		return retVal;
	}

	@Override
	public boolean hasActivity(V parent)
	{
		checkAdminRead(getRequestState().getSingleClient(), "hasResponses");
		return getImplementationManager().hasActivity(parent);
	}

	@Override
	public Set<T> getAllActivity(V parent, PaginationFilter pagination)
	{
		checkAdminRead(getRequestState().getSingleClient(), "hasResponses");
		return getImplementationManager().getAllActivity(parent, pagination);
	}

	@Override
	public ActivityReport getReport(T item, TopLevelReport report, PaginationFilter pagination)
	{
		checkAdminRead(getRequestState().getSingleClient(), "hasResponses");
		return getImplementationManager().getReport(item, report, pagination);
	}

	@Override
	public ActivityRecordTypeDao getDao()
	{
		return getImplementationManager().getDao();
	}
}
