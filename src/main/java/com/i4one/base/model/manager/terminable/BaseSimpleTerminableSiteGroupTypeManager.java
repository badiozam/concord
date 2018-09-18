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

import com.i4one.base.core.Utils;
import com.i4one.base.dao.Dao;
import com.i4one.base.dao.terminable.TerminableSiteGroupRecordType;
import com.i4one.base.dao.terminable.TerminableSiteGroupRecordTypeDao;
import com.i4one.base.model.i18n.IString;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseSimpleTerminableSiteGroupTypeManager<U extends TerminableSiteGroupRecordType, T extends TerminableSiteGroupType<U>> extends BaseSimpleTerminableManager<U, T> implements TerminableManager<U,T>
{
	@Override
	protected T cloneInternal(T item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		T retVal = super.cloneInternal(item);

		String currTimeStamp = String.valueOf(Utils.currentDateTime());
		IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
		retVal.setTitle(workingTitle);

		return retVal;
	}

	@Override
	public TerminableSiteGroupRecordTypeDao<U> getDao()
	{
		Dao<U> baseDao = super.getDao();
		if (baseDao instanceof TerminableSiteGroupRecordTypeDao)
		{
			TerminableSiteGroupRecordTypeDao<U> terminableDao = (TerminableSiteGroupRecordTypeDao<U>) baseDao;
			return terminableDao;
		}
		else
		{
			return null;
		}
	}

}
