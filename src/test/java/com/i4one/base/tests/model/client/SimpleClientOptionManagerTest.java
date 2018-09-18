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
package com.i4one.base.tests.model.client;

import com.i4one.base.model.ReturnType;
import com.i4one.base.tests.core.BaseManagerTest;
import org.junit.Test;
import com.i4one.base.model.client.ClientOption;
import com.i4one.base.model.client.SingleClient;
import static com.i4one.base.model.client.SingleClient.getRoot;
import com.i4one.base.model.client.ClientOptionManager;
import java.util.Objects;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SimpleClientOptionManagerTest extends BaseManagerTest
{
	private ClientOptionManager simpleClientOptionManager;

	@Test
	public void testGetOption()
	{
		SingleClient rootClient = getRoot();

		ClientOption option = getClientOptionManager().getOption(rootClient, "options.language");
		assertNotNull(option);
		assertEquals(rootClient.getSer(), option.getClient().getSer());
		assertEquals("options.language", option.getKey());
		assertEquals("en", option.getValue());

		// The test firstClient doesn't have the language options redefined, and the manager
		// should inherit it from the root node
		//
		assertNotNull(getFirstClient());

		ClientOption childOption = getClientOptionManager().getOption(getFirstClient(), "options.language");
		assertNotNull(childOption);
		assertFalse(Objects.equals(getFirstClient().getSer(), childOption.getClient().getSer()));
		assertEquals(option.getKey(), childOption.getKey());
		assertEquals(option.getValue(), childOption.getValue());
	}

	@Test
	public void testOverrideOption()
	{
		ReturnType<ClientOption> updateStatus = testOverrideOptionImpl();
		assertNotNull(updateStatus.getPre());
		assertNotNull(updateStatus.getPost());
	}

	protected ReturnType<ClientOption> testOverrideOptionImpl()
	{
		SingleClient rootClient = getRoot();

		ClientOption option = getClientOptionManager().getOption(rootClient, "options.language");
		assertNotNull(option);
		assertEquals(rootClient.getSer(), option.getClient().getSer());
		assertEquals("options.language", option.getKey());
		assertEquals("en", option.getValue());

		// The test firstClient doesn't have the language options redefined, and the manager
		// should inherit it from the root node
		//
		SingleClient childClient = getFirstClient();
		assertNotNull(childClient);

		ClientOption childOption = getClientOptionManager().getOption(childClient, "options.language");
		assertNotNull(childOption);
		assertEquals(rootClient.getSer(), childOption.getClient().getSer());

		childOption.setClient(childClient);
		childOption.setValue("es");
		ReturnType<ClientOption> retVal = getClientOptionManager().update(childOption);

		// Test to make sure the database reflects the change
		//
		ClientOption dbChildOption = getClientOptionManager().getOption(childClient, "options.language");
		assertNotNull(dbChildOption);
		assertEquals(childOption.getSer(), dbChildOption.getSer());
		assertEquals(childOption.getKey(), dbChildOption.getKey());
		assertEquals(childOption.getValue(), dbChildOption.getValue());

		return retVal;
	}

	@Test
	public void testNewOption()
	{
		SingleClient client = getFirstClient();

		ClientOption dbOption1 = getClientOptionManager().getOption(client, "test.key");
		ClientOption option = getClientOptionManager().getOption(client, "test.key");
		assertNotNull(option);
		assertFalse(option.exists());

		option = new ClientOption();
		option.setClient(client);
		option.setKey("test.key");
		option.setValue("test.value");

		getClientOptionManager().update(option);

		ClientOption dbOption = getClientOptionManager().getOption(client, "test.key");
		assertNotNull(dbOption);
		assertNotSame(option, dbOption);
		assertEquals(option.getKey(), dbOption.getKey());
		assertEquals(option.getValue(), dbOption.getValue());
		assertFalse(Objects.equals(client.getSer(), dbOption.getClient().getSer()));
	}

	public ClientOptionManager getClientOptionManager()
	{
		return getSimpleClientOptionManager();
	}

	public ClientOptionManager getSimpleClientOptionManager()
	{
		return simpleClientOptionManager;
	}

	@Autowired
	public void setSimpleClientOptionManager(ClientOptionManager simpleClientOptionManager)
	{
		this.simpleClientOptionManager = simpleClientOptionManager;
	}

}
