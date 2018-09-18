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
package com.i4one.base.tests.core;

import org.junit.Test;
import com.i4one.base.core.AgingLRUMap;
import com.i4one.base.core.AgingObject;
import static java.lang.Integer.valueOf;
import static java.lang.Thread.sleep;
import org.junit.Before;

import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class AgedLRUMapTest
{
	private AgingLRUMap<Integer, Integer> map;

	// Max age for each object is 100ms
	//
	private static final int MAXAGE = 100;
	private static final int MAXSIZE = 3;

	@Before
	public void setUp()
	{
		map = new AgingLRUMap<>(MAXSIZE, MAXAGE);
		map.put(0, 1);
		map.put(1, 1);
		map.put(2, 2);
	}

	@Test
	public void testAgedObject() throws InterruptedException
	{
		AgingObject<Integer> agedObject = new AgingObject<>(42, MAXAGE);
		assertNotNull(agedObject.getObject());
		assertEquals(valueOf(42), agedObject.getObject());

		sleep(MAXAGE + 1);

		assertNull(agedObject.getObject());

		// The object should be valid from birth to birth + age milliseconds
		//
		for ( int i = 0; i < MAXAGE; i++ )
		{
			long currTime = agedObject.getBirthTime() + i;
			assertEquals(valueOf(42), agedObject.getObject(currTime));
		}
	}

	@Test
	public void testSetsAndGets() throws InterruptedException
	{
		assertEquals(map.get(0), valueOf(1));
		assertEquals(map.get(1), valueOf(1));
		assertEquals(map.get(2), valueOf(2));
		assertEquals(3, map.size());

		sleep(MAXAGE + 1);

		// Despite having expired objects in there the size
		// remains the same
		//
		assertEquals(3, map.size());

		assertNull(map.get(0));
		assertNull(map.get(1));
		assertNull(map.get(2));

		// Having attempted to access the expired elements should
		// have removed them from the map
		//
		assertEquals(0, map.size());
		assertTrue(map.isEmpty());
	}

	@Test
	public void testDropOff()
	{
		// First make the first and second items the most recently used
		//
		Integer val = map.get(1);
		assertEquals(val, valueOf(1));

		val = map.get(0);
		assertEquals(val, valueOf(1));

		// Now add one more element
		//
		map.put(3, 3);

		// Here both the first and second element should still exist
		//
		assertEquals(valueOf(1), map.get(0));
		assertEquals(valueOf(1), map.get(1));
		assertEquals(valueOf(3), map.get(3));
		assertNull(map.get(2));
	}

	@Test
	public void testOverwrite() throws InterruptedException
	{
		assertEquals(valueOf(2), map.get(2));
		map.put(2, 5);
		assertEquals(valueOf(5), map.get(2));

		// Add more elements
		//
		map.put(3, 3);
		map.put(4, 5);
		map.put(5, 8);

		// All of the first three elements should have been overwritten
		//
		assertNull(map.get(0));
		assertNull(map.get(1));
		assertNull(map.get(2));

		// Make sure the new values work
		//
		assertEquals(valueOf(3), map.get(3));
		assertEquals(valueOf(5), map.get(4));
		assertEquals(valueOf(8), map.get(5));

		// Now, let's sleep for a little to let the objects age
		//
		sleep(MAXAGE / 2 + 1);

		// Re-insert values for a few items
		map.put(3, 3);
		map.put(4, 4);

		sleep(MAXAGE / 2 + 1);

		// Here the oldest element should be removed, but the newly
		// inserted ones still have some time left
		//
		assertNull(map.get(5));
		assertEquals(valueOf(3), map.get(3));
		assertEquals(valueOf(4), map.get(4));
	}
}
