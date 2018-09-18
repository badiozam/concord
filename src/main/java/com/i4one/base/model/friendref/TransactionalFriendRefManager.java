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
package com.i4one.base.model.friendref;

import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseTransactionalManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class TransactionalFriendRefManager extends BaseTransactionalManager<FriendRefRecord,FriendRef> implements FriendRefManager
{
	private FriendRefManager friendRefManager;

	@Override
	public ReturnType<FriendRef> processReferral(User newUser)
	{
		ReturnType<FriendRef> retVal = getFriendRefManager().processReferral(newUser);
		FriendRef ref = retVal.getPost();

		if ( ref.exists() )
		{
			try
			{
				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(ref.getUser());
	
				// We use the "update" method as the transaction message for consistency across
				// messages being recorded
				//
				setTransactionDescr(t, "msg.friendRefManager.update.xaction.descr", "item", ref);
	
				t = createTransaction(retVal, t);
	
				retVal.put("transaction", t);
			}
			catch (Exception ex)
			{
				Errors errors = new Errors("friendRefManager.update", new ErrorMessage("msg.friendRefManager.update.txError", "Could not record transaction for $item: $ex.message", new Object[] { "item", ref, "ex", ex }, ex));
				throw errors;
			}
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<FriendRef> create(FriendRef item)
	{
		ReturnType<FriendRef> retVal = getFriendRefManager().create(item);

		if ( item.exists() )
		{
			try
			{
				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(item.getUser());
	
				setTransactionDescr(t, "msg.friendRefManager.create.xaction.descr", "item", item);
	
				t = createTransaction(retVal, t);

				retVal.put("transaction", t);
			}
			catch (Exception ex)
			{
				Errors errors = new Errors("friendRefManager.create", new ErrorMessage("msg.friendRefManager.create.txError", "Could not record transaction for $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));
				throw errors;
			}
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<FriendRef> update(FriendRef item)
	{
		ReturnType<FriendRef> retVal = getFriendRefManager().update(item);

		try
		{
			// Create a new transaction with the details of what was done
			//
			Transaction t = newTransaction(item.getUser());

			setTransactionDescr(t, "msg.friendRefManager.update.xaction.descr", "item", item);

			t = createTransaction(retVal, t);

			retVal.put("transaction", t);
		}
		catch (Exception ex)
		{
			Errors errors = new Errors("friendRefManager.update", new ErrorMessage("msg.friendRefManager.update.txError", "Could not record transaction for $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));
			throw errors;
		}

		return retVal;
	}

	@Override
	public Manager<FriendRefRecord, FriendRef> getImplementationManager()
	{
		return getFriendRefManager();
	}

	@Override
	public Set<FriendRef> getFriendsByUser(User user, PaginationFilter pagination)
	{
		return getFriendRefManager().getFriendsByUser(user, pagination);
	}

	@Override
	public FriendRef getReferral(int id, User user)
	{
		return getFriendRefManager().getReferral(id, user);
	}

	@Override
	public FriendRef getReferrer(User user)
	{
		return getFriendRefManager().getReferrer(user);
	}

	@Override
	public FriendRefSettings getSettings(SingleClient client)
	{
		return getFriendRefManager().getSettings(client);
	}

	@Override
	public ReturnType<FriendRefSettings> updateSettings(FriendRefSettings settings)
	{
		return getFriendRefManager().updateSettings(settings);
	}

	@Override
	public PaginableRecordTypeDao<FriendRefRecord> getDao()
	{
		return getFriendRefManager().getDao();
	}

	public FriendRefManager getFriendRefManager()
	{
		return friendRefManager;
	}

	@Autowired
	@Qualifier("base.PrivilegedFriendRefManager")
	public void setFriendRefManager(FriendRefManager friendRefManager)
	{
		this.friendRefManager = friendRefManager;
	}

}
