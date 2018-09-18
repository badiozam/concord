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
package com.i4one.base.tests.web.controller.admin.members;

import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.base.model.transaction.TransactionManager;
import com.i4one.base.tests.web.controller.admin.AdminViewControllerTest;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.BaseViewController;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.members.LookupUser;
import com.i4one.base.web.controller.admin.members.MemberLookupController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class MemberLookupControllerTest extends AdminViewControllerTest
{
	private MemberLookupController memberLookupController;

	private UserManager userManager;
	private TransactionManager transactionManager;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}

	@Test
	public void doLookupTest() throws Exception
	{
		setMockRequest(new MockHttpServletRequest("GET", getBaseURL() + "/base/admin/members/index.html"));

		logAdminIn();
		preHandle(getMockRequest(), getMockResponse());

		LookupUser lookup = new LookupUser();
		lookup.setLookupUsername("asdf");

		BindingResult result = new BeanPropertyBindingResult(lookup, "user");
		Model model = getMemberLookupController().doLookup(lookup, result, getMockRequest(), getMockResponse());
		assertNotNull(model);
		assertTrue(lookup.getUser().exists());
		assertFalse(result.hasErrors());
	}

	@Test
	public void doUpdateTest() throws Exception
	{
		setMockRequest(new MockHttpServletRequest("POST", getBaseURL() + "/base/admin/members/index.html"));

		logAdminIn();
		preHandle(getMockRequest(), getMockResponse());

		User user = new User();
		user.setUsername("asdf");
		user.setPassword("password");
		user.setClient(getFirstClient());
		User dbUser = getUserManager().authenticate(user, user.getClient());
		User forUpdate = getUserManager().authenticate(user, user.getClient());

		assertFalse(user.exists());
		assertTrue(dbUser.exists());
		assertTrue(forUpdate.exists());
		assertEquals(dbUser, forUpdate);
		assertNotSame(dbUser, forUpdate);

		forUpdate.setFirstName("First");
		forUpdate.setLastName("Last");
		forUpdate.setEmail("test@asdf.com");

		LookupUser forUpdateLookup = new LookupUser();
		forUpdateLookup.getUser().copyFrom(forUpdate);

		BindingResult result = new BeanPropertyBindingResult(forUpdateLookup, "lookup");
		Model model = getMemberLookupController().doUpdate(forUpdateLookup, result, getMockRequest(), getMockResponse());
		assertNotNull(model);
		assertFalse(result.hasErrors());
		assertTrue(model.containsKey(BaseAdminViewController.MODELSTATUS));

		User updatedUser = getUserManager().authenticate(user, user.getClient());
		assertNotNull(updatedUser);
		assertTrue(updatedUser.exists());
		assertEquals(forUpdate.getFirstName(), updatedUser.getFirstName());
		assertEquals(forUpdate.getLastName(), updatedUser.getLastName());
		assertEquals(forUpdate.getEmail(), updatedUser.getEmail());
	}

	@Test
	public void doUpdateTestFail() throws Exception
	{
		setMockRequest(new MockHttpServletRequest("POST", getBaseURL() + "/base/admin/members/index.html"));

		logAdminIn();
		preHandle(getMockRequest(), getMockResponse());

		User user = new User();
		user.setUsername("asdf");
		user.setPassword("password");
		user.setClient(getFirstClient());
		User dbUser = getUserManager().authenticate(user, user.getClient());
		User forUpdate = getUserManager().authenticate(user, user.getClient());

		assertFalse(user.exists());
		assertTrue(dbUser.exists());
		assertTrue(forUpdate.exists());
		assertEquals(dbUser, forUpdate);
		assertNotSame(dbUser, forUpdate);

		// Attempt an invalid gender
		//
		forUpdate.setFirstName("First");
		forUpdate.setLastName("Last");
		forUpdate.setEmail("test@asdf.com");
		forUpdate.setGender("W");

		LookupUser forUpdateLookup = new LookupUser();
		forUpdateLookup.getUser().copyFrom(forUpdate);

		BindingResult result = new BeanPropertyBindingResult(forUpdateLookup, "lookup");
		Model model = getMemberLookupController().doUpdate(forUpdateLookup, result, getMockRequest(), getMockResponse());
		assertNotNull(model);
		assertTrue(result.hasErrors());
		assertTrue(model.containsKey(BaseAdminViewController.MODELSTATUS));
	}

	@Override
	public BaseViewController getViewController()
	{
		return getMemberLookupController();
	}

	public MemberLookupController getMemberLookupController()
	{
		return memberLookupController;
	}

	@Autowired
	public void setMemberLookupController(MemberLookupController memberLookupController)
	{
		this.memberLookupController = memberLookupController;
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	public TransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Autowired
	public void setTransactionManager(TransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}

}
