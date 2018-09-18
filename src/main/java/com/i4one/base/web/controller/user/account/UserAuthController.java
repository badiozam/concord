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
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import static com.i4one.base.web.interceptor.model.BaseModelInterceptor.USER;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class UserAuthController extends BaseUserViewController
{
	private Authenticator<User> userAuthenticator;

	@RequestMapping(value = "**/user/account/login", method = RequestMethod.GET)
	public Model getLoginForm(@ModelAttribute("authlogin") AuthLogin authLogin, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
	{
		Model retVal = super.initRequest(request, authLogin);

		AuthLogin remembered = AuthLogin.fromPersistence(Persistence.getPersistence(request, null));
		if ( remembered != null )
		{
			authLogin.copyOver(remembered);

			// These are not stored in the delegate and must be copied over manually
			//
			authLogin.setMD5Password(remembered.getMD5Password());
			authLogin.setRememberMe(remembered.isRememberMe());
			authLogin.setClearPassword(remembered.getClearPassword());

			getLogger().debug("Previous login discovered: " + remembered + " w/ remember me value = " + remembered.isRememberMe() + " and MD5Password = " + remembered.getMD5Password() + " and clearPassword = " + remembered.getClearPassword());
		}

		return initResponse(retVal, response, authLogin);
	}

	@RequestMapping(value = "**/user/account/login", method = RequestMethod.POST)
	public ModelAndView doUserLogin(@ModelAttribute("authlogin") @Valid AuthLogin authLogin,
					BindingResult result,
					@RequestParam(value = "redirURL", required = false) String redirURL,
					@RequestParam(value = "redirView", required = false) String redirView,
					HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException
	{
		Model model = super.initRequest(request, authLogin);
		getLogger().debug("After init request authLogin now has client " + authLogin.getClient());

		if ( !result.hasErrors() )
		{
			authLogin.setPassword(authLogin.getClearPassword());
			try
			{
				getUserAuthenticator().login(model, authLogin, response);

				if ( authLogin.isRememberMe() )
				{
					authLogin.persist(Persistence.getPersistence(request, response), 365 * 86400);
				}
				else
				{
					// Remove whatever object is there now from persistence since the user no longer wants
					// to be remembered
					//
					authLogin.unpersist(Persistence.getPersistence(request, response));
				}

				model.clear();
			}
			catch (Errors errors)
			{
				authLogin.setClearPassword("");
				fail(model, "msg.base.user.account.login.failure", result, errors);
			}
		}
		else
		{
			fail(model, "msg.base.user.account.login.failure", result, Errors.NO_ERRORS);
		}

		return redirOnSuccess(model, result, redirView, redirURL, "../index.html", request, response);
	}

	@RequestMapping(value = "**/user/account/login", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object doUserLoginJSON(@ModelAttribute("authlogin") AuthLogin authLogin,
					BindingResult result,
					HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException
	{
		Model model = super.initRequest(request, authLogin);
		getLogger().debug("Processing auth login request for user " + authLogin);

		authLogin.setPassword(authLogin.getClearPassword());
		User user = getUserAuthenticator().login(model, authLogin, response);

		if ( !result.hasErrors() )
		{
			success(model, "msg.base.user.account.login.success");
		}
		else
		{
			authLogin.setClearPassword("");
			fail(model, "msg.base.user.account.login.failure", result, Errors.NO_ERRORS);
		}

		// The user's login was successful, which means this user is now logged in
		//
		model.put(USER, user);

		JsonAuthLoginResponse retVal = new JsonAuthLoginResponse(model);
		retVal.setUser(user);

		return  retVal;
	}

	@RequestMapping(value = "**/user/auth/reset", method = RequestMethod.GET)
	public ModelAndView resetLogin(@ModelAttribute("authlogin") @Valid AuthLogin authLogin,
					BindingResult result,
					HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException
	{
		Model model = super.initRequest(request, authLogin);

		if ( !result.hasErrors() )
		{
			getUserAuthenticator().login(model, authLogin, response);

			if ( result.hasErrors() )
			{
				return new ModelAndView("redirect:index.html", model);
			}
		}

		return redirOnSuccess(model, result, null, null, "../account/resetpassword.html", request, response);
	}

	@RequestMapping(value = "**/user/account/logout")
	public ModelAndView doLogout(@RequestParam(value = "redirURL", required = false) String redirURL,
							@RequestParam( value = "redirView", required = false) String redirView,
							HttpServletRequest request, HttpServletResponse response)
	{
		getUserAuthenticator().logout(new User(), response);
		return redirWithDefault(null, null, redirView, redirURL, "../index.html", request, response);
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
