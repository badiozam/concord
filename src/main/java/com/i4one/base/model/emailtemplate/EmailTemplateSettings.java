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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.MessageKeySettings;
import java.util.List;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hamid Badiozamani
 */
public class EmailTemplateSettings extends MessageKeySettings
{
	private SingleClient client;

	private IString from;
	private IString replyTo;
	private IString bcc;

	private IString subject;
	private IString htmlBody;
	private IString textBody;

	private String prefix;

	private final IString name;

	public EmailTemplateSettings()
	{
		prefix = "";

		client = SingleClient.getRoot();

		from = new IString();
		replyTo = new IString();
		bcc = new IString();

		subject = new IString();
		htmlBody = new IString();
		textBody = new IString();

		name = new IString("E-mail");
	}

	@Override
	public IString getNameSingle()
	{
		return name;
	}

	@Override
	public IString getNamePlural()
	{
		return name;
	}

	@NotBlank(message="msg.base.EmailSettings.invalidPrefix")
	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public IString getFrom()
	{
		return from;
	}

	public void setFrom(IString from)
	{
		this.from = from;
	}

	public List<Message> getFromMessages(SingleClient client, String key)
	{
		return getMessages(client, key, from);
	}
	
	public void setFromMessages(List<Message> messages)
	{
		from = messagesToIString(messages);
	}

	public IString getReplyTo()
	{
		return replyTo;
	}

	public void setReplyTo(IString replyTo)
	{
		this.replyTo = replyTo;
	}

	public List<Message> getReplyToMessages(SingleClient client, String key)
	{
		return getMessages(client, key, replyTo);
	}

	public void setReplyToMessages(List<Message> messages)
	{
		replyTo = messagesToIString(messages);
	}

	public IString getBcc()
	{
		return bcc;
	}

	public void setBcc(IString bcc)
	{
		this.bcc = bcc;
	}

	public List<Message> getBccMessages(SingleClient client, String key)
	{
		return getMessages(client, key, bcc);
	}
	
	public void setBccMessages(List<Message> messages)
	{
		bcc = messagesToIString(messages);
	}

	public IString getSubject()
	{
		return subject;
	}

	public void setSubject(IString subject)
	{
		this.subject = subject;
	}

	public List<Message> getSubjectMessages(SingleClient client, String key)
	{
		return getMessages(client, key, subject);
	}
	
	public void setSubjectMessages(List<Message> messages)
	{
		subject = messagesToIString(messages);
	}

	public IString getHtmlBody()
	{
		return htmlBody;
	}

	public void setHtmlBody(IString htmlBody)
	{
		this.htmlBody = htmlBody;
	}

	public List<Message> getHtmlBodyMessages(SingleClient client, String key)
	{
		return getMessages(client, key, htmlBody);
	}
	
	public void setHtmlBodyMessages(List<Message> messages)
	{
		htmlBody = messagesToIString(messages);
	}

	public IString getTextBody()
	{
		return textBody;
	}

	public void setTextBody(IString textBody)
	{
		this.textBody = textBody;
	}

	public List<Message> getTextBodyMessages(SingleClient client, String key)
	{
		return getMessages(client, key, textBody);
	}
	
	public void setTextBodyMessages(List<Message> messages)
	{
		textBody = messagesToIString(messages);
	}

	public SingleClient getClient()
	{
		return client;
	}

	public void setClient(SingleClient client)
	{
		this.client = client;
	}

}
