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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.BaseSiteGroupActivityPrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedRaffleEntryManager extends BaseSiteGroupActivityPrivilegedManager<RaffleEntryRecord, RaffleEntry, Raffle> implements RaffleEntryManager
{
	private RaffleEntryManager raffleEntryManager;

	@Override
	public Set<RaffleEntry> getAllRaffleEntries(User user, PaginationFilter pagination)
	{
		// Only the user (or admin) can read all entries for a given user
		//
		checkRead(emptyInstance(), user, "getAllRaffleEntries");

		return getRaffleEntryManager().getAllRaffleEntries(user, pagination);
	}

	@Override
	public Set<RaffleEntry> getAllRaffleEntries(Raffle raffle, PaginationFilter pagination)
	{
		// Only administrators can read all raffle entries
		//
		checkRead(emptyInstance(), new User(), "getAllRaffleEntries");

		return getRaffleEntryManager().getAllRaffleEntries(raffle, pagination);
	}

	@Override
	public Set<RaffleEntry> getRaffleEntries(Raffle raffle, User user, PaginationFilter pagination)
	{
		// Only the user (or admin) can read all entries for a given user
		//
		checkRead(emptyInstance(), user, "getRaffleEntries");

		return getRaffleEntryManager().getRaffleEntries(raffle, user, pagination);
	}

	@Override
	public UserBalance getRaffleEntryBalance(Raffle raffle, User user)
	{
		checkRead(emptyInstance(), user, "getTotalRaffleEntries");
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
		return item.getUser();
	}

	@Override
	public RaffleEntryManager getImplementationManager()
	{
		return getRaffleEntryManager();
	}

	@Override
	public SingleClient getClient(RaffleEntry item)
	{
		return item.getRaffle().getClient();
	}

	public RaffleEntryManager getRaffleEntryManager()
	{
		return raffleEntryManager;
	}

	@Autowired
	public void setRaffleEntryManager(RaffleEntryManager expensiveRaffleEntryManager)
	{
		this.raffleEntryManager = expensiveRaffleEntryManager;
	}

	@Override
	public ActivityReport getReport(RaffleEntry item, TopLevelReport report, PaginationFilter pagination)
	{
		return getRaffleEntryManager().getReport(item, report, pagination);
	}
}
