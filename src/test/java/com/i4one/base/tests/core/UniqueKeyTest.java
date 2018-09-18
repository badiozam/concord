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

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.UniqueKey;
import com.i4one.base.model.i18n.IString;
import com.i4one.promotion.model.code.Code;
import com.i4one.promotion.model.trivia.Trivia;
import java.util.function.Consumer;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Before;

/**
 * @author Hamid Badiozamani
 */
public class UniqueKeyTest extends BaseLoggable
{
	Code code1;
	Code code2;

	Trivia trivia1;
	Trivia trivia2;

	@Before
	public void setUp()
	{
		code1 = new Code();
		code2 = new Code();

		code1.setCode("code1");
		code1.setStartTimeSeconds(5);
		code1.setEndTimeSeconds(10);

		code2.setCode("code2");
		code2.setStartTimeSeconds(5);
		code2.setEndTimeSeconds(10);

		trivia1 = new Trivia();
		trivia2 = new Trivia();

		trivia1.setTitle(new IString("trivia1"));
		trivia1.setStartTimeSeconds(5);
		trivia1.setEndTimeSeconds(10);

		trivia2.setTitle(new IString("trivia2"));
		trivia2.setStartTimeSeconds(5);
		trivia2.setEndTimeSeconds(10);
	}

	@Test
	public void testEqualsByKeyDirect()
	{
		UniqueKey key1 = new UniqueKey(code1);
		UniqueKey key2 = new UniqueKey(code2);

		code1.setCode("code2");
		assertTrue(key1.equals(key2));
	}

	@Test
	public void testInequalityDirect()
	{
		UniqueKey key1 = new UniqueKey(code1);
		UniqueKey key2 = new UniqueKey(code2);

		assertFalse(key1.equals(key2));
	}

	@Test
	public void testEqualsBySerDirect()
	{
		UniqueKey key1 = new UniqueKey(code1);
		UniqueKey key2 = new UniqueKey(code2);

		code1.getDelegate().setSer(1);
		code2.getDelegate().setSer(1);

		assertTrue(key1.equals(key2));
	}

	@Test
	public void testEqualsByKey()
	{
		code1.setCode("code2");
		assertTrue(code1.equals(code2));
		assertTrue(code2.equals(code1));
	}

	@Test
	public void testInequality()
	{
		assertFalse(code1.equals(code2));
		assertFalse(code2.equals(code1));
	}

	@Test
	public void testEqualsBySer()
	{
		code1.getDelegate().setSer(1);
		code2.getDelegate().setSer(1);

		assertTrue(code1.equals(code2));
		assertTrue(code2.equals(code1));
	}

	public void testSerComparison()
	{
		code1.getDelegate().setSer(1);
		code2.getDelegate().setSer(1);

		assertTrue(code1.getDelegate().getSer() == code2.getDelegate().getSer());
		assertTrue(code2.getDelegate().getSer() == code1.getDelegate().getSer());
	}

	@Test
	public void testEqualsPerformance()
	{
		int numIterations = 1000000;

		getLogger().debug("Equality by key over {} iterations:", numIterations);
		long byKeyTime = testPerformance(numIterations, (i) -> { testEqualsByKey(); });
		long bySerTime = testPerformance(numIterations, (i) -> { testEqualsBySer(); });
		long intComparison = testPerformance(numIterations, (i) -> { testSerComparison(); });

		getLogger().debug("By key: {} ms", byKeyTime);
		getLogger().debug("By ser: {} ms (diff w/ by key = {})", bySerTime, byKeyTime - bySerTime);
		getLogger().debug("By direct int: {} ms ( diff w/ by ser = {})", intComparison, bySerTime - intComparison);
	}

	@Test
	public void testInequalityPerformance()
	{
		int numIterations = 1000000;

		getLogger().debug("Inequality by key over {} iterations:", numIterations);
		long byKeyTime = testPerformance(numIterations, (i) -> { testInequality(); });

		getLogger().debug("By key: {} ms", byKeyTime);
	}

	public long testPerformance(int numIterations, Consumer<Integer> consumer)
	{
		long startMillis = System.currentTimeMillis();
		for ( int i = 0; i < numIterations; i++ )
		{
			consumer.accept(i);
		}
		return System.currentTimeMillis() - startMillis;
	}
}
