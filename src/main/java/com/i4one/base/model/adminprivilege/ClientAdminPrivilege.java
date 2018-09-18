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

import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SingleClient;

/**
 * This class represents a single privilege for a given administrator on a given
 singleClient.
 *
 * @author Hamid Badiozamani
 */
public class ClientAdminPrivilege extends BaseSingleClientType<ClientAdminPrivilegeRecord> implements RecordTypeDelegator<ClientAdminPrivilegeRecord>
{
	static final long serialVersionUID = 42L;

	private transient Privilege privilege;
	private transient Admin admin;

	public ClientAdminPrivilege()
	{
		super(new ClientAdminPrivilegeRecord());
	}

	protected ClientAdminPrivilege(ClientAdminPrivilegeRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( admin == null )
		{
			admin = new Admin();
		}
		admin.resetDelegateBySer(getDelegate().getAdminid());

		if ( privilege == null )
		{
			privilege = new Privilege();
		}
		privilege.resetDelegateBySer(getDelegate().getPrivid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setAdmin(getAdmin());
		setPrivilege(getPrivilege());
	}

	public Admin getAdmin()
	{
		return getAdmin(true);
	}

	public Admin getAdmin(boolean doLoad)
	{
		if ( doLoad )
		{
			admin.loadedVersion();
		}

		return admin;
	}

	public void setAdmin(Admin admin)
	{
		this.admin = admin;
		getDelegate().setAdminid(admin.getSer());
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	public Privilege getPrivilege()
	{
		return getPrivilege(true);
	}

	public Privilege getPrivilege(boolean doLoad)
	{
		if ( doLoad )
		{
			privilege.loadedVersion();
		}

		return privilege;
	}

	public void setPrivilege(Privilege privilege)
	{
		this.privilege = privilege;
		getDelegate().setPrivid(privilege.getSer());
	}


	@Override
	protected String uniqueKeyInternal()
	{
		return getAdmin(false).uniqueKey() + "-"  + getPrivilege(false).uniqueKey();
	}
}
