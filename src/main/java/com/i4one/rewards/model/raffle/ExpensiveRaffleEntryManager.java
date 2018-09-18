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
package com.i4one.rewards.model.raffle;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balanceexpense.BalanceExpenseManager;
import com.i4one.base.model.manager.expendable.BaseExpensiveManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class ExpensiveRaffleEntryManager extends BaseExpensiveManager<RaffleEntryRecord, RaffleEntry, Raffle> implements RaffleEntryManager
{
	private BalanceExpenseManager balanceExpenseManager;
	private UserBalanceManager userBalanceManager;

	private RaffleEntryManager raffleEntryManager;

	@Override
	public Set<RaffleEntry> getAllRaffleEntries(User user, PaginationFilter pagination)
	{
		return getRaffleEntryManager().getAllRaffleEntries(user, pagination);
	}

	@Override
	public Set<RaffleEntry> getAllRaffleEntries(Raffle raffle, PaginationFilter pagination)
	{
		return getRaffleEntryManager().getAllRaffleEntries(raffle, pagination);
	}

	@Override
	public Set<RaffleEntry> getRaffleEntries(Raffle raffle, User user, PaginationFilter pagination)
	{
		return getRaffleEntryManager().getRaffleEntries(raffle, user, pagination);
	}

	@Override
	public UserBalance getRaffleEntryBalance(Raffle raffle, User user)
	{
		return getRaffleEntryManager().getRaffleEntryBalance(raffle, user);
	}

	@Override
	public boolean hasPurchases(Raffle raffle)
	{
		return getRaffleEntryManager().hasPurchases(raffle);
	}

	@Override
	public User getUser(RaffleEntry item)
	{
		return item.getUser(false);
	}

	@Override
	public Raffle getAttachee(RaffleEntry raffleEntry)
	{
		return raffleEntry.getRaffle();
	}

	@Override
	public RaffleEntryManager getImplementationManager()
	{
		return getRaffleEntryManager();
	}

	@Override
	protected void processAttached(ReturnType<RaffleEntry> retRaffleEntry, String methodName)
	{
		// We only process the triggers if an entry was successfully processed
		//
		RaffleEntry pre = retRaffleEntry.getPre();
		RaffleEntry post = retRaffleEntry.getPost();

		if ( !post.exists() )
		{
			getLogger().debug("Expenses not processed for " + retRaffleEntry.getPost().getRaffle() + " and user " + retRaffleEntry.getPost().getUser(false));
		}
		else
		{
			super.processAttached(retRaffleEntry, methodName);
		}
	}

	public RaffleEntryManager getRaffleEntryManager()
	{
		return raffleEntryManager;
	}

	@Autowired
	public void setRaffleEntryManager(RaffleEntryManager simpleRaffleEntryManager)
	{
		this.raffleEntryManager = simpleRaffleEntryManager;
	}

	public BalanceExpenseManager getBalanceExpenseManager()
	{
		return balanceExpenseManager;
	}

	@Autowired
	public void setBalanceExpenseManager(BalanceExpenseManager balanceExpenseManager)
	{
		this.balanceExpenseManager = balanceExpenseManager;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

}