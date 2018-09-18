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

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.rewards.model.prize.PrizeWinning;
import com.i4one.rewards.model.prize.PrizeWinningManager;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleShoppingPurchaseManager extends BaseActivityManager<ShoppingPurchaseRecord, ShoppingPurchase, Shopping> implements ShoppingPurchaseManager
{
	private ShoppingManager shoppingManager;
	private PrizeWinningManager prizeWinningManager;

	@Override
	public ShoppingPurchase emptyInstance()
	{
		return new ShoppingPurchase();
	}

	@Override
	public Set<ShoppingPurchase> getAllShoppingPurchases(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByUser(user.getSer(), pagination));
	}

	@Override
	public Set<ShoppingPurchase> getAllShoppingPurchases(Shopping shopping, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByItem(shopping.getSer(), pagination));
	}

	@Override
	public Set<ShoppingPurchase> getShoppingPurchases(Shopping shopping, User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAll(shopping.getSer(), user.getSer(), pagination));
	}

	@Override
	public boolean hasPurchases(Shopping shopping)
	{
		return getDao().hasActivity(shopping.getSer());
	}

	@Override
	protected ReturnType<ShoppingPurchase> createInternal(ShoppingPurchase shoppingPurchase)
	{
		if (
			(shoppingPurchase.getTimeStampSeconds() > shoppingPurchase.getShopping().getPurchaseEndTimeSeconds()) ||
			(shoppingPurchase.getTimeStampSeconds() < shoppingPurchase.getShopping().getPurchaseStartTimeSeconds())
		)
		{
			// The shopping has either expired or is not live yet
			//
			ReturnType<ShoppingPurchase> expired = new ReturnType<>();

			expired.setPre(emptyInstance());
			expired.setPost(emptyInstance());

			return expired;
		}
		else
		{
			Shopping shopping = shoppingPurchase.getShopping();

			int userid = shoppingPurchase.getUser(false).getSer();
			int shoppingid = shopping.getSer();

			// Check to make sure the total quantity doesn't exceed the user limit
			//
			int currPurchaseQuantity = getDao().getTotalActivityQuantity(shoppingid, userid);
			if ( shopping.getUserLimit() > 0 && currPurchaseQuantity + shoppingPurchase.getQuantity() > shopping.getUserLimit() )
			{
				throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg.rewards.shoppingPurchaseManager.create.overlimit", "You have reached the maximum limit allowable for this item: you currently have $currentPurchaseQuantity and by purchasing $item.quantity more you would exceed the maximum of $item.shopping.userLimit", new Object[] { "item", shoppingPurchase, "currentPurchaseQuantity", currPurchaseQuantity }, null));
			}
			else
			{
				// Whether the shopping item is going directly off of the prize inventory pool or off of its reserve
				// is handled by the shopping manager
				//
				getShoppingManager().incrementCurrentReserve(shopping, -1 * shoppingPurchase.getQuantity() );

				// First we record that the activity took place
				//
				ReturnType<ShoppingPurchase> retVal = super.createInternal(shoppingPurchase);

				// Now we record that the user has won a prize
				//
				PrizeWinning pw = shoppingPurchase.getPrizeWinning();
				pw.setOwner(retVal.getPost());

				ReturnType<PrizeWinning> pwCreate = getPrizeWinningManager().create(pw);
				retVal.getPost().setPrizeWinning(pw);
				retVal.addChain(getPrizeWinningManager(), "create", pwCreate);

				// Now that the prize winning exists, we can set it for the shopping purchase
				// for referential integrity. This is important because the reference to the
				// prizewinning record is how we determine the quantity of an item purchased
				// by a particular user.
				//
				getDao().updateBySer(retVal.getPost().getDelegate());

				return retVal;
			}
		}
	}

	@Override
	public ReturnType<ShoppingPurchase> update(ShoppingPurchase shoppingPurchase)
	{
		throw new UnsupportedOperationException("Can't update a shopping purchase.");
	}

	@Override
	public ShoppingPurchaseRecordDao getDao()
	{
		return (ShoppingPurchaseRecordDao) super.getDao();
	}

	public ShoppingManager getShoppingManager()
	{
		return shoppingManager;
	}

	@Autowired
	public void setShoppingManager(ShoppingManager shoppingManager)
	{
		this.shoppingManager = shoppingManager;
	}

	public PrizeWinningManager getPrizeWinningManager()
	{
		return prizeWinningManager;
	}

	@Autowired
	public void setPrizeWinningManager(PrizeWinningManager prizeWinningManager)
	{
		this.prizeWinningManager = prizeWinningManager;
	}
}
