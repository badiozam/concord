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
package com.i4one.base.tests.model.instantwin;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.instantwin.InstantWinManager;
import com.i4one.base.model.instantwin.InstantWinRecord;
import com.i4one.base.model.instantwin.InstantWinRecordDao;
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
public class InstantWinManagerTest extends BaseManagerTest
{
	private InstantWinManager cachedInstantWinManager;

	private InstantWin firstInstantWin;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		// Create two different balances, one for points and one for a raffle entry
		//
		InstantWinRecord instantWin1 = new InstantWinRecord();
		instantWin1.setPercentwin(1.00f);
		instantWin1.setWinnerlimit(0);
		instantWin1.setTitle(new IString("en", "InstantWin 1"));
		instantWin1.setClientid(getFirstClient().getSer());
		instantWin1.setStarttm(Utils.currentTimeSeconds() - 5);
		instantWin1.setEndtm(Utils.currentTimeSeconds() + 5);
		instantWin1.setExclusive(Boolean.TRUE);
		getInstantWinRecordDao().insert(instantWin1);

		assertTrue(instantWin1.exists());

		firstInstantWin = new InstantWin();
		firstInstantWin.setOwnedDelegate(instantWin1);

		// A couple random features to associate with the first instantWin
		//
		getInstantWinRecordDao().associate(getFirstAdmin().getFeatureName(), getFirstAdmin().getSer(), instantWin1.getSer());
		getInstantWinRecordDao().associate(getSecondClient().getFeatureName(), getSecondClient().getSer(), instantWin1.getSer());
		getInstantWinRecordDao().associate(getInstantWinManager().getInterfaceName() + ".update", 0, instantWin1.getSer());
	}

	@Test
	public void testGetByFeature()
	{
		Set<InstantWin> instantWins = getInstantWinManager().getAllInstantWinsByFeature(getFirstAdmin(), SimplePaginationFilter.NONE);
		assertFalse(instantWins.isEmpty());
		assertTrue(instantWins.iterator().next().equals(firstInstantWin));

		instantWins = getInstantWinManager().getAllInstantWinsByFeature(getFirstClient(), SimplePaginationFilter.NONE);
		assertTrue(instantWins.isEmpty());

		instantWins = getInstantWinManager().getAllInstantWinsByFeature(getSecondClient(), SimplePaginationFilter.NONE);
		assertFalse(instantWins.isEmpty());
		assertTrue(instantWins.iterator().next().equals(firstInstantWin));
	}

	@Test
	public void testGetByManager()
	{
		Set<InstantWin> instantWins = getInstantWinManager().getAllInstantWinsByManager(getInstantWinManager(), "update", SimplePaginationFilter.NONE);
		assertFalse(instantWins.isEmpty());
		assertTrue(instantWins.iterator().next().equals(firstInstantWin));

		instantWins = getInstantWinManager().getAllInstantWinsByManager(getSimpleAdminManager(), "update", SimplePaginationFilter.NONE);
		getLogger().debug("Instant wins = " + instantWins + " w/ isEmpty = " + instantWins.isEmpty());
		assertTrue(instantWins.isEmpty());
	}

	@Test
	public void testAssociate()
	{
		Set<InstantWin> associatedInstantWins = getInstantWinManager().getAllInstantWinsByFeature(getFirstClient(), SimplePaginationFilter.NONE);
		assertTrue(associatedInstantWins.isEmpty());

		InstantWin secondInstantWin = new InstantWin();
		secondInstantWin.setClient(getFirstClient());
		secondInstantWin.setStartTimeSeconds(Utils.currentTimeSeconds() - 7);
		secondInstantWin.setEndTimeSeconds(Utils.currentTimeSeconds() + 7);
		secondInstantWin.setPercentWin(1.0f);
		secondInstantWin.setWinnerLimit(2);
		secondInstantWin.setTitle(new IString("en", "InstantWin 2"));
		secondInstantWin.setExclusive(true);

		List<InstantWin> instantWins = new ArrayList<>();
		instantWins.add(firstInstantWin);
		instantWins.add(secondInstantWin);

		List<ReturnType<InstantWin>> instantWinRets = getInstantWinManager().updateExclusiveInstantWins(getFirstClient(), instantWins);
		assertFalse(instantWinRets.isEmpty());
		assertEquals(instantWinRets.size(), instantWins.size());

		instantWinRets.stream().forEach((retVal) ->
		{
			assertTrue(retVal.getPost().exists());
			assertTrue(instantWins.contains(retVal.getPost()));
		});

		associatedInstantWins = getInstantWinManager().getAllInstantWinsByFeature(getFirstClient(), SimplePaginationFilter.NONE);
		assertFalse(associatedInstantWins.isEmpty());
		assertEquals(associatedInstantWins.size(), instantWins.size());
		assertTrue(associatedInstantWins.containsAll(instantWins));
	}

	@Test
	public void testDissociate()
	{
		Set<InstantWin> associatedInstantWins;
		
		// Test to ensure that the instantWins exist
		//
		associatedInstantWins = getInstantWinManager().getAllInstantWinsByManager(getInstantWinManager(), "update", SimplePaginationFilter.NONE);
		assertFalse(associatedInstantWins.isEmpty());

		associatedInstantWins = getInstantWinManager().getAllInstantWinsByFeature(getSecondClient(), SimplePaginationFilter.NONE);
		assertFalse(associatedInstantWins.isEmpty());

		associatedInstantWins.forEach( (instantWin) ->
		{
			getInstantWinManager().dissociate(getSecondClient(), instantWin);
		});

		associatedInstantWins = getInstantWinManager().getAllInstantWinsByFeature(getSecondClient(), SimplePaginationFilter.NONE);
		assertTrue(associatedInstantWins.isEmpty());

		// Test dissociation by manager. The instantWin should not have been deleted if it was still associated with a manager
		//
		associatedInstantWins = getInstantWinManager().getAllInstantWinsByManager(getInstantWinManager(), "update", SimplePaginationFilter.NONE);
		assertFalse(associatedInstantWins.isEmpty());

		associatedInstantWins.forEach( (instantWin) ->
		{
			getInstantWinManager().dissociate(getInstantWinManager(), "update", instantWin);
		});

		associatedInstantWins = getInstantWinManager().getAllInstantWinsByManager(getInstantWinManager(), "update", SimplePaginationFilter.NONE);
		assertTrue(associatedInstantWins.isEmpty());

	}

	public InstantWinManager getInstantWinManager()
	{
		return getCachedInstantWinManager();
	}

	public InstantWinManager getCachedInstantWinManager()
	{
		return cachedInstantWinManager;
	}

	@Autowired
	public void setCachedInstantWinManager(InstantWinManager cachedInstantWinManager)
	{
		this.cachedInstantWinManager = cachedInstantWinManager;
	}

	public InstantWinRecordDao getInstantWinRecordDao()
	{
		return (InstantWinRecordDao) getDaoManager().getNewDao("base.JdbcInstantWinRecordDao");
	}
}
