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
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleAdminHistoryManager extends BaseSimpleManager<AdminHistoryRecord, AdminHistory> implements AdminHistoryManager
{
	@Override
	public Set<AdminHistory> getAdminHistoryByAdmin(Admin admin, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAdminHistoryByAdmin(admin.getSer(), pagination));
	}
	
	@Override
	public Set<AdminHistory> getAdminHistoryByParent(AdminHistory parent, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAdminHistoryByParent(parent.getSer(), pagination));
	}
	
	@Override
	public Set<AdminHistory> getAdminHistoryForItem(RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAdminHistory(item.getFeatureName(), item.getSer(), pagination));
	}

	@Override
	public Set<AdminHistory>getAdminHistoryForFeature(RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAdminHistoryByFeature(item.getFeatureName(), pagination));
	}

	@Override
	public AdminHistoryRecordDao getDao()
	{
		return (AdminHistoryRecordDao) super.getDao();
	}

	@Override
	public AdminHistory emptyInstance()
	{
		return new AdminHistory();
	}


}
