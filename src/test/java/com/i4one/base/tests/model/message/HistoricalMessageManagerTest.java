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
package com.i4one.base.tests.model.message;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.adminhistory.AdminHistoryRecord;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.util.List;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class HistoricalMessageManagerTest extends PrivilegedMessageManagerTest
{
	private MessageManager historicalMessageManager;

	@Override
	public void testCreate()
	{
		super.testCreate();
		assertTrue(existsAdminHistoryRecord(getFirstAdmin(), getFirstClient(), "create"));
	}

	@Override
	public void testOverride()
	{
		ReturnType<Message> updateStatus = testOverrideImp();
		assertNotNull(updateStatus.getPre());
		assertNotNull(updateStatus.getPost());
		assertTrue(updateStatus.containsKey(BaseHistoricalManager.ATTR_ADMINHISTORY));
		assertTrue(existsAdminHistoryRecord(getFirstAdmin(), getFirstClient(), "update"));
	}

	private boolean existsAdminHistoryRecord(Admin admin, SingleClient client, String method)
	{
		List<AdminHistoryRecord> historyRecords = null;

		// Go all the way up to the root to find where the message might have been created
		//
		for (SingleClient currClient = client; currClient.exists(); currClient = currClient.getParent())
		{
			historyRecords = getAdminHistoryRecordDao().getAdminHistoryByAdmin(admin.getSer(), new ClientPagination(currClient, SimplePaginationFilter.NONE));
			if ( historyRecords.size() > 0)
			{
				break;
			}
		}

		assertNotNull(historyRecords);
		assertTrue(historyRecords.size() > 0);

		boolean found = false;
		for ( AdminHistoryRecord currHistoryRecord : historyRecords)
		{
			// The message can be anywhere along the tree
			//
			SingleClient historyClient = new SingleClient();
			historyClient.setSer(currHistoryRecord.getClientid());

			assertEquals(admin.getSer(), currHistoryRecord.getAdminid());
			assertTrue(client.belongsTo(historyClient));

			if ( currHistoryRecord.getAction().equals(method) && currHistoryRecord.getFeature().equals("base.messages") )
			{
				found = true;
				break;
			}
		}

		return found;
	}

	@Override
	public MessageManager getPrivilegedMessageManager()
	{
		return getHistoricalMessageManager();
	}

	public MessageManager getHistoricalMessageManager()
	{
		return historicalMessageManager;
	}

	@Autowired
	public void setHistoricalMessageManager(MessageManager messageManager)
	{
		this.historicalMessageManager = messageManager;
	}
}
