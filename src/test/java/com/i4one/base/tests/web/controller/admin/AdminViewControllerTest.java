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
package com.i4one.base.tests.web.controller.admin;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.Privilege;
import com.i4one.base.model.adminprivilege.PrivilegeManager;
import com.i4one.base.tests.web.controller.ViewControllerTest;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
@Ignore
public class AdminViewControllerTest extends ViewControllerTest
{
	private Admin admin;
	private AdminManager privilegedAdminManager;
	private PrivilegeManager privilegeManager;
	private AdminPrivilegeManager adminPrivilegeManager;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		Admin rootAdmin = new Admin();
		rootAdmin.setUsername("i4one");
		rootAdmin.setPassword("i4one");

		rootAdmin = getSimpleAdminManager().authenticate(rootAdmin, getFirstClient());
		assertTrue(rootAdmin.exists());

		admin = new Admin();
		admin.setUsername("asdf");
		admin.setPassword("asdf");
		admin.setEmail("asdf@asdf.com");
		admin.setName("Test admin");

		ReturnType<Admin> createdAdmin = getSimpleAdminManager().create(admin);
		getSimpleAdminManager().updatePassword(createdAdmin.getPost());

		Privilege readAll = new Privilege();
		readAll.setFeature("*.*");
		readAll.setReadWrite(false);
		readAll = getPrivilegeManager().lookupPrivilege(readAll);

		Privilege writeAll = new Privilege();
		writeAll.setFeature("*.*");
		writeAll.setReadWrite(true);
		writeAll = getPrivilegeManager().lookupPrivilege(writeAll);

		ClientAdminPrivilege readAllFirstClient = new ClientAdminPrivilege();
		readAllFirstClient.setAdmin(admin);
		readAllFirstClient.setClient(getFirstClient());
		readAllFirstClient.setPrivilege(readAll);

		ClientAdminPrivilege writeAllFirstClient = new ClientAdminPrivilege();
		writeAllFirstClient.setAdmin(admin);
		writeAllFirstClient.setClient(getFirstClient());
		writeAllFirstClient.setPrivilege(writeAll);

		logAdminIn(rootAdmin);

		getCachedAdminPrivilegeManager().grant(readAllFirstClient);
		getCachedAdminPrivilegeManager().grant(writeAllFirstClient);

		logAdminOut();

		assertTrue(getAdmin().exists());
	}

	public void logAdminIn() throws Exception
	{
		logAdminIn(getAdmin());
	}

	public Admin getAdmin()
	{
		return admin;
	}

	public void setAdmin(Admin admin)
	{
		this.admin = admin;
	}

	public AdminManager getAdminManager()
	{
		return getPrivilegedAdminManager();
	}

	public AdminManager getPrivilegedAdminManager()
	{
		return privilegedAdminManager;
	}

	@Autowired
	public void setPrivilegedAdminManager(AdminManager privilegedAdminManager)
	{
		this.privilegedAdminManager = privilegedAdminManager;
	}

	public AdminPrivilegeManager getAdminPrivilegeManager()
	{
		return adminPrivilegeManager;
	}

	@Autowired
	public void setAdminPrivilegeManager(AdminPrivilegeManager adminPrivilegeManager)
	{
		this.adminPrivilegeManager = adminPrivilegeManager;
	}

	public PrivilegeManager getPrivilegeManager()
	{
		return privilegeManager;
	}

	@Autowired
	public void setPrivilegeManager(PrivilegeManager privilegeManager)
	{
		this.privilegeManager = privilegeManager;
	}
}