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
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class UserSendVerificationController extends BaseUserViewController
{
	private UserManager userManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@ModelAttribute("user")
	public VerifyAccountUser initUser(HttpServletRequest request) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		VerifyAccountUser user = new VerifyAccountUser();

		user.copyFrom(getRequestState().getModel().getUser());

		return user;
	}

	@RequestMapping(value = "**/user/account/sendverification", method = RequestMethod.GET)
	public Model sendVerification(@ModelAttribute("user") VerifyAccountUser user, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = super.initRequest(request, user);

		addMessageToModel(model, Model.TITLE, "msg.base.user.account.verification.title");

		return initResponse(model, response, user);
	}

	@RequestMapping(value = "**/user/account/sendverification", method = RequestMethod.POST)
	public ModelAndView doSendVerification(@ModelAttribute("user") @Valid VerifyAccountUser user, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = super.initRequest(request, user);
		addMessageToModel(model, Model.TITLE, "msg.base.user.account.verification.title");

		try
		{
			// Since the only field being set is the cell phone field, we can just copy that over the
			// currently logged in user object
			//
			User loggedInUser = model.getUser();
			loggedInUser.setCellPhone(user.getCellPhone());

			getUserManager().generateVerificationCode(loggedInUser);
		}
		catch (Errors errors)
		{
			convertErrors(result, errors);
		}

		return redirOnSuccess(model, result, null, null, "sendverificationdone.html", request, response);
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
}
