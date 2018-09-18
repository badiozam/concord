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
package com.i4one.base.web.interceptor.model;

import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.menu.AdminMenu;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseAdminMenuModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	public static final String MENU = "adminMenu";

	private AdminPrivilegeManager adminPrivilegeManager;

	// The user menu to display, this can be ignored and/or superceded by subclasses or
	// by the VM file itself
	//
	private AdminMenu menu;

	@Override
	public void init()
	{
		menu = new AdminMenu();
	}

	protected Map<String, Object> buildMenu(Admin admin, Model model)
	{
		// Update this and create a Menu object which accepts individual menu item elements
		// but with a given feature privilege string (i.e. "base.users" or "base.friendrefs".
		// This class then returns a Map<String, Object> based on a given administrator who
		// happens to be logged in and filters out elements which do not match that admin's
		// privileges.
		//
		return menu.buildMenu(admin, model, getAdminPrivilegeManager());
	}

	public AdminMenu getMenu()
	{
		return menu;
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
