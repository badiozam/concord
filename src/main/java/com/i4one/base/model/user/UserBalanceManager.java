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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * This interface handles storing, updating and retrieving balances for users.
 *
 * @author Hamid Badiozamani
 */
public interface UserBalanceManager extends Manager<UserBalanceRecord, UserBalance>
{
	/**
	 * Increment a user's balance by a given amount. If the user's balance does
	 * not exist, one is created and set to the given amount. If the balance
	 * is being deducted below zero, no action is taken and an empty value
	 * is returned.
	 * 
	 * @param userBalance The user balance to update
	 * @param amount The amount to increase the balance by
	 * 
	 * @return The result of the create/update of the user's balance object, or
	 * 	a non-existent object if no action was taken.
	 */
	public ReturnType<UserBalance> increment(UserBalance userBalance, int amount);

	/**
	 * Gets a specific balance for a specific user.
	 *
	 * @param user The user whose balance to retrieve
	 * @param balance The balance to lookup
	 *
	 * @return The balance of the user or a non-existent entry
	 */
	public UserBalance getUserBalance(User user, Balance balance);

	/**
	 * Gets a specific balance for a specific user while ensuring that the record cannot
	 * be updated by a separate thread. Furthermore, if the balance record does not
	 * exist, creates a new one.
	 *
	 * @param userBalance The user balance to retrieve. The user, balance, (and
	 * 	optionally the create time) need to be set to create the record if
	 * 	it doesn't exist.
	 *
	 * @return The existing balance of the user or a new balance with a total of 0
	 */
	public UserBalance getUserBalanceForUpdate(UserBalance userBalance);

	/**
	 * Select a random user that has at some point in time
	 * obtained a balance of the given currency. 
	 * 
	 * @param balance The currency that the user must have obtained
	 * 
	 * @return A random user balances with the given criteria or a
	 * 	non-existent entry if there are no users matching the given balance.
	 */
	public UserBalance randomUserBalance(Balance balance);
	
	/**
	 * Attempts to select a number of weighted random users that have at some
	 * point in time obtained a balance of the given currency. Higher balance
	 * are more likely to be selected. There is no guarantee that the count
	 * requested can be supplied and therefore the return value's size will
	 * always be less than or equal to the count.
	 * 
	 * @param balance The currency that the user must have obtained
	 * @param count The number of weighted random user balances to obtain
	 * 
	 * @return A (potentially empty) set of at most count random user balances.
	 */
	public Set<UserBalance> weightedRandomUserBalances(Balance balance, int count);

	/**
	 * Gets all of a user's balances
	 *
	 * @param user The user whose balances to retrieve
	 * @param pagination The pagination/ordering information
	 *
	 * @return A (potentially empty) list of the user's balances
	 */
	public Set<UserBalance> getUserBalances(User user, PaginationFilter pagination);

	/**
	 * Gets all user balance entries for a given balance
	 *
	 * @param balance The balance to look up
	 * @param pagination The pagination/ordering information
	 *
	 * @return A (potentially empty) list of all user balance records for the given balance
	 */
	public Set<UserBalance> getUserBalances(Balance balance, PaginationFilter pagination);
}
