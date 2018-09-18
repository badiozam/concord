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
package com.i4one.base.tests.model.user;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceRecord;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.model.user.UserBalanceRecord;
import com.i4one.base.model.user.UserBalanceRecordDao;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.tests.core.BaseUserManagerTest;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseUserBalanceManagerTest extends BaseUserManagerTest
{
	private UserManager userManager;
	private UserBalanceManager userBalanceManager;

	private Balance firstBalance;
	private Balance secondBalance;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		// Create two different balances, one for points and one for a raffle entry
		//
		BalanceRecord points = new BalanceRecord();
		points.setClientid(getFirstClient().getSer());
		points.setFeatureid(getFirstClient().getSer());
		points.setFeature("clients");
		points.setSingleName(new IString("en", "Points"));
		points.setPluralName(new IString("en", "Point"));

		BalanceRecord raffleEntry = new BalanceRecord();
		raffleEntry.setClientid(getFirstClient().getSer());
		raffleEntry.setFeatureid(1);
		raffleEntry.setFeature("raffles");
		raffleEntry.setSingleName(new IString("en", "Entry"));
		raffleEntry.setPluralName(new IString("en", "Entries"));

		getBalanceRecordDao().insert(points);
		getBalanceRecordDao().insert(raffleEntry);

		// Only give the first user points and entries
		//
		UserBalanceRecord pointsRecord = new UserBalanceRecord();
		pointsRecord.setBalid(points.getSer());
		pointsRecord.setUserid(getFirstUser().getSer());
		pointsRecord.setTotal(5);
		pointsRecord.setCreatetime(Utils.currentTimeSeconds());
		pointsRecord.setUpdatetime(Utils.currentTimeSeconds());

		UserBalanceRecord entriesRecord = new UserBalanceRecord();
		entriesRecord.setBalid(raffleEntry.getSer());
		entriesRecord.setUserid(getFirstUser().getSer());
		entriesRecord.setTotal(7);
		entriesRecord.setCreatetime(Utils.currentTimeSeconds());
		entriesRecord.setUpdatetime(Utils.currentTimeSeconds());

		getUserBalanceRecordDao().insert(pointsRecord);
		getUserBalanceRecordDao().insert(entriesRecord);

		firstBalance = new Balance();
		getFirstBalance().setDelegate(points);

		secondBalance = new Balance();
		getSecondBalance().setDelegate(raffleEntry);

		assertNotNull(getFirstClient());

		assertNotNull(getFirstBalance());
		assertNotNull(getSecondBalance());

		assertTrue(getFirstBalance().exists());
		assertTrue(getSecondBalance().exists());
	}

	@Test
	public void testGetUserBalance()
	{
		UserBalance userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), getFirstBalance());
		assertNotNull(userBalance);
		assertEquals(getFirstBalance(), userBalance.getBalance());
		assertEquals(getFirstUser(), userBalance.getUser());
		assertEquals(5, (long)userBalance.getTotal());
	}

	@Test
	public void testNew()
	{
		int createTime = Utils.currentTimeSeconds();

		ReturnType<UserBalance> retVal = getUserBalanceManager().increment(new UserBalance(getSecondUser(), getSecondBalance(), createTime), 100);
		assertTrue(retVal.getPost().exists());
		assertEquals(100, (long)retVal.getPost().getTotal());
		assertEquals(createTime, retVal.getPost().getCreateTimeSeconds());
		assertEquals(createTime, retVal.getPost().getUpdateTimeSeconds());
	}


	/*
	public void testCreateCollision()
	{
		UserBalance userBalance = new UserBalance();
		userBalance.setBalance(getFirstBalance());
		userBalance.setUser(getFirstUser());

		// Can't have negative amounts
		//
		assertEquals(0, (long)userBalance.getTotal());
		assertTrue(userBalance.incTotal(100));
		assertEquals(100, (long)userBalance.getTotal());
		assertFalse(userBalance.incTotal(-5000));
		assertEquals(100, (long)userBalance.getTotal());

		try
		{
			getUserBalanceManager().create(userBalance);
			fail("Collision detection failed");
		}
		catch (Errors errors)
		{
			assertTrue(errors.getHasErrors());

			ErrorMessage message =  errors.getError("userBalanceManager.create");
			assertEquals("msg.userBalanceManager.create.collision", message.getMessageKey());
			assertTrue(message.getException() instanceof DataAccessException);
		}
	}
	*/

	@Test
	public void testUpdate()
	{
		ReturnType<UserBalance> updateStatus = testUpdateImpl();

		assertNotNull(updateStatus.getPre());
		assertNotNull(updateStatus.getPost());
	}

	protected ReturnType<UserBalance> testUpdateImpl()
	{
		UserBalance userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), getFirstBalance());
		assertNotNull(userBalance);
		assertEquals(getFirstBalance(), userBalance.getBalance());
		assertEquals(getFirstUser(), userBalance.getUser());
		assertEquals(5, (long)userBalance.getTotal());

		ReturnType<UserBalance> retVal = getUserBalanceManager().increment(
			new UserBalance(getFirstUser(), getFirstBalance(), Utils.currentTimeSeconds()),
			-6);

		assertFalse(retVal.getPost().exists());

		retVal = getUserBalanceManager().increment(
			new UserBalance(getFirstUser(), getFirstBalance(), Utils.currentTimeSeconds()),
			8);
		assertTrue(retVal.getPost().exists());

		return retVal;
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	public UserBalanceRecordDao getUserBalanceRecordDao()
	{
		return (UserBalanceRecordDao) getDaoManager().getNewDao("base.JdbcUserBalanceRecordDao");
	}

	public Balance getFirstBalance()
	{
		return firstBalance;
	}

	public void setFirstBalance(Balance firstBalance)
	{
		this.firstBalance = firstBalance;
	}

	public Balance getSecondBalance()
	{
		return secondBalance;
	}

	public void setSecondBalance(Balance secondBalance)
	{
		this.secondBalance = secondBalance;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	@Qualifier("base.CachedUserBalanceManager")
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

}
