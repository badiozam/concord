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
import com.i4one.base.model.manager.BaseTransactionalActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("rewards.RaffleEntryManager")
public class TransactionalRaffleEntryManager extends BaseTransactionalActivityManager<RaffleEntryRecord, RaffleEntry, Raffle> implements RaffleEntryManager
{
	private RaffleEntryManager raffleEntryManager;

	@Override
	public RaffleEntryManager getImplementationManager()
	{
		return getRaffleEntryManager();
	}

	protected void processRaffleEntryTransaction(ReturnType<RaffleEntry> retRaffleEntry)
	{
		RaffleEntry pre = retRaffleEntry.getPre();
		RaffleEntry post = retRaffleEntry.getPost();

		Transaction t = newTransaction(post.getUser());

		if ( post.exists() && pre.equals(post))
		{
			// Previously played
			//
			t.setStatus(Transaction.FAILED);
		}

		// The message is responsible for displaying the proper status
		//
		setTransactionDescr(t, "msg.raffleEntryManager.create.xaction.descr", "raffleEntry", post);

		// See if we can locate a user balance update transaction and set us
		// as the the parent transaction
		//
		t = createTransaction(retRaffleEntry, t);

		// Set our master transaction record since we've created a new transaction
		//
		retRaffleEntry.put("transaction", t);
	}

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

	@Transactional(readOnly = false)
	@Override
	public ReturnType<RaffleEntry> create(RaffleEntry raffleEntry)
	{
		raffleEntry.setTimeStampSeconds(getRequestState().getRequest().getTimeInSeconds());

		ReturnType<RaffleEntry> retVal = getRaffleEntryManager().create(raffleEntry);

		// Only process a transaction if the entry was actually purchased
		//
		if ( retVal.getPost().exists() )
		{
			processRaffleEntryTransaction(retVal);
		}

		return retVal;
	}

	public RaffleEntryManager getRaffleEntryManager()
	{
		return raffleEntryManager;
	}

	@Autowired
	public void setRaffleEntryManager(RaffleEntryManager privilegedRaffleEntryManager)
	{
		this.raffleEntryManager = privilegedRaffleEntryManager;
	}
}
