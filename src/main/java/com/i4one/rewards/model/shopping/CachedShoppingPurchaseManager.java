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
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author Hamid Badiozamani
 */
public class CachedShoppingPurchaseManager extends SimpleShoppingPurchaseManager implements ShoppingPurchaseManager
{
	@Cacheable(value = "shoppingPurchaseManager", key = "target.makeKey('getAllShoppingPurchases', #user, #pagination)")
	@Override
	public Set<ShoppingPurchase> getAllShoppingPurchases(User user, PaginationFilter pagination)
	{
		return super.getAllShoppingPurchases(user, pagination);
	}

	@Cacheable(value = "shoppingPurchaseManager", key = "target.makeKey('getAllShoppingPurchases', #shopping, #pagination)")
	@Override
	public Set<ShoppingPurchase> getAllShoppingPurchases(Shopping shopping, PaginationFilter pagination)
	{
		return super.getAllShoppingPurchases(shopping, pagination);
	}

	@Cacheable(value = "shoppingPurchaseManager", key = "target.makeKey('getShoppingPurchases', #shopping, #user, #pagination)")
	@Override
	public Set<ShoppingPurchase> getShoppingPurchases(Shopping shopping, User user, PaginationFilter pagination)
	{
		return super.getShoppingPurchases(shopping, user, pagination);
	}

	@Cacheable(value = "shoppingPurchaseManager", key = "target.makeKey('hasPurchases', #shopping)")
	@Override
	public boolean hasPurchases(Shopping shopping)
	{
		return super.hasPurchases(shopping);
	}

	@CacheEvict(value = "shoppingPurchaseManager", allEntries = true )
	@Override
	public ReturnType<ShoppingPurchase> create(ShoppingPurchase shoppingPurchase)
	{
		return super.create(shoppingPurchase);
	}

	@CacheEvict(value = "shoppingPurchaseManager", allEntries = true )
	@Override
	public ReturnType<ShoppingPurchase> update(ShoppingPurchase respondent)
	{
		return super.update(respondent);
	}

	@CacheEvict(value = "shoppingPurchaseManager", allEntries = true)
	@Override
	public ShoppingPurchase remove(ShoppingPurchase respondent)
	{
		return super.remove(respondent);
	}
}
