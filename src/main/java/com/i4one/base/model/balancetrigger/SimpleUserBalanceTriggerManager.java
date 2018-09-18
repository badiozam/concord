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
package com.i4one.base.model.balancetrigger;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.transaction.TransactionManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.web.RequestState;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.UserBalanceTriggerManager")
public class SimpleUserBalanceTriggerManager extends BaseActivityManager<UserBalanceTriggerRecord, UserBalanceTrigger, BalanceTrigger> implements UserBalanceTriggerManager
{
	private TransactionManager transactionManager;
	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;
	private BalanceTriggerManager balanceTriggerManager;
	private RequestState requestState;

	@Override
	public UserBalanceTrigger emptyInstance()
	{
		return new UserBalanceTrigger();
	}

	@Override
	public Set<UserBalanceTrigger> getByUser(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByUser(user.getSer(), pagination));
	}

	@Override
	public Set<UserBalanceTrigger> getByBalanceTrigger(BalanceTrigger balanceTrigger, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByItem(balanceTrigger.getSer(), pagination));
	}

	@Override
	public Set<UserBalanceTrigger> getAllUserBalanceTriggers(BalanceTrigger balanceTrigger, User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAll(balanceTrigger.getSer(), user.getSer(), pagination));
	}

	@Override
	public boolean isEligible(BalanceTrigger balanceTrigger, User user)
	{
		return isEligibleAt(balanceTrigger, user, getRequestState().getRequest().getTimeInSeconds());
	}

	@Override
	public boolean isEligibleAt(BalanceTrigger balanceTrigger, User user, int timeStamp)
	{
		// If the trigger has expired then you can't play
		//
		if ( balanceTrigger.isValidAt(timeStamp) )
		{
			int maxUserUsage = balanceTrigger.getMaxUserUsage();

			SimplePaginationFilter pagination = new SimplePaginationFilter();
			pagination.setPerPage(maxUserUsage);
			pagination.setOrderBy("timestamp DESC");

			if ( maxUserUsage == 0 )
			{
				// If this value is 0, we're to ignore it completely. But we
				// need to have at least the last time the user processed this
				// trigger which is why we set this to 1 here.
				//
				pagination.setPerPage(1);
			}

			Set<UserBalanceTrigger> lastPlayed = getAllUserBalanceTriggers(balanceTrigger, user, pagination);
			if ( lastPlayed.isEmpty() )
			{
				// Eligible if the user hasn't played at all
				//
				return true;
			}
			else if ( maxUserUsage > 0 && lastPlayed.size() >= maxUserUsage )
			{
				// The maximum number of times this trigger can be processed
				// for this user has been reached
				//
				return false;
			}
			else
			{
				int lastEligibleSeconds = lastEligibleTimestamp(balanceTrigger, timeStamp);
				int lastPlayedSeconds = lastPlayed.iterator().next().getTimeStampSeconds();

				// If the last time the user played is before or equal to the last eligible seconds, then the
				// user is eligible to play again now.
				//
				return lastPlayedSeconds <= lastEligibleSeconds;
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public int eligibleInSeconds(BalanceTrigger balanceTrigger, User user) 
	{
		return eligibleInSeconds(balanceTrigger, user, getRequestState().getRequest().getTimeInSeconds());
	}

	@Override
	public int eligibleInSeconds(BalanceTrigger balanceTrigger, User user, int fromTimeStamp) 
	{
		SimplePaginationFilter pagination = new SimplePaginationFilter();
		pagination.setPerPage(1);
		pagination.setOrderBy("timestamp DESC");

		Set<UserBalanceTrigger> lastPlayed = getAllUserBalanceTriggers(balanceTrigger, user, pagination);

		if ( !lastPlayed.isEmpty() )
		{
			if ( balanceTrigger.getSynced() )
			{
				// See which cycle we're in right now
				//
				int totalFrequencyCycles = (fromTimeStamp - balanceTrigger.getStartTimeSeconds()) / balanceTrigger.getFrequency();

				// Increment that by one to see when the next cycle lands
				//
				totalFrequencyCycles++;

				int nextCycleSeconds = balanceTrigger.getStartTimeSeconds() + (balanceTrigger.getFrequency() * totalFrequencyCycles);

				return nextCycleSeconds - fromTimeStamp;
			}
			else
			{
				// The frequency has not been reached therefore it must be greater than the the number of seconds that has passed
				// since the trigger was set off
				//
				int lastPlayedSeconds = lastPlayed.iterator().next().getTimeStampSeconds();

				return balanceTrigger.getFrequency() - (fromTimeStamp - lastPlayedSeconds);
			}
		}
		else
		{
			// They're eligible to play now
			//
			return 0;
		}
	}

	/**
	 * Calculates the last eligible time stamp. This is the last time at which if a user
	 * processed the trigger they would be eligible again at the asOf timestamp. Meaning
	 * if the return value is X and the last time the user played is greater than X, then
	 * the user is not eligible to play again. Conversely, if the last time the user played
	 * is less than or equal to X then, the user is eligible to play.
	 * 
	 * @param balanceTrigger The trigger to test
	 * @param timeStamp The time to use for calculating the last eligible time stamp
	 * 
	 * @return The last eligible time stamp.
	 */
	protected int lastEligibleTimestamp(BalanceTrigger balanceTrigger, int timeStamp)
	{
		if ( balanceTrigger.getSynced() )
		{
			// Example:
			//
			// 	start seconds = 3, frequency = 10
			//	Means we want users to be eligible at the following points:
			//	3, 13, 23, ...
			//
			//	timeStamp = 5
			//	totalFrequencyCycles = 5 / 10 = 0
			//	lastCycleSeconds = 3 + (5 * 0) = 3
			//	Which means if the user played
			//		at any time since the start time of 3
			//		up until 5 they would not be eligible
			//
			//	timestamp = 15
			//	totalFrequencyCycles = (15 - 3) / 10 = 1
			//	lastCycleSeconds = 3 + (10 * 1) = 13
			//	Which means if the user played	at any time since
			//		time 13 they would not be eligible
			//
			//	timestamp = 27
			//	totalFrequencyCycles = (27 - 3) / 10 = 2
			//	lastCycleSeconds = 3 + (10 * 2) = 23
			//	Which means if the user played	at any time since
			//		time 23 they would not be eligible
			// 
			int totalFrequencyCycles = (timeStamp - balanceTrigger.getStartTimeSeconds()) / balanceTrigger.getFrequency();
			int lastCycleSeconds = balanceTrigger.getStartTimeSeconds() + (balanceTrigger.getFrequency() * totalFrequencyCycles);

			return lastCycleSeconds;
		}
		else
		{
			// If the user has played at any time after the given time less
			// the frequency, they're not eligible. 
			//
			// Example:
			//
			//	start seconds = 3, frequency = 1
			//	Means we want the user to be eligible at the following points:
			//	3, 4, 5, 6, ...
			//
			//	timeStamp = 5
			//	lastEligibleTime = 5 - 1  = 4
			//	Which means if the user last played at 4, he'd be eligible at 5
			//
			//	timeStamp = 6
			//	lastEligibleTime = 6 - 1 = 5
			//	Which means if the user last played at 5, he'd be eligible at 6
			//
			return timeStamp - balanceTrigger.getFrequency();
		}
	}
	
	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<UserBalanceTrigger>> processTriggers(User user, SingleClient client, Manager<?, ?> manager, String method, RecordTypeDelegator<?> item, PaginationFilter pagination)
	{
		// First process the manager method triggers
		//
		Set<BalanceTrigger> triggers = getBalanceTriggerManager().getAllTriggersByManager(manager, method, new ClientPagination(client, pagination));
		convertWildCards(triggers, client);
		List<ReturnType<UserBalanceTrigger>> retVal = processTriggersInternal(user, triggers);

		// Then the specific item triggers
		//
		triggers = getBalanceTriggerManager().getAllTriggersByFeature(item, new ClientPagination(client, pagination));
		convertWildCards(triggers, client);
		retVal.addAll(processTriggersInternal(user, triggers));

		return retVal;
	}

	protected void convertWildCards(Set<BalanceTrigger> balanceTriggers, SingleClient client)
	{
		for ( BalanceTrigger balanceTrigger : balanceTriggers)
		{
			if ( !balanceTrigger.getBalance().exists() && 
				balanceTrigger.getBalance().getOwner().getSer() == 0 &&
				balanceTrigger.getBalance().getOwner().getFullTableName().equals("base.clients"))
			{
				// This calls for conversion to the given client's default balance
				//
				balanceTrigger.setBalance(getBalanceManager().getDefaultBalance(client));
			}
		}
	}

	protected List<ReturnType<UserBalanceTrigger>> processTriggersInternal(User user, Set<BalanceTrigger> triggers)
	{
		int currTime = getRequestState().getRequest().getTimeInSeconds();
		List<ReturnType<UserBalanceTrigger>> retVal = new ArrayList<>();

		triggers.stream().forEach((trigger) ->
		{
			getLogger().debug("Considering trigger " + trigger);

			// It's more likely that there are one or two triggers associated with a given feature, so this
			// loop will be very short. If the loop was longer, it might be worth considering getting all
			// of the user's triggers and filtering the array in memory rather than hitting the database
			// on each call
			//
			if ( isEligibleAt(trigger, user, currTime))
			{
				getLogger().debug("User " + user.getUsername() + " is eligible for " + trigger + " at " + currTime);

				UserBalanceTrigger userBalanceTrigger = new UserBalanceTrigger();
				userBalanceTrigger.setBalanceTrigger(trigger);
				userBalanceTrigger.setUser(user);
				userBalanceTrigger.setTimeStampSeconds(currTime);

				// Here we update the user's balance which may or may not get us a transaction record
				// depending on the type of manager we have
				//
				ReturnType<UserBalance> updateStatus = getUserBalanceManager().increment(
					new UserBalance(user, trigger.getBalance(), userBalanceTrigger.getTimeStampSeconds()),
					trigger.getAmount());

				Transaction transaction = (Transaction)updateStatus.get("transaction");
				if ( transaction != null )
				{
					if ( trigger.isExclusive() )
					{
						// Exclusive triggers basically get the same description as their
						// invoker and we leave that up  to the TransactionalManager to
						// handle
						//
						transaction.setDescr(new IString(""));
					}
					else
					{
						transaction.setDescr(trigger.getTitle());
					}

					getTransactionManager().update(transaction);
					userBalanceTrigger.setTransaction(transaction);
				}

				// Record that we processed the trigger
				//
				ReturnType<UserBalanceTrigger> createVal = create(userBalanceTrigger);
				createVal.addChain(getUserBalanceManager(), "increment", updateStatus);

				retVal.add(createVal);
			}
			else
			{
				getLogger().debug("User " + user.getUsername() + " is not eligible for " + trigger + " at " + currTime);
			}
		});

		getLogger().debug("Processed triggers = " + retVal);

		return retVal;
	}

	@Override
	public UserBalanceTriggerRecordDao getDao()
	{
		return (UserBalanceTriggerRecordDao) super.getDao();
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
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

	public TransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Autowired
	public void setTransactionManager(TransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
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
}
