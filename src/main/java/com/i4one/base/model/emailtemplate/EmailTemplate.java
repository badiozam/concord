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

import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.email.SimpleEmailManager;
import com.i4one.base.model.email.UserBalanceEmailManager;
import com.i4one.base.model.emailblast.SimpleEmailBlastSender;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamid Badiozamani
 */
public class EmailTemplate extends BaseSingleClientType<EmailTemplateRecord>
{
	static final long serialVersionUID = 42L;

	private transient boolean enabled;

	private Map<String, Message> fromAddressMessage;
	private Map<String, Message> replyToMessage;
	private Map<String, Message> subjectMessage;
	private Map<String, Message> bccMessage;
	private Map<String, Message> textBodyMessage;
	private Map<String, Message> htmlBodyMessage;

	public EmailTemplate()
	{
		super(new EmailTemplateRecord());
	}

	protected EmailTemplate(EmailTemplateRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( fromAddressMessage == null ) { fromAddressMessage = new HashMap<>(); }
		if ( subjectMessage == null ) { subjectMessage = new HashMap<>(); }
		if ( replyToMessage == null ) { replyToMessage = new HashMap<>(); }
		if ( bccMessage == null ) { bccMessage = new HashMap<>(); }
		if ( textBodyMessage == null ) { textBodyMessage = new HashMap<>(); }
		if ( htmlBodyMessage == null ) { htmlBodyMessage = new HashMap<>(); }

		enabled = true;
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		if ( getSubject().isBlank() )
		{
			errors.addError(new ErrorMessage("subject", "msg.base.EmailTemplate.emptysubject", "Subject cannot be empty.", new Object[]{"item", this}));
		}

		if ( getHtmlBody().isBlank() && getTextBody().isBlank() )
		{
			errors.addError(new ErrorMessage("htmlBody", "msg.base.EmailTemplate.emptybody", "Body cannot be empty.", new Object[]{"item", this}));
		}

		return errors;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		getHtmlBody().entrySet().forEach( (item) -> { item.setValue(convertM2OVars(item.getValue())); } );
		getTextBody().entrySet().forEach( (item) -> { item.setValue(convertM2OVars(item.getValue())); } );
	}

	private transient Map<String, String> oldVars;
	private String convertM2OVars(String val)
	{
		if ( oldVars == null )
		{
			oldVars = new HashMap<>();
			oldVars.put("points", UserBalanceEmailManager.POINTS);
			oldVars.put("opencount", SimpleEmailBlastSender.OPENCOUNT);
			oldVars.put("optout", SimpleEmailManager.OPTOUT_HREF);
			oldVars.put("unsubscribe", SimpleEmailManager.OPTOUT_HREF);
			oldVars.put("username", SimpleEmailManager.USER + ".username");
			oldVars.put("firstname", SimpleEmailManager.USER + ".firstName");
			oldVars.put("lastname", SimpleEmailManager.USER + ".lastName");
			oldVars.put("street", SimpleEmailManager.USER + ".street");
			oldVars.put("city", SimpleEmailManager.USER + ".city");
			oldVars.put("state", SimpleEmailManager.USER + ".state");
			oldVars.put("zipcode", SimpleEmailManager.USER + ".zipcode");
			oldVars.put("site.text.programname", SimpleEmailManager.SENDER + ".descr" );
			oldVars.put("yyyy", SimpleEmailManager.SENDER + ".calendar.get(1)" );
			oldVars.put("xxxx", SimpleEmailManager.SENDER + ".name" );
			oldVars.put("appid", SimpleEmailManager.SENDER + ".name" );
			oldVars.put("clientname", SimpleEmailManager.SENDER + ".operator" );
			oldVars.put("clientaddress", SimpleEmailManager.SENDER + ".address" );
			oldVars.put("clienturl", SimpleEmailManager.SENDER + ".URL" );
		}

		String retVal = val;
		for ( String key : oldVars.keySet() )
		{
			retVal = retVal.replaceAll("\\[%" + key + "%\\]", "\\$" + oldVars.get(key));
		}

		return retVal;
	}

	public String getKey()
	{
		return getDelegate().getKey();
	}

	public void setKey(String key)
	{
		getDelegate().setKey(key);
	}

	public IString getFromAddress()
	{
		return getDelegate().getFromaddr();
	}

	public void setFromAddress(IString fromaddr)
	{
		getDelegate().setFromaddr(fromaddr);
	}

	public IString getBcc()
	{
		return getDelegate().getBcc();
	}

	public void setBcc(IString bcc)
	{
		getDelegate().setBcc(bcc);
	}

	public IString getReplyTo()
	{
		return getDelegate().getReplyto();
	}

	public void setReplyTo(IString replyTo)
	{
		getDelegate().setReplyto(replyTo);
	}

	public IString getSubject()
	{
		return getDelegate().getSubject();
	}

	public void setSubject(IString subject)
	{
		getDelegate().setSubject(subject);
	}

	public IString getHtmlBody()
	{
		return getDelegate().getHtmlbody();
	}

	public void setHtmlBody(IString htmlbody)
	{
		getDelegate().setHtmlbody(htmlbody);
	}

	public IString getTextBody()
	{
		return getDelegate().getTextbody();
	}

	public void setTextBody(IString textbody)
	{
		getDelegate().setTextbody(textbody);
	}

	public Message getFromAddressMessage(String lang)
	{
		fromAddressMessage = Message.messagesFromIString(getClient(), "", getDelegate().getFromaddr(), lang);
		return fromAddressMessage.get(lang);
	}

	public Message getSubjectMessage(String lang)
	{
		subjectMessage = Message.messagesFromIString(getClient(), "", getDelegate().getSubject(), lang);
		return subjectMessage.get(lang);
	}

	public Message getReplyToMessage(String lang)
	{
		replyToMessage = Message.messagesFromIString(getClient(), "", getDelegate().getReplyto(), lang);
		return replyToMessage.get(lang);
	}

	public Message getBccMessage(String lang)
	{
		bccMessage = Message.messagesFromIString(getClient(), "", getDelegate().getBcc(), lang);
		return bccMessage.get(lang);
	}

	public Message getTextBodyMessage(String lang)
	{
		textBodyMessage = Message.messagesFromIString(getClient(), "", getDelegate().getTextbody(), lang);
		return textBodyMessage.get(lang);
	}

	public Message getHtmlBodyMessage(String lang)
	{
		htmlBodyMessage = Message.messagesFromIString(getClient(), "", getDelegate().getHtmlbody(), lang);
		return htmlBodyMessage.get(lang);
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getKey();
	}
}