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
import com.i4one.base.model.admin.Admin;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import com.i4one.base.web.persistence.Persistence;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AdminAuthController extends BaseAdminViewController
{
	private Authenticator<Admin> adminAuthenticator;

	@RequestMapping(value = "**/admin/auth/index", method = RequestMethod.GET)
	public Model getLoginForm(@ModelAttribute("adminLogin") AuthAdmin adminLogin, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
	{
		Model retVal = super.initRequest(request, adminLogin);

		AuthAdmin remembered = AuthAdmin.fromPersistence(Persistence.getPersistence(request, null));
		if ( remembered != null )
		{
			adminLogin.copyOver(remembered);

			// These are not stored in the delegate and must be copied over manually
			//
			adminLogin.setMD5Password(remembered.getMD5Password());
			adminLogin.setClearPassword(remembered.getClearPassword());
			adminLogin.setRememberMe(remembered.isRememberMe());

			getLogger().trace("Previous admin login discovered: " + remembered + " w/ remember me value = " + remembered.isRememberMe() + " and MD5Password = " + remembered.getMD5Password() + " and clear password: " + adminLogin.getClearPassword());
		}

		getLogger().debug("Returning with login = " + adminLogin + " (md5password = " + adminLogin.getMD5Password() + ")");
		return initResponse(retVal, response, adminLogin);
	}

	@RequestMapping(value = "**/admin/auth/index", method = RequestMethod.POST)
	public ModelAndView doAdminLogin(@ModelAttribute("adminLogin") @Valid AuthAdmin adminLogin,
						BindingResult result,
						@RequestParam(value = "rememberme", required = false) Boolean rememberMe,
						@RequestParam(value = "redirURL", required = false) String redirURL,
						@RequestParam(value = "redirView", required = false) String redirView,
						HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException
	{
		Model model = super.initRequest(request, adminLogin);

		String defaultRedir = "../index.html";
		if ( !result.hasErrors() )
		{
			try
			{
				// Authenticate the user and collect errors in the result, we use a clear password field
				// in order to be able to redact the password when the user selects the "remember me"
				// feature
				//
				adminLogin.setPassword(adminLogin.getClearPassword());
				Admin loggedInAdmin = getAdminAuthenticator().login(model, adminLogin, response);

				getLogger().debug("Logged in admin = " + loggedInAdmin);
				if ( loggedInAdmin.isForceUpdate() )
				{
					defaultRedir = "../admins/updatepassword.html";
				}

				if ( adminLogin.isRememberMe() )
				{
					getLogger().trace("Remembering admin " + adminLogin);
					adminLogin.rememberMe(Persistence.getPersistence(request, response), 365 * 86400);
				}
				else
				{
					getLogger().trace("Forgetting admin " + adminLogin);
	
					// Remove whatever object is there now from persistence since the user no longer wants
					// to be remembered
					//
					adminLogin.forgetMe(Persistence.getPersistence(request, response));
				}
			}
			catch (Errors errors)
			{
				adminLogin.setClearPassword("");
				fail(model, "msg.base.admin.auth.index.failure", result, errors);
			}
		}

		// Return either the same form on errors, or use one of the redirection URLs
		//
		return redirOnSuccess(model, result, redirView, redirURL, defaultRedir, request, response);
	}

	@RequestMapping(value = "**/admin/auth/reset", method = RequestMethod.GET)
	public ModelAndView resetLogin(@ModelAttribute("authadmin") AuthAdmin authAdmin,
					BindingResult result,
					HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException
	{
		// We don't make AuthAdmin @Valid since we're directly having the password
		// field set (as opposed to the clear password one)
		//
		Model model = initRequest(request, authAdmin);

		if ( !result.hasErrors() )
		{
			getAdminAuthenticator().login(model, authAdmin, response);

			if ( result.hasErrors() )
			{
				return new ModelAndView("redirect:index.html", model).addAllObjects(result.getModel());
			}
		}

		return redirOnSuccess(model, result, null, "../admins/updatepassword.html", null, request, response);
	}

	@RequestMapping(value = "**/admin/auth/logout")
	public String doLogout(HttpServletRequest request, HttpServletResponse response)
	{
		getAdminAuthenticator().logout(new Admin(), response);
		return "redirect:../index.html";
	}

	@Override
	public boolean isAuthRequired()
	{
		// None of these pages require the admin to be authenticated
		//
		return false;
	}

	public Authenticator<Admin> getAdminAuthenticator()
	{
		return adminAuthenticator;
	}

	@Autowired
	public void setAdminAuthenticator(Authenticator<Admin> adminAuthenticator)
	{
		this.adminAuthenticator = adminAuthenticator;
	}
}
