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
package com.i4one.rewards.tests.model.raffle;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.user.UserBalance;
import com.i4one.rewards.model.raffle.Raffle;
import com.i4one.rewards.model.raffle.RaffleEntry;
import com.i4one.rewards.model.raffle.RaffleEntryManager;
import com.i4one.rewards.model.raffle.RaffleManager;
import com.i4one.rewards.model.raffle.category.RaffleCategory;
import com.i4one.rewards.model.raffle.category.RaffleCategoryManager;
import com.i4one.rewards.tests.model.SimpleRewardsManagerTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SimpleRaffleManagerTest extends SimpleRewardsManagerTest
{
	private RaffleCategory firstRaffleCategory;

	private Raffle firstRaffle;
	private Raffle secondRaffle;

	private Balance firstRaffleBalance;
	private Balance secondRaffleBalance;

	private BalanceManager balanceManager;
	private RaffleCategoryManager raffleCategoryManager;
	private RaffleManager raffleManager;
	private RaffleEntryManager raffleEntryManager;
	private BalanceManager simpleBalanceManager;

	protected BalanceExpense firstBalanceExpense;
	protected BalanceExpense secondBalanceExpense;

	@Before
        @Override
	public void setUp() throws Exception
	{
		super.setUp();

		firstRaffleCategory = new RaffleCategory();
		firstRaffleCategory.setClient(getFirstClient());
		firstRaffleCategory.setSiteGroup(getFirstSiteGroup());
		firstRaffleCategory.setDescr(new IString("First Raffle Category Description"));
		firstRaffleCategory.setTitle(new IString("First Raffle Category"));
		firstRaffleCategory.setThumbnailURL("");
		firstRaffleCategory.setDetailPicURL("");
		ReturnType<RaffleCategory> createRaffleCategory = getRaffleCategoryManager().create(firstRaffleCategory);
		assertTrue(createRaffleCategory.getPost().exists());

		int currTime = Utils.currentTimeSeconds();

		firstRaffle = new Raffle();
		firstRaffle.setClient(getFirstClient());
		firstRaffle.setSiteGroup(getFirstSiteGroup());
		firstRaffle.setCategory(getFirstRaffleCategory());
		firstRaffle.setTitle(new IString("First Raffle"));
		firstRaffle.setIntro(new IString("Intro"));
		firstRaffle.setOutro(new IString("Outro"));
		firstRaffle.setStartTimeSeconds(currTime - 10);
		firstRaffle.setEndTimeSeconds(currTime + 10);
		firstRaffle.setPurchaseStartTimeSeconds(currTime - 5);
		firstRaffle.setPurchaseEndTimeSeconds(currTime + 5);
		firstRaffle.setPrize(getFirstPrize());
		firstRaffle.setUserLimit(0);
		getRaffleManager().create(firstRaffle);

		firstRaffleBalance = getBalanceManager().getBalance(firstRaffle);

		firstBalanceExpense = new BalanceExpense();
		firstBalanceExpense.setAmount(10);
		firstBalanceExpense.setBalance(getFirstClientDefaultBalance());

		ReturnType<BalanceExpense> createdFirstBalanceExpense = getBalanceExpenseManager().create(firstBalanceExpense);
		boolean associatedFirstExpense = getBalanceExpenseManager().associate(firstRaffle, createdFirstBalanceExpense.getPost());

		assertTrue(associatedFirstExpense);
		assertTrue(firstBalanceExpense.exists());
		assertEquals(firstRaffle.getFeatureName(), firstBalanceExpense.getOwner().getFeatureName());
		assertEquals(firstRaffle.getSer(), firstBalanceExpense.getOwner().getSer());
		assertTrue(firstRaffle.isLive(currTime));
		assertTrue(firstRaffle.isAvailable());

		assertFalse(firstRaffle.isAvailableAt(currTime + 7));
		assertFalse(firstRaffle.isAvailableAt(currTime - 7));
		assertTrue(firstRaffleBalance.exists());
	}

	@Test
	public void testBuyEntry()
	{
		logAdminOut();
		logUserIn(getFirstUser());

		UserBalance userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), getFirstClientDefaultBalance());
		int originalBalance = userBalance.getTotal();

		RaffleEntry raffleEntry = new RaffleEntry();
		raffleEntry.setUser(getFirstUser());
		raffleEntry.setRaffle(firstRaffle);
		raffleEntry.setQuantity(7);

		ReturnType<RaffleEntry> newEntry = getRaffleEntryManager().create(raffleEntry);
		assertTrue(newEntry.getPost().exists());
		assertEquals(raffleEntry.getQuantity(), newEntry.getPost().getQuantity());

		UserBalance raffleBalance = getUserBalanceManager().getUserBalance(getFirstUser(), firstRaffleBalance);
		assertTrue(raffleBalance.exists());
		assertEquals(raffleEntry.getQuantity(), (long)raffleBalance.getTotal());

		userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), getFirstClientDefaultBalance());
		assertTrue(userBalance.exists());
		assertEquals(originalBalance - 7 * firstBalanceExpense.getAmount(), (long)userBalance.getTotal());
	}

	@Test
	public void testUserLimit() throws InterruptedException
	{
		firstRaffle.setUserLimit(3);
		getRaffleManager().update(firstRaffle);

		logAdminOut();
		logUserIn(getFirstUser());

		RaffleEntry raffleEntry = new RaffleEntry();
		raffleEntry.setUser(getFirstUser());
		raffleEntry.setRaffle(firstRaffle);
		raffleEntry.setQuantity(7);

		try
		{
			ReturnType<RaffleEntry> newEntry = getRaffleEntryManager().create(raffleEntry);
			fail("Able to purchase entries over user limit");
		}
		catch (Errors errors)
		{
			assertTrue(errors.containsError("raffleEntryManager.create"));
		}

		UserBalance userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), firstRaffleBalance);
		assertEquals(0, (long)userBalance.getTotal());
		assertFalse(userBalance.exists());

		raffleEntry.setQuantity(2);
		ReturnType<RaffleEntry> firstEntry = getRaffleEntryManager().create(raffleEntry);
		assertTrue(firstEntry.getPost().exists());

		userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), firstRaffleBalance);
		assertTrue(userBalance.exists());
		assertEquals(2, (long)userBalance.getTotal());
		
		// We have to wait at least one second in order not to violate the unique constraint
		// of one transaction per second
		//
		Thread.sleep(1000l);

		raffleEntry = new RaffleEntry();
		raffleEntry.setUser(getFirstUser());
		raffleEntry.setRaffle(firstRaffle);
		raffleEntry.setQuantity(1);
		ReturnType<RaffleEntry> secondEntry = getRaffleEntryManager().create(raffleEntry);
		assertTrue(secondEntry.getPost().exists());
		assertNotEquals(firstEntry.getPost(), secondEntry.getPost());

		userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), firstRaffleBalance);
		assertTrue(userBalance.exists());
		assertEquals(3, (long)userBalance.getTotal());

		raffleEntry = new RaffleEntry();
		raffleEntry.setUser(getFirstUser());
		raffleEntry.setRaffle(firstRaffle);
		raffleEntry.setQuantity(1);
		try
		{
			ReturnType<RaffleEntry> newEntry = getRaffleEntryManager().create(raffleEntry);
			fail("Able to purchase entries over user limit");
		}
		catch (Errors errors)
		{
			assertTrue(errors.containsError("raffleEntryManager.create"));
		}

		userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), firstRaffleBalance);
		assertTrue(userBalance.exists());
		assertEquals(3, (long)userBalance.getTotal());
	}

	public Raffle getFirstRaffle()
	{
		return firstRaffle;
	}

	public void setFirstRaffle(Raffle firstRaffle)
	{
		this.firstRaffle = firstRaffle;
	}

	public Raffle getSecondRaffle()
	{
		return secondRaffle;
	}

	public void setSecondRaffle(Raffle secondRaffle)
	{
		this.secondRaffle = secondRaffle;
	}

	public RaffleCategory getFirstRaffleCategory()
	{
		return firstRaffleCategory;
	}

	public void setFirstRaffleCategory(RaffleCategory firstRaffleCategory)
	{
		this.firstRaffleCategory = firstRaffleCategory;
	}

	public BalanceManager getSimpleBalanceManager()
	{
		return simpleBalanceManager;
	}

	@Autowired
	public void setSimpleBalanceManager(BalanceManager simpleBalanceManager)
	{
		this.simpleBalanceManager = simpleBalanceManager;
	}

	public RaffleManager getRaffleManager()
	{
		return raffleManager;
	}

	@Autowired
	public void setRaffleManager(RaffleManager simpleRaffleManager)
	{
		this.raffleManager = simpleRaffleManager;
	}

	public RaffleCategoryManager getRaffleCategoryManager()
	{
		return raffleCategoryManager;
	}

	@Autowired
	public void setRaffleCategoryManager(RaffleCategoryManager raffleCategoryManager)
	{
		this.raffleCategoryManager = raffleCategoryManager;
	}

	public RaffleEntryManager getRaffleEntryManager()
	{
		return raffleEntryManager;
	}

	@Autowired
	public void setRaffleEntryManager(RaffleEntryManager raffleEntryManager)
	{
		this.raffleEntryManager = raffleEntryManager;
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}
}
