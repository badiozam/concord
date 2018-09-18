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
package com.i4one.base.model.emailblast.target;

import java.util.Set;

/**
 * Interface that builds a list of items. The list builder can incorporate other
 * builders whose lists can be ANDed or ORed together for a final result.
 *
 * @param <T> The type of items being managed by the list builder.
 * 
 * @author Hamid Badiozamani
 */
public interface ListBuilder<T extends Object>
{
	/**
	 * Build the list of items.
	 * 
	 * @return A (potentially empty) set of items that match
	 * 	the current requirements.
	 */
	public Set<T> build();

	/**
	 * Tests whether a given item belongs in this list or not.
	 * 
	 * @param item The item to test.
	 * 
	 * @return True if the item belongs, false otherwise.
	 */
	public boolean belongs(T item);

	/**
	 * When building the list, intersect the results with the given
	 * list's results. 
	 * 
	 * @param list The list to AND with.
	 */
	public void andWithList(ListBuilder<T> list);


	/**
	 * When building the list union results with the given
	 * list's results. 
	 * 
	 * @param list The list to AND with.
	 */
	public void orWithList(ListBuilder<T> list);

	/**
	 * Returns a unique identifier for this list. The string
	 * should be as compact as possible while uniquely identifying
	 * this list and all its sublists.
	 * 
	 * @return Th unique identifier for this list.
	 */
	public String uniqueKey();
}
