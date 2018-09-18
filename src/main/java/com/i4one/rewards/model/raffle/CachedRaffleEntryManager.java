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
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;

/**
 * @author Hamid Badiozamani
 */
public class CachedRaffleEntryManager extends SimpleRaffleEntryManager implements RaffleEntryManager
{
	@Override
	public Set<RaffleEntry> getAllRaffleEntries(User user, PaginationFilter pagination)
	{
		return super.getAllRaffleEntries(user, pagination);
	}

	@Override
	public Set<RaffleEntry> getAllRaffleEntries(Raffle raffle, PaginationFilter pagination)
	{
		return super.getAllRaffleEntries(raffle, pagination);
	}

	@Override
	public Set<RaffleEntry> getRaffleEntries(Raffle raffle, User user, PaginationFilter pagination)
	{
		return super.getRaffleEntries(raffle, user, pagination);
	}

	@Override
	public UserBalance getRaffleEntryBalance(Raffle raffle, User user)
	{
		return super.getRaffleEntryBalance(raffle, user);
	}

	@Override
	public boolean hasPurchases(Raffle raffle)
	{
		return super.hasPurchases(raffle);
	}

	@CacheEvict(value = "raffleEntryManager", allEntries = true )
	@Override
	public ReturnType<RaffleEntry> create(RaffleEntry raffleEntry)
	{
		return super.create(raffleEntry);
	}

	@CacheEvict(value = "raffleEntryManager", allEntries = true )
	@Override
	public ReturnType<RaffleEntry> update(RaffleEntry respondent)
	{
		return super.update(respondent);
	}

	@CacheEvict(value = "raffleEntryManager", allEntries = true)
	@Override
	public RaffleEntry remove(RaffleEntry respondent)
	{
		return super.remove(respondent);
	}
}
