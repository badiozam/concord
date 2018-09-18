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
package com.i4one.base.model.user.data;

import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("base.UserDatumManager")
public class SimpleUserDatumManager extends BasePaginableManager<UserDatumRecord, UserDatum> implements UserDatumManager
{
	public SimpleUserDatumManager()
	{
		super();
	}

	@Override
	public UserDatum emptyInstance()
	{
		return new UserDatum();
	}

	@Override
	public UserDatum getUserDatum(User user, String name)
	{
		UserDatumRecord userDatumRecord = getDao().getDatum(user.getSer(), name);
		if ( userDatumRecord != null )
		{
			return new UserDatum(userDatumRecord);
		}
		else
		{
			return new UserDatum();
		}
	}

	@Override
	public Set<UserDatum> getUserData(User user, PaginationFilter pagination)
	{
		return this.convertDelegates(getDao().getAllData(user.getSer(), pagination));
	}
	
	@Override
	public UserDatumRecordDao getDao()
	{
		return (UserDatumRecordDao)super.getDao();
	}
}
