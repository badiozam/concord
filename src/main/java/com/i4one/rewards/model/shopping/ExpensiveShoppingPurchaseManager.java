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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.expendable.BaseExpensiveManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class ExpensiveShoppingPurchaseManager extends BaseExpensiveManager<ShoppingPurchaseRecord, ShoppingPurchase, Shopping> implements ShoppingPurchaseManager
{
	private ShoppingPurchaseManager shoppingPurchaseManager;

	@Override
	public Set<ShoppingPurchase> getAllShoppingPurchases(User user, PaginationFilter pagination)
	{
		return getShoppingPurchaseManager().getAllShoppingPurchases(user, pagination);
	}

	@Override
	public Set<ShoppingPurchase> getAllShoppingPurchases(Shopping shopping, PaginationFilter pagination)
	{
		return getShoppingPurchaseManager().getAllShoppingPurchases(shopping, pagination);
	}

	@Override
	public Set<ShoppingPurchase> getShoppingPurchases(Shopping shopping, User user, PaginationFilter pagination)
	{
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
		return item.getUser(false);
	}

	@Override
	public Shopping getAttachee(ShoppingPurchase shoppingPurchase)
	{
		return shoppingPurchase.getShopping();
	}

	@Override
	public ShoppingPurchaseManager getImplementationManager()
	{
		return getShoppingPurchaseManager();
	}

	@Override
	protected void processAttached(ReturnType<ShoppingPurchase> retShoppingPurchase, String methodName)
	{
		// We only process the expenses if an entry was successfully processed
		//
		ShoppingPurchase pre = retShoppingPurchase.getPre();
		ShoppingPurchase post = retShoppingPurchase.getPost();

		if ( !post.exists() )
		{
			getLogger().debug("Expenses not processed for " + retShoppingPurchase.getPost().getShopping() + " and user " + retShoppingPurchase.getPost().getUser(false));
		}
		else
		{
			super.processAttached(retShoppingPurchase, methodName);
		}
	}

	public ShoppingPurchaseManager getShoppingPurchaseManager()
	{
		return shoppingPurchaseManager;
	}

	@Autowired
	@Qualifier("rewards.SimpleShoppingPurchaseManager")
	public void setShoppingPurchaseManager(ShoppingPurchaseManager shoppingPurchaseManager)
	{
		this.shoppingPurchaseManager = shoppingPurchaseManager;
	}
}
