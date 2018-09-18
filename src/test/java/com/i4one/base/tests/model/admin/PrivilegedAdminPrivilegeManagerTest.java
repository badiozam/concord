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
package com.i4one.base.tests.model.admin;

import com.i4one.base.model.PermissionDeniedException;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.Privilege;
import com.i4one.base.model.manager.PrivilegedManager;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class PrivilegedAdminPrivilegeManagerTest extends SimpleAdminPrivilegeManagerTest
{
	private AdminPrivilegeManager privilegedAdminPrivilegeManager;

	private Privilege GRANT_PRIVILEGE;
	private Privilege REVOKE_PRIVILEGE;

	private Admin secondPrivAdmin;
	private AdminManager adminManager;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		GRANT_PRIVILEGE = ((PrivilegedManager)getPrivilegedAdminPrivilegeManager()).getWritePrivilege();
		REVOKE_PRIVILEGE = GRANT_PRIVILEGE;

		secondPrivAdmin = new Admin();
		secondPrivAdmin.setUsername("asdf");
		secondPrivAdmin.setPassword("password");
		secondPrivAdmin.setName("Hamid Badiozamani");
		secondPrivAdmin.setEmail("asdf@m2omedia.com");

		getAdminRecordDao().insert(secondPrivAdmin.getDelegate());
		getAdminRecordDao().updatePassword(secondPrivAdmin.getDelegate(), secondPrivAdmin.getPassword());

		ClientAdminPrivilege adminPriv = new ClientAdminPrivilege();
		adminPriv.setAdmin(secondPrivAdmin);
		adminPriv.setPrivilege(GRANT_PRIVILEGE);
		adminPriv.setClient(getFirstClient());

		Admin rootAdmin = new Admin();
		rootAdmin.setUsername("i4one");
		rootAdmin.setPassword("i4one");
		rootAdmin = getAdminManager().authenticate(rootAdmin, getFirstClient());
		assertTrue(rootAdmin.exists());

		logAdminIn(rootAdmin);
		getPrivilegedAdminPrivilegeManager().grant(adminPriv);

		// The second admin is the super user now with the privileges to maniupulate
		// the first admin's rights
		//
		logAdminIn(adminPriv.getAdmin());

		// Clear the cache after every run
		//
		getPrivilegedAdminPrivilegeManager().init();
	}

	@Test
	public void testPrivilegedAccess()
	{
		// First we give grant privileges to the first admin
		//
		ClientAdminPrivilege grantPriv = new ClientAdminPrivilege();
		grantPriv.setAdmin(getFirstAdmin());
		grantPriv.setClient(getFirstClient());
		grantPriv.setPrivilege(GRANT_PRIVILEGE);

		/*
		 * This is already taken care of by the super class
		 *
		getPrivilegedAdminPrivilegeManager().grant(grantPriv);
		*/

		// Then we revoke the second admin's privileges
		//
		ClientAdminPrivilege revokePriv = new ClientAdminPrivilege();
		revokePriv.setAdmin(getSecondPrivAdmin());
		revokePriv.setClient(getFirstClient());
		revokePriv.setPrivilege(REVOKE_PRIVILEGE);
		getPrivilegedAdminPrivilegeManager().revoke(revokePriv);

		// At this point, we have no privileges to grant or revoke any more
		//
		try
		{
			grantPriv = new ClientAdminPrivilege();
			grantPriv.setAdmin(getFirstAdmin());
			grantPriv.setClient(getFirstClient());
			grantPriv.setPrivilege(GRANT_PRIVILEGE);

			getPrivilegedAdminPrivilegeManager().grant(grantPriv);
			fail("Failed to check grant permissions.");
		}
		catch (PermissionDeniedException errors)
		{
			assertEquals("msg.adminPrivilegeManager.grant.denied", errors.getErrorMessage().getMessageKey());
		}

		try
		{
			revokePriv = new ClientAdminPrivilege();
			revokePriv.setAdmin(getFirstAdmin());
			revokePriv.setClient(getFirstClient());
			revokePriv.setPrivilege(REVOKE_PRIVILEGE);

			getPrivilegedAdminPrivilegeManager().revoke(grantPriv);
			fail("Failed to check revoke permissions.");
		}
		catch (PermissionDeniedException errors)
		{
			assertEquals("msg.adminPrivilegeManager.revoke.denied", errors.getErrorMessage().getMessageKey());
		}
	}

	/**
	 * We override this method so that the super-classes test methods
	 * are run again using the privileged AdminPrivilege manager.
	 *
	 * @return The privileged AdminPrivilege manager which should satisfy
	 * 	all of the super-class test cases
	 */
	@Override
	public AdminPrivilegeManager getAdminPrivilegeManager()
	{
		return getPrivilegedAdminPrivilegeManager();
	}

	public AdminPrivilegeManager getPrivilegedAdminPrivilegeManager()
	{
		return privilegedAdminPrivilegeManager;
	}

	@Autowired
	public void setPrivilegedAdminPrivilegeManager(AdminPrivilegeManager privilegedAdminPrivilegeManager)
	{
		this.privilegedAdminPrivilegeManager = privilegedAdminPrivilegeManager;
	}

	public AdminManager getAdminManager()
	{
		return adminManager;
	}

	@Autowired
	public void setAdminManager(AdminManager adminManager)
	{
		this.adminManager = adminManager;
	}

	public Admin getSecondPrivAdmin()
	{
		return secondPrivAdmin;
	}

	public void setSecondPrivAdmin(Admin secondAdmin)
	{
		this.secondPrivAdmin = secondAdmin;
	}

}