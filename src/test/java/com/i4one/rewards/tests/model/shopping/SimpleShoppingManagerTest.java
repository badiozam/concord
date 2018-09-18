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
package com.i4one.rewards.tests.model.shopping;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.i18n.IString;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.shopping.Shopping;
import com.i4one.rewards.model.shopping.ShoppingPurchase;
import com.i4one.rewards.model.shopping.ShoppingPurchaseManager;
import com.i4one.rewards.model.shopping.ShoppingManager;
import com.i4one.rewards.model.shopping.category.ShoppingCategory;
import com.i4one.rewards.model.shopping.category.ShoppingCategoryManager;
import com.i4one.rewards.tests.model.SimpleRewardsManagerTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SimpleShoppingManagerTest extends SimpleRewardsManagerTest
{
	private ShoppingCategory firstShoppingCategory;

	private Shopping firstShopping;
	private Shopping secondShopping;

	private ShoppingCategoryManager shoppingCategoryManager;
	private ShoppingManager shoppingManager;
	private ShoppingPurchaseManager shoppingPurchaseManager;
	private BalanceManager simpleBalanceManager;

	protected BalanceExpense firstBalanceExpense;
	protected BalanceExpense secondBalanceExpense;

	@Before
        @Override
	public void setUp() throws Exception
	{
		super.setUp();

		assertEquals(2, firstPrize.getInitialInventory());
		ReturnType<Prize> updatedPrizeRet = getPrizeManager().incrementTotalInventory(firstPrize, 5);
		Prize updatedPrize = updatedPrizeRet.getPost();

		assertEquals(7, updatedPrize.getInitialInventory());
		assertNotSame(updatedPrize, firstPrize);
		assertEquals(updatedPrize, firstPrize);

		firstShoppingCategory = new ShoppingCategory();
		firstShoppingCategory.setClient(getFirstClient());
		firstShoppingCategory.setSiteGroup(getFirstSiteGroup());
		firstShoppingCategory.setDescr(new IString("First Shopping Category Description"));
		firstShoppingCategory.setTitle(new IString("First Shopping Category"));
		firstShoppingCategory.setThumbnailURL("");
		firstShoppingCategory.setDetailPicURL("");
		ReturnType<ShoppingCategory> createShoppingCategory = getShoppingCategoryManager().create(firstShoppingCategory);
		assertTrue(createShoppingCategory.getPost().exists());

		int currTime = Utils.currentTimeSeconds();

		firstShopping = new Shopping();
		firstShopping.setClient(getFirstClient());
		firstShopping.setSiteGroup(getFirstSiteGroup());
		firstShopping.setCategory(getFirstShoppingCategory());
		firstShopping.setTitle(new IString("First Shopping"));
		firstShopping.setIntro(new IString("Intro"));
		firstShopping.setOutro(new IString("Outro"));
		firstShopping.setStartTimeSeconds(currTime - 10);
		firstShopping.setEndTimeSeconds(currTime + 10);
		firstShopping.setPurchaseStartTimeSeconds(currTime - 5);
		firstShopping.setPurchaseEndTimeSeconds(currTime + 5);
		firstShopping.setPrize(getFirstPrize());
		firstShopping.setUserLimit(0);
		firstShopping.setInitialReserve(7);
		ReturnType<Shopping> createdShopping = getShoppingManager().create(firstShopping);
		assertTrue(createdShopping.getPost().exists());
		assertEquals(7, createdShopping.getPost().getInitialReserve());
		assertEquals(7, createdShopping.getPost().getCurrentReserve());

		firstBalanceExpense = new BalanceExpense();
		firstBalanceExpense.setAmount(10);
		firstBalanceExpense.setBalance(getFirstClientDefaultBalance());

		ReturnType<BalanceExpense> createdFirstBalanceExpense = getBalanceExpenseManager().create(firstBalanceExpense);
		boolean associatedFirstExpense = getBalanceExpenseManager().associate(firstShopping, createdFirstBalanceExpense.getPost());

		assertTrue(associatedFirstExpense);
		assertTrue(firstBalanceExpense.exists());
		assertEquals(firstShopping.getFeatureName(), firstBalanceExpense.getOwner().getFeatureName());
		assertEquals(firstShopping.getSer(), firstBalanceExpense.getOwner().getSer());
		assertTrue(firstShopping.isLive(currTime));
		assertTrue(firstShopping.isAvailable());

		assertFalse(firstShopping.isAvailableAt(currTime + 7));
		assertFalse(firstShopping.isAvailableAt(currTime - 7));
	}

	@Test
	public void testPurchase() throws InterruptedException
	{
		logAdminOut();
		logUserIn(getFirstUser());

		ShoppingPurchase shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(firstShopping);
		shoppingPurchase.setQuantity(7);

		ReturnType<ShoppingPurchase> newPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
		assertTrue(newPurchase.getPost().exists());
		assertEquals(7, newPurchase.getPost().getQuantity());

		// We have to wait at least one second in order not to violate the unique constraint
		// of one transaction per second
		//
		Thread.sleep(1000l);

		Shopping shopping = new Shopping();
		shopping.setSer(firstShopping.getSer());

		assertNotSame(shopping, firstShopping);
		assertEquals(shopping, firstShopping);

		shopping.loadedVersion();

		assertEquals(0, shopping.getCurrentReserve());
		assertEquals(7, shopping.getInitialReserve());
	}

	@Test
	public void testUserLimit() throws InterruptedException
	{
		firstShopping.setUserLimit(3);
		getShoppingManager().update(firstShopping);

		logAdminOut();
		logUserIn(getFirstUser());

		ShoppingPurchase shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(firstShopping);
		shoppingPurchase.setQuantity(7);

		try
		{
			ReturnType<ShoppingPurchase> newPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
			fail("Able to purchase entries over user limit");
		}
		catch (Errors errors)
		{
			assertTrue(errors.containsError("shoppingPurchaseManager.create"));
		}

		shoppingPurchase.setQuantity(2);
		ReturnType<ShoppingPurchase> firstPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
		assertTrue(firstPurchase.getPost().exists());

		// We have to wait at least one second in order not to violate the unique constraint
		// of one transaction per second
		//
		Thread.sleep(1000l);

		shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(firstShopping);
		shoppingPurchase.setQuantity(1);
		ReturnType<ShoppingPurchase> secondPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
		assertTrue(secondPurchase.getPost().exists());
		assertNotEquals(firstPurchase.getPost(), secondPurchase.getPost());

		// We have to wait at least one second in order not to violate the unique constraint
		// of one transaction per second
		//
		Thread.sleep(1000l);

		shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(firstShopping);
		shoppingPurchase.setQuantity(1);
		try
		{
			ReturnType<ShoppingPurchase> newPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
			fail("Able to purchase over user limit");
		}
		catch (Errors errors)
		{
			assertTrue(errors.containsError("shoppingPurchaseManager.create"));
		}
	}

	@Test
	public void testReserve() throws InterruptedException
	{
		int initReserve = firstShopping.getInitialReserve();
		int currReserve = firstShopping.getCurrentReserve();

		firstShopping.setInitialReserve(2);
		getShoppingManager().update(firstShopping);

		Shopping shopping = new Shopping();
		shopping.setSer(firstShopping.getSer());
		shopping.loadedVersion();

		assertNotSame(shopping, firstShopping);
		assertEquals(shopping, firstShopping);
		assertEquals(initReserve, shopping.getInitialReserve());
		assertEquals(currReserve, shopping.getCurrentReserve());

		getShoppingManager().incrementTotalReserve(shopping, -4);
		shopping = new Shopping();
		shopping.setSer(firstShopping.getSer());
		shopping.loadedVersion();

		assertEquals(initReserve - 4, shopping.getInitialReserve());
		assertEquals(currReserve - 4, shopping.getCurrentReserve());

		// By this point we should have 3 shopping items in reserve
		//
		logAdminOut();
		logUserIn(getFirstUser());

		ShoppingPurchase shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(shopping);
		shoppingPurchase.setQuantity(4);

		try
		{
			ReturnType<ShoppingPurchase> newPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
			fail("Able to purchase over available inventory");
		}
		catch (Errors errors)
		{
			assertTrue(errors.containsError("shoppingManager.incrementCurrentReserve"));
		}

		shoppingPurchase.setQuantity(2);
		ReturnType<ShoppingPurchase> firstPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
		assertTrue(firstPurchase.getPost().exists());

		// We have to wait at least one second in order not to violate the unique constraint
		// of one transaction per second
		//
		Thread.sleep(1000l);

		shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(firstShopping);
		shoppingPurchase.setQuantity(1);
		ReturnType<ShoppingPurchase> secondPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
		assertTrue(secondPurchase.getPost().exists());
		assertNotEquals(firstPurchase.getPost(), secondPurchase.getPost());

		shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(firstShopping);
		shoppingPurchase.setQuantity(1);
		try
		{
			ReturnType<ShoppingPurchase> newPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
			fail("Able to purchase over available inventory");
		}
		catch (Errors errors)
		{
			assertTrue(errors.containsError("shoppingManager.incrementCurrentReserve"));
		}
	}

	@Test
	public void testInventory() throws InterruptedException
	{
		int initReserve = firstShopping.getInitialReserve();
		int currReserve = firstShopping.getCurrentReserve();

		Shopping shopping = new Shopping();
		shopping.setSer(firstShopping.getSer());
		shopping.loadedVersion();

		assertNotSame(shopping, firstShopping);
		assertEquals(shopping, firstShopping);
		assertEquals(initReserve, shopping.getInitialReserve());
		assertEquals(currReserve, shopping.getCurrentReserve());

		getShoppingManager().incrementTotalReserve(shopping, -1 * initReserve);
		shopping = new Shopping();
		shopping.setSer(firstShopping.getSer());
		shopping.loadedVersion();

		assertEquals(0, shopping.getInitialReserve());
		assertEquals(0, shopping.getCurrentReserve());
		assertEquals(7, shopping.getPrize().getInitialInventory());
		assertEquals(7, shopping.getPrize().getCurrentInventory());

		getPrizeManager().incrementTotalInventory(shopping.getPrize(), -4);

		// By this point we should have 3 prize items
		//
		logAdminOut();
		logUserIn(getFirstUser());

		ShoppingPurchase shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(shopping);
		shoppingPurchase.setQuantity(4);

		try
		{
			ReturnType<ShoppingPurchase> newPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
			fail("Able to purchase over available inventory");
		}
		catch (Errors errors)
		{
			assertTrue(errors.containsError("shoppingManager.incrementCurrentReserve"));
		}

		shoppingPurchase.setQuantity(2);
		ReturnType<ShoppingPurchase> firstPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
		assertTrue(firstPurchase.getPost().exists());

		// We have to wait at least one second in order not to violate the unique constraint
		// of one transaction per second
		//
		Thread.sleep(1000l);

		shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(firstShopping);
		shoppingPurchase.setQuantity(1);
		ReturnType<ShoppingPurchase> secondPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
		assertTrue(secondPurchase.getPost().exists());
		assertNotEquals(firstPurchase.getPost(), secondPurchase.getPost());

		shoppingPurchase = new ShoppingPurchase();
		shoppingPurchase.setUser(getFirstUser());
		shoppingPurchase.setShopping(firstShopping);
		shoppingPurchase.setQuantity(1);
		try
		{
			ReturnType<ShoppingPurchase> newPurchase = getShoppingPurchaseManager().create(shoppingPurchase);
			fail("Able to purchase over available inventory");
		}
		catch (Errors errors)
		{
			assertTrue(errors.containsError("shoppingManager.incrementCurrentReserve"));
		}
	}

	public Shopping getFirstShopping()
	{
		return firstShopping;
	}

	public void setFirstShopping(Shopping firstShopping)
	{
		this.firstShopping = firstShopping;
	}

	public Shopping getSecondShopping()
	{
		return secondShopping;
	}

	public void setSecondShopping(Shopping secondShopping)
	{
		this.secondShopping = secondShopping;
	}

	public ShoppingCategory getFirstShoppingCategory()
	{
		return firstShoppingCategory;
	}

	public void setFirstShoppingCategory(ShoppingCategory firstShoppingCategory)
	{
		this.firstShoppingCategory = firstShoppingCategory;
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

	public ShoppingManager getShoppingManager()
	{
		return shoppingManager;
	}

	@Autowired
	public void setShoppingManager(ShoppingManager simpleShoppingManager)
	{
		this.shoppingManager = simpleShoppingManager;
	}

	public ShoppingCategoryManager getShoppingCategoryManager()
	{
		return shoppingCategoryManager;
	}

	@Autowired
	public void setShoppingCategoryManager(ShoppingCategoryManager shoppingCategoryManager)
	{
		this.shoppingCategoryManager = shoppingCategoryManager;
	}

	public ShoppingPurchaseManager getShoppingPurchaseManager()
	{
		return shoppingPurchaseManager;
	}

	@Autowired
	public void setShoppingPurchaseManager(ShoppingPurchaseManager shoppingPurchaseManager)
	{
		this.shoppingPurchaseManager = shoppingPurchaseManager;
	}
}
