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

import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.balance.Balance;

/**
 * A user's balance object. This model handles automatically updating the last
 * update time of the underlying record
 *
 * @author Hamid Badiozamani
 */
public class UserBalance extends BaseRecordTypeDelegator<UserBalanceRecord>
{
	static final long serialVersionUID = 42L;

	private transient User user;
	private transient Balance balance;

	public UserBalance()
	{
		super(new UserBalanceRecord());
	}

	public UserBalance(User user, Balance balance, int timeStamp)
	{
		super(new UserBalanceRecord());

		setUser(user);
		setBalance(balance);
		setCreateTimeSeconds(timeStamp);
		setUpdateTimeSeconds(timeStamp);
	}

	protected UserBalance(UserBalanceRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		if ( user == null )
		{
			user = new User();
		}
		user.resetDelegateBySer(getDelegate().getUserid());

		if ( balance == null )
		{
			balance = new Balance();
		}
		balance.resetDelegateBySer(getDelegate().getBalid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setUser(getUser());
		setBalance(getBalance());
	}

	public User getUser()
	{
		return getUser(true);
	}

	public User getUser(boolean doLoad)
	{
		if ( doLoad )
		{
			user.loadedVersion();
		}

		return user;
	}

	public final void setUser(User user)
	{
		this.user = user;
		getDelegate().setUserid(user.getSer());
	}

	public Balance getBalance()
	{
		return getBalance(true);
	}

	public Balance getBalance(boolean doLoad)
	{
		if ( doLoad )
		{
			balance.loadedVersion();
		}

		return balance;
	}

	public final void setBalance(Balance balance)
	{
		this.balance = balance;
		getDelegate().setBalid(balance.getSer());
	}

	public Integer getTotal()
	{
		return getDelegate().getTotal();
	}

	public void setTotal(int total)
	{
		getDelegate().setTotal(total);
	}

	public int getUpdateTimeSeconds()
	{
		return getDelegate().getUpdatetime();
	}

	public final void setUpdateTimeSeconds(int updateTime)
	{
		getDelegate().setUpdatetime(updateTime);
	}

	public int getCreateTimeSeconds()
	{
		return getDelegate().getCreatetime();
	}

	public final void setCreateTimeSeconds(int createTime)
	{
		getDelegate().setCreatetime(createTime);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getUserid() + "-" + getDelegate().getBalid();
	}
}
