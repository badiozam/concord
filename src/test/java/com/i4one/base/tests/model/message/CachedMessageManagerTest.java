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

import static com.i4one.base.model.client.SingleClient.getRoot;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.message.MessageRecord;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class CachedMessageManagerTest extends SimpleMessageManagerTest
{
	private Long maxAge;

	private MessageManager cachedMessageManager;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		maxAge = 1000L;
		assertNotNull(getMaxAge());
	}

	@Test
	public void testGetCachedMessage() throws InterruptedException
	{
		Message cachedMessage = getMessageManager().getMessage(getFirstClient(), getFirstKey(), getFirstLanguage());
		assertNotNull(cachedMessage);
		assertTrue(cachedMessage.exists());
		assertEquals(getFirstLanguage(), cachedMessage.getLanguage());
		assertEquals(getFirstKey(), cachedMessage.getKey());
		assertEquals(getFirstValue(), cachedMessage.getValue());

		MessageRecord dbRecord = getMessageRecordDao().getMessage(getRoot().getSer(), getFirstLanguage(), getFirstKey());
		assertNotNull(dbRecord);
		dbRecord.setValue(getFirstValue() + getSecondValue());
		getMessageRecordDao().updateBySer(dbRecord);
		assertEquals((long)dbRecord.getSer(), (long)cachedMessage.getSer());

		// Here the cache should not have been updated yet
		//
		Message stillCachedMessage = getMessageManager().getMessage(getFirstClient(), getFirstKey(), getFirstLanguage());
		assertNotNull(stillCachedMessage);
		assertTrue(stillCachedMessage.exists());
		assertEquals(cachedMessage, stillCachedMessage);
		assertEquals(getFirstLanguage(), stillCachedMessage.getLanguage());
		assertEquals(getFirstKey(), stillCachedMessage.getKey());
		assertEquals(getFirstValue(), stillCachedMessage.getValue());

		// Sleep a little to allow the cache to expire
		//
		Thread.sleep(maxAge + 1);

		Message dbMessage = getMessageManager().getMessage(getFirstClient(), getFirstKey(),  getFirstLanguage());
		assertNotNull(dbMessage);
		assertTrue(dbMessage.exists());
		assertEquals(getFirstLanguage(), dbMessage.getLanguage());
		assertEquals(getFirstKey(), dbMessage.getKey());
		assertEquals(getFirstValue() + getSecondValue(), dbMessage.getValue());
	}

	@Test
	public void testGetCachedAllMessages() throws InterruptedException
	{
		String partialKey = getFirstKey();
		partialKey = partialKey.substring(0, partialKey.indexOf('.'));

		List<Message> cachedMessages = getMessageManager().getAllMessages(getFirstClient(), partialKey, getFirstLanguage(), SimplePaginationFilter.NONE);
		assertNotNull(cachedMessages);
		assertFalse(cachedMessages.isEmpty());
		for ( Message cachedMessage : cachedMessages )
		{
			assertTrue(cachedMessage.exists());
			assertTrue(cachedMessage.getKey().startsWith(partialKey));
		}

		// Here the cache should not have been updated yet
		//
		List<Message> stillCachedMessages = getMessageManager().getAllMessages(getFirstClient(), partialKey, getFirstLanguage(), SimplePaginationFilter.NONE);
		assertNotNull(stillCachedMessages);
		assertFalse(stillCachedMessages.isEmpty());
		assertSame(cachedMessages, stillCachedMessages);

		// Sleep a little to allow the cache to expire
		//
		Thread.sleep(maxAge + 1);

		List<Message> dbMessages = getMessageManager().getAllMessages(getFirstClient(), partialKey, getFirstLanguage(), SimplePaginationFilter.NONE);
		assertNotNull(dbMessages);
		assertFalse(dbMessages.isEmpty());
		assertNotSame(cachedMessages, dbMessages);
		assertNotSame(stillCachedMessages, dbMessages);
	}

	public Long getMaxAge()
	{
		return maxAge;
	}

	@Override
	public MessageManager getMessageManager()
	{
		return getCachedMessageManager();
	}

	public MessageManager getCachedMessageManager()
	{
		return cachedMessageManager;
	}

	@Autowired
	public void setCachedMessageManager(MessageManager cachedMessageManager)
	{
		this.cachedMessageManager = cachedMessageManager;
	}

}
