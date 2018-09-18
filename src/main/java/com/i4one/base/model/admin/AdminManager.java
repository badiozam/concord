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
package com.i4one.base.model.admin;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.AuthenticationManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface AdminManager extends Manager<AdminRecord,Admin>, AuthenticationManager<Admin>
{
	/**
	 * Set a newly generated password for the admin
	 * 
	 * @param admin The admin whose password to send/reset
	 * 
	 * @return The admin record as retrieved from the database as well as a
	 * 	"resetPassword" mapping to the newly generated password
	 * 
	 * @throws java.lang.Exception 
	 */
	public ReturnType<Admin> resetPassword(Admin admin) throws java.lang.Exception;

	/**
	 * Update an administrator's password
	 * 
	 * @param admin The admin whose password we are to update
	 * 
	 * @return Whether the update was successful or not
	 */
	public ReturnType<Admin> updatePassword(Admin admin);

	/**
	 * Get a list of all of the administrators that a supervisor can manage
	 * 
	 * @param supervisor The supervisor administrator
	 * @param pagination The pagination filter to use
	 * 
	 * @return A (potentially empty) list of administrators that the supervisor
	 * 	can manage.
	 */
	public Set<Admin> getAdmins(Admin supervisor, PaginationFilter pagination);
}