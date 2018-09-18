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
package com.i4one.base.tests.web.controller.user.auth;

import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.tests.web.controller.user.UserViewControllerTest;
import com.i4one.base.web.controller.user.account.AuthLogin;
import com.i4one.base.web.controller.user.account.UserAuthController;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
public class UserAuthControllerTest extends UserViewControllerTest
{
	private UserAuthController userAuthController;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		setMockRequest(new MockHttpServletRequest("GET", getBaseURL() + "/base/auth/index.html"));

		preHandle(getMockRequest(), getMockResponse());

		SingleClient client = getRequestState().getSingleClient();
		assertTrue(client.exists());

		SingleClient modelClient = getRequestState().getModel().getSingleClient();
		assertTrue(client.exists());
		assertEquals(client, modelClient);
	}

	@Test
	public void testUserLoginUnauth() throws NoSuchAlgorithmException
	{
		AuthLogin login = new AuthLogin();
		login.setUsername("asdf");
		login.setClearPassword("asdf");

		BindingResult result = new BeanPropertyBindingResult(login, "login");
		ModelAndView modelAndView = getUserAuthController().doUserLogin(login, result, "", "", getMockRequest(), getMockResponse());
		assertNotNull(modelAndView);
		assertTrue(result.hasErrors());

		List<ObjectError> globalErrors = result.getGlobalErrors();
		assertNotNull(globalErrors);
		assertTrue(globalErrors.size() > 0);

		ObjectError authError = globalErrors.get(0);
		assertNotNull(authError);
		assertNotNull(authError.getCode());
		assertEquals(authError.getCode(), "msg.userManager.authenticate.failed");
	}

	@Test
	public void testLoginAuth() throws NoSuchAlgorithmException
	{
		AuthLogin login = new AuthLogin();
		login.setUsername("asdf");
		login.setClearPassword("password");

		BindingResult result = new BeanPropertyBindingResult(login, "login");
		ModelAndView modelAndView = getUserAuthController().doUserLogin(login, result, "", "", getMockRequest(), getMockResponse());
		assertNotNull(modelAndView);
		if ( result.hasErrors() )
		{
			getLogger().debug(Utils.toCSV(result.getAllErrors()));
		}
		assertFalse(result.hasErrors());
	}

	public UserAuthController getUserAuthController()
	{
		return userAuthController;
	}

	@Autowired
	public void setUserAuthController(UserAuthController userAuthController)
	{
		this.userAuthController = userAuthController;
		setViewController(userAuthController);
	}

}
