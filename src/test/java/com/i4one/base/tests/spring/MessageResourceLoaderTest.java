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
package com.i4one.base.tests.spring;

import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminManager;
import org.apache.velocity.exception.ResourceNotFoundException;
import static com.i4one.base.model.client.SingleClient.getRoot;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.tests.core.BaseManagerTest;
import com.i4one.base.spring.MessageResourceLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class MessageResourceLoaderTest extends BaseManagerTest
{
	private Message testMessage;

	private AdminManager adminManager;
	private MessageManager messageManager;

	private MessageResourceLoader messageResourceLoader;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		testMessage = new Message();
		testMessage.setClient(getFirstClient());
		testMessage.setLanguage("en");
		testMessage.setKey("msg.test.message");
		testMessage.setValue("Test message value");

		Admin firstAdmin = new Admin();
		firstAdmin.setUsername("i4one");
		firstAdmin.setPassword("i4one");
		firstAdmin = getAdminManager().authenticate(firstAdmin, getFirstClient());
		assertTrue(firstAdmin.exists());

		// Need to have this because the message manager is saving the message's history
		//
		logAdminIn(firstAdmin);

		messageManager.create(testMessage);
	}

	@Test
	public void testLoad() throws IOException
	{
		InputStream stream = getMessageResourceLoader().getResourceStream(getTestMessage().getClient().getName() + "/" + getTestMessage().getLanguage() + "/" + getTestMessage().getKey());
		assertNotNull(stream);

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String readerValue = reader.readLine();

		assertNotNull(readerValue);
		assertEquals(getTestMessage().getValue(), readerValue);

		// The same key should exist for the root client as well, which means we can access it the same way
		//
		stream = getMessageResourceLoader().getResourceStream(getRoot().getName() + "/" + getTestMessage().getLanguage() + "/" + getTestMessage().getKey());
		assertNotNull(stream);

		reader = new BufferedReader(new InputStreamReader(stream));
		readerValue = reader.readLine();

		assertNotNull(readerValue);
		assertEquals(getTestMessage().getValue(), readerValue);

		// We should not be able to get a non-existant language
		//
		try
		{
			stream = getMessageResourceLoader().getResourceStream(getTestMessage().getClient().getName() + "/es/" + getTestMessage().getKey());
			fail("Could not detect non-existant language");
		}
		catch (ResourceNotFoundException rnfe)
		{
			assertNotNull(rnfe);
		}

		try
		{
			stream = getMessageResourceLoader().getResourceStream(getTestMessage().getClient().getName() + "/" + getTestMessage().getLanguage() + "/nonexistantkey");
			fail("Could not detect non-existant key");
		}
		catch (ResourceNotFoundException rnfe)
		{
			assertNotNull(rnfe);
		}
	}

	public MessageResourceLoader getMessageResourceLoader()
	{
		return messageResourceLoader;
	}

	@Autowired
	public void setMessageResourceLoader(MessageResourceLoader messageResourceLoader)
	{
		this.messageResourceLoader = messageResourceLoader;
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public AdminManager getAdminManager()
	{
		return adminManager;
	}

	@Autowired
	public void setAdminManager(AdminManager adminManager)
	{
		this.adminManager = adminManager;
	}

	public Message getTestMessage()
	{
		return testMessage;
	}

	public void setTestMessage(Message testMessage)
	{
		this.testMessage = testMessage;
	}

}
