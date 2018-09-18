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
package com.i4one.base.tests.web.controller;

import com.i4one.base.web.controller.BaseViewController;
import org.junit.After;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.junit.Before;
import javax.servlet.http.HttpServletRequest;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.DefaultViewController;
import org.springframework.mock.web.MockHttpServletRequest;
import org.junit.Test;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class DefaultViewControllerTest extends ViewControllerTest
{
	private DefaultViewController defaultViewController;

	private String clientName;
	private String language;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		clientName = "first";
		language = "en";

		initRequest(new MockHttpServletRequest("GET", getBaseURL() + "/base/index.html"), new MockHttpServletResponse());
	}

	@After
	public void tearDown() throws Exception
	{
		postHandle(getMockRequest(), getMockResponse(), new ModelAndView("", getRequestState().getModel()));
	}

	@Test
	public void testGet()
	{
		Model model = getDefaultViewController().handleRequest(getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertEquals(getRequestState().getModel(), model);
		assertTrue(model.containsKey(Model.REQUEST));
		assertTrue(model.containsKey(Model.MODEL));
		assertTrue(model.containsKey(Model.TITLE));

		assertTrue(model.getRequest() instanceof HttpServletRequest);
		assertSame(model.get(Model.MODEL), model);
		assertNotNull(model.getSingleClient());
		assertEquals(clientName, model.getSingleClient().getName());
		assertEquals(language, model.getLanguage());
		assertEquals(getBaseURL(), getRequestState().getModel().getBaseURL());
	}

	@Override
	public BaseViewController getViewController()
	{
		return getDefaultViewController();
	}

	public DefaultViewController getDefaultViewController()
	{
		return defaultViewController;
	}

	@Autowired
	public void setDefaultViewController(DefaultViewController defaultViewController)
	{
		this.defaultViewController = defaultViewController;
	}
}