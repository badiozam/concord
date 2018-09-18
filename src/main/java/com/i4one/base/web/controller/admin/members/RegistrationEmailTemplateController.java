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
package com.i4one.base.web.controller.admin.members;

import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.user.EmailNotificationUserManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.emails.BaseEmailTemplateController;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class RegistrationEmailTemplateController extends BaseEmailTemplateController
{
	@Override
	protected String getEmailTemplateKey()
	{
		return EmailNotificationUserManager.REGISTRATION_EMAIL_KEY;
	}

	@RequestMapping(value = "**/base/admin/members/regemail", method = RequestMethod.GET)
	public Model view(@ModelAttribute("emailTemplate") EmailTemplate emailTemplate,
					HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return viewEmailTemplate(emailTemplate, request, response);
	}

	@RequestMapping(value = "**/base/admin/members/regemail", method = RequestMethod.POST)
	public Model update(@ModelAttribute("emailTemplate") EmailTemplate emailTemplate,
						BindingResult result,
						HttpServletRequest request,
						HttpServletResponse response)
	{
		return updateEmailTemplate(emailTemplate, result, request, response);
	}
}
