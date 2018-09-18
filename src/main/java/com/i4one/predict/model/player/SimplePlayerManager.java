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
package com.i4one.predict.model.player;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.BalanceTriggerManager;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import com.i4one.base.model.balancetrigger.UserBalanceTriggerManager;
import com.i4one.base.model.user.User;
import com.i4one.predict.model.term.Term;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Hamid Badiozamani
 */
public class SimplePlayerManager extends BasePaginableManager<PlayerRecord, Player> implements PlayerManager
{
	private BalanceTriggerManager balanceTriggerManager;
	private UserBalanceTriggerManager userBalanceTriggerManager;

	public SimplePlayerManager()
	{
		super();
	}

	@Override
	public Player getPlayer(Term term, User user)
	{
		if ( term.exists() && user.exists() )
		{
			PlayerRecord delegate = SimplePlayerManager.this.getDao().getPlayer(term.getSer(), user.getSer());

			// If the current term is live but this user doesn't have a player record
			// we create one
			//
			if ( delegate == null )
			{		
				getLogger().debug("No player for " + term + " and user " + user);
				delegate = new PlayerRecord();
				if ( term.isLive(Utils.currentTimeSeconds()) )
				{
					delegate.setUserid(user.getSer());
					delegate.setTermid(term.getSer());

					delegate.setPending(0);
					delegate.setCorrect(0);
					delegate.setIncorrect(0);

					delegate.setScore(0);
					delegate.setScorerank(0);

					delegate.setWinstreak(0);
					delegate.setWinstreakrank(0);

					delegate.setAccuracy(0.0f);
					delegate.setAccuracyrank(0);

					delegate.setLastplayedtime(0);

					// Create an empty record for this player for now
					//
					SimplePlayerManager.this.getDao().insert(delegate);
				}
				else
				{
					// Non-existent player for the given non-live term, ignore
					//
				}
			}

			return new Player(delegate);
		}
		else
		{
			// Non-existent user/term = non-existent player
			//
			return new Player();
		}
	}

	@Override
	public Set<Player> getAllPlayers(Term term, PaginationFilter pagination)
	{
		return this.convertDelegates(getDao().getAllPlayers(term.getSer(), pagination));
	}

	@Override
	public Player emptyInstance()
	{
		return new Player();
	}

	@Override
	public PlayerRecordDao getDao()
	{
		return (PlayerRecordDao)super.getDao();
	}

	@Override
	public List<ReturnType<UserBalanceTrigger>> collectAllowance(User user, Term term)
	{
		return getUserBalanceTriggerManager().processTriggers(user,
			term.getClient(),
			this,
			"collectAllowance",
			term,
			SimplePaginationFilter.NONE);
	}

	@Override
	public Map<BalanceTrigger, Integer> getAllowanceEligibility(User user, Term term, int timestamp)
	{
		HashMap<BalanceTrigger, Integer> retVal = new HashMap<>();
		Set<BalanceTrigger> triggers = getBalanceTriggerManager().getAllTriggersByFeature(term, SimplePaginationFilter.NONE);

		triggers.stream().forEach((currTrigger) ->
		{
			retVal.put(currTrigger, getUserBalanceTriggerManager().eligibleInSeconds(currTrigger, user, timestamp));
		});

		return retVal;
	}

	public BalanceTriggerManager getBalanceTriggerManager()
	{
		return balanceTriggerManager;
	}

	@Autowired
	public void setBalanceTriggerManager(BalanceTriggerManager balanceTriggerManager)
	{
		this.balanceTriggerManager = balanceTriggerManager;
	}

	public UserBalanceTriggerManager getUserBalanceTriggerManager()
	{
		return userBalanceTriggerManager;
	}

	@Autowired
	public void setUserBalanceTriggerManager(UserBalanceTriggerManager userBalanceTriggerManager)
	{
		this.userBalanceTriggerManager = userBalanceTriggerManager;
	}
}
