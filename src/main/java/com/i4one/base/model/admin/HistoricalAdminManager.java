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
package com.i4one.base.model.admin;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.adminhistory.AdminHistory;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class HistoricalAdminManager extends BaseHistoricalManager<AdminRecord, Admin> implements AdminManager
{
	private PrivilegedManager<AdminRecord, Admin> privilegedAdminManager;

	@Override
	public PrivilegedManager<AdminRecord, Admin> getImplementationManager()
	{
		return getPrivilegedAdminManager();
	}

	@Override
	public Set<Admin> getAdmins(Admin supervisor, PaginationFilter pagination)
	{
		return getAdminManager().getAdmins(supervisor, pagination);
	}

	@Override
	public Admin authenticate(Admin item, SingleClient client)
	{
		return getAdminManager().authenticate(item, client);
	}

	public AdminManager getAdminManager()
	{
		return (AdminManager) getPrivilegedAdminManager();
	}

	public PrivilegedManager<AdminRecord, Admin> getPrivilegedAdminManager()
	{
		return privilegedAdminManager;
	}

	@Autowired
	public <P extends AdminManager & PrivilegedManager<AdminRecord, Admin>>
	 void setPrivilegedAdminManager(P privilegedAdminManager)
	{
		this.privilegedAdminManager = privilegedAdminManager;
	}

	@Override
	public ReturnType<Admin> resetPassword(Admin admin) throws Exception
	{
		ReturnType<Admin> retVal = getAdminManager().resetPassword(admin);

		AdminHistory adminHistory = newAdminHistory(retVal.getPre(), admin, "resetPassword");
		setHistoryChainOwnership(retVal, adminHistory);
		
		retVal.put(ATTR_ADMINHISTORY, adminHistory);
		return retVal;
	}

	@Override
	public ReturnType<Admin> updatePassword(Admin admin)
	{
		ReturnType<Admin> retVal = getAdminManager().updatePassword(admin);

		AdminHistory adminHistory = newAdminHistory(retVal.getPre(), admin, "updatePassword");
		setHistoryChainOwnership(retVal, adminHistory);
		
		retVal.put(ATTR_ADMINHISTORY, adminHistory);
		return retVal;
	}

}
