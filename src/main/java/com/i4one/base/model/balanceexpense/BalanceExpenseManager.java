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

import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.Manager;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface BalanceExpenseManager extends Manager<BalanceExpenseRecord, BalanceExpense>
{
	/**
	 * Update and associate the given expenses with the incoming item. Any new expenses
	 * are created, any existing ones will be updated. All expenses will be associated
	 * with the incoming item. All previous associations with other items for any of
	 * the incoming expenses is preserved. Only expenses with amounts not equaling zero
	 * will be created.
	 * 
	 * @param item The item to associate the expenses with
	 * @param expenses The expenses to update and associate with the item.
	 * 
	 * @return The list of create/update statuses for each of the incoming expenses
	 */
	public List<ReturnType<BalanceExpense>> updateBalanceExpenses(SingleClientType<?> item, Collection<BalanceExpense> expenses);

	/**
	 * Link the incoming expense to the given item.
	 * 
	 * @param item The item to associate the expense with
	 * @param expenses The expense to associate
	 * 
	 * @return True if the item was newly associated, false otherwise
	 */
	public boolean associate(SingleClientType<?> item, BalanceExpense expenses);

	/**
	 * Unlink the incoming expense from the given item. All other associations remain intact.
	 * 
	 * @param item The item to dissociate the expense from
	 * @param expenses The expense to dissociate
	 * 
	 * @return True if the item was previously associated and was properly dissociated, false otherwise
	 */
	public boolean dissociate(SingleClientType<?> item, BalanceExpense expenses);

	/**
	 * Get all expenses for a particular feature item.
	 * 
	 * @param item The item for which the expense is associated
	 * 
	 * @return A (potentially empty) list of expenses associated with the given item
	 */
	public Set<BalanceExpense> getAllExpensesByFeature(SingleClientType<?> item);
}