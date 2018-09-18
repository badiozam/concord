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

import com.i4one.base.core.Utils;
import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseDelegatingManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.targeting.Target;
import com.i4one.base.model.targeting.TargetListPagination;
import com.i4one.base.model.targeting.TargetResolver;
import com.i4one.base.model.user.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class TargetableEmailBlastManager extends BaseDelegatingManager<EmailBlastRecord, EmailBlast> implements EmailBlastManager
{
	private EmailBlastManager emailBlastManager;
	private TargetResolver targetResolver;

	@Override
	public ReturnType<EmailBlast> create(EmailBlast emailBlast)
	{
		//Set<Target> targets = targetResolver.resolveTargets(emailBlast.getTargets());
		emailBlast.setTargetSQL(Utils.toCSV(emailBlast.getTargets(), Target::getKey));

		return super.create(emailBlast);
	}

	@Override
	public ReturnType<EmailBlast> update(EmailBlast emailBlast)
	{
		emailBlast.setTargetSQL(Utils.toCSV(emailBlast.getTargets(), Target::getKey));

		return super.update(emailBlast);
	}

	@Override
	public EmailBlast initModelObject(EmailBlast emailBlast)
	{
		emailBlast.setTargets(targetResolver.resolveTargets(emailBlast.getTargets()));

		return emailBlast;
	}

	@Override
	public Manager<EmailBlastRecord, EmailBlast> getImplementationManager()
	{
		return getEmailBlastManager();
	}

	@Override
	public Set<EmailBlast> getFuture(SingleClient client, int asOf, PaginationFilter pagination)
	{
		return initSet(getEmailBlastManager().getFuture(client, asOf, pagination));
	}

	@Override
	public Set<EmailBlast> getLive(SingleClient client, int asOf, PaginationFilter pagination)
	{
		return initSet(getEmailBlastManager().getLive(client, asOf, pagination));
	}

	@Override
	public Set<EmailBlast> getCompleted(SingleClient client, int asOf, PaginationFilter pagination)
	{
		return initSet(getEmailBlastManager().getCompleted(client, asOf, pagination));
	}

	@Override
	public ReturnType<EmailBlast> updateAttributes(EmailBlast item)
	{
		return getEmailBlastManager().updateAttributes(item);
	}

	@Override
	public Set<User> getTargetUsers(EmailBlast emailBlast, TargetListPagination pagination)
	{
		Set<User> retVal = getEmailBlastManager().getTargetUsers(emailBlast, pagination);

		if ( retVal.isEmpty() && !emailBlast.getTargetSQL().startsWith("SELECT"))
		{
			return getTargetUsersInternal(emailBlast, pagination);
		}

		return retVal;
	}

	protected Set<User> getTargetUsersInternal(EmailBlast emailBlast, TargetListPagination pagination)
	{
		Set<User> retVal = getTargetResolver().resolveUsers(
			getTargetResolver().resolveTargets(emailBlast.getTargets()), 0, 0);

		return retVal;
	}

	@Override
	public PaginableRecordTypeDao<EmailBlastRecord> getDao()
	{
		return getEmailBlastManager().getDao();
	}

	public TargetResolver getTargetResolver()
	{
		return targetResolver;
	}

	@Autowired
	public void setTargetResolver(TargetResolver targetResolver)
	{
		this.targetResolver = targetResolver;
	}

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	@Qualifier("base.CachedEmailBlastManager")
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}
}
