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
package com.i4one.base.model.emailtemplate;

import com.i4one.base.dao.ClientRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseClientTypePrivilegedManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedEmailTemplateManager extends BaseClientTypePrivilegedManager<EmailTemplateRecord,EmailTemplate> implements EmailTemplateManager
{
	private EmailTemplateManager emailTemplateManager;

	@Override
	public SingleClient getClient(EmailTemplate item)
	{
		getLogger().debug("Getting client for " + item);
		return item.getClient();
	}

	@Override
	public EmailTemplateSettings getSettings(SingleClient client, String prefix)
	{
		return getEmailTemplateManager().getSettings(client, prefix);
	}

	@Override
	public ReturnType<EmailTemplateSettings> updateSettings(EmailTemplateSettings settings)
	{
		return getEmailTemplateManager().updateSettings(settings);
	}


	@Override
	public Manager<EmailTemplateRecord, EmailTemplate> getImplementationManager()
	{
		return getEmailTemplateManager();
	}

	@Override
	public EmailTemplate getEmailTemplate(SingleClient client, String key)
	{
		return getEmailTemplateManager().getEmailTemplate(client, key);
	}

	@Override
	public Set<EmailTemplate> getAllEmailTemplates(SingleClient client, PaginationFilter pagination)
	{
		return getEmailTemplateManager().getAllEmailTemplates(client, pagination);
	}

	public EmailTemplateManager getEmailTemplateManager()
	{
		return emailTemplateManager;
	}

	@Autowired
	@Qualifier("base.CachedEmailTemplateManager")
	public void setEmailTemplateManager(EmailTemplateManager emailTemplateManager)
	{
		this.emailTemplateManager = emailTemplateManager;
	}

	@Override
	public ClientRecordTypeDao<EmailTemplateRecord> getDao()
	{
		return getEmailTemplateManager().getDao();
	}
}
