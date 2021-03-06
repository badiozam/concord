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

import com.i4one.base.model.manager.activity.QuantifiedActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface PrizeFulfillmentManager extends QuantifiedActivityManager<PrizeFulfillmentRecord, PrizeFulfillment, PrizeWinning>
{
	/**
	 * Retrieve all of the prize fulfillments for a given prize winning.
	 * 
	 * @param prizeWinning The prize winning item to retrieve fulfillments for
	 * @param pagination The pagination filter for sorting/limiting
	 * 
	 * @return A (potentially empty) set of all prize fulfillments for the
	 * 	given prize winning.
	 */
	public Set<PrizeFulfillment> getAllPrizeFulfillments(PrizeWinning prizeWinning, PaginationFilter pagination);

	/**
	 * Get the total quantity out of the given prize winning that has been
	 * fulfilled.
	 * 
	 * @param prizeWinning The prize winning to check
	 * 
	 * @return The total quantity fulfilled thus far.
	 */
	public int getTotalFulfilled(PrizeWinning prizeWinning);
}
