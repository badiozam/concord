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
package com.i4one.base.model.balance;

import com.i4one.base.dao.Dao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface BalanceRecordDao extends Dao<BalanceRecord>
{
	/**
	 * Retrieve the balance of a certain type for the given reference table and id
	 *
	 * @param tableid The unique identifier of the table
	 * @param tablename The table that contains that unique identifier
	 * @param pagination The qualifier/sorting info
	 *
	 * @return The balance or null if not found
	 */
	BalanceRecord getBalance(int tableid, String tablename, PaginationFilter pagination);

	/**
	 * Retrieve all balances (using pagination to apply filters).
	 *
	 * @param pagination The qualifier/sorting info
	 * 
	 * @return A potentially empty list of all balances for the given item
	 */
	List<BalanceRecord> getAllBalances(PaginationFilter pagination);
}