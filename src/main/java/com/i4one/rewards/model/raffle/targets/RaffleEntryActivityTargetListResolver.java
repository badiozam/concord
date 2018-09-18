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
package com.i4one.rewards.model.raffle.targets;

import com.i4one.base.model.manager.targetable.BaseActivityTargetListResolver;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.rewards.model.raffle.Raffle;
import com.i4one.rewards.model.raffle.RaffleEntry;
import com.i4one.rewards.model.raffle.RaffleEntryManager;
import com.i4one.rewards.model.raffle.RaffleEntryRecord;
import com.i4one.rewards.model.raffle.RaffleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class RaffleEntryActivityTargetListResolver extends BaseActivityTargetListResolver<RaffleEntryRecord, RaffleEntry, Raffle, RaffleEntryTarget>
{
	private RaffleManager raffleManager;
	private RaffleEntryManager raffleEntryManager;

	@Override
	protected RaffleEntryTarget emptyInstance(String key)
	{
		return new RaffleEntryTarget(key);
	}

	@Override
	protected RaffleManager getParentManager()
	{
		return getRaffleManager();
	}

	@Override
	protected ActivityManager<RaffleEntryRecord, RaffleEntry, Raffle> getActivityManager()
	{
		return getRaffleEntryManager();
	}

	public RaffleManager getRaffleManager()
	{
		return raffleManager;
	}

	@Autowired
	public void setRaffleManager(RaffleManager raffleManager)
	{
		this.raffleManager = raffleManager;
	}

	public RaffleEntryManager getRaffleEntryManager()
	{
		return raffleEntryManager;
	}

	@Autowired
	public void setRaffleEntryManager(RaffleEntryManager raffleEntryManager)
	{
		this.raffleEntryManager = raffleEntryManager;
	}

}
