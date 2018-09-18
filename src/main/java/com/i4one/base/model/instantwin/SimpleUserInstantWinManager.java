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
package com.i4one.base.model.instantwin;


import com.i4one.base.core.Utils;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.TransactionManager;
import com.i4one.base.model.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleUserInstantWinManager extends BaseActivityManager<UserInstantWinRecord, UserInstantWin, InstantWin> implements UserInstantWinManager
{
	private TransactionManager transactionManager;
	private InstantWinManager instantWinManager;

	@Override
	public UserInstantWin emptyInstance()
	{
		return new UserInstantWin();
	}

	@Override
	public boolean hasWon(InstantWin instantWin, User user)
	{
		return getDao().get(user.getSer(), instantWin.getSer()) != null;
	}

	@Override
	public Set<UserInstantWin> getByUser(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByUser(user.getSer(), pagination));
	}

	@Override
	public Set<UserInstantWin> getByInstantWin(InstantWin instantWin, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByItem(instantWin.getSer(), pagination));
	}

	@Override
	public Set<UserInstantWin> getAllUserInstantWins(InstantWin instantWin, User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAll(instantWin.getSer(), user.getSer(), pagination));
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<UserInstantWin>> processInstantWins(User user, Manager<?, ?> manager, String method, RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		// We only have specific instant wins for a feature to process so the manager object is currently ignored
		// but may be used in the future
		//
		Set<InstantWin> instantWins = getInstantWinManager().getAllInstantWinsByFeature(item, pagination);
		List<ReturnType<UserInstantWin>> retVal = processInstantWinsInternal(user, instantWins);

		return retVal;
	}

	protected boolean isWinner(InstantWin instantWin, User user, int currentTimeSeconds)
	{
		// Can't trust the incoming parameter so we get the latest from the database
		//
		InstantWinRecord iwRecord = getInstantWinManager().getDao().getBySer(instantWin.getSer(), false);
		InstantWin dbInstantWin = new InstantWin(iwRecord);

		// Our preliminary winner limit check, since there's no point in
		// doing anything else if the limit on winners has already been reached
		//
		boolean isWinner = (dbInstantWin.getWinnerLimit() == 0) ||
					(dbInstantWin.getWinnerLimit() > 0 && dbInstantWin.getWinnerCount() < dbInstantWin.getWinnerLimit());

		if ( dbInstantWin.isCherryPick() )
		{
			// Only the cherry picked user will win
			//
			isWinner &= instantWin.getUser(false).equals(user);
		}

		boolean isRandomWinner = false;
		if ( isWinner && dbInstantWin.getPercentWin() > 0.0f )
		{
			if ( dbInstantWin.getPercentWin() >= 1.0f )
			{
				isRandomWinner = true;
			}
			else
			{
				// We might as well use the current time stamp that
				// we have instead of having the implementation do
				// another system call
				//
				Random random = new Random(currentTimeSeconds);

				isRandomWinner = Math.abs(random.nextFloat()) < dbInstantWin.getPercentWin();
			}
		}

		isWinner &= dbInstantWin.isValidAt(currentTimeSeconds);
		isWinner &= ( dbInstantWin.getUser(false).equals(user) || isRandomWinner);

		// Can't win twice
		//
		isWinner &= !hasWon(instantWin, user);

		// If we've made it this far, we only need to check the limits on winners before we can award
		// the winning status. However, we only increment the winner count if the limit is set so
		// the record is not locked unnecessarily
		//
		if ( isWinner && dbInstantWin.getWinnerLimit() > 0 )
		{
			// We need to lock the record to ensure that a winner is not selected when the
			// limit has been reached
			//
			iwRecord = getInstantWinManager().getDao().getBySer(instantWin.getSer(), true);
			dbInstantWin = new InstantWin(iwRecord);

			// Only record the row update if we have a winner and if there is a winner limit
			//
			if ( isWinner && dbInstantWin.getWinnerLimit() > 0 )
			{
				((InstantWinRecordDao)getInstantWinManager().getDao()).incrementWinnerCount(dbInstantWin.getSer());
			}
		}

		return isWinner;
	}

	protected List<ReturnType<UserInstantWin>> processInstantWinsInternal(User user, Set<InstantWin> instantWins)
	{
		int currTime = Utils.currentTimeSeconds();
		List<ReturnType<UserInstantWin>> retVal = new ArrayList<>();

		instantWins.stream().forEach((instantWin) ->
		{
			getLogger().debug("Considering instantWin " + instantWin);

			UserInstantWin userInstantWin = new UserInstantWin();
			userInstantWin.setInstantWin(instantWin);
			userInstantWin.setUser(user);
			userInstantWin.setTimeStampSeconds(currTime);
			userInstantWin.setDidWin( isWinner(instantWin, user, currTime) );

			// It's more likely that there are one or two instantWins associated with a given feature, so this
			// loop will be very short
			//
			if ( userInstantWin.getDidWin() )
			{
				getLogger().debug("User " + user.getUsername() + " is a winner for " + instantWin + " at " + currTime);

				// Record that we processed the instantWin
				//
				ReturnType<UserInstantWin> createVal = create(userInstantWin);

				retVal.add(createVal);
			}
			else
			{
				getLogger().debug("User " + user.getUsername() + " did not win " + instantWin + " at " + currTime);
				retVal.add(new ReturnType<>(userInstantWin));
			}
		});

		getLogger().debug("Processed instantWins = " + retVal);

		return retVal;
	}

	@Override
	public UserInstantWinRecordDao getDao()
	{
		return (UserInstantWinRecordDao) super.getDao();
	}

	public InstantWinManager getInstantWinManager()
	{
		return instantWinManager;
	}

	@Autowired
	public void setInstantWinManager(InstantWinManager instantWinManager)
	{
		this.instantWinManager = instantWinManager;
	}

	public TransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Autowired
	public void setTransactionManager(TransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}

}
