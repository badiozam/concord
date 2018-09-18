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
package com.i4one.base.web.controller.user.account;

import com.i4one.base.model.Errors;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
public class UserVerifyAccountController extends BaseUserViewController
{
	private UserManager userManager;

	private Authenticator<User> userAuthenticator;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@RequestMapping(value = "**/user/account/verify", method = RequestMethod.GET )
	public Model verifyAccount(@ModelAttribute("verificationToken") VerificationToken verificationToken, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = super.initRequest(request, verificationToken);

		return initResponse(model, response, verificationToken);
	}

	@RequestMapping(value = "**/user/account/verify", method = RequestMethod.POST )
	public ModelAndView doVerifyAccount(@ModelAttribute("verificationToken") @Valid VerificationToken verificationToken, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = super.initRequest(request, verificationToken);

		try
		{
			User user = model.getUser();
			if ( !getUserManager().verify(user, verificationToken.getCode()).getPost() )
			{
				result.addError(new ObjectError("", new String[] { "msg.base.user.account.verify.invalidCode" }, new Object[] {}, "Sorry, the code you entered does not match the one sent to your mobile phone. Please check the code and try again."));
			}
			else
			{
				getUserAuthenticator().relogin(user, request, response);
			}

		}
		catch (Errors errors)
		{
			convertErrors(result, errors);
		}

		return redirOnSuccess(model, result, null, null, "verifydone.html", request, response);
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

	public Authenticator<User> getUserAuthenticator()
	{
		return userAuthenticator;
	}

	@Autowired
	public void setUserAuthenticator(Authenticator<User> userAuthenticator)
	{
		this.userAuthenticator = userAuthenticator;
	}

}
