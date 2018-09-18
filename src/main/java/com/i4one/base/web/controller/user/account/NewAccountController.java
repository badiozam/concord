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

import static com.i4one.base.core.Base.getInstance;
import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.ClientSettings;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.user.facebook.AccessToken;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import com.i4one.base.web.persistence.Persistence;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class NewAccountController extends BaseUserViewController
{
	private UserManager userManager;

	private SingleClientManager singleClientManager;

	private Authenticator<User> userAuthenticator;

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		ClientSettings clientSettings = getSingleClientManager().getSettings(model.getSingleClient());

		if ( clientSettings.isCaptchaEnabled() )
		{
			model.put("captchaHTML", clientSettings.getReCaptcha().createRecaptchaHtml(request.getParameter("error"), null));
		}
		else
		{
			model.put("captchaHTML", "");
		}

		Calendar cal = model.getRequest().getCalendar();
		
		// Valid months needed for birth date
		//
		String[] months = DateFormatSymbols.getInstance(model.getRequest().getLocale()).getShortMonths();
		Map<Integer, String> validMonths = new LinkedHashMap<>();
		validMonths.put(-1, "--");
		int i = 0;
		for ( String currMonth : months )
		{
			// For some reason we get an empty month from the DateFormatSymbols' getShortMonths() method
			//
			if ( !Utils.isEmpty(currMonth))
			{
				validMonths.put(++i, currMonth);
			}
		}
		model.put("validMM", validMonths);

		// Valid days needed for birth date
		//
		Map<Integer, String> validDays = new LinkedHashMap<>(31);
		validDays.put(-1, "--");
		int minDay = cal.getMinimum(Calendar.DAY_OF_MONTH);
		int maxDay = cal.getMaximum(Calendar.DAY_OF_MONTH);
		for (i = minDay; i <= maxDay; i++ )
		{
			validDays.put(i, String.valueOf(i));
		}
		model.put("validDD", validDays);

		// Valid years needed for birth date
		//
		int youngest = 10;
		int oldest = 100;
		int currYear = cal.get(Calendar.YEAR);
		Map<Integer,String> validYears = new LinkedHashMap<>(oldest - youngest);
		validYears.put(-1, "--");

		for (i = (oldest - youngest); i >= youngest; i-- )
		{
			validYears.put(currYear - i, String.valueOf(currYear - i));
		}
		model.put("validYYYY", validYears);

		// Valid states (which are already in the language since they're part of the model object)
		//
		Map<String,String> validStates = new LinkedHashMap<>();
		String[] validStatesArray =  Utils.forceEmptyStr(model.buildMessage("msg.base.User.validStates")).split("\n");
		Arrays.asList(validStatesArray)
			.stream()
			.map( (currState) -> { return Utils.trimString(currState); } )
			.forEach( (currState) -> { validStates.put(currState, currState);} );
		model.put("validStates", validStates);

		// Valid genders, a list of possible genders is matched to the equivalent human-readable
		// value using the key format 'msg.base.User.gender.<selected gender>' where <selected gender>
		// is one of the values in msg.base.User.validGenders
		//
		Map<String, String> validGenders = new LinkedHashMap<>();
		String[] validGendersArray = Utils.forceEmptyStr(model.buildMessage("msg.base.User.validGenders")).split("\n");
		Arrays.asList(validGendersArray)
			.stream()
			.map( (currGender) -> { return Utils.trimString(currGender); } )
			.forEach( (currGender) -> { validGenders.put(currGender, model.buildMessage("msg.base.User.gender." + currGender));} );
		model.put("validGenders", validGenders);

		Map<String, String> validCanEmail = new LinkedHashMap<>();
		validCanEmail.put("true", model.buildMessage("msg.base.User.canEmail.true"));
		validCanEmail.put("false", model.buildMessage("msg.base.User.canEmail.false"));
		model.put("validCanEmail", validCanEmail);

		if ( modelAttribute instanceof NewUser )
		{
			NewUser newUser = (NewUser)modelAttribute;

			newUser.setModel(model);
			newUser.setClient(model.getSingleClient());
			newUser.setClientSettings(clientSettings);
		}

		return model;
	}

	@RequestMapping(value = "**/user/account/register", method = RequestMethod.GET)
	public Model getRegistrationForm(@ModelAttribute("newuser") NewUser newUser,
						BindingResult result,
						@RequestParam(value = "code", required = false) String code,
						HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, newUser);

		// See if we have a Facebook token or not
		//
		AccessToken accessToken = (AccessToken)Persistence.getObject(request, AccessToken.PERSISTENCE);

		// If we have one, use it to get the newUser's information and pre-populate the fields
		//
		if ( accessToken != null && !Utils.isEmpty(accessToken.getToken()) )
		{
			String fbSecret = model.getSingleClient().getOptionValue("fb.secret");
			FacebookClient fbClient = new DefaultFacebookClient(accessToken.getToken(), fbSecret, Version.VERSION_2_6);

			com.restfb.types.User fbUser = fbClient.fetchObject("me", com.restfb.types.User.class, Parameter.with("fields", "id,first_name,last_name,middle_name,email,gender,birthday,hometown"));
			getLogger().debug("Got user {} from FB", fbUser);
	
			newUser.setFbUser(fbUser);
			newUser.setFacebookAccessToken(accessToken.getToken());

			// Remove the access token object since we no longer need it after having saved
			// the relevant information in the form backing object
			//
			Persistence.putObject(request, response, AccessToken.PERSISTENCE, null);
		}

		addMessageToModel(model, Model.TITLE, "msg.base.user.account.register.title");

		return initResponse(model, response, newUser);
	}

	@RequestMapping(value = "**/user/account/register", method = RequestMethod.POST)
	public ModelAndView doUserRegistration(@ModelAttribute("newuser") @Valid NewUser newUser,
						BindingResult result,
						@RequestParam(value = "redirURL", required = false) String redirURL,
						@RequestParam(value = "redirView", required = false) String redirView,
						HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, newUser);

		addMessageToModel(model, Model.TITLE, "msg.base.user.account.register.title");

		if ( !result.hasErrors() )
		{
			try
			{
				getLogger().debug("Performing user registration for " + newUser);
				getUserManager().create(newUser);

				// If the newUser signed up successfully, we can log them in and process any
				// first time login triggers
				//
				getUserAuthenticator().login(model, newUser, response);
			}
			catch (Errors errors)
			{
				fail(model, "msg.base.user.account.register.fieldErrors", result, errors);
			}
		}
		else
		{
			result.addError(new ObjectError("user", new String[] {"msg.base.user.account.register.fieldErrors"}, new Object[] { "user", newUser}, "Please correct the errors above"));
			fail(model, "msg.base.user.account.register.title", result, new Errors());
		}

		return redirOnSuccess(model, result, redirView, redirURL, "registerdone.html", request, response);
	}
	
	@RequestMapping(value ="**/user/account/checkusername.jsc", produces = "text/javascript")
	public void doCheckUser(@RequestParam(value = "username", required = false) String username,
				@RequestParam(value = "email", required = false) String email,
				@RequestParam(value = "cellphone", required = false) String cellphone,
				HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);
		SingleClient client = model.getSingleClient();

		User user = new User();
		user.setClient(client);
		user.setUsername(Utils.forceEmptyStr(username));
		user.setEmail(Utils.forceEmptyStr(email));
		user.setCellPhone(Utils.forceEmptyStr(cellphone));

		String result = getInstance().getGson().toJson(Boolean.valueOf(getUserManager().existsUser(user)));
		//String result = Base.getInstance().getJacksonObjectMapper().writeValueAsString(Boolean.valueOf(getUserManager().existsUser(user)));

		response.setContentType("text/javascript");
		response.getWriter().print(result);
	}

	@RequestMapping(value = "**/user/account/forgot", method = RequestMethod.GET)
	public Model getUserForgotPasswordForm(@ModelAttribute("forgotuser") User login, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		return initResponse(initRequest(request, login), response, login);
	}

	@RequestMapping(value = "**/user/account/forgot", method = RequestMethod.POST)
	public ModelAndView doUserForgotPassword(@ModelAttribute("forgotuser") ForgotPasswordUser user,
							BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Model model = super.initRequest(request, user);

		try
		{
			// We can look up by either username or e-mail address but the form only
			// sets the e-mail address field
			//
			user.setUsername(user.getEmail());

			ReturnType<User> resetUser = getUserManager().resetPassword(user);
			if ( !resetUser.getPost().exists() )
			{
				result.addError(new ObjectError("forgotuser", new String[] {"msg.userManager.resetPassword.failed"}, new Object[] { "user", user}, "Sorry, there were no users matching the username / e-mail address '$user.email'"));
				fail(model, "msg.base.user.account.forgot.failed", result, Errors.NO_ERRORS);
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.user.account.forgot.failed", result, errors);
		}

		return redirOnSuccess(model, result, null, null, "forgotdone.html", request, response);
	}

	@RequestMapping(value = "**/user/account/optout", method = RequestMethod.GET)
	public Model getOptoutForm(@ModelAttribute("id") String id, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = super.initRequest(request, id);
		SingleClient client = model.getSingleClient();

		// Attempt to look up the user by e-mail or username
		//
		User byUsername = new User();
		byUsername.setClient(client);
		byUsername.setUsername(Utils.forceEmptyStr(id));

		User byEmail = new User();
		byEmail.setClient(client);
		byEmail.setEmail(Utils.forceEmptyStr(id));

		User bySer = new User();
		bySer.setClient(client);
		bySer.getDelegate().setSer(Utils.defaultIfNaN(id, 0));

		boolean existsUser = getUserManager().existsUser(byUsername) || getUserManager().existsUser(byEmail) || getUserManager().existsUser(bySer);

		if ( !existsUser )
		{
			result.addError(new ObjectError("user", new String[] {"msg.userManager.optout.dne"}, new Object[] { "id", id}, "Sorry, we were not able to find your account. Please log into your account to update your settings."));
		}

		model.put("id", id);
		return initResponse(model, response, id);
	}

	@RequestMapping(value = "**/user/account/optout", method = RequestMethod.POST)
	public ModelAndView doOptout(@ModelAttribute("id") String id, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Model model = super.initRequest(request, id);
		SingleClient client = model.getSingleClient();

		try
		{
			// Attempt to look up the user by e-mail or username
			//
			User byUsername = new User();
			byUsername.setClient(client);
			byUsername.setUsername(Utils.forceEmptyStr(id));

			User byEmail = new User();
			byEmail.setClient(client);
			byEmail.setEmail(Utils.forceEmptyStr(id));

			User bySer = new User();
			bySer.setClient(client);
			bySer.getDelegate().setSer(Utils.defaultIfNaN(id, 0));

			boolean existsUser = getUserManager().optout(byUsername) || getUserManager().optout(byEmail) || getUserManager().optout(bySer);
			if ( !existsUser )
			{
				result.addError(new ObjectError("user", new String[] {"msg.userManager.optout.dne"}, new Object[] { "id", id}, "Sorry, we were not able to find your account (\"$id\"). Please log into your account to update your settings."));
			}
		}
		catch (Errors errors)
		{
			convertErrors(result, errors);
		}

		return redirOnSuccess(model, result, null, null, "optoutdone.html", request, response);
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

	public SingleClientManager getSingleClientManager()
	{
		return singleClientManager;
	}

	@Autowired
	public void setSingleClientManager(SingleClientManager singleClientManager)
	{
		this.singleClientManager = singleClientManager;
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

