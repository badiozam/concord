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
package com.i4one.base.model.user.data;

import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;

/**
 * Manager for short-term user data. A typical use-case for these are verification
 * codes and session information.
 * 
 * @author Hamid Badiozamani
 */
public interface UserDatumManager extends PaginableManager<UserDatumRecord, UserDatum>
{
	/**
	 * Get a piece of data associated with a given user by name
	 * 
	 * @param user The user account
	 * @param name The name of the datum to retrieve
	 * 
	 * @return The user datum
	 */
	public UserDatum getUserDatum(User user, String name);

	/**
	 * Get a list of all data associated with a user
	 * 
	 * @param user The user account
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of all datum associated with the user
	 */
	public Set<UserDatum> getUserData(User user, PaginationFilter pagination);
}
