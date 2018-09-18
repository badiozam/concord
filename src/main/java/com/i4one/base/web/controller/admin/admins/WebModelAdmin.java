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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminRecord;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class WebModelAdmin extends Admin
{
	private final transient Set<ClientAdminPrivilege> clientAdminPrivileges;

	@Override
	public void setUsername(String username)
	{
		getDelegate().setUsername(username);
	}

	@Override
	public String getPassword()
	{
		return "";
	}

	public WebModelAdmin()
	{
		this.clientAdminPrivileges = new LinkedHashSet<>();
	}

	protected WebModelAdmin(AdminRecord delegate)
	{
		super(delegate);
		this.clientAdminPrivileges = new LinkedHashSet<>();
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		if ( Utils.isEmpty(getUsername()))
		{
			errors.addError(new ErrorMessage("username", "msg.base.Admin.invalidUsername", "Usernames cannot be empty", new Object[]{"admin", this}));
		}

		return errors;
	}

	public Set<ClientAdminPrivilege> getClientAdminPrivileges()
	{
		return Collections.unmodifiableSet(clientAdminPrivileges);
	}

	public void setClientAdminPrivileges(Collection<ClientAdminPrivilege> clientAdminPrivileges)
	{
		this.clientAdminPrivileges.clear();
		this.clientAdminPrivileges.addAll(clientAdminPrivileges);
	}

}
