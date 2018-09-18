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
package com.i4one.rewards.model.prize;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.QuantifiedActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface PrizeWinningManager extends QuantifiedActivityManager<PrizeWinningRecord, PrizeWinning, Prize>
{
	/**
	 * Retrieve all of the prize winnings for a given user.
	 * 
	 * @param user The prize user to retrieve winnings for
	 * @param pagination The pagination filter for sorting/limiting
	 * 
	 * @return A (potentially empty) set of all prize winnings for the
	 * 	given prize.
	 */
	public Set<PrizeWinning> getAllPrizeWinnings(User user, PaginationFilter pagination);
	/**
	 * Retrieve all of the prize winnings for a given prize item.
	 * 
	 * @param prize The prize item to retrieve winnings for
	 * @param pagination The pagination filter for sorting/limiting
	 * 
	 * @return A (potentially empty) set of all prize winnings for the
	 * 	given prize.
	 */
	public Set<PrizeWinning> getAllPrizeWinnings(Prize prize, PaginationFilter pagination);

	/**
	 * Retrieve all of a user's winnings for a given prize item.
	 * 
	 * @param prize The prize item to look up
	 * @param user The user who may have won the item
	 * @param pagination The pagination filter for sorting/limiting
	 * 
	 * @return A potentially (empty) set of the prize winnings for the given user.
	 */
	public Set<PrizeWinning> getPrizeWinnings(Prize prize, User user, PaginationFilter pagination);

	/**
	 * Fulfill a set of prizes with the given fulfillment template. A separate
	 * fulfillment record is created for each winning for all unfulfilled
	 * quantities of the prize winning.
	 * 
	 * @param prizeWinnings The prize winnings to fulfill
	 * @param fulfillment The fulfillment template to use when 
	 * 
	 * @return The results of the created fulfillments
	 */
	public List<ReturnType<PrizeFulfillment>> fulfillPrizeWinnings(Set<PrizeWinning> prizeWinnings, PrizeFulfillment fulfillment);
}
