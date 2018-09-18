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
package com.i4one.rewards.model.shopping;

import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface ShoppingPurchaseManager extends ActivityManager<ShoppingPurchaseRecord, ShoppingPurchase, Shopping>
{
	/**
	 * Retrieve all of the shopping purchases for a given user.
	 * 
	 * @param user The shopping user to retrieve purchases for
	 * @param pagination The pagination filter for sorting/limiting
	 * 
	 * @return A (potentially empty) set of all shopping purchases for the
	 * 	given shopping.
	 */
	public Set<ShoppingPurchase> getAllShoppingPurchases(User user, PaginationFilter pagination);
	/**
	 * Retrieve all of the shopping purchases for a given shopping item.
	 * 
	 * @param shopping The shopping item to retrieve purchases for
	 * @param pagination The pagination filter for sorting/limiting
	 * 
	 * @return A (potentially empty) set of all shopping purchases for the
	 * 	given shopping.
	 */
	public Set<ShoppingPurchase> getAllShoppingPurchases(Shopping shopping, PaginationFilter pagination);

	/**
	 * Retrieve all of a user's purchases for a given shopping item.
	 * 
	 * @param shopping The shopping item to look up
	 * @param user The user who may have purchased the item
	 * @param pagination The pagination filter for sorting/limiting
	 * 
	 * @return A (potentially empty) set of all shopping purchases for the
	 * 	given shopping.
	 */
	public Set<ShoppingPurchase> getShoppingPurchases(Shopping shopping, User user, PaginationFilter pagination);

	/**
	 * Determines whether a given shopping item has any purchases associated
	 * with it or not.
	 * 
	 * @param shopping The shopping whose purchases we're to look up
	 * 
	 * @return True whether there are any purchases, false otherwise
	 */
	public boolean hasPurchases(Shopping shopping);
}
