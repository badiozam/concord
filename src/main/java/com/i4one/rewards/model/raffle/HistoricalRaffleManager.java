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
import com.i4one.base.model.adminhistory.AdminHistory;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.categorizable.BaseCategorizableHistoricalManager;
import com.i4one.base.model.manager.categorizable.CategorizableHistoricalManager;
import com.i4one.base.model.manager.categorizable.CategorizablePrivilegedManager;
import com.i4one.rewards.model.prize.PrizeWinning;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class HistoricalRaffleManager extends BaseCategorizableHistoricalManager<RaffleRecord, Raffle> implements CategorizableHistoricalManager<RaffleRecord, Raffle>, RaffleManager
{
	private CategorizablePrivilegedManager<RaffleRecord, Raffle> privilegedRaffleManager;

	@Override
	public List<ReturnType<PrizeWinning>> selectWinners(Raffle raffle)
	{
		return getRaffleManager().selectWinners(raffle);
	}

	@Override
	public RaffleSettings getSettings(SingleClient client)
	{
		return getRaffleManager().getSettings(client);
	}

	@Override
	public ReturnType<RaffleSettings> updateSettings(RaffleSettings settings)
	{
		return getRaffleManager().updateSettings(settings);
	}

	@Override
	public void incrementCurrentReserve(Raffle raffle, int amount)
	{
		getRaffleManager().incrementCurrentReserve(raffle, amount);
	}

	@Override
	public ReturnType<Raffle> incrementTotalReserve(Raffle raffle, int amount)
	{
		ReturnType<Raffle> retVal = getRaffleManager().incrementTotalReserve(raffle, amount);

		AdminHistory adminHistory = newAdminHistory(retVal.getPre(), retVal.getPost(), "incrementTotalReserve");
		setHistoryChainOwnership(retVal, adminHistory);

		retVal.put(ATTR_ADMINHISTORY, adminHistory);

		return retVal;
	}

	@Override
	public Raffle getAttached(Balance balance)
	{
		return getRaffleManager().getAttached(balance);
	}

	public RaffleManager getRaffleManager()
	{
		return (RaffleManager) getPrivilegedRaffleManager();
	}

	public CategorizablePrivilegedManager<RaffleRecord, Raffle> getPrivilegedRaffleManager()
	{
		return privilegedRaffleManager;
	}

	@Autowired
	public <P extends RaffleManager & CategorizablePrivilegedManager<RaffleRecord, Raffle>>
	 void setPrivilegedRaffleManager(P privilegedRaffleManager)
	{
		this.privilegedRaffleManager = privilegedRaffleManager;
	}

	@Override
	public CategorizablePrivilegedManager<RaffleRecord, Raffle> getImplementationManager()
	{
		return getPrivilegedRaffleManager();
	}
}
