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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.adminhistory.AdminHistory;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class HistoricalUserBalanceManager extends BaseHistoricalManager<UserBalanceRecord, UserBalance> implements UserBalanceManager
{
	private UserBalanceManager privilegedUserBalanceManager;

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public UserBalance getUserBalanceForUpdate(UserBalance userBalance)
	{
		return getUserBalanceManager().getUserBalanceForUpdate(userBalance);
	}

	@Override
	public ReturnType<UserBalance> increment(UserBalance userBalance, int amount)
	{
		ReturnType<UserBalance> retVal = getPrivilegedUserBalanceManager().increment(userBalance, amount);

		if ( retVal.getPost().exists() )
		{
			AdminHistory adminHistory = newAdminHistory(retVal.getPre(), retVal.getPost(), "increment");
			setHistoryChainOwnership(retVal, adminHistory);

			retVal.put(ATTR_ADMINHISTORY, adminHistory);
		}

		return retVal;
	}

	@Override
	public UserBalance getUserBalance(User user, Balance balance)
	{
		return getUserBalanceManager().getUserBalance(user, balance);
	}

	@Override
	public UserBalance randomUserBalance(Balance balance)
	{
		return getUserBalanceManager().randomUserBalance(balance);
	}

	@Override
	public Set<UserBalance> weightedRandomUserBalances(Balance balance, int count)
	{
		return getUserBalanceManager().weightedRandomUserBalances(balance, count);
	}

	@Override
	public Set<UserBalance> getUserBalances(User user, PaginationFilter pagination)
	{
		return getUserBalanceManager().getUserBalances(user, pagination);
	}

	@Override
	public Set<UserBalance> getUserBalances(Balance balance, PaginationFilter pagination)
	{
		return getUserBalanceManager().getUserBalances(balance, pagination);
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return (UserBalanceManager) getPrivilegedUserBalanceManager();
	}

	public UserBalanceManager getPrivilegedUserBalanceManager()
	{
		return privilegedUserBalanceManager;
	}

	@Autowired
	public <P extends UserBalanceManager & PrivilegedManager<UserBalanceRecord, UserBalance>>
	 void setPrivilegedUserBalanceManager(P privilegedUserBalanceManager)
	{
		this.privilegedUserBalanceManager = privilegedUserBalanceManager;
	}

	@Override
	public PrivilegedManager<UserBalanceRecord, UserBalance> getImplementationManager()
	{
		return (PrivilegedManager<UserBalanceRecord, UserBalance>) getPrivilegedUserBalanceManager();
	}
}
