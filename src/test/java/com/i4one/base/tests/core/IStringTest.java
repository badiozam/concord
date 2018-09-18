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

import com.i4one.base.model.i18n.IString;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class IStringTest
{
	private IString istr;

	@Before
	public void init()
	{
		istr = new IString();

		istr.put("en", "English");
		istr.put("es", "Español");
	}

	@Test
	public void testGet()
	{
		assertEquals(istr.get("en"), "English");
		assertEquals(istr.get("es"), "Español");
	}

	@Test
	public void testSet()
	{
		istr.set("en", "American English");

		assertEquals(istr.get("en"), "American English");
		assertEquals(istr.get("es"), "Español");
	}

	@Test
	public void testBlank()
	{
		assertFalse(istr.isBlank());

		istr.set("en", "");
		assertFalse(istr.isBlank());

		istr.set("es", "");
		assertTrue(istr.isBlank());
	}

	@Test
	public void testAllNotBlank()
	{
		assertTrue(istr.isAllNotBlank());

		istr.set("en", "");
		assertFalse(istr.isAllNotBlank());

		istr.set("es", "");
		assertFalse(istr.isAllNotBlank());
	}

	@Test
	public void testEmptyConstructor()
	{
		IString test = new IString();

		assertTrue(test.isBlank());
		assertFalse(test.isAllNotBlank());
		assertNull(test.get("en"));

		test = new IString("test");
		assertFalse(test.isBlank());
		assertTrue(test.isAllNotBlank());
		assertNotNull(test.get("en"));
	}

	@Test
	public void testDefaultToFirst()
	{
		assertEquals("English", istr.get("fa"));
	}
}
