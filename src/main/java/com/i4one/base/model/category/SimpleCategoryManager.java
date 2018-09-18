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
package com.i4one.base.model.category;

import com.i4one.base.dao.Dao;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class SimpleCategoryManager<U extends CategoryRecord, T extends Category<U>> extends BaseSimpleManager<U, T> implements CategoryManager<U,T>
{
	@Override
	public T getCategory(String title, PaginationFilter pagination)
	{
		U record = getDao().getByTitle(title, pagination);

		T retVal = emptyInstance();
		if ( record != null )
		{
			retVal.setOwnedDelegate(record);
		}

		return retVal;
	}

	@Override
	public Set<T> getAllCategories(PaginationFilter pagination)
	{
		return super.convertDelegates(getDao().getAllCategories(pagination));
	}

	@Override
	public boolean isEmpty(T item)
	{
		return getDao().isEmpty(item.getDelegate());
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		// We call the super-class' update to make sure the validators get called
		// and to load the previous object from the database in order to have the
		// latest copy
		//
		ReturnType<T> retVal = super.update(item);

		T preItem = retVal.getPre();

		// We check to see if the previous client is a member of the new client being set.
		// If the new client is a parent or superset of the existing client, then the update can
		// proceed. If the new client is either a child or a subset of the existing client
		// then no guarantee can be made that the items in the category will also all be within
		// the category's client range and the update must be aborted
		//
		// NOTE: We need to pull these out into their own variable in order to work around the following
		// JDK 1.8.0_20 bug: https://bugs.openjdk.java.net/browse/JDK-8056984
		//
		SingleClient preItemClient = preItem.getClient();
		SingleClient itemClient = item.getClient();
		if ( !preItemClient.isMemberOf(itemClient) && !isEmpty(item) )
		{
			throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".update.notempty", "Category $item.title ($item.ser) is not empty and reducing its client level invalidates its member objects", new Object[] { "item", item }));
		}
		else
		{
			return retVal;
		}
	}

	@Override
	public CategoryRecordDao<U> getDao()
	{
		Dao<U> baseDao = super.getDao();
		if ( baseDao instanceof CategoryRecordDao )
		{
			CategoryRecordDao<U> categoryRecordDao = (CategoryRecordDao<U>) baseDao;
			return categoryRecordDao;
		}
		else
		{
			return null;
		}
	}
}
