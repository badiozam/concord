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
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.friendref.FriendRef;
import com.i4one.base.model.friendref.FriendRefManager;
import com.i4one.base.model.friendref.FriendRefSettings;
import com.i4one.base.model.manager.BaseDelegatingManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.web.RequestState;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Trickles points to friends based on referral.
 * 
 * @author Hamid Badiozamani
 */
@Service
public class FriendRefUserBalanceManager extends BaseDelegatingManager<UserBalanceRecord, UserBalance> implements UserBalanceManager
{
	private RequestState requestState;

	private FriendRefManager friendRefManager;
	private UserBalanceManager userBalanceManager;

	public static final String TRICKLE = "trickle";
	public static final String SETTINGS = "settings";

	@Override
	public UserBalance getUserBalanceForUpdate(UserBalance balance)
	{
		return getUserBalanceManager().getUserBalanceForUpdate(balance);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<UserBalance> increment(UserBalance userBalance, int amount)
	{
		ReturnType<UserBalance> retVal = getUserBalanceManager().increment(userBalance, amount);

		RequestState state = getRequestState();

		// Trickle only works through user activity, so if there's no user logged in we can
		// assume that the increment happened by an administrator and we can skip this
		//
		if ( state.getUser().exists() )
		{
			FriendRefSettings settings = getFriendRefManager().getSettings(state.getSingleClient());
	
			if ( settings.isEnabled() && retVal.getPost().exists() )
			{
				if ( state.get(TRICKLE) == null )
				{
					// We set a trickle count since the referrer may have their own
					// referrer and the trickled down rules only apply to one level
					//
					state.set(TRICKLE, 1);
	
					FriendRef referral = getFriendRefManager().getReferrer(userBalance.getUser());
					if ( referral.exists() )
					{
						getLogger().debug("Found referral " + referral);
	
						int trickleAmount = Math.round(settings.getTricklePercentage() * amount);
						if ( trickleAmount > 0 )
						{
							getLogger().debug("Trickle amount is " + trickleAmount);
							if ( referral.isTrickleEligible(settings, state.getRequest().getTimeInSeconds()) )
							{
								getLogger().debug("Referrer is eligible, incrementing balance for referrer " + referral.getUser());
	
								// We use the autowired manager here to ensure that we have all of the functionality
								// (e.g. transaction recording, privilege checking, further trickling, etc.)
								//
								ReturnType<UserBalance> trickle = getUserBalanceManager().increment(
									new UserBalance(referral.getUser(), userBalance.getBalance(), userBalance.getUpdateTimeSeconds()),
									trickleAmount);
	
								trickle.put(SETTINGS, settings);
								retVal.put(TRICKLE, trickle);
	
								getLogger().debug("Trickle complete for " + referral.getUser());
							}
						}
					}
	
					state.set(TRICKLE, null);
				}
				else
				{
					// Skipping since we've already trickled down to one member
					//
					getLogger().debug("Skipping trickle to referrer of referrer");
				}
			}
		}

		return retVal;
	}

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	@Qualifier("base.CachedUserBalanceManager")
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

	public FriendRefManager getFriendRefManager()
	{
		return friendRefManager;
	}

	@Autowired
	public void setFriendRefManager(FriendRefManager friendRefManager)
	{
		this.friendRefManager = friendRefManager;
	}

	@Override
	public Manager<UserBalanceRecord, UserBalance> getImplementationManager()
	{
		return getUserBalanceManager();
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
}
