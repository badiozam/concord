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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.BaseSiteGroupActivityPrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedShoppingPurchaseManager extends BaseSiteGroupActivityPrivilegedManager<ShoppingPurchaseRecord, ShoppingPurchase, Shopping> implements ShoppingPurchaseManager
{
	private ShoppingPurchaseManager shoppingPurchaseManager;

	@Override
	public Set<ShoppingPurchase> getAllShoppingPurchases(User user, PaginationFilter pagination)
	{
		// Only the user (or admin) can read all entries for a given user
		//
		checkRead(emptyInstance(), user, "getAllShoppingPurchases");

		return getShoppingPurchaseManager().getAllShoppingPurchases(user, pagination);
	}

	@Override
	public Set<ShoppingPurchase> getAllShoppingPurchases(Shopping shopping, PaginationFilter pagination)
	{
		// Only administrators can read all raffle entries
		//
		checkRead(emptyInstance(), new User(), "getAllShoppingPurchases");

		return getShoppingPurchaseManager().getAllShoppingPurchases(shopping, pagination);
	}

	@Override
	public Set<ShoppingPurchase> getShoppingPurchases(Shopping shopping, User user, PaginationFilter pagination)
	{
		// Only the user (or admin) can read all entries for a given user
		//
		checkRead(emptyInstance(), user, "getShoppingPurchases");

		return getShoppingPurchaseManager().getShoppingPurchases(shopping, user, pagination);
	}

	@Override
	public boolean hasPurchases(Shopping shopping)
	{
		return getShoppingPurchaseManager().hasPurchases(shopping);
	}

	@Override
	public User getUser(ShoppingPurchase item)
	{
		return item.getUser();
	}

	@Override
	public ShoppingPurchaseManager getImplementationManager()
	{
		return getShoppingPurchaseManager();
	}

	@Override
	public SingleClient getClient(ShoppingPurchase item)
	{
		return item.getShopping().getClient();
	}

	public ShoppingPurchaseManager getShoppingPurchaseManager()
	{
		return shoppingPurchaseManager;
	}

	@Autowired
	public void setShoppingPurchaseManager(ShoppingPurchaseManager expensiveShoppingPurchaseManager)
	{
		this.shoppingPurchaseManager = expensiveShoppingPurchaseManager;
	}

	@Override
	public ActivityReport getReport(ShoppingPurchase item, TopLevelReport report, PaginationFilter pagination)
	{
		return getShoppingPurchaseManager().getReport(item, report, pagination);
	}
}
