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
package com.i4one.rewards.tests.model;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balanceexpense.BalanceExpenseManager;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.tests.model.user.BaseUserBalanceManagerTest;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.prize.PrizeManager;
import org.junit.Before;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public abstract class SimpleRewardsManagerTest extends BaseUserBalanceManagerTest
{
	protected BalanceExpenseManager balanceExpenseManager;
	protected PrizeManager prizeManager;
	protected Prize firstPrize;

	@Before
        @Override
	public void setUp() throws Exception
	{
		super.setUp();

		logAdminIn(getFirstAdmin());

		firstPrize = new Prize();
		firstPrize.setClient(getFirstClient());
		firstPrize.setSiteGroup(getFirstSiteGroup());
		firstPrize.setTitle(new IString("First Prize"));
		firstPrize.setDescr(new IString("First Prize description"));
		firstPrize.setDetailPicURL("");
		firstPrize.setThumbnailURL("");
		firstPrize.setInitialInventory(2);
		firstPrize.setCurrentInventory(0);

		ReturnType<Prize> createPrize = getPrizeManager().create(firstPrize);
		assertTrue(createPrize.getPost().exists());

		ReturnType<UserBalance> incrementBalance = getUserBalanceManager().increment(
			new UserBalance(getFirstUser(), getFirstClientDefaultBalance(), Utils.currentTimeSeconds()), 100);
		assertTrue(incrementBalance.getPost().exists());
		assertTrue(incrementBalance.getPost().getTotal() >= 100);
	}

	public Prize getFirstPrize()
	{
		return firstPrize;
	}

	public void setFirstPrize(Prize firstPrize)
	{
		this.firstPrize = firstPrize;
	}

	public PrizeManager getPrizeManager()
	{
		return prizeManager;
	}

	@Autowired
	public void setPrizeManager(PrizeManager prizeManager)
	{
		this.prizeManager = prizeManager;
	}

	public BalanceExpenseManager getBalanceExpenseManager()
	{
		return balanceExpenseManager;
	}

	@Autowired
	public void setBalanceExpenseManager(BalanceExpenseManager balanceExpenseManager)
	{
		this.balanceExpenseManager = balanceExpenseManager;
	}
}
