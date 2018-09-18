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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Hamid Badiozamani
 */
public class CachedClientManagerTest extends SimpleClientManagerTest
{
	private Long maxAge;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		getCachedSingleClientManager().init();

		maxAge = (Long) 1000L;
		assertNotNull(getMaxAge());
	}

	@Test
	public void testCacheExpiration() throws InterruptedException, Exception
	{
		logAdminIn(getRootAdmin());

		// First create an entry the normal way, note that since this key
		// is brand new, it will be created under the "ROOT" node and not
		// test "test" client which we're setting
		//
		SingleClient newClient = new SingleClient();
		newClient.setParent(SingleClient.getRoot());
		newClient.setEmail("cache@i4oneinteractive.com");
		newClient.setName("cache");
		newClient.setDescr("test create");

		getClientManager().create(newClient);
		assertTrue(newClient.exists());

		logAdminOut();

		// This ensures that the value is cached
		//
		SingleClient cachedClient = getClientManager().getClient(newClient.getSer());
		assertNotNull(cachedClient);
		assertTrue(cachedClient.exists());
		assertEquals(newClient.getName(), newClient.getName());
		assertEquals(newClient.getDescr(), newClient.getDescr());

		// Here we update the value of the option directly in the database
		//
		SingleClient directUpdate = new SingleClient();
		directUpdate.getDelegate().setParentid(SingleClient.getRoot().getSer());
		directUpdate.getDelegate().setName(newClient.getName());
		directUpdate.getDelegate().setSer(newClient.getSer());
		directUpdate.setDescr("db value");

		getClientRecordDao().updateBySer(directUpdate.getDelegate());
		assertTrue(directUpdate.exists());

		// The cached value should not reflect this change yet
		//
		SingleClient staleCacheClient = getClientManager().getClient(newClient.getSer());
		assertNotNull(staleCacheClient);
		assertNotSame(directUpdate, staleCacheClient);
		assertEquals(directUpdate.getName(), staleCacheClient.getName());
		assertFalse(directUpdate.getDescr().equals(staleCacheClient.getDescr()));

		// The two are equivalent because their serial numbers are the same
		//
		assertEquals(directUpdate, staleCacheClient);

		// Now we wait for the cache to expire and test again
		//
		Thread.sleep(getMaxAge() + 1);

		SingleClient updatedCacheOption = getClientManager().getClient(newClient.getSer());
		assertNotNull(updatedCacheOption);
		assertNotSame(directUpdate, updatedCacheOption);
		assertEquals(directUpdate, updatedCacheOption);
		assertEquals(directUpdate.getName(), updatedCacheOption.getName());
		assertEquals(directUpdate.getDescr(), updatedCacheOption.getDescr());
	}

	@Override
	public SingleClientManager getClientManager()
	{
		return getCachedSingleClientManager();
	}

	public Long getMaxAge()
	{
		return maxAge;
	}
}
