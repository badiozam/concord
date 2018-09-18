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
package com.i4one.base.model.manager;

import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.email.EmailManager;
import com.i4one.base.model.emailtemplate.EmailTemplateManager;
import com.i4one.base.web.RequestState;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A base class for e-mail notification managers. These managers are request scoped
 * because the e-mails they send out depend on the incoming request for URLs and
 * other information.
 * 
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public abstract class BaseEmailNotificationManager<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseDelegatingManager<U, T> implements DelegatingManager<U, T>
{
	private EmailManager emailManager;
	private EmailTemplateManager emailTemplateManager;
	private RequestState requestState;


	public EmailManager getEmailManager()
	{
		return emailManager;
	}

	@Autowired
	public void setEmailManager(EmailManager emailManager)
	{
		this.emailManager = emailManager;
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

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}
}
