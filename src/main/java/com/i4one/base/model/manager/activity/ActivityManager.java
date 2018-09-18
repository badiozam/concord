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
import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 * @param <V>
 */
public interface ActivityManager<U extends ActivityRecordType, T extends ActivityType<U, V>, V extends RecordTypeDelegator<?>> extends PaginableManager<U,T>
{
	@Override
	public ActivityReport getReport(T item, TopLevelReport report, PaginationFilter pagination);

	/**
	 * Revert all activity for the given parent. Typically, each activity record
	 * associated with the parent is removed.
	 * 
	 * @param parent The parent for which to revert the activity.
	 * @param pagination The sorting and limiting pagination filter
	 * 
	 * @return A (potentially empty) list of former activity removal statuses.
	 */
	public List<ReturnType<T>> revert(V parent, PaginationFilter pagination);

	/**
	 * Retrieves all activity for a given parent.
	 * 
	 * @param parent The parent for which to retrieve all activity.
	 * @param pagination The pagination filter for sorting.
	 * 
	 * @return A (potentially empty) set of all activity for the given parent
	 */
	public Set<T> getAllActivity(V parent, PaginationFilter pagination);

	/**
	 * Retrieves the activity response for the given parent and user. If the user
	 * has not responded to the given parent, an empty object is returned. If
	 * there are multiple responses, only the most recent response is returned.
	 * 
	 * @param parent The parent 
	 * @param user The user
	 * 
	 * @return The (potentially empty) activity object.
	 */
	public T getActivity(V parent, User user);

	/**
	 * Determines whether a given parent has any activity associated
	 * with it or not.
	 * 
	 * @param parent The parent whose responses we're to look up
	 * 
	 * @return True if there is any activity, false otherwise
	 */
	public boolean hasActivity(V parent);

	@Override
	public ActivityRecordTypeDao getDao();
}