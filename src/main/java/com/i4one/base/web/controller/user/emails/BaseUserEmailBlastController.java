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
package com.i4one.base.web.controller.user.emails;

import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.emailblast.EmailBlastManager;
import com.i4one.base.model.emailblast.EmailBlastResponse;
import com.i4one.base.model.emailblast.EmailBlastResponseManager;
import com.i4one.base.model.emailblast.EmailBlastSender;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.user.BaseUserViewController;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseUserEmailBlastController extends BaseUserViewController
{
	private EmailBlastSender emailBlastSender;
	private EmailBlastManager emailBlastManager;
	private EmailBlastResponseManager emailBlastResponseManager;
	private UserManager userManager;

	protected void recordEmailResponse(User user, EmailBlast emailBlast)
	{
		if (emailBlast.exists() && user.exists())
		{
			EmailBlastResponse response = new EmailBlastResponse();
			response.setUser(user);
			response.setEmailBlast(emailBlast);

			getEmailBlastResponseManager().create(response);
		}
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

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	public EmailBlastSender getEmailBlastSender()
	{
		return emailBlastSender;
	}

	@Autowired
	public void setEmailBlastSender(EmailBlastSender emailBlastSender)
	{
		this.emailBlastSender = emailBlastSender;
	}

	public EmailBlastResponseManager getEmailBlastResponseManager()
	{
		return emailBlastResponseManager;
	}

	@Autowired
	public void setEmailBlastResponseManager(EmailBlastResponseManager emailBlastResponseManager)
	{
		this.emailBlastResponseManager = emailBlastResponseManager;
	}
}
