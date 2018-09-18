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
import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.emailtemplate.EmailTemplateManager;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.BaseSimpleClientTypeManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEmailBlastManager extends BaseSimpleClientTypeManager<EmailBlastRecord, EmailBlast> implements EmailBlastManager
{
	private EmailTemplateManager emailTemplateManager;
	private UserManager userManager;

	@Override
	protected EmailBlast cloneInternal(EmailBlast item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		EmailBlast retVal = super.cloneInternal(item);

		String currTimeStamp = String.valueOf(Utils.currentDateTime());
		IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
		retVal.setTitle(workingTitle);
		retVal.setTarget(new IString(""));
		//retVal.setTargetSQL("");
		retVal.setMatureTimeSeconds( item.getMatureTimeSeconds() + (86400 * 365));

		retVal.getEmailTemplate().setSer(0);
		retVal.getEmailTemplate().setKey(currTimeStamp);

		return retVal;
	}

	@Override
	protected ReturnType<EmailBlast> createInternal(EmailBlast item)
	{
		ReturnType<EmailTemplate> createdTemplate = null;

		// Items excluded from creation (and by extension cloning)
		//
		item.getDelegate().setTarget(null);
		//item.getDelegate().setTargetsql(null);
		item.getDelegate().setTotalcount(0);
		item.getDelegate().setTotalsent(0);
		item.getDelegate().setSendstarttm(0);
		item.getDelegate().setSendendtm(0);
		item.getDelegate().setStatus(EmailBlastStatus.PENDING);


		EmailTemplate emailTemplate = item.getEmailTemplate();
		if ( !emailTemplate.exists() )
		{
			// We have to create the template if it doesn't exist
			//
			createdTemplate = getEmailTemplateManager().create(emailTemplate);
			item.setEmailTemplate(emailTemplate);
		}

		// By this point we should have a valid e-mail template serial number
		//
		ReturnType<EmailBlast> retVal = super.createInternal(item);

		// If the template was created we need to add it to the chain of operations
		//
		if ( createdTemplate != null )
		{
			retVal.addChain(getEmailTemplateManager(), "create", createdTemplate);

			// We rename the key of the e-mail template so we can easily find it
			//
			createdTemplate.getPost().setKey(item.getFeatureName() + ":" + item.getSer());
			ReturnType<EmailTemplate> updatedTemplate = getEmailTemplateManager().update(emailTemplate);

			retVal.addChain(getEmailTemplateManager(), "update", updatedTemplate);
		}

		return retVal;
	}

	@Override
	protected ReturnType<EmailBlast> updateInternal(EmailBlastRecord lockedRecord, EmailBlast item)
	{
		// Items excluded from being updated
		//
		item.getDelegate().setStatus(null);
		item.getDelegate().setSendstarttm(null);
		item.getDelegate().setSendendtm(null);
		item.getDelegate().setTotalcount(null);
		item.getDelegate().setTotalsent(null);
		item.getDelegate().setTarget(null);
		//item.getDelegate().setTargetsql(null);

		ReturnType<EmailBlast> retVal = super.updateInternal(lockedRecord, item);

		// We need to reload the item since we modified the incoming object
		// to avoid setting any of the internal columns
		//
		retVal.setPost(getById(item.getSer()));

		// We don't want to leave the item that was handed to us with null references
		//
		item.getDelegate().setStatus(retVal.getPost().getDelegate().getStatus());
		item.getDelegate().setSendstarttm(retVal.getPost().getDelegate().getSendstarttm());
		item.getDelegate().setSendendtm(retVal.getPost().getDelegate().getSendendtm());
		item.getDelegate().setTotalcount(retVal.getPost().getDelegate().getTotalcount());
		item.getDelegate().setTotalsent(retVal.getPost().getDelegate().getTotalsent());
		item.getDelegate().setTarget(retVal.getPost().getDelegate().getTarget());
		//item.getDelegate().setTargetsql(retVal.getPost().getDelegate().getTargetsql());

		retVal.addChain(getEmailTemplateManager(), "update", getEmailTemplateManager().update(item.getEmailTemplate()));

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<EmailBlast> updateAttributes(EmailBlast item)
	{
		EmailBlastRecord lockedRecord = lock(item);

		// Items excluded from being updated
		//
		item.getDelegate().setEmailtemplateid(null);
		item.getDelegate().setTarget(null);
		item.getDelegate().setTargetsql(null);
		item.getDelegate().setMaturetm(null);
		item.getDelegate().setSchedule(null);
		item.getDelegate().setTitle(null);

		ReturnType<EmailBlast> retVal = super.updateInternal(lockedRecord, item);

		// We need to reload the item since we modified the incoming object
		// to avoid setting any of the external columns
		//
		retVal.setPost(getById(item.getSer()));

		// We don't want to leave the incoming item hanging with null
		// references
		//
		item.getDelegate().setEmailtemplateid(retVal.getPost().getDelegate().getEmailtemplateid());
		item.getDelegate().setTarget(retVal.getPost().getDelegate().getTarget());
		item.getDelegate().setTargetsql(retVal.getPost().getDelegate().getTargetsql());
		item.getDelegate().setMaturetm(retVal.getPost().getDelegate().getMaturetm());
		item.getDelegate().setSchedule(retVal.getPost().getDelegate().getSchedule());
		item.getDelegate().setTitle(retVal.getPost().getDelegate().getTitle());

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public EmailBlast remove(EmailBlast emailBlast)
	{
		getEmailTemplateManager().remove(emailBlast.getEmailTemplate());

		// The foreign key automatically removes this e-mail blast when
		// its template is removed
		//EmailBlast retVal = super.remove(emailBlast);

		return emailBlast;
	}

	@Override
	public Set<EmailBlast> getFuture(SingleClient client, int asOf, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getFuture(asOf, new ClientPagination(client, pagination)));
	}

	@Override
	public Set<EmailBlast> getLive(SingleClient client, int asOf, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getLive(asOf, new ClientPagination(client, pagination)));
	}

	@Override
	public Set<EmailBlast> getCompleted(SingleClient client, int asOf, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getCompleted(asOf, new ClientPagination(client, pagination)));
	}

	@Override
	public Set<User> getTargetUsers(EmailBlast emailBlast, TargetListPagination pagination)
	{
		Set<User> retVal = new LinkedHashSet<>();

		// We compile the list if we haven't already
		//
		if ( !getDao().hasCompiledTargetList(emailBlast.getSer()))
		{
			// XXX: Need to defer the decision to persist the list to SimpleEmailBlast sender.
			//
			if ( emailBlast.getTargetSQL().startsWith("SELECT"))
			{
				getDao().compileTargetList(emailBlast.getSer(), emailBlast.getTargetSQL());
			}
		}

		for (Integer userid : getDao().getTargetList(emailBlast.getSer(), pagination))
		{
			User user = new User();
			user.resetDelegateBySer(userid);
		}

		return retVal;
	}

	@Override
	public EmailBlastRecordDao getDao()
	{
		return (EmailBlastRecordDao) super.getDao();
	}

	@Override
	public EmailBlast emptyInstance()
	{
		return new EmailBlast();
	}

	public EmailTemplateManager getEmailTemplateManager()
	{
		return emailTemplateManager;
	}

	@Autowired
	public void setEmailTemplateManager(EmailTemplateManager emailTemplateManager)
	{
		this.emailTemplateManager = emailTemplateManager;
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

}
