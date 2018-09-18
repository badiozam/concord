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
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.tests.core.BaseManagerTest;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
@Ignore
public abstract class SimpleClientManagerTest extends BaseManagerTest
{
	private BalanceManager balanceManager;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		logAdminIn(getRootAdmin());
	}

	@Test
	public void testRootClient()
	{
		SingleClient dbClient = getClientManager().getClient("ROOT");
		SingleClient rootClient = SingleClient.getRoot();

		assertNotNull(dbClient);
		assertNotNull(rootClient);
		assertEquals(dbClient.getSer(), rootClient.getSer());
		assertEquals(dbClient.getName(), rootClient.getName());
		assertEquals((long)0, (long)dbClient.getParent().getSer());
		assertEquals((long)0, (long)rootClient.getParent().getSer());
	}

	@Test
	public void testGet()
	{
		SingleClient byName = getClientManager().getClient("first");
		assertNotNull(byName);
		assertTrue(byName.exists());
		assertEquals("first", byName.getName());

		SingleClient bySer = getClientManager().getClient(byName.getSer());
		assertNotNull(bySer);
		assertTrue(bySer.exists());
		assertEquals("first", bySer.getName());
		assertEquals(byName, bySer);

		SingleClient nonExistent = getClientManager().getClient("does not exist");
		assertNotNull(nonExistent);
		assertFalse(nonExistent.exists());
	}

	@Test
	public void testCreate() throws Exception
	{
		SingleClient newClient = new SingleClient();
		newClient.setName("create");
		newClient.setParent(SingleClient.getRoot());
		newClient.setEmail("create@i4oneinteractive.com");
		newClient.setDescr("Testing create");

		getClientManager().create(newClient);
		assertTrue(newClient.exists());
		assertTrue(newClient.belongsTo(SingleClient.getRoot()));
	}

	@Test
	public void testCreateFail()
	{
		try
		{
			SingleClient existingClient = getClientManager().getClient("first");
			getClientManager().create(existingClient);
			fail("Creating an existing record check failed");
		}
		catch (Errors errors)
		{
			assertTrue(errors.hasErrors());

			ErrorMessage message = errors.getError("singleClientManager.create");
			assertEquals("msg.singleClientManager.create.oldrecord", message.getMessageKey());
		}

		try
		{
			SingleClient collisionClient = new SingleClient();
			collisionClient.setName("second");
			collisionClient.setParent(SingleClient.getRoot());
			collisionClient.setEmail("collision@i4oneinteractive.com");
			collisionClient.setDescr("Testing collision");

			getClientManager().create(collisionClient);
			fail("Collision detection failed");
		}
		catch (Errors errors)
		{
			assertTrue(errors.hasErrors());

			ErrorMessage message = errors.getError("singleClientManager.create");
			assertEquals("msg.singleClientManager.create.collision", message.getMessageKey());
		}

	}

	@Test
	public void testUpdate()
	{
		ReturnType<SingleClient> updateStatus = testUpdateImpl();
		assertNotNull(updateStatus.getPre());
		assertNotNull(updateStatus.getPost());
	}

	protected ReturnType<SingleClient> testUpdateImpl()
	{
		SingleClient secondClient = getClientManager().getClient("second");
		assertTrue(secondClient.exists());

		secondClient.setName("update");
		secondClient.setEmail("newemail@i4oneinteractive.com");
		secondClient.setDescr("new description");
		ReturnType<SingleClient> retVal = getClientManager().update(secondClient);

		SingleClient updatedClient = getClientManager().getClient(secondClient.getSer());
		assertTrue(updatedClient.exists());
		assertEquals(secondClient, updatedClient);
		assertEquals(secondClient.getName(), updatedClient.getName());
		assertEquals(secondClient.getEmail(), updatedClient.getEmail());
		assertEquals(secondClient.getDescr(), updatedClient.getDescr());

		return retVal;
	}

	@Test
	public void testUpdateFail()
	{
		SingleClient secondClient = getClientManager().getClient("second");
		assertTrue(secondClient.exists());

		try
		{
			secondClient.setName("first");
			getClientManager().update(secondClient);

			fail("Collision detection failed");
		}
		catch (Errors errors)
		{
			assertTrue(errors.hasErrors());

			ErrorMessage message = errors.getError("singleClientManager.update");
			assertEquals("msg.singleClientManager.update.collision", message.getMessageKey());
		}
	}

	@Test
	public void testRemove()
	{
		SingleClient secondClient = getClientManager().getClient("second");
		assertNotNull(secondClient);
		assertTrue(secondClient.exists());

		Balance secondClientBalance = getBalanceManager().getDefaultBalance(secondClient);
		getCachedBalanceManager().remove(secondClientBalance);
		SingleClient removedClient = getClientManager().remove(secondClient);
		assertNotNull(removedClient);
		assertTrue(removedClient.exists());
		assertEquals(removedClient, secondClient);

		SingleClient byName = getClientManager().getClient("second");
		assertNotNull(byName);
		assertFalse(byName.exists());

		SingleClient bySer = getClientManager().getClient(removedClient.getSer());
		assertNotNull(bySer);
		assertFalse(bySer.exists());
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

	public abstract SingleClientManager getClientManager();
}
