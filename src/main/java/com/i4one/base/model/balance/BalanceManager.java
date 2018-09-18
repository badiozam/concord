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

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface BalanceManager extends Manager<BalanceRecord,Balance>
{
	/**
	 * Get a balance for the specified object of a given type
	 * 
	 * @param type The object of the given type for which to retrieve the balance
	 * 
	 * @return The balance object for the specified object
	 */
	public Balance getBalance(SingleClientType<?> type);

	/**
	 * Get all of the balances for a specific client
	 * 
	 * @param client The client for which to retrieve all balances for
	 * @param pagination The pagination to use for sorting
	 * 
	 * @return A (potentially empty) list of all balances for the given client
	 */
	public Set<Balance> getAllBalances(SingleClient client, PaginationFilter pagination);

	/**
	 * Get the default balance for a specific leaf client. This is typically the "points"
	 * that represents the universal currency for the client. 
	 * 
	 * @param client The client for which to retrieve the default balance
	 * 
	 * @return The default balance for the client
	 */
	public Balance getDefaultBalance(SingleClient client);

	/**
	 * Get the object that this balance refers to.
	 * 
	 * @param balance The balance to resolve.
	 * 
	 * @return The object that the balance refers to
	 */
	public RecordTypeDelegator<?> getAttached(Balance balance);

	/**
	 * Remove an attachment resolver from the list to be consulted when returning the
	 * type associated with a balance.
	 * 
	 * @param resolver The resolver to be removed
	 */
	public void removeBalanceAttachmentResolver(BalanceAttachmentResolver<?,?> resolver );

	/**
	 * Set an attachment resolver to be used to return the type associated with a
	 * particular balance.
	 * 
	 * @param resolver The resolver to be consulted when resolving a balance type
	 */
	public void setBalanceAttachmentResolver(BalanceAttachmentResolver<?,?> resolver);
}
