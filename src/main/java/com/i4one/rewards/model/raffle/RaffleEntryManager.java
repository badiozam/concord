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
package com.i4one.rewards.model.raffle;

import com.i4one.base.model.manager.activity.QuantifiedActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface RaffleEntryManager extends QuantifiedActivityManager<RaffleEntryRecord, RaffleEntry, Raffle>
{
	/**
	 * Retrieve all of the raffle entries for a given user.
	 * 
	 * @param user The raffle user to retrieve entries for
	 * @param pagination The pagination filter for sorting/limiting
	 * 
	 * @return A (potentially empty) set of all raffle entries for the
	 * 	given raffle.
	 */
	public Set<RaffleEntry> getAllRaffleEntries(User user, PaginationFilter pagination);

	/**
	 * Retrieve all of the raffle entries for a given raffle item.
	 * 
	 * @param raffle The raffle item to retrieve entries for
	 * @param pagination The pagination filter for sorting/limiting
	 * 
	 * @return A (potentially empty) set of all raffle entries for the
	 * 	given raffle.
	 */
	public Set<RaffleEntry> getAllRaffleEntries(Raffle raffle, PaginationFilter pagination);

	/**
	 * Retrieve all of a user's entries for a given raffle item.
	 * 
	 * @param raffle The raffle item to look up
	 * @param user The user who may have entries the item
	 * @param pagination the value of pagination
	 * 
	 * @return A (potentially empty) set of all raffle entries for the
	 * 	given user in the current raffle.
	 */
	public Set<RaffleEntry> getRaffleEntries(Raffle raffle, User user, PaginationFilter pagination);

	/**
	 * Get the entry balance of a particular user has in the given raffle. Note that
	 * this balance may not correspond to the sum of the entries the user has purchased as
	 * a user may be credited entries through other means than purchasing RaffleEntrys.
	 * 
	 * @param raffle The raffle
	 * @param user The user 
	 * 
	 * @return The total number of entries in the raffle.
	 */
	public UserBalance getRaffleEntryBalance(Raffle raffle, User user);

	/**
	 * Determines whether a given raffle item has any entries associated
	 * with it or not.
	 * 
	 * @param raffle The raffle whose entries we're to look up
	 * 
	 * @return True whether there are any entries, false otherwise
	 */
	public boolean hasPurchases(Raffle raffle);
}
