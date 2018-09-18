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

import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface TransactionManager extends Manager<TransactionRecord, Transaction>
{
	/**
	 * Get all root transactions for the given user. Root transactions are those
	 * that do not have a parent, or whose parent belong to another user.
	 * 
	 * @param user The user whose transactions we are to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of transactions for the given user
	 */
	public Set<Transaction> getRootTransactions(User user, PaginationFilter pagination);

	/**
	 * Get all transactions for the given user
	 * 
	 * @param user The user whose transactions we are to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of transactions for the given user
	 */
	public Set<Transaction> getTransactions(User user, PaginationFilter pagination);

	/**
	 * Get a list of transactions that belong to a particular parent
	 * 
	 * @param parent The parent transaction
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of transactions for the given transaction
	 */
	public Set<Transaction> getTransactions(Transaction parent, PaginationFilter pagination);

	/**
	 * Attach one transaction to another in a parent-child relationship
	 * 
	 * @param child The child transaction
	 * @param parent The parent transaction
	 */
	public void setParentTransaction(Transaction child, Transaction parent);
}
