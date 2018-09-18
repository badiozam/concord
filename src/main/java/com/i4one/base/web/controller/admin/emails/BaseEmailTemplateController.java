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
package com.i4one.base.web.controller.admin.emails;

import com.i4one.base.model.Errors;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.emailtemplate.EmailTemplateManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseEmailTemplateController extends BaseAdminViewController
{
	private EmailTemplateManager emailTemplateManager;

	/**
	 * The e-mail template that is being created/updated by this controller
	 * 
	 * @return The e-mail template key
	 */
	protected abstract String getEmailTemplateKey();

	@Override
	public Model initRequest(HttpServletRequest request, Object object)
	{
		Model retVal = super.initRequest(request, object);

		return retVal;
	}

	public Model viewEmailTemplate(@ModelAttribute("emailTemplate") EmailTemplate emailTemplate,
					HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, emailTemplate);

		emailTemplate.copyFrom(getEmailTemplateManager().getEmailTemplate(model.getSingleClient(), getEmailTemplateKey()));

		return initResponse(model, response, emailTemplate);
	}

	public Model updateEmailTemplate(@ModelAttribute("emailTemplate") EmailTemplate emailTemplate,
						BindingResult result,
						HttpServletRequest request,
						HttpServletResponse response)
	{
		Model model = initRequest(request, emailTemplate);

		try
		{
			String key = getEmailTemplateKey();
			emailTemplate.setKey(key);
			EmailTemplate dbTemplate = getEmailTemplateManager().getEmailTemplate(model.getSingleClient(), key);

			if ( !dbTemplate.exists() )
			{
				getEmailTemplateManager().create(emailTemplate);
			}
			else
			{
				emailTemplate.setSer(dbTemplate.getSer());
				getEmailTemplateManager().update(emailTemplate);
			}
			success(model, "msg.base.admin.emailtemplate.update.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.emailtemplate.update.failure", result, errors);
		}

		return initResponse(model, response, emailTemplate);
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
}
