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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class TransactionalUserManagerTest extends SimpleUserManagerTest
{
	private UserManager transactionalUserManager;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		// We set this to empty to indicate that the request is happening
		// without admin privileges
		//
		logAdminOut();
		logUserIn(getFirstUser());
	}

	@Override
	public void testUpdate()
	{
		try
		{
			ReturnType<User> updateStatus = testUpdateImpl();
			getLogger().debug("updateStatus = " + updateStatus);
	
			assertNotNull(updateStatus.getPre());
			assertNotNull(updateStatus.getPost());

			Transaction transaction = (Transaction) updateStatus.get("transaction");
			assertNotNull(transaction);
			assertTrue(transaction.exists());
		}
		catch (NoSuchAlgorithmException nsae)
		{
			fail(nsae.toString());
		}
	}

	@Test
	@Override
	public void testGetAllUsers()
	{
		// No need to test this here because the admin
		// isn't going to be logged in
		//
	}

	@Override
	public UserManager getUserManager()
	{
		return getTransactionalUserManager();
	}

	public UserManager getTransactionalUserManager()
	{
		return transactionalUserManager;
	}

	@Autowired
	public void setTransactionalUserManager(UserManager transactionalUserManager)
	{
		this.transactionalUserManager = transactionalUserManager;
	}

}
