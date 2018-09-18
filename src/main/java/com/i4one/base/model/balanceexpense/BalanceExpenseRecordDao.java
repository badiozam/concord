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
package com.i4one.base.model.balanceexpense;

import com.i4one.base.dao.Dao;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface BalanceExpenseRecordDao extends Dao<BalanceExpenseRecord>
{
	/**
	 * Look up balance expenses by a particular feature.
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * 
	 * @return A (potentially empty) list of expenses for the feature
	 */
	public List<BalanceExpenseRecord> getByFeature(String feature, int id);

	/**
	 * Look up a balance expense by a particular feature and balance.
	 * 
	 * @param feature The feature (i.e. table name) 
	 * @param id The id of the feature (i.e. ser)
	 * @param balanceid The balance id of the item
	 * 
	 * @return The balance expense or null if not found
	 */
	public BalanceExpenseRecord getByFeature(String feature, int id, int balanceid);
}