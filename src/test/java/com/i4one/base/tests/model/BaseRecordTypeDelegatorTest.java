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
package com.i4one.base.tests.model;

import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.tests.core.BaseManagerTest;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseRecordTypeDelegatorTest<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseManagerTest
{
	public abstract T newItem();

	@Test
	public void testEqualityBySer()
	{
		T item1 = newItem();
		T item2 = newItem();

		item1.setSer(5);
		item2.setSer(5);

		assertTrue(item1.equals(item2));
		assertTrue(item2.equals(item1));
	}

	@Test
	public void testEqualityConsistentWithComparison()
	{
		T item1 = newItem();
		T item2 = newItem();

		item1.setSer(0);
		item2.setSer(0);

		// The equals method may be overridden in subclasses to use
		// something other than the uniqueKeyInternal() implementation
		// for better performance. This tests that both results are
		// consistent.
		//
		assertEquals(item1.uniqueKey().compareTo(item2.uniqueKey()) == 0, item1.equals(item2));
	}
}
