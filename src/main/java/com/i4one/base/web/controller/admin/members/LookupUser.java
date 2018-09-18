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
package com.i4one.base.web.controller.admin.members;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;

/**
 * @author Hamid Badiozamani
 */
public class LookupUser extends BaseLoggable
{
	static final long serialVersionUID = 42L;

	private String lookupUsername;
	private boolean selectRandom; 
	private User user;
	private UserBalance defaultBalance;

	private PaginationFilter transactionPagination;

	public LookupUser()
	{
		lookupUsername = "";

		user = new User();
		defaultBalance = new UserBalance();

		transactionPagination = new SimplePaginationFilter();
		transactionPagination.setOrderBy("timestamp DESC, ser DESC");
		transactionPagination.setCurrentPage(0);
		transactionPagination.setPerPage(1000);
	}

	public String getLookupUsername()
	{
		return lookupUsername;
	}

	public void setLookupUsername(String lookupUsername)
	{
		this.lookupUsername = lookupUsername;
	}

	public PaginationFilter getTransactionPagination()
	{
		return transactionPagination;
	}

	public void setTransactionPagination(PaginationFilter transactionPagination)
	{
		this.transactionPagination = transactionPagination;
	}

	public boolean getSelectRandom()
	{
		return selectRandom;
	}

	public void setSelectRandom(boolean selectRandom)
	{
		this.selectRandom = selectRandom;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public UserBalance getDefaultBalance()
	{
		return defaultBalance;
	}

	public void setDefaultBalance(UserBalance defaultBalance)
	{
		this.defaultBalance = defaultBalance;
	}

	public User buildLookupUser()
	{
		User retVal = new User();
		retVal.setUsername(getLookupUsername());

		return retVal;
	}

	@Override
	public boolean equals(Object right)
	{
		if ( right == null )
		{
			return false;
		}
		else
		{
			if ( right instanceof LookupUser )
			{
				return super.equals(right);
			}
			else
			{
				return false;
			}
		}
	}
}
