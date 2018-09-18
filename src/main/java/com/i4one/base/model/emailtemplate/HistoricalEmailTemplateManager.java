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
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.PrivilegedManager;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("base.EmailTemplateManager")
public class HistoricalEmailTemplateManager extends BaseHistoricalManager<EmailTemplateRecord, EmailTemplate> implements EmailTemplateManager
{
	private EmailTemplateManager privilegedEmailTemplateManager;

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
	public PrivilegedManager<EmailTemplateRecord, EmailTemplate> getImplementationManager()
	{
		return (PrivilegedManager<EmailTemplateRecord, EmailTemplate>) getPrivilegedEmailTemplateManager();
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
		return getPrivilegedEmailTemplateManager();
	}

	public EmailTemplateManager getPrivilegedEmailTemplateManager()
	{
		return privilegedEmailTemplateManager;
	}

	@Autowired
	public <P extends EmailTemplateManager & PrivilegedManager<EmailTemplateRecord, EmailTemplate>>
	 void setPrivilegedEmailTemplateManager(P privilegedEmailTemplateManager)
	{
		this.privilegedEmailTemplateManager = privilegedEmailTemplateManager;
	}

	@Override
	public ClientRecordTypeDao<EmailTemplateRecord> getDao()
	{
		return getEmailTemplateManager().getDao();
	}
}
