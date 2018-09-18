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
import com.i4one.base.model.manager.BaseTransactionalActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("rewards.ShoppingPurchaseManager")
public class TransactionalShoppingPurchaseManager extends BaseTransactionalActivityManager<ShoppingPurchaseRecord, ShoppingPurchase, Shopping> implements ShoppingPurchaseManager
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
	public ShoppingPurchaseManager getImplementationManager()
	{
		return getShoppingPurchaseManager();
	}

	protected void processShoppingPurchaseTransaction(ReturnType<ShoppingPurchase> retShoppingPurchase)
	{
		ShoppingPurchase pre = retShoppingPurchase.getPre();
		ShoppingPurchase post = retShoppingPurchase.getPost();

		Transaction t = newTransaction(post.getUser());

		if ( post.exists() )
		{
			// The message is responsible for displaying the proper previously played status
			//
			setTransactionDescr(t, "msg.shoppingPurchaseManager.create.xaction.descr", "shoppingPurchase", post);
	
			// See if we can locate a user balance update transaction and set us
			// as the the parent transaction
			//
			t = createTransaction(retShoppingPurchase, t);
	
			// Set our master transaction record since we've created a new transaction
			//
			retShoppingPurchase.put("transaction", t);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<ShoppingPurchase> create(ShoppingPurchase shoppingPurchase)
	{
		ReturnType<ShoppingPurchase> retVal = getShoppingPurchaseManager().create(shoppingPurchase);

		processShoppingPurchaseTransaction(retVal);

		return retVal;
	}

	@Override
	public boolean hasPurchases(Shopping shopping)
	{
		return getShoppingPurchaseManager().hasPurchases(shopping);
	}

	public ShoppingPurchaseManager getShoppingPurchaseManager()
	{
		return shoppingPurchaseManager;
	}

	@Autowired
	public void setShoppingPurchaseManager(ShoppingPurchaseManager privilegedShoppingPurchaseManager)
	{
		this.shoppingPurchaseManager = privilegedShoppingPurchaseManager;
	}
}
