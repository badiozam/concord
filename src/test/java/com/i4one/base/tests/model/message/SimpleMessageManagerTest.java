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

import static com.i4one.base.core.Utils.currentTimeSeconds;
import com.i4one.base.model.ReturnType;
import static com.i4one.base.model.client.SingleClient.getRoot;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.message.MessageRecord;
import com.i4one.base.tests.core.BaseManagerTest;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SimpleMessageManagerTest extends BaseManagerTest
{
	private MessageManager simpleMessageManager;

	private String firstKey;
	private String secondKey;

	private String firstLanguage;
	private String secondLanguage;

	private String firstValue;
	private String secondValue;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		firstKey = "first.key";
		secondKey = "second.key";

		firstValue = "first Value";
		secondValue = "second Value";

		firstLanguage = "en";
		secondLanguage = "es";

		MessageRecord message = new MessageRecord();
		message.setClientid(getRoot().getSer());
		message.setKey(firstKey);
		message.setLanguage(firstLanguage);
		message.setValue(firstValue);
		message.setUpdateTime(currentTimeSeconds());

		getMessageRecordDao().insert(message);
		assertTrue(message.exists());

		message = new MessageRecord();
		message.setClientid(getRoot().getSer());
		message.setKey(secondKey);
		message.setLanguage(firstLanguage);
		message.setValue(secondValue);
		message.setUpdateTime(currentTimeSeconds());

		getMessageRecordDao().insert(message);
		assertTrue(message.exists());
	}

	@Test
	public void testGet()
	{
		Message message = getMessageManager().getMessage(getFirstClient(), getFirstKey(), getFirstLanguage());
		assertNotNull(message);
		assertTrue(message.exists());
		assertEquals(getRoot(), message.getClient());
		assertEquals(getFirstKey(), message.getKey());
		assertEquals(getFirstLanguage(), message.getLanguage());
		assertEquals(getFirstValue(), message.getValue());

		message = getMessageManager().getMessage(getFirstClient(), getFirstKey(), getSecondLanguage());
		assertNotNull(message);
		assertFalse(message.exists());

		message = getMessageManager().getMessage(getFirstClient(), getSecondKey(), getFirstLanguage());
		assertNotNull(message);
		assertTrue(message.exists());
		assertEquals(getRoot(), message.getClient());
		assertEquals(getSecondKey(), message.getKey());
		assertEquals(getFirstLanguage(), message.getLanguage());
		assertEquals(getSecondValue(), message.getValue());
	}

	@Test
	public void testGetAlll()
	{
		String partialKey = getFirstKey();
		partialKey = partialKey.substring(0, partialKey.indexOf("."));

		List<Message> messages = getMessageManager().getAllMessages(getFirstClient(), partialKey, getFirstLanguage(), SimplePaginationFilter.NONE);
		assertNotNull(messages);
		assertFalse(messages.isEmpty());
		for ( Message message : messages )
		{
			assertTrue(message.exists());
			assertTrue(message.getKey().startsWith(partialKey));
		}
	}

	@Test
	public void testCreate()
	{
		// This key has been created in the setup for the root client but for the first language.
		// Here we check to see if creating it for the second language works properly
		//
		Message message = new Message();
		message.setClient(getFirstClient());
		message.setKey(getSecondKey());
		message.setLanguage(getSecondLanguage());
		message.setValue(getSecondValue());

		getMessageManager().create(message);
		assertNotNull(message);
		assertTrue(message.exists());

		Message dbMessage = getMessageManager().getMessage(getFirstClient(), getSecondKey(),  getSecondLanguage());
		assertNotNull(dbMessage);
		assertTrue(dbMessage.exists());
		assertEquals(getSecondKey(), dbMessage.getKey());
		assertEquals(getSecondLanguage(), dbMessage.getLanguage());
		assertEquals(getSecondValue(), dbMessage.getValue());

		// Note that despite setting the client to firstClient above, create
		// recognizes the new key and creates it under the ROOT client node
		//
		assertEquals(getRoot(), dbMessage.getClient());
	}

	@Test
	public void testOverride()
	{
		ReturnType<Message> updateStatus = testOverrideImp();

		assertNotNull(updateStatus.getPre());
		assertNotNull(updateStatus.getPost());
	}

	protected ReturnType<Message> testOverrideImp()
	{
		Message parentMessage = getMessageManager().getMessage(getFirstClient(), getFirstKey());

		assertNotNull(parentMessage);
		assertTrue(parentMessage.exists());
		assertEquals(getRoot(), parentMessage.getClient());
		assertEquals(getFirstKey(), parentMessage.getKey());
		assertEquals(getFirstValue(), parentMessage.getValue());

		// The test client doesn't have the language options redefined, and the manager
		// should inherit it from the root node
		//
		Message overrideMessage = new Message();
		overrideMessage.setClient(getFirstClient());
		overrideMessage.setKey(getFirstKey());
		overrideMessage.setLanguage(parentMessage.getLanguage());
		overrideMessage.setValue(getFirstValue() + getSecondValue());
		ReturnType<Message> retVal = getMessageManager().update(overrideMessage);

		// Test to make sure the database reflects the change
		//
		Message dbMessage = getMessageManager().getMessage(getFirstClient(), getFirstKey());
		assertNotNull(dbMessage);
		assertEquals(overrideMessage.getSer(), dbMessage.getSer());
		assertEquals(overrideMessage.getKey(), dbMessage.getKey());
		assertEquals(overrideMessage.getValue(), dbMessage.getValue());

		return retVal;
	}

	public String getFirstKey()
	{
		return firstKey;
	}

	public void setFirstKey(String firstKey)
	{
		this.firstKey = firstKey;
	}

	public String getSecondKey()
	{
		return secondKey;
	}

	public void setSecondKey(String secondKey)
	{
		this.secondKey = secondKey;
	}

	public String getFirstLanguage()
	{
		return firstLanguage;
	}

	public void setFirstLanguage(String firstLanguage)
	{
		this.firstLanguage = firstLanguage;
	}

	public String getFirstValue()
	{
		return firstValue;
	}

	public void setFirstValue(String firstValue)
	{
		this.firstValue = firstValue;
	}

	public String getSecondLanguage()
	{
		return secondLanguage;
	}

	public void setSecondLanguage(String secondLanguage)
	{
		this.secondLanguage = secondLanguage;
	}

	public String getSecondValue()
	{
		return secondValue;
	}

	public void setSecondValue(String secondValue)
	{
		this.secondValue = secondValue;
	}

	public MessageManager getMessageManager()
	{
		return getSimpleMessageManager();
	}

	public MessageManager getSimpleMessageManager()
	{
		return simpleMessageManager;
	}

	@Autowired
	public void setSimpleMessageManager(MessageManager simpleMessageManager)
	{
		this.simpleMessageManager = simpleMessageManager;
	}
}
