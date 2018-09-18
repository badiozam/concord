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
package com.i4one.base.tests.model.admin;

import com.i4one.base.model.adminhistory.AdminHistoryRecord;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class HistoricalAdminPrivilegeManagerTest extends PrivilegedAdminPrivilegeManagerTest
{
	private AdminPrivilegeManager adminPrivilegeManager;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}

	@Test
	@Override
	public void testGrant()
	{
		super.testGrant();

		List<AdminHistoryRecord> historyRecords = getAdminHistoryRecordDao().getAdminHistoryByAdmin(getSecondPrivAdmin().getSer(), new ClientPagination(getFirstClient(), SimplePaginationFilter.NONE));
		assertNotNull(historyRecords);
		assertTrue(historyRecords.size() > 0);

		boolean found = false;
		for ( AdminHistoryRecord currHistoryRecord : historyRecords)
		{
			assertEquals(getSecondPrivAdmin().getSer(), currHistoryRecord.getAdminid());
			assertEquals(getFirstClient().getSer(), currHistoryRecord.getClientid());

			if ( currHistoryRecord.getAction().equals("grant") && currHistoryRecord.getFeature().equals("base.clientadmins") )
			{
				found = true;
				break;
			}
		}

		assertTrue(found);
	}

	@Test
	@Override
	public void testRevoke()
	{
		super.testRevoke();

		List<AdminHistoryRecord> historyRecords = getAdminHistoryRecordDao().getAdminHistoryByAdmin(getSecondPrivAdmin().getSer(), new ClientPagination(getFirstClient(), SimplePaginationFilter.NONE));
		assertNotNull(historyRecords);
		assertTrue(historyRecords.size() > 0);

		boolean found = false;
		for ( AdminHistoryRecord currHistoryRecord : historyRecords)
		{
			assertEquals(getSecondPrivAdmin().getSer(), currHistoryRecord.getAdminid());
			assertEquals(getFirstClient().getSer(), currHistoryRecord.getClientid());

			if ( currHistoryRecord.getAction().equals("revoke") && currHistoryRecord.getFeature().equals("base.clientadmins") )
			{
				found = true;
				break;
			}
		}

		assertTrue(found);
	}

	@Override
	public AdminPrivilegeManager getAdminPrivilegeManager()
	{
		return adminPrivilegeManager;
	}

	@Autowired
	public void setAdminPrivilegeManager(AdminPrivilegeManager adminPrivilegeManager)
	{
		this.adminPrivilegeManager = adminPrivilegeManager;
	}
}
