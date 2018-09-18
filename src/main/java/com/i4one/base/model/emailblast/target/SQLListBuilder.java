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
 * A list builder loaded from an SQL statement. The implementations should
 * ideally return a list of integers that match a set of pre-defined qualifiers.
 * For example, a CodeResponseListBuilder.
 * 
 * @author Hamid Badiozamani
 */
public interface SQLListBuilder extends ListBuilder<Integer>
{
	/**
	 * Get the column name that represents this integer.
	 * 
	 * @return The set of columns
	 */
	public String getColumnName();

	/**
	 * Gets all of the tables that are involved with this builder. The list
	 * would include any tables that belong to the and and or lists.
	 * 
	 * @return The list of tables involved with this builder.
	 */
	public Set<String> getTables();

	/**
	 * Get the conditions that need to be ANDed together.
	 * 
	 * @return The AND conditions.
	 */
	public Set<String> getAndConditions();

	/**
	 * Get the conditions that need to be ORed together.
	 * 
	 * @return The OR conditions.
	 */
	public Set<String> getOrConditions();

	/**
	 * Build the SQL statement that will ultimately return the integers.
	 * 
	 * @return The SQL statement that will return the integers.
	 */
	public String buildSQL();

	/**
	 * Intersect the results with the given list.
	 * 
	 * @param list The list to intersect the results with.
	 */
	public void andWithList(SQLListBuilder list);

	/**
	 * Union the results with the given list.
	 * 
	 * @param list The list to union the results with.
	 */
	public void orWithList(SQLListBuilder list);
}
