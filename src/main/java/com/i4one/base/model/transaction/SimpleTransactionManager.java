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
package com.i4one.base.model.transaction;

import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("transactionManager")
public class SimpleTransactionManager extends BaseSimpleManager<TransactionRecord, Transaction> implements TransactionManager 
{
	private UserManager userManager;

	@Override
	public Transaction emptyInstance()
	{
		return new Transaction();
	}

	@Override
	public Set<Transaction> getRootTransactions(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getRootTransactionsByUser(user.getSer(), pagination));
	}

	@Override
	public Set<Transaction> getTransactions(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getTransactionsByUser(user.getSer(), pagination));
	}

	@Override
	public Set<Transaction> getTransactions(Transaction parent, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getTransactionsByParent(parent.getSer(), pagination));
	}

	@Override
	public void setParentTransaction(Transaction child, Transaction parent)
	{
		getLogger().trace("Object is a transaction checking existing parent");

		if ( child.exists() && !child.getParent().exists() )
		{
			getLogger().trace("Transaction has no existing parent, setting the parent to " + parent);

			// Might be useful to have these saved for debugging purposes
			//
			//tx.setSourceIP(parentTransaction.getSourceIP());
			//tx.setTimestampSeconds(parentTransaction.getTimestampSeconds());
			child.setParent(parent);

			getDao().updateBySer(child.getDelegate());
		}
	}

	@Override
	public TransactionRecordDao getDao()
	{
		return (TransactionRecordDao)super.getDao();
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

}
