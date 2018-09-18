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
package com.i4one.base.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class keeps a list of objects and removes the oldest
 * item from the map if the data structure hits it's max limit
 *
 * @author Hamid Badiozamani
 * 
 * @param <T> The key type
 * @param <U> The value type
 */
public class LRUMap<T, U> extends LinkedHashMap<T, U>
{
	static final long serialVersionUID = 42L;

	/**
	 * Holds the maximum size of this map
	 */
	private int maxSize = 0;

	/**
	 * The default constructor
	 *
	 * @param maxSize The maximum size of this map
	 */
	public LRUMap(int maxSize)
	{
		// The documentation for HashMap states that if:
		//
		//      initialCapacity = maxCapacity / loadFactor
		//
		//                      <=>
		//
		//      loadFactor = maxCapacity / initialCapacity
		//
		// then no rehash()es are done. Therefore, we choose
		// a load factor of 1 since we always know we can't
		// grow beyond the capacity given
		//

		// Construct the LinkedHashMap with this size as the
		// initial size, keep one more slot open for any
		// overflows that may happen
		//
		super( maxSize + 1, 1, true );

		// Store this value for later use
		//
		this.maxSize = maxSize;
	}

	/**
	 * This method determines whether the oldest item
	 * is to be removed or not
	 *
	 * @param element This represents the entry that is
	 *	to be removed if we return true
	 *
	 * @return true if the oldest item is to be removed, false otherwise
	 */
	@Override
	public boolean removeEldestEntry(Map.Entry<T,U> element)
	{
		// Remove it if we've hit our capacity (meaning
		// we've overflowed)
		//
		return size() > maxSize;
	}

	public int getMaxSize()
	{
		return maxSize;
	}

	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}
}
