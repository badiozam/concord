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
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.preference.PreferenceAnswerManager;
import com.i4one.base.model.preference.PreferenceManager;
import com.i4one.base.model.preference.UserPreferenceManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
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
public class UserPreferenceController extends BaseUserViewController
{
	private UserManager userManager;

	private PreferenceManager preferenceManager;
	private PreferenceAnswerManager preferenceAnswerManager;
	private UserPreferenceManager userPreferenceManager;

	private Authenticator<User> userAuthenticator;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		Map<String, String> validCanEmail = new LinkedHashMap<>();
		validCanEmail.put("true", model.buildMessage("msg.base.User.canEmail.true"));
		validCanEmail.put("false", model.buildMessage("msg.base.User.canEmail.false"));
		model.put("validCanEmail", validCanEmail);

		return model;
	}

	/**
	 * Initialize the user being used as the model attribute. We assume that all
	 * methods in this controller are operations on the logged in user.
	 * 
	 * @param request The incoming HTTP request
	 * 
	 * @return The currently logged in user
	 */
	@ModelAttribute("userPreferences")
	public WebModelUserPreferences initWebModelPreferences(HttpServletRequest request)
	{
		WebModelUserPreferences retVal = new WebModelUserPreferences();

		getLogger().debug("initWebModelPreferences called.");

		retVal.setPreferenceManager(getPreferenceManager());
		retVal.setPreferenceAnswerManager(getPreferenceAnswerManager());
		retVal.setUserPreferenceManager(getUserPreferenceManager());

		retVal.setModel(getRequestState().getModel());

		retVal.setUser(new WebModelUserPreferenceUser());
		retVal.getUser().consume(getUserManager().getById(getRequestState().getUser().getSer()));
		//retVal.getUser().resetDelegateBySer(getRequestState().getModel().getUser().getSer());

		getLogger().debug("initWebModelPreferences returning with {}.", retVal);

		return retVal;
	}

	@RequestMapping(value = "**/user/account/updatepreferences", method = RequestMethod.GET)
	public Model getUserUpdateProfileForm(@ModelAttribute("userPreferences") WebModelUserPreferences userPreferences, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, userPreferences);
		addMessageToModel(model, Model.TITLE, "msg.base.user.account.profile.title");

		return initResponse(model, response, userPreferences);
	}

	@RequestMapping(value = "**/user/account/updatepreferences", method = RequestMethod.POST)
	public Model doUserUpdateProfile(@ModelAttribute("userPreferences") @Valid WebModelUserPreferences userPreferences,
						BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
	{
		Model model = initRequest(request, userPreferences);
		addMessageToModel(model, Model.TITLE, "msg.base.user.account.profile.title");

		try
		{
			getLogger().debug("Updating user " + userPreferences.getUser());

			// Need to set the permission to update
			//
			userPreferences.getUser().setSer(model.getUser().getSer());

			// Perform the update, the manager will take care of merging the two profiles
			//
			ReturnType<User> retVal = getUserManager().update(userPreferences.getUser());

			// Perform the updating of the user's preferences
			//
			getUserPreferenceManager().setUserPreferences(userPreferences.getUser(), model.getSingleClient(), userPreferences.getUserPreferences());

			// Login to get the most up-to-date profile
			//
			getUserAuthenticator().relogin(retVal.getPost(), request, response);
			userPreferences.getUser().copyOver(retVal.getPost());

			// If we got this far, we know the object was updated successfully
			//
			success(model, "msg.userManager.update.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.userManager.update.failure", result, errors);
		}

		return initResponse(model, response, userPreferences);
	}

	public PreferenceManager getPreferenceManager()
	{
		return preferenceManager;
	}

	@Autowired
	public void setPreferenceManager(PreferenceManager preferenceManager)
	{
		this.preferenceManager = preferenceManager;
	}

	public PreferenceAnswerManager getPreferenceAnswerManager()
	{
		return preferenceAnswerManager;
	}

	@Autowired
	public void setPreferenceAnswerManager(PreferenceAnswerManager preferenceAnswerManager)
	{
		this.preferenceAnswerManager = preferenceAnswerManager;
	}

	public UserPreferenceManager getUserPreferenceManager()
	{
		return userPreferenceManager;
	}

	@Autowired
	public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager)
	{
		this.userPreferenceManager = userPreferenceManager;
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
