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

import com.i4one.base.dao.ClientRecordTypeDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface TransactionRecordDao extends ClientRecordTypeDao<TransactionRecord>
{
	/**
	 * Get all transactions of a certain type for the given user
	 *
	 * @param balid The balance type of transaction to retrieve
	 * @param userid The user whose transactions to retrieve
	 * @param pagination The qualifier/sorting info.
	 *
	 * @return A (potentially empty) list of all transactions for the given user
	 * 	relating to the item.
	 */
	List<TransactionRecord> getTransactions(int balid, int userid, PaginationFilter pagination);

	/**
	 * Get all root transactions for the given user.
	 *
	 * @param userid The user whose transactions to retrieve
	 * @param pagination The qualifier/sorting info.
	 *
	 * @return A (potentially empty) list of all transactions for the given user
	 */
	List<TransactionRecord> getRootTransactionsByUser(int userid, PaginationFilter pagination);

	/**
	 * Get all transactions for the given user
	 *
	 * @param userid The user whose transactions to retrieve
	 * @param pagination The qualifier/sorting info.
	 *
	 * @return A (potentially empty) list of all transactions for the given user
	 */
	List<TransactionRecord> getTransactionsByUser(int userid, PaginationFilter pagination);

	/**
	 * Get all transactions for the given parent
	 *
	 * @param parentid The parent whose transactions to retrieve
	 * @param pagination The qualifier/sorting info.
	 *
	 * @return A (potentially empty) list of transactions for the given parent transaction
	 */
	List<TransactionRecord> getTransactionsByParent(int parentid, PaginationFilter pagination);
}