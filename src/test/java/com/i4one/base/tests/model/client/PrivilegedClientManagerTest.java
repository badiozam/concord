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
package com.i4one.base.tests.model.client;

import com.i4one.base.model.PermissionDeniedException;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.Privilege;
import static com.i4one.base.model.client.SingleClient.getRoot;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.manager.PrivilegedManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class PrivilegedClientManagerTest extends CachedClientManagerTest
{
	private AdminPrivilegeManager simpleAdminPrivilegeManager;

	private SingleClientManager privilegedSingleClientManager;
	private Privilege WRITE_PRIVILEGE;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		WRITE_PRIVILEGE = ((PrivilegedManager) getPrivilegedSingleClientManager()).getWritePrivilege();

		ClientAdminPrivilege adminPriv = new ClientAdminPrivilege();
		adminPriv.setAdmin(getFirstAdmin());
		adminPriv.setPrivilege(WRITE_PRIVILEGE);
		adminPriv.setClient(getRoot());

		// Save the read-write privilege for the first admin bypassing any
		// privilege checking
		//
		getSimpleAdminPrivilegeManager().grant(adminPriv);

		// The second admin is the super user with the privileges to maniupulate
		// the first admin's rights
		//
		logAdminIn(getFirstAdmin());
	}

	@Override
	public void testCreate() throws Exception
	{
		logAdminIn(getRootAdmin());
		super.testCreate();
	}

	@Test(expected=PermissionDeniedException.class)
	public void testCreateDenied() throws Exception
	{
		logAdminIn(getSecondAdmin());
		super.testCreate();
	}

	@Test(expected=PermissionDeniedException.class)
	public void testUpdateDenied() throws Exception
	{
		logAdminIn(getSecondAdmin());
		super.testUpdate();
	}

	@Test(expected=PermissionDeniedException.class)
	public void testRemoveDenied() throws Exception
	{
		logAdminIn(getSecondAdmin());
		super.testRemove();
	}

	@Override
	public SingleClientManager getClientManager()
	{
		return getPrivilegedSingleClientManager();
	}

	public SingleClientManager getPrivilegedSingleClientManager()
	{
		return privilegedSingleClientManager;
	}

	@Autowired
	public void setPrivilegedSingleClientManager(SingleClientManager privilegedSingleClientManager)
	{
		this.privilegedSingleClientManager = privilegedSingleClientManager;
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
