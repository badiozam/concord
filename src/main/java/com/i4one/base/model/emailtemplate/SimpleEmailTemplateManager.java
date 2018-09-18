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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseSimpleClientTypeManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEmailTemplateManager extends BaseSimpleClientTypeManager<EmailTemplateRecord, EmailTemplate> implements EmailTemplateManager
{
	private static final String FROM_KEY = ".from";
	private static final String REPLYTO_KEY = ".replyto";
	private static final String BCC_KEY = ".bcc";

	private static final String SUBJECT_KEY = ".subject";
	private static final String HTMLBODY_KEY = ".htmlbody";
	private static final String TEXTBODY_KEY = ".textbody";

	private MessageManager messageManager;

	@Override
	public EmailTemplate getEmailTemplate(SingleClient client, String key)
	{
		EmailTemplateRecord emailTemplateRecord = getDao().getEmailTemplate(client.getSer(), key);
		if ( emailTemplateRecord != null )
		{
			return convertDelegate(emailTemplateRecord);
		}
		else
		{
			if ( client.isRoot() )
			{
				// No more parents to check
				//
				EmailTemplate retVal = new EmailTemplate();
				retVal.setKey(key);

				return retVal;
			}
			else
			{
				// Check the parent level if we don't have the template
				// defined here. We set the serial number to zero in case
				// the template needs to be overridden for this client, in
				// which case it should get created so as not to overwrite
				// the parent template.
				//
				EmailTemplate parentTemplate = getEmailTemplate(client.getParent(), key);
				parentTemplate.setSer(0);

				return parentTemplate;
			}
		}
	}

	@Override
	public Set<EmailTemplate> getAllEmailTemplates(SingleClient client, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAll(new ClientPagination(client, pagination)));
	}

	@Override
	public EmailTemplateSettings getSettings(SingleClient client, String prefix)
	{
		EmailTemplateSettings emailSettings = new EmailTemplateSettings();

		emailSettings.setClient(client);
		emailSettings.setPrefix(prefix);

		List<Message> fromMessages = getMessageManager().getAllMessages(client, emailSettings.getPrefix() + FROM_KEY);
		List<Message> replyToMessages = getMessageManager().getAllMessages(client, emailSettings.getPrefix() + REPLYTO_KEY);
		List<Message> bccMessages = getMessageManager().getAllMessages(client, emailSettings.getPrefix() + BCC_KEY);

		emailSettings.setFromMessages(fromMessages);
		emailSettings.setReplyToMessages(replyToMessages);
		emailSettings.setBccMessages(bccMessages);

		List<Message> subjectMessages = getMessageManager().getAllMessages(client, emailSettings.getPrefix() + SUBJECT_KEY);
		List<Message> htmlBodyMessages = getMessageManager().getAllMessages(client, emailSettings.getPrefix() + HTMLBODY_KEY);
		List<Message> textBodyMessages = getMessageManager().getAllMessages(client, emailSettings.getPrefix() + TEXTBODY_KEY);

		emailSettings.setSubjectMessages(subjectMessages);
		emailSettings.setHtmlBodyMessages(htmlBodyMessages);
		emailSettings.setTextBodyMessages(textBodyMessages);

		return emailSettings;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<EmailTemplateSettings> updateSettings(EmailTemplateSettings settings)
	{
		ReturnType<EmailTemplateSettings> retVal = new ReturnType<>();

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client, settings.getPrefix()));

		List<Message> fromMessages = settings.getFromMessages(client, settings.getPrefix() + FROM_KEY);
		List<Message> replyToMessages = settings.getReplyToMessages(client, settings.getPrefix() + REPLYTO_KEY);
		List<Message> bccMessages = settings.getBccMessages(client, settings.getPrefix() + BCC_KEY);

		fromMessages.forEach( (message) -> { getMessageManager().update(message); } );
		replyToMessages.forEach( (message) -> { getMessageManager().update(message); } );
		bccMessages.forEach( (message) -> { getMessageManager().update(message); } );

		List<Message> subjectMessages = settings.getSubjectMessages(client, settings.getPrefix() + SUBJECT_KEY);
		List<Message> htmlBodyMessages = settings.getHtmlBodyMessages(client, settings.getPrefix() + HTMLBODY_KEY);
		List<Message> plainBodyMessages = settings.getTextBodyMessages(client, settings.getPrefix() + TEXTBODY_KEY);

		subjectMessages.forEach( (message) -> { getMessageManager().update(message); } );
		htmlBodyMessages.forEach( (message) -> { getMessageManager().update(message); } );
		plainBodyMessages.forEach( (message) -> { getMessageManager().update(message); } );

		retVal.setPost(settings);
		return retVal;
	}

	@Override
	public EmailTemplateRecordDao getDao()
	{
		return (EmailTemplateRecordDao) super.getDao();
	}

	@Override
	public EmailTemplate emptyInstance()
	{
		return new EmailTemplate();
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

}
