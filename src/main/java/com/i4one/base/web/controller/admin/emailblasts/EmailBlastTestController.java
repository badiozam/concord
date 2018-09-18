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
package com.i4one.base.web.controller.admin.emailblasts;

import com.i4one.base.model.Errors;
import com.i4one.base.model.emailblast.EmailBlastManager;
import com.i4one.base.model.emailblast.EmailBlastSender;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class EmailBlastTestController extends BaseAdminViewController
{
	private EmailBlastSender emailBlastSender;
	private UserManager userManager;

	private EmailBlastManager emailBlastManager;

	protected String getMessageRoot()
	{
		return "msg.base.admin.emailblasts";
	}

	@RequestMapping(value = { "**/base/admin/emailblasts/test" }, method = RequestMethod.GET)
	public Model testForm(@ModelAttribute("testblast") TestBlast testblast,
					@RequestParam(value = "id", required = false) Integer emailblastId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model =  initRequest(request, testblast);

		if ( emailblastId != null )
		{
			testblast.getEmailBlast().setSer(emailblastId);
			testblast.getEmailBlast().loadedVersion();
		}

		if ( !testblast.getEmailBlast().exists() )
		{
			testblast.setHasSent(true);
			fail(model, getMessageRoot() + ".test.failed", null, Errors.NO_ERRORS);
		}

		return initResponse(model, response, testblast);
	}

	@RequestMapping(value = "**/base/admin/emailblasts/test", method = RequestMethod.POST)
	public Model doSendTest(@ModelAttribute("testblast") TestBlast testblast, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model =  initRequest(request, testblast);

		Errors errors = testblast.validate();

		if ( errors.hasErrors() )
		{
			fail(model, getMessageRoot() + ".test.failed", result, errors);
		}
		else
		{
			for ( String username : testblast.getUsernames().split("\n"))
			{
				User currUser = new User();
				currUser.setUsername(username);

				currUser = getUserManager().lookupUser(currUser);
				if ( currUser.exists() )
				{
					testblast.getEmailBlast().loadedVersion();

					getLogger().debug("Sending email using email template {}", testblast.getEmailBlast());
					getEmailBlastSender().sendEmailBlast(currUser, testblast.getEmailBlast());
				}
			}

			testblast.setHasSent(true);
			success(model, getMessageRoot() + ".test.successful");
		}

		return initResponse(model, response, testblast);
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

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}
}
