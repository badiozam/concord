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
package com.i4one.base.tests.model.user;

import com.i4one.base.model.PermissionDeniedException;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.Privilege;
import static com.i4one.base.model.client.SingleClient.getRoot;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class PrivilegedUserManagerTest extends SimpleUserManagerTest
{
	private AdminPrivilegeManager simpleAdminPrivilegeManager;

	private UserManager privilegedUserManager;
	private Privilege WRITE_PRIVILEGE;
	private Privilege READ_PRIVILEGE;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		WRITE_PRIVILEGE = ((PrivilegedManager)getUserManager()).getWritePrivilege();
		READ_PRIVILEGE = ((PrivilegedManager)getUserManager()).getReadPrivilege();

		assertNotNull(WRITE_PRIVILEGE);
		assertNotNull(READ_PRIVILEGE);

		ClientAdminPrivilege adminPriv = new ClientAdminPrivilege();
		adminPriv.setAdmin(getFirstAdmin());
		adminPriv.setPrivilege(WRITE_PRIVILEGE);
		adminPriv.setClient(getRoot());

		// Save the read privilege for the first admin bypassing any
		// privilege checking
		//
		getSimpleAdminPrivilegeManager().grant(adminPriv);

		adminPriv = new ClientAdminPrivilege();
		adminPriv.setAdmin(getFirstAdmin());
		adminPriv.setPrivilege(READ_PRIVILEGE);
		adminPriv.setClient(getRoot());

		// Save the read-write privilege for the first admin bypassing any
		// privilege checking
		//
		getCachedAdminPrivilegeManager().grant(adminPriv);

		// The second admin is the super user with the privileges to maniupulate
		// the first admin's rights
		//
		logAdminIn(getFirstAdmin());
	}

	@Test
	public void testLookup()
	{
		User u = new User();
		u.setUsername("hamid");
		u.setClient(getFirstClient());

		assertTrue(getUserManager().lookupUser(u).exists());
		assertFalse(u.exists());

		// Now test all of the conditions where the user should not exist
		//
		u = new User();
		u.setUsername("asdf1");
		u.setClient(getFirstClient());

		assertFalse(getUserManager().lookupUser(u).exists());
		assertFalse(u.exists());
	}

	@Test(expected=PermissionDeniedException.class)
	public void testLookupDenied() throws Exception
	{
		logAdminIn(getSecondAdmin());
		testLookup();
	}

	@Test(expected=PermissionDeniedException.class)
	public void testUpdateDenied() throws NoSuchAlgorithmException, Exception
	{
		User u = new User();
		u.setUsername(getFirstUser().getUsername());
		u.setClient(getFirstUser().getClient());
		u.setPassword("password");

		logAdminIn(getSecondAdmin());

		u.setUsername(getFirstUser().getUsername());
		u.setClient(getFirstUser().getClient());
		u.setPassword("password");

		User de = getUserManager().authenticate(u, u.getClient());
		assertNotNull(de);
		assertTrue(de.exists());

		logUserOut(de);

		de.setUsername("bob");

		getUserManager().update(de);
	}

	@Test
	public void testUpdateAllowed() throws Exception
	{
		getLogger().debug("Testing update allowed");

		logAdminIn(getSecondAdmin());
		logUserIn(getFirstUser());

		super.testUpdate();
	}

	@Override
	public UserManager getUserManager()
	{
		return getPrivilegedUserManager();
	}

	public UserManager getPrivilegedUserManager()
	{
		return privilegedUserManager;
	}

	@Autowired
	public void setPrivilegedUserManager(UserManager privilegedUserManager)
	{
		this.privilegedUserManager = privilegedUserManager;
	}

	public AdminPrivilegeManager getSimpleAdminPrivilegeManager()
	{
		return simpleAdminPrivilegeManager;
	}

	@Autowired
	public void setSimpleAdminPrivilegeManager(AdminPrivilegeManager simpleAdminPrivilegeManager)
	{
		this.simpleAdminPrivilegeManager = simpleAdminPrivilegeManager;
	}

}
