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

import com.i4one.base.core.Base64;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class Base64Test
{
	private String testStr;

	@Before
	public void setUp()
	{
		testStr = "The quick brown fox jumped over the lazy dog.";
	}

	@Test
	public void testEncodeDecode()
	{
		// Encode the string
		//
		String encodedStr = Base64.encodeObject(testStr);

		// Make sure it was encoded properly
		//
		assertEquals(encodedStr, "rO0ABXQALVRoZSBxdWljayBicm93biBmb3gganVtcGVkIG92ZXIgdGhlIGxhenkgZG9nLg==");

		// Decode the string
		//
		String decodedStr = (String)Base64.decodeToObject(encodedStr);

		// Make sure it was decoded properly
		//
		assertEquals(decodedStr, "The quick brown fox jumped over the lazy dog.");
	}
}

