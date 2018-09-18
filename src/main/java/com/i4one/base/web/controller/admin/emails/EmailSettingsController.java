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
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplateManager;
import com.i4one.base.model.emailtemplate.EmailTemplateSettings;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EmailSettingsController extends BaseAdminViewController
{
	private EmailTemplateManager emailTemplateManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object object)
	{
		Model retVal = super.initRequest(request, object);

		if ( object instanceof EmailTemplateSettings )
		{
			// Map the timezones to their own values and make available to the
			// model for rendering the drop-down menu
			//
			retVal.put("timezones", Arrays.asList(TimeZone.getAvailableIDs())
						.stream()
						.collect(Collectors.toMap((tz) -> { return tz; }, (tz) -> { return tz; })));
		}

		return retVal;
	}

	@RequestMapping(value = "**/base/admin/emails/index", method = RequestMethod.GET)
	public Model viewEmailSettings(@ModelAttribute("emailSettings") EmailTemplateSettings emailSettings,
					@RequestParam(value = "id", required = false) String id,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, emailSettings);

		emailSettings.setPrefix(id);

		// We get our own copy since we may want to perform edits which may affect other
		// requests that use the same client reference
		//
		SingleClient client = model.getSingleClient();

		EmailTemplateSettings currSettings = getEmailTemplateManager().getSettings(client, id);
		emailSettings.setFrom(currSettings.getFrom());
		emailSettings.setBcc(currSettings.getBcc());
		emailSettings.setHtmlBody(currSettings.getHtmlBody());
		emailSettings.setReplyTo(currSettings.getReplyTo());
		emailSettings.setSubject(currSettings.getSubject());
		emailSettings.setTextBody(currSettings.getTextBody());

		return initResponse(model, response, emailSettings);
	}

	@RequestMapping(value = "**/base/admin/emails/index", method = RequestMethod.POST)
	public Model updateEmailSettings(@ModelAttribute("emailSettings") @Valid EmailTemplateSettings emailSettings, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, emailSettings);

		try
		{
			SingleClient client = model.getSingleClient();
			emailSettings.setClient(client);

			getEmailTemplateManager().updateSettings(emailSettings);

			success(model, "msg.base.admin.emails.index.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.emails.index.failure", result, errors);
		}

		return initResponse(model, response, emailSettings);
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

