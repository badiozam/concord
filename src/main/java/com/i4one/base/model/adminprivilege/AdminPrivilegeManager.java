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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface AdminPrivilegeManager extends Manager<ClientAdminPrivilegeRecord,ClientAdminPrivilege>
{
	/**
	 * Get a list of privileges for the given admin and client
	 *
	 * @param admin The admin to look up
	 * @param client The client for which to check the admin's privileges
	 * @param pagination The pagination/ordering information
	 *
	 * @return A (potentially empty) list of privileges this admin has
	 *	 for the given client
	 */
	public Set<ClientAdminPrivilege> getAdminPrivileges(Admin admin, SingleClient client, PaginationFilter pagination);

	/**
	 * Get a list of privileges for the given admin and client
	 *
	 * @param admin The admin to look up
	 * @param privilege The privilege to check
	 * @param pagination The pagination/ordering information
	 *
	 * @return A (potentially empty) list of privileges this admin has
	 *	 for the given client
	 */
	public Set<ClientAdminPrivilege> getAdminPrivileges(Admin admin, Privilege privilege, PaginationFilter pagination);

	/**
	 * Get a list of privileges for the given admin
	 *
	 * @param admin The admin to look up
	 * @param pagination The pagination/ordering information
	 *
	 * @return A (potentially empty) list of privileges this admin has
	 *	 for the given client
	 */
	public Set<ClientAdminPrivilege> getAdminPrivileges(Admin admin, PaginationFilter pagination);

	/**
	 * Get a list of privileges for the given admin and client
	 *
	 * @param client The client for which to check the admin's privileges
	 * @param pagination The pagination/ordering information
	 *
	 * @return A (potentially empty) list of privileges this admin has
	 *	 for the given client
	 */
	public Set<ClientAdminPrivilege> getAdminPrivileges(SingleClient client, PaginationFilter pagination);

	/**
	 * Check to see if the given admin has the given privilege, updates the
	 * input parameter's information with that of the database.
	 *
	 * @param priv The privilege to check
	 *
	 * @return True if the admin has the given privilege, false otherwise
	 */
	public boolean hasAdminPrivilege(ClientAdminPrivilege priv);

	/**
	 * Give an administrator a privilege
	 *
	 * @param priv The privilege to give
	 * 
	 * @return The newly granted privilege
	 */
	public ReturnType<ClientAdminPrivilege> grant(ClientAdminPrivilege priv);


	/**
	 * Revoke a privilege from an administrator
	 *
	 * @param priv The privilege to revoke
	 * 
	 * @return The newly revoked privilege
	 */
	public ReturnType<ClientAdminPrivilege> revoke(ClientAdminPrivilege priv);
}
