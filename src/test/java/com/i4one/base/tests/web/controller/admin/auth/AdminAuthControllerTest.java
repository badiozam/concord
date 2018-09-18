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
package com.i4one.base.tests.web.controller.admin.auth;

import com.i4one.base.tests.web.controller.admin.AdminViewControllerTest;
import com.i4one.base.web.controller.BaseViewController;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import java.util.List;
import com.i4one.base.web.controller.admin.auth.AdminAuthController;
import com.i4one.base.web.controller.admin.auth.AuthAdmin;
import java.security.NoSuchAlgorithmException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class AdminAuthControllerTest extends AdminViewControllerTest
{
	private AdminAuthController adminAuthController;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		initRequest(new MockHttpServletRequest("GET", getBaseURL() + "/base/admin/auth/index.html"));
	}

	@Test
	public void testAdminLoginUnauth() throws NoSuchAlgorithmException
	{
		AuthAdmin login = new AuthAdmin();
		login.setUsername("asdf");
		login.setClearPassword("password");

		BindingResult result = new BeanPropertyBindingResult(login, "login");
		ModelAndView modelAndView = getAdminAuthController().doAdminLogin(login, result, false, "", "", getMockRequest(), getMockResponse());
		assertNotNull(modelAndView);
		assertTrue(result.hasErrors());

		List<ObjectError> globalErrors = result.getGlobalErrors();
		assertNotNull(globalErrors);
		assertTrue(globalErrors.size() > 0);

		ObjectError authError = globalErrors.get(0);
		assertNotNull(authError);
		assertNotNull(authError.getCode());
		assertEquals(authError.getCode(), "msg.adminManager.authenticate.failed");
	}

	@Test
	public void testAdminLoginAuth() throws NoSuchAlgorithmException
	{
		AuthAdmin login = new AuthAdmin();
		login.setUsername("asdf");
		login.setClearPassword("asdf");

		BindingResult result = new BeanPropertyBindingResult(login, "login");
		ModelAndView modelAndView = getAdminAuthController().doAdminLogin(login, result, false, "", "", getMockRequest(), getMockResponse());
		assertNotNull(modelAndView);
		assertFalse(result.hasErrors());
	}

	@Override
	public BaseViewController getViewController()
	{
		return getAdminAuthController();
	}

	public AdminAuthController getAdminAuthController()
	{
		return adminAuthController;
	}

	@Autowired
	public void setAdminAuthController(AdminAuthController adminAuthController)
	{
		this.adminAuthController = adminAuthController;
	}

}
