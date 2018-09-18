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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.adminhistory.AdminHistoryRecord;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.HistoricalManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import java.util.List;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Hamid Badiozamani
 */
public class HistoricalUserBalanceManagerTest extends PrivilegedUserBalanceManagerTest
{
	private UserBalanceManager historicalUserBalanceManager;

	@Override
	public void testUpdate()
	{
		ReturnType<UserBalance> updateStatus = testUpdateImpl();
		assertNotNull(updateStatus.getPre());
		assertNotNull(updateStatus.getPost());
		assertTrue(updateStatus.containsKey(BaseHistoricalManager.ATTR_ADMINHISTORY));

		assertTrue(existsAdminHistoryRecord(getFirstAdmin(), getFirstClient(), "increment"));
	}

	private boolean existsAdminHistoryRecord(Admin admin, SingleClient client, String method)
	{
		List<AdminHistoryRecord> historyRecords = getAdminHistoryRecordDao().getAdminHistoryByAdmin(admin.getSer(), new ClientPagination(client, SimplePaginationFilter.NONE));
		assertNotNull(historyRecords);
		assertTrue(historyRecords.size() > 0);

		boolean found = false;
		for ( AdminHistoryRecord currHistoryRecord : historyRecords)
		{
			assertEquals(admin.getSer(), currHistoryRecord.getAdminid());
			assertEquals(client.getSer(), currHistoryRecord.getClientid());

			String featureName = ((HistoricalManager)getHistoricalUserBalanceManager()).getFeatureName();
			if ( currHistoryRecord.getAction().equals(method) && currHistoryRecord.getFeature().equals(featureName) )
			{
				found = true;
				break;
			}
		}

		return found;
	}

	@Override
	public UserBalanceManager getUserBalanceManager()
	{
		return getHistoricalUserBalanceManager();
	}

	public UserBalanceManager getHistoricalUserBalanceManager()
	{
		return historicalUserBalanceManager;
	}

	@Autowired
	@Qualifier("base.HistoricalUserBalanceManager")
	public void setHistoricalUserBalanceManager(UserBalanceManager historicalUserBalanceManager)
	{
		this.historicalUserBalanceManager = historicalUserBalanceManager;
	}
}
