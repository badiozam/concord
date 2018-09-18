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
package com.i4one.base.web.controller.admin.admins;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.admin.AdminManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import java.lang.reflect.InvocationTargetException;
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
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AdminResetPasswordController extends BaseAdminViewController
{
	private AdminManager adminManager;

	/**
	 * Initialize the admin being used as the model attribute. We assume that all
	 * methods in this controller are operations on the logged in admin.
	 * 
	 * @param request The incoming HTTP request
	 * 
	 * @return The currently logged in admin
	 */
	@ModelAttribute("admin")
	public ResetPasswordAdmin initAdmin(HttpServletRequest request)
	{
		ResetPasswordAdmin resetPassword = new ResetPasswordAdmin();

		Admin loggedInAdmin = UserAdminModelInterceptor.getAdmin(request);

		resetPassword.resetDelegateBySer(loggedInAdmin.getSer());
		resetPassword.loadedVersion();

		resetPassword.setForceUpdate(loggedInAdmin.isForceUpdate());

		return resetPassword;
	}

	@RequestMapping(value = "**/base/admin/admins/updatepassword", method = RequestMethod.GET)
	public Model updatePassword(@ModelAttribute("admin") ResetPasswordAdmin admin, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, admin);

		getLogger().debug("Logged in admin = {}", admin);

		return initResponse(model, response, admin);
	}

	@RequestMapping(value = "**/base/admin/admins/updatepassword", method = RequestMethod.POST)
	public ModelAndView doUpdatePassword(@ModelAttribute("admin") @Valid ResetPasswordAdmin resetAdmin, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException
	{
		Model model = initRequest(request, resetAdmin);

		Admin loggedInAdmin = getAdmin(model);
		getLogger().debug("Logged in admin = {}", loggedInAdmin);

		boolean authorized = loggedInAdmin.isForceUpdate();

		if ( !authorized )
		{
			Admin authAdmin = new Admin();
			authAdmin.resetDelegateBySer(loggedInAdmin.getSer());
			authAdmin.setPassword(resetAdmin.getCurrentPassword());

			authorized = getAdminManager().authenticate(authAdmin, model.getSingleClient()).exists();
		}
		else
		{
			getLogger().debug("Admin has set force update on, skipping authentication");
		}
		
		if ( authorized )
		{
			if ( !result.hasErrors() )
			{
				try
				{
					resetAdmin.setSer(loggedInAdmin.getSer());

					getAdminManager().updatePassword(resetAdmin);

					success(model, "msg.base.admin.admins.updatepassword.success");
				}
				catch (Errors errors)
				{
					convertErrors(result, errors);
				}
			}
			else
			{
				getLogger().debug("Skipping update due to errors: " + result.getErrorCount() + " : " + result.getAllErrors());
			}
		}
		else
		{
			convertErrors(result, new Errors(new ErrorMessage("currentPassword", "msg.base.Admin.invalidCurrentPassword", "The current password you entered is not correct", new Object[]{"admin", this})));
		}

		// We need to sanitize the output so they don't get displayed in the source
		//
		resetAdmin.setNewPassword("");
		resetAdmin.setCurrentPassword("");
		resetAdmin.setConfirmPassword("");

		return redirOnSuccess(model, result, null, null, null, request, response);
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
