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
package com.i4one.base.model.manager.terminable;

import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.dao.terminable.TerminableClientRecordTypeDao;
import com.i4one.base.model.manager.pagination.PaginableManager;
import java.util.Set;

/**
 * This interface extends the standard manager interface to include methods
 * that deal with time-sensitive objects
 *
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public interface TerminableManager<U extends TerminableClientRecordType, T extends TerminableClientType<U>> extends PaginableManager<U, T>
{
	/**
	 * Get all items that are valid as of the given pagination filter's time.
	 *
	 * @param pagination The pagination filter, in addition to the 
	 * 	client the filter's current time is used to retrieve live
	 * 	items for a particular time.
	 *
	 * @return The (potentially empty) set of currently valid items
	 */
	public Set<T> getLive(TerminablePagination pagination);

	/**
	 * Get all items in a given range as specified by the pagination
	 *
	 * @param pagination The pagination/ordering information
	 *
	 * @return A list of items in the given range
	 */
	public Set<T> getByRange(TerminablePagination pagination);

	@Override
	public TerminableClientRecordTypeDao<U> getDao();
}
