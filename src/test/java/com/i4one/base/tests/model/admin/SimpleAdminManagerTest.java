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

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.model.admin.AdminRecordDao;
import com.i4one.base.tests.core.BaseManagerTest;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Hamid Badiozamani
 */
public class SimpleAdminManagerTest extends BaseManagerTest
{
	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		Admin admin = new Admin();
		admin.setUsername("hamid");
		admin.setPassword("password");
		admin.setEmail("hamid@m2omedia.com");
		admin.setName("Hamid Badiozamani");

		getAdminManager().create(admin);
		getAdminManager().updatePassword(admin);
	}

	@Test
	public void testAuthenticate() throws NoSuchAlgorithmException
	{
		// Successful authentication
		//
		Admin admin = new Admin();
		admin.setUsername("hamid");
		admin.setPassword("password");

		admin = getAdminManager().authenticate(admin, getFirstClient());
		assertTrue(admin.exists());

		// Unknown user
		//
		admin = new Admin();
		admin.setUsername("unknownuser");
		admin.setPassword("asdf");

		getLogger().debug("Authenticating " + admin);
		admin = getAdminManager().authenticate(admin, getFirstClient());
		getLogger().debug("Admin is " + admin);
		assertFalse(admin.exists());

		// Bad password
		//
		admin = new Admin();
		admin.setUsername("hamid");
		admin.setPassword("asdf");

		admin = getAdminManager().authenticate(admin, getFirstClient());
		assertFalse(admin.exists());
	}

	@Test
	public void testCreate() throws NoSuchAlgorithmException
	{
		Admin admin = new Admin();
		admin.setUsername("newadmin");
		admin.setPassword("asdf");
		admin.setEmail("asdf@m2omedia.com");
		admin.setName("Hamid Badiozamani");

		getAdminManager().create(admin);
		assertTrue(admin.exists());

		try
		{
			getAdminManager().create(admin);
			fail("Creating an existing record check failed");
		}
		catch (Errors errors)
		{
			assertTrue(errors.hasErrors());

			ErrorMessage message = errors.getError("adminManager.create");
			assertEquals("msg.adminManager.create.oldrecord", message.getMessageKey());
		}

		try
		{
			admin.getDelegate().setSer(0);
			getAdminManager().create(admin);
			fail("Collision detection failed");
		}
		catch (Errors errors)
		{
			assertTrue(errors.hasErrors());

			ErrorMessage message = errors.getError("adminManager.create");
			assertEquals("msg.adminManager.create.collision", message.getMessageKey());
		}
	}

	@Test
	public void testUpdate() throws NoSuchAlgorithmException
	{
		ReturnType<Admin> updateStatus = testUpdateImpl();
		assertNotNull(updateStatus.getPre());
		assertNotNull(updateStatus.getPost());
	}

	protected ReturnType<Admin> testUpdateImpl() throws NoSuchAlgorithmException
	{
		Admin admin = new Admin();
		admin.setUsername("hamid");
		admin.setPassword("password");

		admin = getAdminManager().authenticate(admin, getFirstClient());
		assertTrue(admin.exists());

		admin.setUsername("updateadmin");
		ReturnType<Admin> retVal = getAdminManager().update(admin);

		assertNotNull(retVal.getPre());
		assertNotNull(retVal.getPost());

		return retVal;
	}

	public AdminManager getAdminManager()
	{
		return getSimpleAdminManager();
	}

	private AdminRecordDao getAdminRecordDao()
	{
		return (AdminRecordDao) getDaoManager().getNewDao("adminDao");
	}
}
