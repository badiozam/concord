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

import com.i4one.base.web.controller.BaseViewController;
import com.i4one.base.web.controller.admin.DefaultAdminViewController;
import org.junit.Before;
import javax.servlet.http.HttpServletRequest;
import com.i4one.base.web.controller.Model;
import org.springframework.mock.web.MockHttpServletRequest;
import org.junit.Test;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Hamid Badiozamani
 */
public class DefaultAdminViewControllerTest extends AdminViewControllerTest
{
	private DefaultAdminViewController defaultAdminViewController;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		setViewController(new DefaultAdminViewController());
		initRequest(new MockHttpServletRequest("GET", getBaseURL() + "/base/admin/index.html"));
	}

    @Test
    public void testUnauthGet() throws Exception
	{
		MockHttpServletResponse response = new MockHttpServletResponse();

		// We shouldn't be logged in at this time
		//
		MockHttpServletRequest request = new MockHttpServletRequest("GET", getBaseURL() + "/base/admin/index.html");
		request.setSecure(true);

		assertFalse(initRequest(request, response));
		assertEquals(getBaseURL() + "/base/admin/auth/index.html", response.getRedirectedUrl());
	}

	@Test
	public void testAuthGet() throws Exception
	{
		MockHttpServletRequest request = new MockHttpServletRequest("GET", getBaseURL() + "/base/admin/index.html");
		request.setSecure(true);

		initRequest(request);
		logAdminIn();

		assertTrue(preHandle(getMockRequest(), getMockResponse()));
		Model model = getDefaultAdminViewController().handleRequest(getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertSame(getRequestState().getModel(), model);
		assertTrue(model.containsKey(Model.REQUEST));
		assertTrue(model.containsKey(Model.MODEL));
		assertTrue(model.containsKey(Model.TITLE));

		assertTrue(model.getRequest() instanceof HttpServletRequest);
		assertSame(model.get(Model.MODEL), model);
		assertNotNull(model.getSingleClient());
		assertEquals(getFirstClient().getName(), model.getSingleClient().getName());
		assertEquals(getFirstClient().getLocale().getLanguage(), model.getLanguage());
		assertEquals(getBaseURL(), model.getBaseURL());
	}

	@Override
	public BaseViewController getViewController()
	{
		return getDefaultAdminViewController();
	}

	public DefaultAdminViewController getDefaultAdminViewController()
	{
		return defaultAdminViewController;
	}

	@Autowired
	public void setDefaultAdminViewController(DefaultAdminViewController defaultAdminViewController)
	{
		this.defaultAdminViewController = defaultAdminViewController;
	}

}