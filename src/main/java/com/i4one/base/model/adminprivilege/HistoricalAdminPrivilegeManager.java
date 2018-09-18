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

import com.i4one.base.model.adminhistory.AdminHistory;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Records an admin history record for write operations. Takes a PrivilegedAdminPrivilegeManager as its delegate.
 *
 * @author Hamid Badiozamani
 */
@Service("base.AdminPrivilegeManager")
public class HistoricalAdminPrivilegeManager extends BaseHistoricalManager<ClientAdminPrivilegeRecord, ClientAdminPrivilege> implements AdminPrivilegeManager
{
	// The delegate privilege manager (must be a privilegedAdminPrivilegeManager since we need
	// an Admin in the historical record as well)
	//
	private PrivilegedManager<ClientAdminPrivilegeRecord, ClientAdminPrivilege> privilegedAdminPrivilegeManager;

	@Override
	public void init()
	{
		super.init();
	}

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

	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClientAdminPrivilege> grant(ClientAdminPrivilege priv)
	{
		ReturnType<ClientAdminPrivilege> retVal = getAdminPrivilegeManager().grant(priv);

		AdminHistory adminHistory = newAdminHistory(new ClientAdminPrivilege(), priv, "grant");
		setHistoryChainOwnership(retVal, adminHistory);

		retVal.put(ATTR_ADMINHISTORY, adminHistory);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClientAdminPrivilege> revoke(ClientAdminPrivilege priv)
	{
		ReturnType<ClientAdminPrivilege> retVal = getAdminPrivilegeManager().revoke(priv);

		AdminHistory adminHistory = newAdminHistory(priv, new ClientAdminPrivilege(), "revoke");
		setHistoryChainOwnership(retVal, adminHistory);

		retVal.put(ATTR_ADMINHISTORY, adminHistory);

		return retVal;
	}

	public AdminPrivilegeManager getAdminPrivilegeManager()
	{
		return (AdminPrivilegeManager) getPrivilegedAdminPrivilegeManager();
	}

	public PrivilegedManager<ClientAdminPrivilegeRecord,ClientAdminPrivilege> getPrivilegedAdminPrivilegeManager()
	{
		return privilegedAdminPrivilegeManager;
	}

	@Autowired
	public <P extends AdminPrivilegeManager & PrivilegedManager<ClientAdminPrivilegeRecord, ClientAdminPrivilege>>
	 void setPrivilegedAdminPrivilegeManager(P privilegedAdminPrivilegeManager)
	{
		this.privilegedAdminPrivilegeManager = privilegedAdminPrivilegeManager;
	}

	@Override
	public PrivilegedManager<ClientAdminPrivilegeRecord,ClientAdminPrivilege> getImplementationManager()
	{
		return getPrivilegedAdminPrivilegeManager();
	}
}
