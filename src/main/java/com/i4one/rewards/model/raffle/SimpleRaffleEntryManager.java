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

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleRaffleEntryManager extends BaseActivityManager<RaffleEntryRecord, RaffleEntry, Raffle> implements RaffleEntryManager
{
	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;

	@Override
	public RaffleEntry emptyInstance()
	{
		return new RaffleEntry();
	}

	@Override
	public Set<RaffleEntry> getAllRaffleEntries(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByUser(user.getSer(), pagination));
	}

	@Override
	public Set<RaffleEntry> getAllRaffleEntries(Raffle raffle, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByItem(raffle.getSer(), pagination));
	}

	@Override
	public Set<RaffleEntry> getRaffleEntries(Raffle raffle, User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAll(raffle.getSer(), user.getSer(), pagination));
	}

	@Override
	public UserBalance getRaffleEntryBalance(Raffle raffle, User user)
	{
		Balance raffleBalance = getBalanceManager().getBalance(raffle);
		UserBalance retVal = getUserBalanceManager().getUserBalance(user, raffleBalance);

		// If the user has no entries, we'll want to make sure that at least
		// the foreign keys are properly set
		//
		if ( !retVal.exists() )
		{
			retVal.setBalance(raffleBalance);
			retVal.setUser(user);
		}

		return retVal;
	}

	@Override
	public boolean hasPurchases(Raffle raffle)
	{
		return getDao().hasActivity(raffle.getSer());
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<RaffleEntry> create(RaffleEntry raffleEntry)
	{
		if (
			(raffleEntry.getTimeStampSeconds() > raffleEntry.getRaffle().getPurchaseEndTimeSeconds()) ||
			(raffleEntry.getTimeStampSeconds() < raffleEntry.getRaffle().getPurchaseStartTimeSeconds())
		)
		{
			// The raffle has either expired or is not live yet
			//
			ReturnType<RaffleEntry> expired = new ReturnType<>();

			expired.setPre(raffleEntry);
			expired.setPost(raffleEntry);

			return expired;
		}
		else
		{
			Raffle raffle = raffleEntry.getRaffle();

			int userid = raffleEntry.getUser(false).getSer();
			int raffleid = raffle.getSer();

			// Check to make sure the total quantity doesn't exceed the user limit
			//
			int currEntries = getDao().getTotalActivityQuantity(raffleid, userid);
			if ( raffle.getUserLimit() > 0 && currEntries + raffleEntry.getQuantity() > raffle.getUserLimit() )
			{
				throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg.rewards.raffleEntryManager.create.overlimit", "You have reached the maximum entry limit allowable for this item: you currently have $currentPurchaseQuantity and by purchasing $item.quantity more you would exceed the maximum of $item.raffle.userLimit", new Object[] { "item", raffleEntry, "currentPurchaseQuantity", currEntries }, null));
			}
			else
			{
				ReturnType<RaffleEntry> retVal = super.create(raffleEntry);
				Balance raffleBalance = getBalanceManager().getBalance(raffle);

				ReturnType<UserBalance> userBalance = getUserBalanceManager().increment(
					new UserBalance(raffleEntry.getUser(), raffleBalance, raffleEntry.getTimeStampSeconds()),
					raffleEntry.getQuantity());

				if ( userBalance.getPost().exists() )
				{
					retVal.addChain(getUserBalanceManager(), "increment", userBalance);
					return retVal;
				}
				else
				{
					throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg.rewards.raffleEntryManager.create.nsf", "Cannot increment entry balance", new Object[] { "item", raffleEntry, "currentPurchaseQuantity", currEntries }, null));
				}

			}
		}
	}

	@Override
	public List<ReturnType<RaffleEntry>> revert(Raffle parent, PaginationFilter pagination)
	{
		List<ReturnType<RaffleEntry>> retVal = super.revert(parent, pagination);

		Balance raffleBalance = getBalanceManager().getBalance(parent);
		for ( ReturnType<RaffleEntry> removedEntry : retVal )
		{
			RaffleEntry raffleEntry = removedEntry.getPre();

			ReturnType<UserBalance> userBalance = getUserBalanceManager().increment(
				new UserBalance(raffleEntry.getUser(), raffleBalance, raffleEntry.getTimeStampSeconds()),
				-1 * raffleEntry.getQuantity());

			removedEntry.addChain(getUserBalanceManager(), "increment", userBalance);
		}

		return retVal;
	}

	@Override
	public ReturnType<RaffleEntry> update(RaffleEntry raffleEntry)
	{
		throw new UnsupportedOperationException("Can't update a raffle entry.");
	}

	@Override
	public RaffleEntryRecordDao getDao()
	{
		return (RaffleEntryRecordDao) super.getDao();
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

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}
}