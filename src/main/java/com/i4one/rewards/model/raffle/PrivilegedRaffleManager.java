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
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.categorizable.BaseCategorizablePrivilegedManager;
import com.i4one.base.model.manager.categorizable.CategorizablePrivilegedManager;
import com.i4one.base.model.manager.categorizable.CategorizableTerminableManager;
import com.i4one.rewards.model.prize.PrizeWinning;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedRaffleManager extends BaseCategorizablePrivilegedManager<RaffleRecord, Raffle> implements CategorizablePrivilegedManager<RaffleRecord, Raffle>, RaffleManager
{
	private RaffleManager raffleManager;

	@Override
	public List<ReturnType<PrizeWinning>> selectWinners(Raffle raffle)
	{
		checkWrite(getClient(raffle), "selectWinners");
		return getRaffleManager().selectWinners(raffle);
	}

	@Override
	public RaffleSettings getSettings(SingleClient client)
	{
		// Anyone can read the current settings because we need to know
		// whether the feature is enabled or not.
		//
		//checkRead(client, "updateSettings");
		return getRaffleManager().getSettings(client);
	}

	@Override
	public ReturnType<RaffleSettings> updateSettings(RaffleSettings settings)
	{
		checkWrite(settings.getClient(), "updateSettings");
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
		// Only administrators can increase the reserve
		//
		super.checkWrite(getClient(raffle), "incrementCurrentReserve");

		return getRaffleManager().incrementTotalReserve(raffle, amount);
	}

	@Override
	public Raffle getAttached(Balance balance)
	{
		Raffle retVal = getRaffleManager().getAttached(balance);
		if ( retVal.exists() )
		{
			checkRead(getClient(retVal), "getAttached");
		}

		return retVal;
	}

	public RaffleManager getRaffleManager()
	{
		return raffleManager;
	}

	@Autowired
	public void setRaffleManager(RaffleManager expendableRaffleManager)
	{
		this.raffleManager = expendableRaffleManager;
	}

	@Override
	public CategorizableTerminableManager<RaffleRecord, Raffle> getImplementationManager()
	{
		return getRaffleManager();
	}

	@Override
	public SingleClient getClient(Raffle item)
	{
		return item.getClient(false);
	}
}
