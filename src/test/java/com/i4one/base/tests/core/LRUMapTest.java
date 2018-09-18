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
import com.i4one.base.core.LRUMap;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class LRUMapTest
{
	private LRUMap<Integer, Integer> map;

	public LRUMapTest()
	{
	}

	@Before
	public void setUp()
	{
		// Create a map that is three items large
		//
		map = new LRUMap<>(3);

		// Populate the map with the numbers corresponding to the Fibonacci sequence
		//
		map.put(0, 1);
		map.put(1, 1);
		map.put(2, 2);
	}

	@Test
	public void testSetsAndGets()
	{
		assertEquals(map.get(0), Integer.valueOf(1));
		assertEquals(map.get(1), Integer.valueOf(1));
		assertEquals(map.get(2), Integer.valueOf(2));
	}

	@Test
	public void testDropOff()
	{
		// First make the first and second items the most recently used
		//
		Integer val = map.get(1);
		assertEquals(val, Integer.valueOf(1));

		val = map.get(0);
		assertEquals(val, Integer.valueOf(1));

		// Now add one more element
		//
		map.put(3, 3);

		// Here both the first and second element should still exist
		//
		assertEquals(Integer.valueOf(1), map.get(0));
		assertEquals(Integer.valueOf(1), map.get(1));
		assertEquals(Integer.valueOf(3), map.get(3));
		assertNull(map.get(2));
	}

	@Test
	public void testOverwrite()
	{
		assertEquals(Integer.valueOf(2), map.get(2));
		map.put(2, 5);
		assertEquals(Integer.valueOf(5), map.get(2));

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
		assertEquals(Integer.valueOf(3), map.get(3));
		assertEquals(Integer.valueOf(5), map.get(4));
		assertEquals(Integer.valueOf(8), map.get(5));
	}
}

