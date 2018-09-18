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

import com.i4one.base.dao.Dao;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.manager.BaseTransactionalManager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.TransactionalManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class uses the Decorator pattern to record transactional behavior
 * for write-methods of a user's balance
 *
 * @author Hamid Badiozamani
 */
@Service("base.UserBalanceManager")
public class TransactionalUserBalanceManager extends BaseTransactionalManager<UserBalanceRecord, UserBalance> implements UserBalanceManager, TransactionalManager<UserBalanceRecord, UserBalance>
{
	private UserBalanceManager userBalanceManager;

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

	@Transactional(readOnly = false)
	@Override
	public ReturnType<UserBalance> increment(UserBalance userBalance, int amount)
	{
		userBalance.setUpdateTimeSeconds(getRequestState().getRequest().getTimeInSeconds());

		ReturnType<UserBalance> retVal = getUserBalanceManager().increment(userBalance, amount);
		UserBalance post = retVal.getPost();

		// For user balances, even if the administrator updated the balance the user should see a transaction
		//
		//if ( adminHistory == null || !adminHistory.exists() )
		if ( post.exists() )
		{
			try
			{
				UserBalanceRecord preRecord = (retVal.getPre()).getDelegate();
				UserBalanceRecord postRecord = (retVal.getPost()).getDelegate();

				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(post.getUser());
				t.setBalance(post.getBalance());
				t.setPrevBalance(preRecord.getTotal());
				t.setNewBalance(postRecord.getTotal());
				t.setAmountTransacted(postRecord.getTotal() - preRecord.getTotal());

				// This is meant to be overridden by whatever manager calls us
				//
				setTransactionDescr(t, "msg.userBalanceManager.update.xaction.descr");

				t = createTransaction(retVal, t);

				retVal.put("transaction", t);

				getLogger().debug("Recorded transaction: " + t);
			}
			catch (Exception e)
			{
				Errors errors = new Errors("userBalanceManager.update", new ErrorMessage("msg.userBalanceManager.update.txError", "Could not record transaction for $userBalance", new Object[] { "userBalance", userBalance }, e));
				throw errors;
			}

			// We could put this functionality in a FriendRefTransactionalUserBalanceManager, but
			// that seems like overkill so we handle the special case here. Note that we do this
			// after the original user's transaction has been recorded to avoid transactions from
			// different users having a parent/child relationship (though that might actually be
			// beneficial).
			//
			ReturnType<UserBalance> trickle = (ReturnType<UserBalance>) retVal.get(FriendRefUserBalanceManager.TRICKLE);
			if ( trickle != null )
			{
				UserBalanceRecord preRecord = (trickle.getPre()).getDelegate();
				UserBalanceRecord postRecord = (trickle.getPost()).getDelegate();

				// If there was a trickle, it belongs to the referrer
				//
				Transaction t = newTransaction(trickle.getPost().getUser());
				t.setBalance(trickle.getPost().getBalance(false));
				t.setPrevBalance(preRecord.getTotal());
				t.setNewBalance(postRecord.getTotal());
				t.setAmountTransacted(postRecord.getTotal() - preRecord.getTotal());

				t.setParent((Transaction) retVal.get("transaction"));

				setTransactionDescr(t, "msg.userBalanceManager.trickle.xaction.descr", "trickle", trickle, "friend", post.getUser());

				t = createTransaction(trickle, t);

				trickle.put("transaction", t);

				getLogger().debug("Recorded transaction: " + t);
			}
		}
		/*
		else
		{
			getLogger().debug("Skipping transaction creation since admin history record was created:" + adminHistory);
			getLogger().debug("Skipping transaction creation since the balance was not updated :" + userBalance);
		}
		 * 
		 */

		return retVal;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	@Qualifier("base.HistoricalUserBalanceManager")
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

	@Override
	public PrivilegedManager<UserBalanceRecord, UserBalance> getImplementationManager()
	{
		return (PrivilegedManager<UserBalanceRecord, UserBalance>) getUserBalanceManager();
	}

	@Override
	public Dao<UserBalanceRecord> getDao()
	{
		return getUserBalanceManager().getDao();
	}
}
