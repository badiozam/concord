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
package com.i4one.base.model.adminhistory;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface AdminHistoryManager extends Manager<AdminHistoryRecord,AdminHistory>
{
	/**
	 * Get an administrator's history
	 * 
	 * @param admin The administrator whose history to retrieve
	 * @param pagination The pagination/ordering information
	 * 
	 * @return A (potentially empty) list of history records for the given admin
	 */
	public Set<AdminHistory> getAdminHistoryByAdmin(Admin admin, PaginationFilter pagination);

	/**
	 * Get the history records associated with a parent history record
	 * 
	 * @param parent The parent history record
	 * @param pagination The pagination/ordering information
	 * 
	 * @return A (potentially empty) list of history records for the given parent
	 */
	public Set<AdminHistory> getAdminHistoryByParent(AdminHistory parent, PaginationFilter pagination);

	/**
	 * Get the history records associated with a given item
	 * 
	 * @param item The item whose historical records are stored in the format  "featureName:id"
	 * @param pagination The pagination/ordering information
	 * 
	 * @return A (potentially empty) list of history records for the given item
	 */
	public Set<AdminHistory> getAdminHistoryForItem(RecordTypeDelegator<?> item, PaginationFilter pagination);

	/**
	 * Get the history records associated with a given feature.
	 * 
	 * @param item An item that belongs to a feature for which historical records are to be retrieved
	 * @param pagination The pagination/ordering information
	 * 
	 * @return A (potentially empty)list of history records for the given feature.
	 */
	public Set<AdminHistory>getAdminHistoryForFeature(RecordTypeDelegator<?> item, PaginationFilter pagination);
}
