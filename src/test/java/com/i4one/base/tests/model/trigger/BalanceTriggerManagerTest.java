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
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.BalanceTriggerManager;
import com.i4one.base.model.balancetrigger.BalanceTriggerRecord;
import com.i4one.base.model.balancetrigger.BalanceTriggerRecordDao;
import com.i4one.base.tests.core.BaseManagerTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class BalanceTriggerManagerTest extends BaseManagerTest
{
	private BalanceTriggerManager balanceTriggerManager;

	private BalanceTrigger firstTrigger;
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

		assertTrue(points.exists());
		assertTrue(trigger1.exists());

		firstTrigger = new BalanceTrigger();
		firstTrigger.setOwnedDelegate(trigger1);

		// A couple random features to associate with the first trigger
		//
		getBalanceTriggerRecordDao().associate(getFirstAdmin().getFeatureName(), getFirstAdmin().getSer(), trigger1.getSer());
		getBalanceTriggerRecordDao().associate(getSecondClient().getFeatureName(), getSecondClient().getSer(), trigger1.getSer());
		getBalanceTriggerRecordDao().associate("balanceTriggerManager.update", 0, trigger1.getSer());
	}

	@Test
	public void testGetByFeature()
	{
		Set<BalanceTrigger> triggers = getBalanceTriggerManager().getAllTriggersByFeature(getFirstAdmin(), SimplePaginationFilter.NONE);
		assertFalse(triggers.isEmpty());
		assertTrue(triggers.iterator().next().equals(firstTrigger));

		triggers = getBalanceTriggerManager().getAllTriggersByFeature(getFirstClient(), SimplePaginationFilter.NONE);
		assertTrue(triggers.isEmpty());

		triggers = getBalanceTriggerManager().getAllTriggersByFeature(getSecondClient(), SimplePaginationFilter.NONE);
		assertFalse(triggers.isEmpty());
		assertTrue(triggers.iterator().next().equals(firstTrigger));
	}

	@Test
	public void testGetByManager()
	{
		Set<BalanceTrigger> triggers = getBalanceTriggerManager().getAllTriggersByManager(getBalanceTriggerManager(), "update", SimplePaginationFilter.NONE);
		assertFalse(triggers.isEmpty());
		assertTrue(triggers.iterator().next().equals(firstTrigger));

		triggers = getBalanceTriggerManager().getAllTriggersByManager(getSimpleAdminManager(), "update", SimplePaginationFilter.NONE);
		assertTrue(triggers.isEmpty());
	}

	@Test
	public void testAssociate()
	{
		Set<BalanceTrigger> associatedTriggers = getBalanceTriggerManager().getAllTriggersByFeature(getFirstClient(), SimplePaginationFilter.NONE);
		assertTrue(associatedTriggers.isEmpty());

		BalanceTrigger secondTrigger = new BalanceTrigger();
		secondTrigger.setClient(getFirstClient());
		secondTrigger.setAmount(500);
		secondTrigger.setBalance(firstBalance);
		secondTrigger.setStartTimeSeconds(Utils.currentTimeSeconds() - 7);
		secondTrigger.setEndTimeSeconds(Utils.currentTimeSeconds() + 7);
		secondTrigger.setFrequency(5);
		secondTrigger.setMaxGlobalUsage(0);
		secondTrigger.setMaxUserUsage(0);
		secondTrigger.setSynced(true);
		secondTrigger.setTitle(new IString("en", "Trigger 2"));
		secondTrigger.setExclusive(true);

		List<BalanceTrigger> triggers = new ArrayList<>();
		triggers.add(firstTrigger);
		triggers.add(secondTrigger);

		List<ReturnType<BalanceTrigger>> triggerRets = getBalanceTriggerManager().updateExclusiveBalanceTriggers(getFirstClient(), triggers);
		assertFalse(triggerRets.isEmpty());
		assertEquals(triggerRets.size(), triggers.size());

		triggerRets.stream().forEach((retVal) ->
		{
			assertTrue(retVal.getPost().exists());
			assertTrue(triggers.contains(retVal.getPost()));
		});

		associatedTriggers = getBalanceTriggerManager().getAllTriggersByFeature(getFirstClient(), SimplePaginationFilter.NONE);
		assertFalse(associatedTriggers.isEmpty());
		assertEquals(associatedTriggers.size(), triggers.size());
		assertTrue(associatedTriggers.containsAll(triggers));
	}

	@Test
	public void testDissociate()
	{
		Set<BalanceTrigger> associatedTriggers;
		
		// Test to ensure that the triggers exist
		//
		associatedTriggers = getBalanceTriggerManager().getAllTriggersByManager(getBalanceTriggerManager(), "update", SimplePaginationFilter.NONE);
		assertFalse(associatedTriggers.isEmpty());

		associatedTriggers = getBalanceTriggerManager().getAllTriggersByFeature(getSecondClient(), SimplePaginationFilter.NONE);
		assertFalse(associatedTriggers.isEmpty());

		associatedTriggers.forEach( (trigger) ->
		{
			getBalanceTriggerManager().dissociate(getSecondClient(), trigger);
		});

		associatedTriggers = getBalanceTriggerManager().getAllTriggersByFeature(getSecondClient(), SimplePaginationFilter.NONE);
		assertTrue(associatedTriggers.isEmpty());

		// Test dissociation by manager. The trigger should not have been deleted if it was still associated with a manager
		//
		associatedTriggers = getBalanceTriggerManager().getAllTriggersByManager(getBalanceTriggerManager(), "update", SimplePaginationFilter.NONE);
		assertFalse(associatedTriggers.isEmpty());

		associatedTriggers.forEach( (trigger) ->
		{
			getBalanceTriggerManager().dissociate(getBalanceTriggerManager(), "update", trigger);
		});

		associatedTriggers = getBalanceTriggerManager().getAllTriggersByManager(getBalanceTriggerManager(), "update", SimplePaginationFilter.NONE);
		assertTrue(associatedTriggers.isEmpty());

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

	public BalanceTriggerManager getBalanceTriggerManager()
	{
		return balanceTriggerManager;
	}

	@Autowired
	public void setBalanceTriggerManager(BalanceTriggerManager balanceTriggerManager)
	{
		this.balanceTriggerManager = balanceTriggerManager;
	}

	public BalanceTriggerRecordDao getBalanceTriggerRecordDao()
	{
		return (BalanceTriggerRecordDao) getDaoManager().getNewDao("base.JdbcBalanceTriggerRecordDao");
	}
}
