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

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class UserResetPasswordController extends BaseUserViewController
{
	private UserManager userManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	/**
	 * Initialize the user being used as the model attribute. We assume that all
	 * methods in this controller are operations on the logged in user.
	 * 
	 * @param request The incoming HTTP request
	 * 
	 * @return The currently logged in user
	 */
	@ModelAttribute("user")
	public ResetPasswordUser initUser(HttpServletRequest request)
	{
		ResetPasswordUser resetPassword = new ResetPasswordUser();

		User loggedInUser = getRequestState().getModel().getUser();

		resetPassword.resetDelegateBySer(loggedInUser.getSer());
		resetPassword.setForceUpdate(loggedInUser.isForceUpdate());

		return resetPassword;
	}

	@RequestMapping(value = "**/user/account/resetpassword", method = RequestMethod.GET)
	public Model getUserUpdatePassword(@ModelAttribute("user") ResetPasswordUser resetPassword, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = super.initRequest(request, resetPassword);

		addMessageToModel(model, Model.TITLE, "msg.base.user.account.resetpassword.title");

		return initResponse(model, response, resetPassword);
	}
	
	@RequestMapping(value = "**/user/account/resetpassword", method = RequestMethod.POST)
	public Model doUserUpdatePassword(@ModelAttribute("user") @Valid ResetPasswordUser resetPassword, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException
	{
		Model model = super.initRequest(request, resetPassword);
		addMessageToModel(model, Model.TITLE, "msg.base.user.account.resetpassword.title");

		User loggedInUser = model.getUser();

		boolean authorized = loggedInUser.isForceUpdate();
		if ( !authorized )
		{
			User authUser = new User();
			authUser.resetDelegateBySer(loggedInUser.getSer());
			authUser.setPassword(resetPassword.getCurrentPassword());

			getLogger().debug("Authenticating " + authUser);
			authorized = getUserManager().authenticate(authUser, model.getSingleClient()).exists();
		}
		else
		{
			getLogger().debug("User has set force update on, skipping authentication");
		}
		
		if ( authorized )
		{
			try
			{
				resetPassword.setSer(loggedInUser.getSer());
				getLogger().debug("Going to update password " + resetPassword);

				getUserManager().updatePassword(resetPassword);
				success(model, "msg.base.user.account.resetpassword.success");
			}
			catch (Errors errors)
			{
				fail(model, "msg.base.user.account.resetpassword.failed", result, errors);
			}
		}
		else
		{
			Errors errors = new Errors(new ErrorMessage("currentPassword", "msg.base.User.invalidCurrentPassword", "The current password you entered is not correct", new Object[]{"user", this}));
			fail(model, "msg.base.user.account.resetpassword.failed", result, errors);
		}

		resetPassword.setCurrentPassword("");
		resetPassword.setConfirmPassword("");
		resetPassword.setClearPassword("");

		return initResponse(model, response, resetPassword);
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
