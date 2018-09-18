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
import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.rewards.model.PrizeType;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface PrizeManager extends PaginableManager<PrizeRecord, Prize>
{
	/**
	 * Locks a prize record and returns its most recent copy
	 * 
	 * @param prize The prize record to update
	 * 
	 * @return The most recent shopping record for update
	 */
	public Prize getPrizeForUpdate(Prize prize);

	/**
	 * Increment the total inventory by the given amount. Note that the
	 * amount can be positive or negative and that the current inventory
	 * is also incremented by the given amount
	 * 
	 * @param prize The prize whose inventory to increment
	 * @param amount The amount to increase the total inventory by
	 * 
	 * @return The prize with the updated inventory
	 */
	public ReturnType<Prize> incrementTotalInventory(Prize prize, int amount);

	/**
	 * Increment the current inventory by the given amount. Note that the
	 * amount can only be negative and is deducted from the total
	 * inventory. This method is intended for direct inventory purchases
	 * by users.
	 * 
	 * @param prize The prize whose inventory to increment
	 * @param amount The amount to decrease the current inventory by
	 * 
	 * @return The prize with the updated inventory
	 */
	public ReturnType<Prize> incrementCurrentInventory(Prize prize, int amount);

	/**
	 * Reserve the prize inventory by the given amount. Note that the
	 * amount can be positive or negative and is applied toward the total
	 * inventory.
	 * 
	 * @param prizeType The item which needs this prize whose inventory to increment
	 * @param amount The amount to reserve
	 * 
	 * @return The prize with the updated inventory
	 */
	public ReturnType<Prize> reserveInventory(PrizeType<?> prizeType, int amount);

	/**
	 * Search for prizes containing the given title
	 * 
	 * @param title The partial title of the prize to look up
	 * @param pagination The qualifiers for retrieving the prizes
	 * 
	 * @return A (potentially empty) set of prizes with the
	 * 	given string value.
	 */
	public Set<Prize> search(String title, PaginationFilter pagination);
}
