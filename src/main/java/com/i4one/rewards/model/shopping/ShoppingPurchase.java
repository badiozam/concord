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

import com.i4one.base.model.SiteGroupActivityType;
import com.i4one.rewards.model.prize.BasePrizeWinningActivityType;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.prize.PrizeWinningUsageType;

/**
 * @author Hamid Badiozamani
 */
public class ShoppingPurchase extends BasePrizeWinningActivityType<ShoppingPurchaseRecord, ShoppingRecord, Shopping> implements PrizeWinningUsageType<ShoppingPurchaseRecord>,SiteGroupActivityType<ShoppingPurchaseRecord, Shopping>
{
	static final long serialVersionUID = 42L;

	public ShoppingPurchase()
	{
		super(new ShoppingPurchaseRecord());
	}

	protected ShoppingPurchase(ShoppingPurchaseRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getItemid() + "-" + getDelegate().getUserid();
	}

	public Shopping getShopping()
	{
		return getActionItem();
	}

	public Shopping getShopping(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	public void setShopping(Shopping shopping)
	{
		setActionItem(shopping);
	}

	@Override
	public Prize getPrize()
	{
		return getShopping().getPrize();
	}

	@Override
	protected Shopping newActionItem()
	{
		return new Shopping();
	}
}
