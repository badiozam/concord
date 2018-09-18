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


import com.i4one.base.dao.categorizable.CategorizableRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.expendable.BaseTerminableExpendableManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class ExpendableShoppingManager extends BaseTerminableExpendableManager<ShoppingRecord, Shopping> implements ShoppingManager
{
	private ShoppingManager shoppingManager;

	@Override
	public ShoppingManager getImplementationManager()
	{
		return getShoppingManager();
	}

	@Override
	public ShoppingSettings getSettings(SingleClient client)
	{
		return getShoppingManager().getSettings(client);
	}

	@Override
	public ReturnType<ShoppingSettings> updateSettings(ShoppingSettings settings)
	{
		return getShoppingManager().updateSettings(settings);
	}

	@Override
	public void incrementCurrentReserve(Shopping shopping, int amount)
	{
		getShoppingManager().incrementCurrentReserve(shopping, amount);
	}

	@Override
	public ReturnType<Shopping> incrementTotalReserve(Shopping shopping, int amount)
	{
		return getShoppingManager().incrementTotalReserve(shopping, amount);
	}

	@Override
	public CategorizableRecordTypeDao<ShoppingRecord> getDao()
	{
		return getShoppingManager().getDao();
	}

	public ShoppingManager getShoppingManager()
	{
		return shoppingManager;
	}

	@Autowired
	@Qualifier("rewards.CachedShoppingManager")
	public void setShoppingManager(ShoppingManager shoppingManager)
	{
		this.shoppingManager = shoppingManager;
	}
}
