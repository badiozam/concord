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

import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public interface CategoryManager<U extends CategoryRecord, T extends Category<U>> extends Manager<U,T>
{
	/**
	 * Look up a prize by title
	 *
	 * @param title The title of the event
	 * @param pagination The pagination/sort ordering
	 *
	 * @return The event or a non-existent Event object
	 */
	public T getCategory(String title, PaginationFilter pagination);


	/**
	 * Get all categories for a given client
	 * 
	 * @param pagination The pagination/sort ordering
	 * 
	 * @return A (potentially empty) list of all categories for that client
	 */
	public Set<T> getAllCategories(PaginationFilter pagination);

	/**
	 * Whether the given category has any member objects
	 * 
	 * @param item The category to check
	 * 
	 * @return True if the category has member objects, false otherwise
	 */
	public boolean isEmpty(T item);
	
	@Override
	public T emptyInstance();
}
