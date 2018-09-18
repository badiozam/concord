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
package com.i4one.base.dao.terminable;

import com.i4one.base.dao.ClientRecordTypeDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.util.List;

/**
 * Retrieves terminable records
 * 
 * @author Hamid Badiozamani
 * 
 * @param <T> The specific terminable type to retrieve 
 */
public interface TerminableClientRecordTypeDao<T extends TerminableClientRecordType> extends ClientRecordTypeDao<T>
{
	/**
	 * Gets all items that fall into the given range [startTime, endTime].
	 * Either parameter could be null to represent infinity
	 *
	 * @param startSeconds The start seconds to match
	 * @param endSeconds The end seconds to match
	 * @param pagination The pagination and qualifier info
	 * 
	 * @return A list of all items that match the given range
	 */
	public List<T> getByRange(Integer startSeconds, Integer endSeconds, PaginationFilter pagination);

	/**
	 * Gets all items that have a start time less than or equal to the given time and an
	 * end time that is greater than or equal to the given time
	 *
	 * @param asOf The time to match
	 * @param pagination The pagination and qualifier info
	 *
	 * @return A list of all items that match
	 */
	public List<T> getLive(int asOf, PaginationFilter pagination);

	/**
	 * Get all items that have a start time less than or equal to the value as set
	 * by setCurrentTime and an end time that is greater than or equal to the same.
	 * 
	 * @param pagination The pagination and qualifier info
	 * 
	 * @return A (potentially empty list) of all items that match
	 */
	public List<T> getLive(TerminablePagination pagination);

	/**
	 * Gets all items that fall into the given range [startTime, endTime]
	 * as set by the setStartTime and setEndTime
	 *
	 * @param pagination The pagination and qualifier info
	 * 
	 * @return A list of all items that match the given range
	 */
	public List<T> getByRange(TerminablePagination pagination);
}
