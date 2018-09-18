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
package com.i4one.base.model.manager.categorizable;

import com.i4one.base.dao.categorizable.CategorizableRecordTypeDao;
import com.i4one.base.dao.categorizable.CategorizableTerminableClientRecordType;
import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.category.Category;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.terminable.BaseSimpleTerminableManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 * @param <U> The database record type
 * @param <T> The model type
 * @param <V> The category model type
 */
public abstract class BaseSimpleCategorizableManager<U extends CategorizableTerminableClientRecordType, T extends CategorizableTerminableClientType<U,V>, V extends Category<?>> extends BaseSimpleTerminableManager<U, T> implements CategorizableTerminableManager<U, T>
{
	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		// We only allow an item to be created if its client/sitegroup belongs to
		// (or is equal to) the item category's client/sitegroup
		//
		// NOTE: We need to pull this out into its own variable in order to work around the following
		// JDK 1.8.0_20 bug: https://bugs.openjdk.java.net/browse/JDK-8056984
		//
		SingleClient categoryClient = item.getCategory().getClient();
		if ( item.belongsTo(categoryClient))
		{
			return super.create(item);
		}
		else
		{
			throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".create.mismatch", "The category $item.category does not belong to the same client group as the item $item", new Object[] { "item", item }));
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		// We do the update first to give the validator's a chance to throw any
		// pre-database hit errors
		//
		ReturnType<T> retVal = super.update(item);

		// We only allow an item to be updated if its client is equal to
		// the item category's client. This should be a redundant check as
		// the the validate() method of the item should have caught this
		// but we can't rely on that entirely
		//
		// NOTE: We need to pull this out into its own variable in order to work around the following
		// JDK 1.8.0_20 bug: https://bugs.openjdk.java.net/browse/JDK-8056984
		//
		SingleClient categoryClient = item.getCategory().getClient();
		if ( !item.belongsTo(categoryClient) )
		{
			throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".update.mismatch", "The category $item.category does not belong to the same client group as the item $item", new Object[] { "item", item }));
		}
		else
		{
			return retVal;
		}
	}

	@Override
	public CategorizableRecordTypeDao<U> getDao()
	{
		PaginableRecordTypeDao<U> baseDao = super.getDao();
		if ( baseDao instanceof CategorizableRecordTypeDao )
		{
			CategorizableRecordTypeDao<U> categorizableDao = (CategorizableRecordTypeDao<U>) baseDao;
			return categorizableDao;
		}
		else
		{
			return null;
		}
	}
}
