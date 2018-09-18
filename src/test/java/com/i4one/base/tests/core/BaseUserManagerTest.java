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
package com.i4one.base.tests.core;

import com.i4one.base.core.Utils;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.user.UserRecordDao;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Hamid Badiozamani
 */
@Ignore
public abstract class BaseUserManagerTest extends BaseManagerTest
{
	private UserManager simpleUserManager;
	private Authenticator<User> userAuthenticator;

	private User firstUser;
	private User secondUser;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		// Create two users
		//
		firstUser = new User();
		firstUser.setClient(getFirstClient());
		firstUser.setUsername("hamid");
		firstUser.setEmail("hamid@m2omedia.com");
		firstUser.setPassword("password");
		firstUser.setCellPhone("1234567890");
		firstUser.setFirstName("Hamid");
		firstUser.setLastName("Badiozamani");
		firstUser.setCity("San Diego");
		firstUser.setState("CA");
		firstUser.setZipcode("92129");
		firstUser.setGender("m");
		firstUser.setCreateTimeSeconds(Utils.currentTimeSeconds());
		firstUser.setLastLoginTimeSeconds(Utils.currentTimeSeconds());

		secondUser = new User();
		secondUser.setClient(getFirstClient());
		secondUser.setUsername("asdf");
		secondUser.setPassword("password");
		secondUser.setEmail("asdf@m2omedia.com");
		secondUser.setCellPhone("9876543210");
		secondUser.setFirstName("Asdf");
		secondUser.setLastName("Asdf");
		secondUser.setCity("San Diego");
		secondUser.setState("CA");
		secondUser.setZipcode("92126");
		secondUser.setGender("m");
		secondUser.setCreateTimeSeconds(Utils.currentTimeSeconds());
		secondUser.setLastLoginTimeSeconds(Utils.currentTimeSeconds());

		getUserRecordDao().insert(firstUser.getDelegate());
		getUserRecordDao().updatePassword(firstUser.getDelegate(), firstUser.getPassword());

		getUserRecordDao().insert(secondUser.getDelegate());
		getUserRecordDao().updatePassword(secondUser.getDelegate(), secondUser.getPassword());

		assertTrue(firstUser.exists());
		assertTrue(secondUser.exists());

		getLogger().debug("First user: " + firstUser);
		getLogger().debug("Second user: " + secondUser);
	}

	public void logUserIn(User user)
	{
		logUserOut(user);

		// Assume we're going to test admin stuff which will have the Admin attribute set
		//
		getRequestState().getMockRequest().setRequestURI(getBaseURL() + "/base/user/index.html");

		getLogger().debug("Logging in user " + user);
		getUserAuthenticator().login(new Model(getMockRequest()), user, getMockResponse());

		// We have to be the browser and set the cookies coming from the response
		//
		initCookies();

		assertEquals(user, UserAdminModelInterceptor.getUser(getMockRequest()));
	}


	public void logUserOut(User user)
	{
		getUserAuthenticator().logout(user, getMockResponse());

		// We have to be the browser and set the cookies coming from the response
		//
		initCookies();

		User stillLoggedIn = UserAdminModelInterceptor.getUser(getMockRequest());
		assertFalse(stillLoggedIn.exists());
	}

	public HttpServletRequest getMockRequest()
	{
		return getRequestState().getRequest();
	}

	public void setMockRequest(MockHttpServletRequest request)
	{
		getRequestState().setMockRequest(request);
	}

	public MockHttpServletResponse getMockResponse()
	{
		return getRequestState().getResponse();
	}

	public void setMockResponse(MockHttpServletResponse response)
	{
		getRequestState().setResponse(response);
	}

	public UserManager getSimpleUserManager()
	{
		return simpleUserManager;
	}

	@Autowired
	public void setSimpleUserManager(UserManager simpleUserManager)
	{
		this.simpleUserManager = simpleUserManager;
	}

	public Authenticator<User> getUserAuthenticator()
	{
		return userAuthenticator;
	}

	@Autowired
	public void setUserAuthenticator(Authenticator<User> userAuthenticator)
	{
		this.userAuthenticator = userAuthenticator;
	}

	public User getFirstUser()
	{
		return firstUser;
	}

	public void setFirstUser(User firstUser)
	{
		this.firstUser = firstUser;
	}

	public User getSecondUser()
	{
		return secondUser;
	}

	public void setSecondUser(User secondUser)
	{
		this.secondUser = secondUser;
	}

	public UserRecordDao getUserRecordDao()
	{
		return (UserRecordDao) getDaoManager().getNewDao("base.JdbcUserRecordDao");
	}

}
