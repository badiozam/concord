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

import com.i4one.base.model.ActivityType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface UserBalanceExpenseManager extends ActivityManager<UserBalanceExpenseRecord, UserBalanceExpense, BalanceExpense>
{
	/**
	 * Get all expenses by user.
	 * 
	 * @param user The user whose expenses we are to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return  The (potentially empty) list of expenses for the user
	 */
	public Set<UserBalanceExpense> getByUser(User user, PaginationFilter pagination);

	/**
	 * Get all instances of the given expense processed by the given user.
	 * 
	 * @param balanceExpense The expense to look up
	 * @param user The user to look up
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return All instances of this expense processed for this user
	 */
	public Set<UserBalanceExpense> getAllUserBalanceExpenses(BalanceExpense balanceExpense, User user, PaginationFilter pagination);

	/**
	 * Process the expenses associated with a given feature for a given user.
	 * 
	 * @param user The user to process
	 * @param item The item that the manager's method is operating on
	 * @param activity The activity item that is triggering these expenses
	 * @param pagination The pagination/sort ordering information to use for
	 * 	retrieving the balance expenses
	 * 
	 * @param <T> The attachee type
	 * 
	 * @return A (potentially empty) list of all of the expense instances that were processed
	 */
	public <T extends SingleClientType<?>> List<ReturnType<UserBalanceExpense>> processExpenses(User user, T item, ActivityType<?,T> activity, PaginationFilter pagination);

	/**
	 * Reverts any existing expenses associated with a given feature for a given activity record.
	 * If successful, the expense records are removed.
	 * 
	 * @param item The item that the manager's method is operating on
	 * @param activity The activity item that originally triggered the expenses
	 * 
	 * @param <T> The attachee type
	 * 
	 * @return A (potentially empty) list of all of the user balances that were updated
	 * 	as a result of the reversion.
	 */
	public <T extends SingleClientType<?>> List<ReturnType<UserBalance>> revertExpenses(T item, ActivityType<?, T> activity);
}