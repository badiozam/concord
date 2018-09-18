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
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.friendref.FriendRef;
import com.i4one.base.model.friendref.FriendRefManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedUserBalanceManager extends BaseUserPrivilegedManager<UserBalanceRecord, UserBalance> implements UserBalanceManager
{
	private UserBalanceManager userBalanceManager;
	private FriendRefManager friendRefManager;

	@Transactional(readOnly = false)
	@Override
	public ReturnType<UserBalance> increment(UserBalance userBalance, int amount)
	{
		ReturnType<UserBalance> retVal = getUserBalanceManager().increment(userBalance, amount);
		checkWrite(retVal.getPost(), userBalance.getUser(false), "increment");

		return retVal;
	}

	@Override
	public UserBalance getUserBalance(User user, Balance balance)
	{
		return getUserBalanceManager().getUserBalance(user, balance);
	}

	@Override
	public UserBalance getUserBalanceForUpdate(UserBalance balance)
	{
		return getUserBalanceManager().getUserBalanceForUpdate(balance);
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

	private boolean hasAccess(User loggedIn, User beingAccessed, int depth)
	{
		// Get the referrer for the beingAccessed that's currently logged in
		//
		FriendRef ref = getFriendRefManager().getReferrer(loggedIn);
		User referrer = ref.getUser(false);

		if ( ref.exists() )
		{
			// The beingAccessed currently logged in has access to his referrer's beingAccessed balance, which is
			// in this case the incoming parameter beingAccessed
			//
			if ( referrer.equals(beingAccessed) )
			{
				return true;
			}
			else if ( depth > 0 )
			{
				return false;
			}
			else
			{
				// Pretend the referrer of this user is who's logged
				// in. This allows a logged in user to access his
				// referrer's referrer which allows for trickle points
				// for any INITIAL referral sign up credit that the
				// logged in user's referrer receives to be able to go
				// to his referrer as well.
				//
				return hasAccess(referrer, beingAccessed, 1);
			}
		}
		else
		{
			getLogger().debug("User " + loggedIn + " does not have a referrer");

			// Fall back to the default since we can't allow write
			//
			return false;
		}
	}

	@Override
	protected boolean checkWriteInternal(UserBalance item, User beingAccessed, String methodName)
	{
		User loggedIn = getUser();
		if ( loggedIn.exists() )
		{
			getLogger().debug("User " + loggedIn + " is logged in, checking referrer");

			return hasAccess(loggedIn, beingAccessed, 0);
		}
		else
		{
			getLogger().debug("No user logged in when attempting to check user balance write");

			// No beingAccessed logged in, fall back to the default
			//
			return false;
		}
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	@Qualifier("base.FriendRefUserBalanceManager")
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
	public User getUser(UserBalance item)
	{
		return item.getUser();
	}

	@Override
	public Manager<UserBalanceRecord, UserBalance> getImplementationManager()
	{
		return getUserBalanceManager();
	}

	@Override
	public SingleClient getClient(UserBalance item)
	{
		return item.getUser().getClient();
	}
}
