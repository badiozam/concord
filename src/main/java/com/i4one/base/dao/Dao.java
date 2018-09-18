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
package com.i4one.base.dao;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;

/**
 * A Data Access Object that retrieves objects from a particular source 
 * 
 * @author Hamid Badiozamani
 * 
 * @param <U> The type of object to retrieve
 */
public interface Dao<U extends RecordType>
{
	/**
	 * Retrieve the type by serial number
	 *
	 * @param ser The serial number of the object
	 *
	 * @return The matching object or null if not found
	 */
	public U getBySer(int ser);

	/**
	 * Retrieve the type by serial number
	 *
	 * @param ser The serial number of the object
	 * @param forUpdate Whether to lock the record for update thru
	 * 	the end of the transaction
	 *
	 * @return The matching object or null if not found
	 */
	public U getBySer(int ser, boolean forUpdate);

	/**
	 * Create a new record
	 *
	 * @param t The record to create
	 */
	public void insert(U t);

	/**
	 * Update an existing record
	 *
	 * @param t The record to update
	 */
	public void updateBySer(U t);

	/**
	 * Update an existing record's columns
	 *
	 * @param t The record to update
	 * @param columnList The list of table columns to update
	 */
	public void updateBySer(U t, String... columnList);

	/**
	 * Remove an existing record
	 *
	 * @param ser The record's serial number to remove
	 */
	public void deleteBySer(int ser);

	/**
	 * Returns the default sort order for this DAO
	 * 
	 * @return The sort order column
	 */
	public String getOrderBy();

	public void processReport(U sample, TopLevelReport report);

	public void processReport(U sample, ClassificationReport<U, ?> report, PaginationFilter pagination);
}