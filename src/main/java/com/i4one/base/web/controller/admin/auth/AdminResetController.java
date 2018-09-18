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
package com.i4one.base.web.controller.admin.auth;

import com.i4one.base.model.Errors;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AdminResetController extends BaseAdminViewController
{
	private AdminManager adminManager;

	@RequestMapping(value = "**/admin/auth/forgot", method = RequestMethod.GET)
	public Model getAdminForgotPasswordForm(@ModelAttribute("admin") ForgotPasswordAdmin login, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		return initResponse(initRequest(request, login), response, login);
	}

	@RequestMapping(value = "**/admin/auth/forgot", method = RequestMethod.POST)
	public ModelAndView doAdminForgotPassword(@ModelAttribute("admin") ForgotPasswordAdmin admin,
							BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Model model = super.initRequest(request, admin);

		try
		{
			if ( !getAdminManager().resetPassword(admin).getPost().exists() )
			{
				result.addError(new ObjectError("admin", new String[] {"msg.adminManager.resetPassword.failed"}, new Object[] { "admin", admin}, "Sorry, there were no admins matching the e-mail address '$admin.email'"));
			}
		}
		catch (Errors errors)
		{
			convertErrors(result, errors);
		}

		return redirOnSuccess(model, result, null, null, "forgotdone.html", request, response);
	}

	@Override
	public boolean isAuthRequired()
	{
		// None of these pages require the admin to be authenticated
		//
		return false;
	}

	public AdminManager getAdminManager()
	{
		return adminManager;
	}

	@Autowired
	public void setAdminManager(AdminManager adminManager)
	{
		this.adminManager = adminManager;
	}
}
