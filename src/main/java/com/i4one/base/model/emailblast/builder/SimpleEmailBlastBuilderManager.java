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
package com.i4one.base.model.emailblast.builder;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.emailblast.EmailBlastManager;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.emailtemplate.EmailTemplateManager;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEmailBlastBuilderManager extends BasePaginableManager<EmailBlastBuilderRecord, EmailBlastBuilder> implements EmailBlastBuilderManager
{
	private EmailBlastManager emailBlastManager;
	private EmailTemplateManager emailTemplateManager;

	@Override
	public ReturnType<EmailBlast> build(EmailBlastBuilder builder)
	{
		// We keep a reference to the state that was passed to us
		// but everything else needs to be loaded directly from
		// the database
		//
		Map<String, IString> state = builder.getSavedState();
		builder = this.getById(builder.getSer());

		EmailBlast built = new EmailBlast();
		built.setClient(builder.getClient());
		built.setTitle(builder.getTitle());

		EmailTemplate builderEmailTemplate = builder.getEmailTemplate();
		EmailTemplate builtEmailTemplate = new EmailTemplate();

		builtEmailTemplate.setFromAddress(builderEmailTemplate.getFromAddress());

		IString subject = new IString();
		IString htmlBody = new IString();
		IString textBody = new IString();

		IString builderSubject = builderEmailTemplate.getSubject();
		IString builderHtmlBody = builderEmailTemplate.getHtmlBody();
		IString builderTextBody = builderEmailTemplate.getTextBody();

		for ( String lang : builder.getClient().getLanguageList() )
		{
			Map<String, Object> langMap = getLangMapping(state, lang);

			String langSubject = Utils.subVars(langMap, "[^", "/", "^]", builderSubject.get(lang));
			subject.set(lang, langSubject);

			String langHtmlBody = Utils.subVars(langMap, "[^", "/", "^]", builderHtmlBody.get(lang));
			htmlBody.set(lang, langHtmlBody);

			String langTextBody = Utils.subVars(langMap, "[^", "/", "^]", builderTextBody.get(lang));
			textBody.set(lang, langTextBody);
		}

		builtEmailTemplate.setSubject(subject);
		builtEmailTemplate.setHtmlBody(htmlBody);
		builtEmailTemplate.setTextBody(textBody);

		built.setEmailTemplate(builtEmailTemplate);
		built.setTarget(builder.getTarget());
		built.setTargetSQL(builder.getTargetSQL());
		built.setMatureTimeSeconds( Utils.currentTimeSeconds() + (86400 * 365));

		return getEmailBlastManager().create(built);
	}

	private Map<String, Object> getLangMapping(Map<String, IString> i18nMapping, String lang)
	{
		return i18nMapping.entrySet()
			.stream()
			.collect(Collectors.toMap(
				(item) -> { return item.getKey(); },
				(item) -> { return item.getValue().get(lang); }));
	}

	@Transactional(readOnly = false)
	@Override
	public void saveState(EmailBlastBuilder item)
	{
		item.setOverrides();

		item.actualizeRelations();
		getDao().saveState(item.getSer(), item.getDelegate().getSavedstate());
	}

	@Override
	protected EmailBlastBuilder cloneInternal(EmailBlastBuilder item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		EmailBlastBuilder retVal = super.cloneInternal(item);

		String currTimeStamp = String.valueOf(Utils.currentDateTime());
		IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
		retVal.setTitle(workingTitle);
		retVal.setTarget(new IString(""));
		retVal.setTargetSQL("");
		retVal.setSavedState(Collections.emptyMap());

		retVal.getEmailTemplate().setSer(0);
		retVal.getEmailTemplate().setKey(currTimeStamp);

		return retVal;
	}

	@Override
	protected ReturnType<EmailBlastBuilder> createInternal(EmailBlastBuilder item)
	{
		ReturnType<EmailTemplate> createdTemplate = null;

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
		ReturnType<EmailBlastBuilder> retVal = super.createInternal(item);

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
	protected ReturnType<EmailBlastBuilder> updateInternal(EmailBlastBuilderRecord lockedRecord, EmailBlastBuilder item)
	{
		ReturnType<EmailBlastBuilder> retVal = super.updateInternal(lockedRecord, item);

		retVal.addChain(getEmailTemplateManager(), "update", getEmailTemplateManager().update(item.getEmailTemplate()));

		return retVal;
	}

	@Override
	public EmailBlastBuilder emptyInstance()
	{
		return new EmailBlastBuilder();
	}

	@Override
	public Set<EmailBlastBuilder> getAllBuilders(SingleClient client, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAll(new ClientPagination(client, pagination)));
	}

	@Override
	public EmailBlastBuilderRecordDao getDao()
	{
		return (EmailBlastBuilderRecordDao) super.getDao();
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

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}

}
