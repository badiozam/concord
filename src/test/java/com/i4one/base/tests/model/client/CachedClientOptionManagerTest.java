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

import org.junit.Before;
import com.i4one.base.model.client.SingleClient;
import static com.i4one.base.model.client.SingleClient.getRoot;
import com.i4one.base.model.client.ClientOption;
import com.i4one.base.model.client.ClientOptionManager;
import static java.lang.Thread.sleep;
import java.util.Objects;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class CachedClientOptionManagerTest extends SimpleClientOptionManagerTest
{
	private Long maxAge;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		getCachedClientOptionManager().init();

		// Maximum age is 1 second
		//
		maxAge = 1000L;
		assertNotNull(getMaxAge());
	}

	@Test
	public void testCacheExpiration() throws InterruptedException
	{
		SingleClient client = getCachedSingleClientManager().getClient("first");

		// First create an entry the normal way, note that since this key
		// is brand new, it will be created under the "ROOT" node and not
		// test "test" client which we're setting
		//
		ClientOption option = new ClientOption();
		option.setClient(client);
		option.setKey("test.expirationKey");
		option.setValue("test.value");

		getClientOptionManager().update(option);
		assertTrue(option.exists());

		// This ensures that the value is cached
		//
		ClientOption cachedOption = getClientOptionManager().getOption(client, option.getKey());
		assertNotNull(cachedOption);
		assertTrue(cachedOption.exists());
		assertNotSame(option, cachedOption);
		assertEquals(option.getKey(), cachedOption.getKey());
		assertEquals(option.getValue(), cachedOption.getValue());

		// Here we update the value of the option directly in the database
		//
		ClientOption directUpdate = new ClientOption();
		directUpdate.getDelegate().setSer(option.getSer());
		directUpdate.setClient(client);
		directUpdate.setKey("test.expirationKey");
		directUpdate.setValue("db value");

		getClientOptionRecordDao().updateBySer(directUpdate.getDelegate());
		assertTrue(directUpdate.exists());

		// The cached value should not reflect this change yet
		//
		ClientOption staleCacheOption = getClientOptionManager().getOption(client, "test.expirationKey");
		assertNotNull(staleCacheOption);
		assertNotSame(directUpdate, staleCacheOption);
		assertEquals(directUpdate.getKey(), staleCacheOption.getKey());
		assertFalse(directUpdate.getValue().equals(staleCacheOption.getValue()));
		assertFalse(Objects.equals(client.getSer(), staleCacheOption.getClient().getSer()));
		assertEquals(getRoot().getSer(), staleCacheOption.getClient().getSer());

		// Now we wait for the cache to expire and test again
		//
		sleep(getMaxAge() + 1);

		ClientOption updatedCacheOption = getClientOptionManager().getOption(client, "test.expirationKey");
		assertNotNull(updatedCacheOption);
		assertNotSame(directUpdate, updatedCacheOption);
		assertEquals(directUpdate.getKey(), updatedCacheOption.getKey());
		assertEquals(directUpdate.getValue(), updatedCacheOption.getValue());
		assertEquals(client.getSer(), updatedCacheOption.getClient().getSer());
	}

	@Override
	public ClientOptionManager getClientOptionManager()
	{
		return getCachedClientOptionManager();
	}

	public Long getMaxAge()
	{
		return maxAge;
	}

}