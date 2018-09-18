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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.classifier.ClassificationReport;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleUserBalanceManager extends BaseSimpleManager<UserBalanceRecord,UserBalance> implements UserBalanceManager
{
	@Override
	public UserBalance getUserBalance(User user, Balance balance)
	{
		// We make an exception for the default balance of a client since
		// we're likely going to need that in the future (as opposed to say
		// a raffle that someone may not want to enter). Furthemore, the
		// default balance is used as a means to determine which user "belongs"
		// to a given client
		//
		if ( balance.isDefaultBalance() )
		{
			return getUserBalanceForUpdate(new UserBalance(user, balance, Utils.currentTimeSeconds()));
		}
		else
		{
			UserBalanceRecord record = getDao().getUserBalance(balance.getDelegate().getSer(), user.getSer());
	
			UserBalance retVal = new UserBalance();
			if ( record != null )
			{
				retVal.setOwnedDelegate(record);
			}
			else
			{
				// This is to show that we have a non-existent balance
				// of this type to the caller. If the user did have a
				// balance record, the caller would expect this item to
				// exist which is why we set it here as well.
				//
				retVal.setBalance(balance);
			}
	
			return retVal;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<UserBalance> increment(UserBalance userBalance, int amount)
	{
		// Create the balance if it doesn't already exist or look it up by serial number
		//
		UserBalance forUpdateUserBalance = getUserBalanceForUpdate(userBalance);

		int newTotal = forUpdateUserBalance.getTotal() + amount;
		if ( newTotal < 0 )
		{
			ReturnType<UserBalance> nsf = new ReturnType<>();

			forUpdateUserBalance.setSer(0);
			nsf.setPre(forUpdateUserBalance);
			nsf.setPost(forUpdateUserBalance);

			return nsf;
		}
		else
		{
			if ( forUpdateUserBalance.getUpdateTimeSeconds() > userBalance.getUpdateTimeSeconds() )
			{
				throw new Errors(getInterfaceName() + ".increment", new ErrorMessage("msg." + getInterfaceName() + ".increment.invalidTime", "Invalid update time $item.updateTimeSeconds", new Object[] { "item", userBalance}));
			}
			else
			{
				if ( userBalance.getUpdateTimeSeconds() == 0 )
				{
					throw new IllegalArgumentException("Invalid update time");
				}
	
				UserBalanceRecord newBalRecord = getDao().increment(forUpdateUserBalance.getSer(),
					amount,
					userBalance.getUpdateTimeSeconds());

				UserBalance newBalance = new UserBalance(newBalRecord);

				ReturnType<UserBalance> retVal = new ReturnType<>();
				retVal.setPre(forUpdateUserBalance);
				retVal.setPost(newBalance);

				return retVal;
			}
		}
	}

	@Override
	public ReturnType<UserBalance> create(UserBalance userBalance)
	{
		throw new UnsupportedOperationException("Direct balance create unavailable, use getUserBalanceForUpdate(..) method instead.");
	}

	@Override
	public ReturnType<UserBalance> update(UserBalance userBalance)
	{
		throw new UnsupportedOperationException("Direct balance update unavailable, use increment(..) method instead.");
	}

	@Transactional(readOnly = false)
	@Override
	public UserBalance getUserBalanceForUpdate(UserBalance userBalance)
	{
		return getUserBalanceForUpdate(userBalance, 0);
	}

	@Override
	public UserBalance randomUserBalance(Balance balance)
	{
		return convertDelegate(getDao().random(balance.getSer()));
	}

	@Override
	public Set<UserBalance> weightedRandomUserBalances(Balance balance, int count)
	{
		return convertDelegates(getDao().weightedRandoms(balance.getSer(), count));
	}

	private UserBalance getUserBalanceForUpdate(UserBalance userBalance, int startBalance)
	{
		if ( userBalance.exists() )
		{
			UserBalanceRecord lockedRecord = lock(userBalance);
			return new UserBalance(lockedRecord);
		}
		else
		{
			User user = userBalance.getUser(false);
			Balance balance = userBalance.getBalance(false);
	
			if ( user.exists() && balance.exists() )
			{
				UserBalanceRecord record = getDao().getUserBalance(balance.getSer(), user.getSer());
	
				// Create a new record with a zero balance if none exists
				//
				if ( record == null )
				{
					record = new UserBalanceRecord();
					record.setUserid(user.getSer());
					record.setBalid(balance.getSer());
					record.setTotal(startBalance);
					record.setCreatetime(userBalance.getCreateTimeSeconds());
					record.setUpdatetime(userBalance.getCreateTimeSeconds());

					if ( record.getCreatetime() == 0 )
					{
						throw new IllegalArgumentException("Invalid create time");
					}
	
					// Since the record doesn't exist, it's not visible
					// to any outside threads when we create which means
					// that locking is unnecessary
					//
					getDao().insert(record);
				}
				else
				{
					// Record exists, lock it for update before returning
					//
					record = getDao().getBySer(record.getSer(), true);
				}
	
				return new UserBalance(record);
			}
			else
			{
				return new UserBalance();
			}
		}
	}

	@Override
	public <R extends Object> ClassificationReport<UserBalanceRecord, R> getReport(UserBalance item, ClassificationReport<UserBalanceRecord, R> report, PaginationFilter pagination)
	{
		getDao().processReport(item.getDelegate(), report, pagination);
		return report;
	}

	@Override
	public Set<UserBalance> getUserBalances(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getUserBalancesByUserid(user.getSer(), pagination));
	}

	@Override
	public Set<UserBalance> getUserBalances(Balance balance, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getUserBalancesByBalid(balance.getSer(), pagination));
	}

	@Override
	public UserBalanceRecordDao getDao()
	{
		return (UserBalanceRecordDao) super.getDao();
	}

	@Override
	public UserBalance emptyInstance()
	{
		return new UserBalance();
	}
}
