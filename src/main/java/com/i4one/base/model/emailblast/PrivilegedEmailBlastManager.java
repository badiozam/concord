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
package com.i4one.base.model.emailblast;

import com.i4one.base.model.targeting.TargetListPagination;
import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseClientTypePrivilegedManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedEmailBlastManager extends BaseClientTypePrivilegedManager<EmailBlastRecord,EmailBlast> implements EmailBlastManager
{
	private EmailBlastManager emailBlastManager;

	@Override
	public Set<EmailBlast> getFuture(SingleClient client, int asOf, PaginationFilter pagination)
	{
		checkRead(client, "getFuture");
		return getEmailBlastManager().getFuture(client, asOf, pagination);
	}

	@Override
	public Set<EmailBlast> getLive(SingleClient client, int asOf, PaginationFilter pagination)
	{
		checkRead(client, "getLive");
		return getEmailBlastManager().getLive(client, asOf, pagination);
	}

	@Override
	public Set<EmailBlast> getCompleted(SingleClient client, int asOf, PaginationFilter pagination)
	{
		checkRead(client, "getCompleted");
		return getEmailBlastManager().getCompleted(client, asOf, pagination);
	}

	@Override
	public ReturnType<EmailBlast> updateAttributes(EmailBlast item)
	{
		checkWrite(item.getClient(), "update");
		return getEmailBlastManager().updateAttributes(item);
	}

	@Override
	public Set<User> getTargetUsers(EmailBlast emailBlast, TargetListPagination pagination)
	{
		return getEmailBlastManager().getTargetUsers(emailBlast, pagination);
	}

	@Override
	public SingleClient getClient(EmailBlast item)
	{
		getLogger().debug("Getting client for " + item);
		return item.getClient();
	}

	@Override
	public Manager<EmailBlastRecord, EmailBlast> getImplementationManager()
	{
		return getEmailBlastManager();
	}

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	@Qualifier("base.TargetableEmailBlastManager")
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}

	@Override
	public PaginableRecordTypeDao<EmailBlastRecord> getDao()
	{
		return getEmailBlastManager().getDao();
	}
}
