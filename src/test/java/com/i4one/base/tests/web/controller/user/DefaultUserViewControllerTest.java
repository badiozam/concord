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
package com.i4one.base.tests.web.controller.user;

import com.i4one.base.web.controller.BaseViewController;
import org.junit.Before;
import javax.servlet.http.HttpServletRequest;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.DefaultUserViewController;
import org.springframework.mock.web.MockHttpServletRequest;
import org.junit.Test;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class DefaultUserViewControllerTest extends UserViewControllerTest
{
	private DefaultUserViewController defaultUserViewController;

	private String clientName;
	private String language;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		clientName = "first";
		language = "en";
	}

	@Test
	public void testAuthGet() throws Exception
	{
		assertTrue(initRequest(new MockHttpServletRequest("GET", "/" + clientName + "/" + language + "/base/user/index.html")));

		Model model = getDefaultUserViewController().handleRequest(getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertTrue(model.containsKey(Model.REQUEST));
		assertTrue(model.containsKey(Model.MODEL));
		assertTrue(model.containsKey(Model.TITLE));

		assertTrue(model.getRequest() instanceof HttpServletRequest);
		assertSame(model.get(Model.MODEL), model);
		assertNotNull(model.getSingleClient());
		assertEquals(clientName, model.getSingleClient().getName());
		assertEquals(language, model.getLanguage());
		assertEquals(getBaseURL(), model.getBaseURL());
	}

	@Override
	public BaseViewController getViewController()
	{
		return getDefaultUserViewController();
	}

	public DefaultUserViewController getDefaultUserViewController()
	{
		return defaultUserViewController;
	}

	@Autowired
	public void setDefaultUserViewController(DefaultUserViewController defaultUserViewController)
	{
		this.defaultUserViewController = defaultUserViewController;
	}
}