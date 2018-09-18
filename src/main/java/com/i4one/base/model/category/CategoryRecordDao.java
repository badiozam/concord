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
package com.i4one.base.model.category;

import com.i4one.base.dao.ClientRecordTypeDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 * @param <U>
 */
public interface CategoryRecordDao<U extends CategoryRecord> extends ClientRecordTypeDao<U>
{
	/**
	 * Look up a category by client/title
	 *
	 * @param title The title of the prize
	 * @param pagination The qualifier/sorting info
	 *
	 * @return The prize matching the client/title values or null if not found.
	 */
	public U getByTitle(String title, PaginationFilter pagination);

	/**
	 * Get all categories using pagination filtering
	 * 
	 * @param pagination The qualifier/sorting info
	 * 
	 * @return A (potentially empty) list of categories
	 */
	public List<U> getAllCategories(PaginationFilter pagination);

	/**
	 * Tests whether the given item has any member objects
	 * 
	 * @param item The record to test
	 * 
	 * @return True if the item has no member objects, false otherwise
	 */
	public boolean isEmpty(U item);
}
