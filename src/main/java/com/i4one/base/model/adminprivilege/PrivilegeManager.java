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
package com.i4one.base.model.adminprivilege;

import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface PrivilegeManager extends Manager<PrivilegeRecord, Privilege>
{
	/**
	 * Get the set of all available privileges
	 * 
	 * @param pagination The pagination filter to use when retrieving the items
	 * 
	 * @return The set of all available privileges
	 */
	public Set<Privilege> getAllPrivileges(PaginationFilter pagination);

	/**
	 * Looks up a privilege to see if it exists
	 *
	 * @param featureName The feature the privilege grants access to
	 * @param hasWrite Whether the privilege is read-only or read-write
	 *
	 * @return The record in the database or the input parameter
	 * 	if the record doesn't exist
	 */
	public Privilege lookupPrivilege(String featureName, boolean hasWrite);

	/**
	 * Looks up a privilege to see if it exists
	 *
	 * @param priv The privilege to look up
	 *
	 * @return The record in the database or the input parameter
	 * 	if the record doesn't exist
	 */
	public Privilege lookupPrivilege(Privilege priv);
}
