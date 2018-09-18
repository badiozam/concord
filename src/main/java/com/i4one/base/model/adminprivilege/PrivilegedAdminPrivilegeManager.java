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
package com.i4one.base.model.adminprivilege;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseClientTypePrivilegedManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * This class implements the AdminPrivilegeManager interface and incorporates
 * privilege checking for write-methods. It inherits from BasePrivilegedManager
 * since an administrator needs to have write privileges to grant other
 * administrators privileges.
 *
 * @author Hamid Badiozamami
 */
@Service
public class PrivilegedAdminPrivilegeManager extends BaseClientTypePrivilegedManager<ClientAdminPrivilegeRecord,ClientAdminPrivilege> implements AdminPrivilegeManager,PrivilegedManager<ClientAdminPrivilegeRecord, ClientAdminPrivilege>
{
	private AdminPrivilegeManager adminPrivilegeManager;

	@Override
	public Set<ClientAdminPrivilege> getAdminPrivileges(Admin admin, SingleClient client, PaginationFilter pagination)
	{
		return getAdminPrivilegeManager().getAdminPrivileges(admin, client, pagination);
	}

	@Override
	public Set<ClientAdminPrivilege> getAdminPrivileges(Admin admin, Privilege privilege, PaginationFilter pagination)
	{
		return getAdminPrivilegeManager().getAdminPrivileges(admin, privilege, pagination);
	}

	@Override
	public Set<ClientAdminPrivilege> getAdminPrivileges(Admin admin, PaginationFilter pagination)
	{
		return getAdminPrivilegeManager().getAdminPrivileges(admin, pagination);
	}

	@Override
	public Set<ClientAdminPrivilege> getAdminPrivileges(SingleClient client, PaginationFilter pagination)
	{
		return getAdminPrivilegeManager().getAdminPrivileges(client, pagination);
	}

	@Override
	public boolean hasAdminPrivilege(ClientAdminPrivilege priv)
	{
		return getAdminPrivilegeManager().hasAdminPrivilege(priv);
	}

	@Override
	public ReturnType<ClientAdminPrivilege> grant(ClientAdminPrivilege priv)
	{
		checkWrite(priv.getClient(), "grant");
		return getAdminPrivilegeManager().grant(priv);
	}

	@Override
	public ReturnType<ClientAdminPrivilege> revoke(ClientAdminPrivilege priv)
	{
		checkWrite(priv.getClient(), "revoke");
		return getAdminPrivilegeManager().revoke(priv);
	}

	@Override
	public AdminPrivilegeManager getAdminPrivilegeManager()
	{
		return adminPrivilegeManager;
	}

	@Override
	@Autowired
	@Qualifier("base.CachedAdminPrivilegeManager")
	public void setAdminPrivilegeManager(AdminPrivilegeManager adminPrivilegeManager)
	{
		this.adminPrivilegeManager = adminPrivilegeManager;
	}

	@Override
	public Manager<ClientAdminPrivilegeRecord, ClientAdminPrivilege> getImplementationManager()
	{
		return getAdminPrivilegeManager();
	}

	@Override
	public SingleClient getClient(ClientAdminPrivilege item)
	{
		return item.getClient();
	}
}