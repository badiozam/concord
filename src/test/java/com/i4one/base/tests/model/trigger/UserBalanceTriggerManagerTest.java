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
package com.i4one.base.tests.model.trigger;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceRecord;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.BalanceTriggerManager;
import com.i4one.base.model.balancetrigger.BalanceTriggerRecord;
import com.i4one.base.model.balancetrigger.BalanceTriggerRecordDao;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import com.i4one.base.model.balancetrigger.UserBalanceTriggerManager;
import com.i4one.base.model.balancetrigger.UserBalanceTriggerRecordDao;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.tests.core.BaseUserManagerTest;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class UserBalanceTriggerManagerTest extends BaseUserManagerTest
{
	private UserBalanceTriggerManager userBalanceTriggerManager;
	private UserBalanceManager userBalanceManager;
	private BalanceTriggerManager balanceTriggerManager;

	private BalanceTrigger firstTrigger;
	private BalanceTrigger secondTrigger;
	private BalanceTrigger syncedTrigger;

	private Balance firstBalance;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		// Create a balance
		//
		BalanceRecord points = new BalanceRecord();
		points.setClientid(getFirstClient().getSer());
		points.setFeatureid(getFirstClient().getSer());
		points.setFeature("clients");
		points.setSingleName(new IString("en", "Points"));
		points.setPluralName(new IString("en", "Point"));
		getBalanceRecordDao().insert(points);

		assertTrue(points.exists());

		firstBalance = new Balance();
		firstBalance.setOwnedDelegate(points);

		BalanceTriggerRecord trigger1 = new BalanceTriggerRecord();
		trigger1.setAmount(100);
		trigger1.setBalid(points.getSer());
		trigger1.setFrequency(1);
		trigger1.setMaxglobalusage(0);
		trigger1.setMaxuserusage(0);
		trigger1.setSynced(false);
		trigger1.setTitle(new IString("en", "Trigger 1"));
		trigger1.setClientid(getFirstClient().getSer());
		trigger1.setStarttm(Utils.currentTimeSeconds() - 5);
		trigger1.setEndtm(Utils.currentTimeSeconds() + 5);
		trigger1.setExclusive(Boolean.TRUE);
		getBalanceTriggerRecordDao().insert(trigger1);

		assertTrue(trigger1.exists());

		firstTrigger = new BalanceTrigger();
		firstTrigger.setOwnedDelegate(trigger1);

		BalanceTriggerRecord trigger2 = new BalanceTriggerRecord();
		trigger2.setAmount(100);
		trigger2.setBalid(points.getSer());
		trigger2.setFrequency(1);
		trigger2.setMaxglobalusage(0);
		trigger2.setMaxuserusage(2);
		trigger2.setSynced(false);
		trigger2.setTitle(new IString("en", "Trigger 2"));
		trigger2.setClientid(getFirstClient().getSer());
		trigger2.setStarttm(Utils.currentTimeSeconds() - 5);
		trigger2.setEndtm(Utils.currentTimeSeconds() + 5);
		trigger2.setExclusive(Boolean.TRUE);
		getBalanceTriggerRecordDao().insert(trigger2);

		assertTrue(trigger2.exists());

		secondTrigger = new BalanceTrigger();
		secondTrigger.setOwnedDelegate(trigger2);

		BalanceTriggerRecord syncedTriggerRecord = new BalanceTriggerRecord();
		syncedTriggerRecord.setAmount(100);
		syncedTriggerRecord.setBalid(points.getSer());
		syncedTriggerRecord.setFrequency(2);
		syncedTriggerRecord.setMaxglobalusage(0);
		syncedTriggerRecord.setMaxuserusage(0);
		syncedTriggerRecord.setSynced(true);
		syncedTriggerRecord.setTitle(new IString("en", "Synced Trigger"));
		syncedTriggerRecord.setClientid(getFirstClient().getSer());
		syncedTriggerRecord.setStarttm(Utils.currentTimeSeconds() - 5);
		syncedTriggerRecord.setEndtm(Utils.currentTimeSeconds() + 5);
		syncedTriggerRecord.setExclusive(Boolean.TRUE);
		getBalanceTriggerRecordDao().insert(syncedTriggerRecord);

		assertTrue(trigger2.exists());

		syncedTrigger = new BalanceTrigger();
		syncedTrigger.setOwnedDelegate(syncedTriggerRecord);

		// Associate the trigger to any manager update method for testing purposes only
		//
		getBalanceTriggerRecordDao().associate("balanceTriggerManager.update", 0, trigger1.getSer());
		getBalanceTriggerRecordDao().associate("siteGroupManager.update", 0, trigger2.getSer());
		getBalanceTriggerRecordDao().associate("singleClientManager.update", 0, syncedTriggerRecord.getSer());

		// We give the user some points in the new balance we just created
		//
		logUserIn(getFirstUser());

		UserBalance userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), getFirstBalance());
		assertNotNull(userBalance);
		assertFalse(userBalance.exists());
		assertEquals((long)0, (long)userBalance.getTotal());

		ReturnType<UserBalance> incrementedBalance = getUserBalanceManager().increment(
			new UserBalance(getFirstUser(), getFirstBalance(), Utils.currentTimeSeconds()),
			10);

		assertTrue(incrementedBalance.getPost().exists());
		assertEquals(getFirstBalance(), incrementedBalance.getPost().getBalance(false));
		assertEquals((long)10, (long)incrementedBalance.getPost().getTotal());
	}

	@Test
	public void testMultiUsage() throws InterruptedException
	{
		boolean isEligible = getUserBalanceTriggerManager().isEligible(getFirstTrigger(), getFirstUser());
		assertTrue(isEligible);

		List<ReturnType<UserBalanceTrigger>> processedTriggers = getUserBalanceTriggerManager().processTriggers(getFirstUser(), getFirstClient(), getBalanceTriggerManager(), "update", new BalanceTrigger(), SimplePaginationFilter.NONE);

		// Immediately following the processed triggers, the user should next be eligible at the frequency
		//
		int eligibleInSeconds = getUserBalanceTriggerManager().eligibleInSeconds(getFirstTrigger(), getFirstUser());
		assertEquals(getFirstTrigger().getFrequency(), eligibleInSeconds);

		assertNotNull(processedTriggers);
		assertFalse(processedTriggers.isEmpty());
		assertEquals(1, processedTriggers.size());

		ReturnType<UserBalanceTrigger> processedTrigger = processedTriggers.iterator().next();
		assertNotNull(processedTrigger);
		assertTrue(processedTrigger.getPost().exists());
		assertEquals(getFirstTrigger(), processedTrigger.getPost().getBalanceTrigger(false));

		// The user's balance should've gone up by 100
		//
		UserBalance userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), getFirstBalance());
		assertNotNull(userBalance);
		assertTrue(userBalance.exists());
		assertEquals((long)110, (long)userBalance.getTotal());


		// Shouldn't be eligible again at the same time
		//
		isEligible = getUserBalanceTriggerManager().isEligibleAt(getFirstTrigger(), getFirstUser(), processedTrigger.getPost().getTimeStampSeconds());
		assertFalse(isEligible);

		// The user should be available frequency seconds after it though
		//
		isEligible = getUserBalanceTriggerManager().isEligibleAt(getFirstTrigger(), getFirstUser(), processedTrigger.getPost().getTimeStampSeconds() + getFirstTrigger().getFrequency());
		assertTrue(isEligible);

		// Attempt to process it as well just to ensure the processing matches the eligibility test
		//
		processedTriggers = getUserBalanceTriggerManager().processTriggers(getFirstUser(), getFirstClient(), getBalanceTriggerManager(), "update", new BalanceTrigger(), SimplePaginationFilter.NONE);
		assertNotNull(processedTriggers);
		assertTrue(processedTriggers.isEmpty());

		// Wait for a second to allow the frequency time to elapse
		//
		Thread.sleep(getFirstTrigger().getFrequency() * 1000);
		getRequestState().init();

		isEligible = getUserBalanceTriggerManager().isEligible(getFirstTrigger(), getFirstUser());
		assertTrue(isEligible);

		eligibleInSeconds = getUserBalanceTriggerManager().eligibleInSeconds(getFirstTrigger(), getFirstUser());
		assertTrue(eligibleInSeconds <= 0);

		// Should be eligible again
		//
		processedTriggers = getUserBalanceTriggerManager().processTriggers(getFirstUser(), getFirstClient(), getBalanceTriggerManager(), "update", new BalanceTrigger(), SimplePaginationFilter.NONE);
		assertNotNull(processedTriggers);
		assertFalse(processedTriggers.isEmpty());
		assertEquals(1, processedTriggers.size());
	}

	@Test
	public void testLimitedUsage() throws InterruptedException
	{
		boolean isEligible = getUserBalanceTriggerManager().isEligible(getSecondTrigger(), getFirstUser());
		assertTrue(isEligible);

		// First process, the user has 1 more before the max user usage runs out
		//
		List<ReturnType<UserBalanceTrigger>> processedTriggers = getUserBalanceTriggerManager().processTriggers(getFirstUser(), getFirstClient(), getCachedSiteGroupManager(), "update", new SiteGroup(), SimplePaginationFilter.NONE);

		// Immediately following the processed triggers, the user should next be eligible at the frequency
		//
		int eligibleInSeconds = getUserBalanceTriggerManager().eligibleInSeconds(getSecondTrigger(), getFirstUser());
		assertEquals(getSecondTrigger().getFrequency(), eligibleInSeconds);

		assertNotNull(processedTriggers);
		assertFalse(processedTriggers.isEmpty());
		assertEquals(1, processedTriggers.size());

		ReturnType<UserBalanceTrigger> processedTrigger = processedTriggers.iterator().next();
		assertNotNull(processedTrigger);
		assertTrue(processedTrigger.getPost().exists());
		assertEquals(getSecondTrigger(), processedTrigger.getPost().getBalanceTrigger(false));

		// The user's balance should've gone up by 100
		//
		UserBalance userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), getFirstBalance());
		assertNotNull(userBalance);
		assertTrue(userBalance.exists());
		assertEquals((long)110, (long)userBalance.getTotal());


		// Shouldn't be eligible again at the same time
		//
		isEligible = getUserBalanceTriggerManager().isEligibleAt(getSecondTrigger(), getFirstUser(), processedTrigger.getPost().getTimeStampSeconds());
		assertFalse(isEligible);

		// The user should be available "frequency seconds" afterward
		//
		isEligible = getUserBalanceTriggerManager().isEligibleAt(getSecondTrigger(), getFirstUser(), processedTrigger.getPost().getTimeStampSeconds() + getSecondTrigger().getFrequency());
		assertTrue(isEligible);

		// Attempt to process it as well just to ensure the processing matches the eligibility test
		//
		processedTriggers = getUserBalanceTriggerManager().processTriggers(getFirstUser(), getFirstClient(), getCachedSiteGroupManager(), "update", new SiteGroup(), SimplePaginationFilter.NONE);
		assertNotNull(processedTriggers);
		assertTrue(processedTriggers.isEmpty());

		// Wait for a second to allow the frequency time to elapse
		//
		Thread.sleep(getSecondTrigger().getFrequency() * 1000);
		getRequestState().init();

		isEligible = getUserBalanceTriggerManager().isEligible(getSecondTrigger(), getFirstUser());
		assertTrue(isEligible);

		eligibleInSeconds = getUserBalanceTriggerManager().eligibleInSeconds(getSecondTrigger(), getFirstUser());
		assertTrue(eligibleInSeconds <= 0);

		// Now, since the max user usage is set to two and the user has processed the trigger once and the frequency
		// time has passed
		//
		processedTriggers = getUserBalanceTriggerManager().processTriggers(getFirstUser(), getFirstClient(), getCachedSiteGroupManager(), "update", new SiteGroup(), SimplePaginationFilter.NONE);
		assertNotNull(processedTriggers);
		assertFalse(processedTriggers.isEmpty());
		assertEquals(1, processedTriggers.size());

		userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), getFirstBalance());
		assertNotNull(userBalance);
		assertTrue(userBalance.exists());
		assertEquals((long)210, (long)userBalance.getTotal());

		// Wait for a second to allow the frequency time to elapse
		//
		Thread.sleep(getSecondTrigger().getFrequency() * 1000);
		getRequestState().init();

		isEligible = getUserBalanceTriggerManager().isEligible(getSecondTrigger(), getFirstUser());
		assertFalse(isEligible);

		// Note that eligibility time does not consider the maxUserUsage variable
		//
		eligibleInSeconds = getUserBalanceTriggerManager().eligibleInSeconds(getSecondTrigger(), getFirstUser());
		assertTrue(eligibleInSeconds <= 0);

		// Now, since the max user usage is set to two and the user has processed the trigger once and the frequency
		// time has passed
		//
		processedTriggers = getUserBalanceTriggerManager().processTriggers(getFirstUser(), getFirstClient(), getCachedSiteGroupManager(), "update", new SiteGroup(), SimplePaginationFilter.NONE);
		assertNotNull(processedTriggers);
		assertTrue(processedTriggers.isEmpty());
	}

	@Test
	public void testSyncedUsage() throws InterruptedException
	{
		boolean isEligible = getUserBalanceTriggerManager().isEligible(getSyncedTrigger(), getFirstUser());
		assertTrue(isEligible);

		List<ReturnType<UserBalanceTrigger>> processedTriggers = getUserBalanceTriggerManager().processTriggers(getFirstUser(), getFirstClient(), getCachedSingleClientManager(), "update", new SingleClient(), SimplePaginationFilter.NONE);

		// Ensure there's no eligibility for double play at the exact same time
		//
		int eligibleInSeconds = getUserBalanceTriggerManager().eligibleInSeconds(getSyncedTrigger(), getFirstUser());
		assertTrue(eligibleInSeconds > 0 && eligibleInSeconds <= getSyncedTrigger().getFrequency());

		assertNotNull(processedTriggers);
		assertFalse(processedTriggers.isEmpty());
		assertEquals(1, processedTriggers.size());

		ReturnType<UserBalanceTrigger> processedTrigger = processedTriggers.iterator().next();
		assertNotNull(processedTrigger);
		assertTrue(processedTrigger.getPost().exists());
		assertEquals(getSyncedTrigger(), processedTrigger.getPost().getBalanceTrigger(false));

		// The user's balance should've gone up by 100
		//
		UserBalance userBalance = getUserBalanceManager().getUserBalance(getFirstUser(), getFirstBalance());
		assertNotNull(userBalance);
		assertTrue(userBalance.exists());
		assertEquals((long)110, (long)userBalance.getTotal());
	}

	public UserBalanceTriggerManager getUserBalanceTriggerManager()
	{
		return userBalanceTriggerManager;
	}

	@Autowired
	public void setUserBalanceTriggerManager(UserBalanceTriggerManager userBalanceTriggerManager)
	{
		this.userBalanceTriggerManager = userBalanceTriggerManager;
	}

	public BalanceTriggerManager getBalanceTriggerManager()
	{
		return balanceTriggerManager;
	}

	@Autowired
	public void setBalanceTriggerManager(BalanceTriggerManager balanceTriggerManager)
	{
		this.balanceTriggerManager = balanceTriggerManager;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

	public BalanceTrigger getSecondTrigger()
	{
		return secondTrigger;
	}

	public void setSecondTrigger(BalanceTrigger secondTrigger)
	{
		this.secondTrigger = secondTrigger;
	}

	public BalanceTrigger getSyncedTrigger()
	{
		return syncedTrigger;
	}

	public void setSyncedTrigger(BalanceTrigger syncedTrigger)
	{
		this.syncedTrigger = syncedTrigger;
	}

	public BalanceTrigger getFirstTrigger()
	{
		return firstTrigger;
	}

	public void setFirstTrigger(BalanceTrigger firstTrigger)
	{
		this.firstTrigger = firstTrigger;
	}

	public Balance getFirstBalance()
	{
		return firstBalance;
	}

	public void setFirstBalance(Balance firstBalance)
	{
		this.firstBalance = firstBalance;
	}

	public UserBalanceTriggerRecordDao getUserBalanceTriggerRecordDao()
	{
		return (UserBalanceTriggerRecordDao) getDaoManager().getNewDao("base.JdbcUserBalanceTriggerRecordDao");
	}

	public BalanceTriggerRecordDao getBalanceTriggerRecordDao()
	{
		return (BalanceTriggerRecordDao) getDaoManager().getNewDao("base.JdbcBalanceTriggerRecordDao");
	}
}
