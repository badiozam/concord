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

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseEmailNotificationManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.HashMap;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("base.AdminManager")
public class EmailNotificationAdminManager extends BaseEmailNotificationManager<AdminRecord,Admin> implements AdminManager
{
	private AdminManager adminManager;

	@Override
	public Manager<AdminRecord, Admin> getImplementationManager()
	{
		return getAdminManager();
	}

	@Override
	public ReturnType<Admin> resetPassword(Admin admin) throws Exception
	{
		try
		{
			ReturnType<Admin> retVal = getAdminManager().resetPassword(admin);

			if ( retVal.getPost().exists() )
			{
				HashMap<String, Object> subs = new HashMap<>();

				subs.put("reset", retVal.get("resetPassword"));
				subs.put(Admin.class.getSimpleName(), retVal.getPost());
				subs.put("request", getRequestState().getRequest());

				// The client making the request
				//
				SingleClient client = getRequestState().getSingleClient();

				getEmailManager().sendEmail(client, retVal.getPost().getEmail(), "emails.base.resetAdminPassword", subs);
			}

			return retVal;
		}
		catch (Exception ex)
		{
			throw new Errors(getInterfaceName() + ".resetPassword", new ErrorMessage("msg." + getInterfaceName() + ".resetPassword.emailError", "Could not send e-mail to $user.email", new Object[] { "admin", admin }, ex));
		}
	}

	@Override
	public ReturnType<Admin> updatePassword(Admin admin)
	{
		return getAdminManager().updatePassword(admin);
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
		return adminManager;
	}

	@Autowired
	@Qualifier("base.HistoricalAdminManager")
	public void setAdminManager(AdminManager adminManager)
	{
		this.adminManager = adminManager;
	}
}
