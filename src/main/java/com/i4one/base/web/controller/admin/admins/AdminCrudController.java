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
package com.i4one.base.web.controller.admin.admins;

import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.admin.AdminRecord;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseCrudController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AdminCrudController extends BaseCrudController<AdminRecord, Admin>
{
	private AdminManager adminManager;
	private AdminPrivilegeManager adminPrivilegeManager;

	@Override
	public Model initRequest(HttpServletRequest request, Admin modelAttribute)
	{
		Model retVal = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelAdmin )
		{
			WebModelAdmin admin = (WebModelAdmin)modelAttribute;

			// Set all of the privileges this administrator has
			//
			admin.setClientAdminPrivileges(getAdminPrivilegeManager().getAdminPrivileges(admin, SimplePaginationFilter.NONE));
		}
		else if ( modelAttribute instanceof ResetPasswordAdmin )
		{
			ResetPasswordAdmin admin = (ResetPasswordAdmin)modelAttribute;

			Admin loggedInAdmin = getAdmin(retVal);
			admin.setName(loggedInAdmin.getName());
			admin.setUsername(loggedInAdmin.getUsername());
			admin.setEmail(loggedInAdmin.getEmail());
			admin.setForceUpdate(loggedInAdmin.isForceUpdate());
		}

		return retVal;
	}


	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.admins";
	}

	@Override
	protected Manager<AdminRecord, Admin> getManager()
	{
		return getAdminManager();
	}

	@RequestMapping(value = "**/base/admin/admins/update", method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("admin") WebModelAdmin admin,
					@RequestParam(value = "id", required = false) Integer adminId,
					HttpServletRequest request, HttpServletResponse response)
	{
		return createUpdateImpl(admin, adminId, request, response);
	}

	@RequestMapping(value = "**/base/admin/admins/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("admin") @Valid WebModelAdmin admin, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(admin, result, request, response);
	}

	@RequestMapping(value = { "**/base/admin/admins/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer adminId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(adminId, request, response);
	}

	public AdminManager getAdminManager()
	{
		return adminManager;
	}

	@Autowired
	public void setAdminManager(AdminManager adminManager)
	{
		this.adminManager = adminManager;
	}

	public AdminPrivilegeManager getAdminPrivilegeManager()
	{
		return adminPrivilegeManager;
	}

	@Autowired
	public void setAdminPrivilegeManager(AdminPrivilegeManager adminPrivilegeManager)
	{
		this.adminPrivilegeManager = adminPrivilegeManager;
	}
}
