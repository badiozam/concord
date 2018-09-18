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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.tests.core.BaseUserManagerTest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;

/**
 * @author Hamid Badiozamani
 */
public class SimpleUserManagerTest extends BaseUserManagerTest
{
	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		getUserManager().init();
	}

	@Test
	public void testAuthorize() throws java.lang.Exception
	{
		logAdminOut();

		User u = new User();
		u.setUsername(getFirstUser().getUsername());
		u.setPassword("password");
		u.setClient(getFirstClient());

		assertTrue(getUserManager().authenticate(u, u.getClient()).exists());
		assertFalse(u.exists());

		// Test for a non-existant user
		//
		u = new User();
		u.setUsername("bob");
		u.setPassword("marley");
		u.setClient(getFirstClient());

		assertFalse(getUserManager().authenticate(u, u.getClient()).exists());
		assertFalse(u.exists());

		// Test for a user with a bad password
		//
		u = new User();
		u.setUsername(getFirstUser().getUsername());
		u.setPassword("badpassword");
		u.setClient(getFirstClient());

		assertFalse(getUserManager().authenticate(u, u.getClient()).exists());
		assertFalse(u.exists());

		// If an administrator is logged in, any user can be accessed with
		// any given password
		//
		logAdminIn(getFirstAdmin());

		assertTrue(getUserManager().authenticate(u, u.getClient()).exists());
		assertFalse(u.exists());
	}

	@Test
	public void testRegister() throws java.lang.Exception
	{
		// Attempt to create a new user
		//
		User u = createTestUser();
		u.setUsername("testregister");
		u.setEmail("testregister@none.com");
		u.setClient(getFirstClient());

		ReturnType<User> retVal = getUserManager().create(u);
		assertNotNull(retVal);
		assertTrue(retVal.getPost().exists());

		User auth = new User();
		auth.setUsername("testregister");
		auth.setPassword("password");

		// Now see if the user was actually saved
		//
		assertTrue(getUserManager().authenticate(u, u.getClient()).exists());
	}

	@Test
	public void testRegisterUsernameCollision() throws NoSuchAlgorithmException
	{
		// Attempt to create an existing member by username
		//
		User u = createTestUser();
		u.setEmail("testregisterexisting@none.com");
		u.setClient(getFirstClient());
		u.setCreateTimeSeconds(Utils.currentTimeSeconds());

		try
		{
			getUserManager().create(u);
			fail("Collision detection on username failed.");
		}
		catch (Errors errors)
		{
			assertTrue(errors.getHasErrors());

			ErrorMessage message =  errors.getError("userManager.create");
			assertEquals("msg.userManager.create.collision", message.getMessageKey());
			assertTrue(message.getException() instanceof DataAccessException);
		}
	}

	@Test
	public void testRegisterEmailCollision() throws NoSuchAlgorithmException
	{
		// Attempt to create an existing member by e-mail
		//
		User u = createTestUser();
		u.setUsername("testregister");
		u.setCellPhone("5432123456");
		u.setEmail("hamid@m2omedia.com");
		u.setClient(getFirstClient());

		try
		{
			getUserManager().create(u);
			fail("Collision detection on e-mail failed");
		}
		catch (Errors errors)
		{
			assertTrue(errors.getHasErrors());

			ErrorMessage message =  errors.getError("userManager.create");
			assertEquals("msg.userManager.create.collision", message.getMessageKey());
			assertTrue(message.getException() instanceof DataAccessException);
		}
	}

	@Test
	public void testExists() throws NoSuchAlgorithmException
	{
		User u = createTestUser();
		u.setUsername(getFirstUser().getUsername());
		u.setPassword("password");
		u.setClient(getFirstClient());

		assertTrue(getUserManager().existsUser(u));

		// Test for a non-existant user
		//
		u = new User();
		u.setUsername("bob");
		u.setPassword("marley");
		u.setClient(getFirstClient());

		assertFalse(getUserManager().existsUser(u));
	}

	@Test
	public void testUpdate()
	{
		try
		{
			ReturnType<User> updateStatus = testUpdateImpl();
			assertNotNull(updateStatus.getPre());
			assertNotNull(updateStatus.getPost());
		}
		catch (NoSuchAlgorithmException nsae)
		{
			fail(nsae.toString());
		}
	}
	
	protected User createTestUser() throws NoSuchAlgorithmException
	{
		User u = new User();

		u.setUsername("hamid");
		u.setCellPhone("5432123456");
		u.setEmail("none@none.com");
		u.setClient(getFirstClient());
		u.setFirstName("Hamid");
		u.setLastName("Badiozamani");
		u.setCity("San Diego");
		u.setState("CA");
		u.setZipcode("92129");
		u.setGender("m");
		u.setPassword("password");
		u.setCreateTimeSeconds(Utils.currentTimeSeconds());

		return u;
	}

	protected ReturnType<User> testUpdateImpl() throws NoSuchAlgorithmException
	{
		User u = createTestUser();
		u.setUsername(getFirstUser().getUsername());
		u.setClient(getFirstUser().getClient());
		u.setPassword("password");

		User de = getUserManager().authenticate(u, u.getClient());
		assertNotNull(de);
		assertTrue(de.exists());

		de.setUsername("bob");

		ReturnType<User> retVal = getUserManager().update(de);
		assertNotNull(retVal);

		u.setSer(de.getSer());
		User dbUser = getUserManager().authenticate(u, u.getClient());

		assertNotNull(dbUser);
		assertEquals(de, dbUser);
		assertEquals(de.getUsername(), dbUser.getUsername());
		assertEquals(de.getPassword(), dbUser.getPassword());

		return retVal;
	}

	@Test
	public void testUpdateCollison() throws NoSuchAlgorithmException
	{
		User u = new User();
		u.setUsername(getFirstUser().getUsername());
		u.setClient(getFirstClient());
		u.setPassword("password");

		User firstUser = getUserManager().authenticate(u, u.getClient());
		assertNotNull(firstUser);
		assertTrue(firstUser.exists());

		// Attempt a collision by username
		//
		firstUser.setEmail(getSecondUser().getEmail());
		try
		{
			getUserManager().update(firstUser);
			fail("Collision detection on username failed");
		}
		catch (Errors errors)
		{
			assertTrue(errors.getHasErrors());

			ErrorMessage message =  errors.getError("userManager.update");
			assertNotNull(message);
			assertEquals("msg.userManager.update.collision", message.getMessageKey());
			assertTrue(message.getException() instanceof DataAccessException);
		}
	}

	@Test
	public void testGetAllUsers()
	{
		Set<User> users = getUserManager().getAllUsers(getFirstClient(), SimplePaginationFilter.NONE);
		assertNotNull(users);
		assertFalse(users.isEmpty());

		Set<User> members = getUserManager().getAllMembers(getSecondClient(), SimplePaginationFilter.NONE);
		assertNotNull(members);
		assertTrue(members.isEmpty());
	}

	public UserManager getUserManager()
	{
		return getSimpleUserManager();
	}
}
