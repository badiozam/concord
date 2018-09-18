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
package com.i4one.base.web.controller.user.support;

import com.i4one.base.core.LegacyEmailTemplate;
import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
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
public class SupportFormController extends BaseUserViewController
{
	private UserManager userManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object object)
	{
		Model model = super.initRequest(request, object);

		// Valid support categories (which are already in the language since they're part of the model object)
		//
		Map<String,String> validStates = new LinkedHashMap<>();
		String[] validStatesArray =  Utils.forceEmptyStr(model.buildMessage("msg.base.UserClient.validCategories")).split("\n");
		Arrays.asList(validStatesArray)
			.stream()
			.map( (currCategory) -> { return Utils.trimString(currCategory); } )
			.forEach( (currCategory) -> { validStates.put(currCategory, currCategory);} );
		model.put("validCategories", validStates);

		return model;
	}

	@RequestMapping(value = "**/user/support", method = RequestMethod.GET)
	public Model getSupportForm(@ModelAttribute("userClient") UserClient userClient, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, userClient);

		addMessageToModel(model, Model.TITLE, "msg.base.user.support.title");

		User user = model.getUser();
		if (user.exists() )
		{
			userClient.setEmail(user.getEmail());
		}

		return initResponse(model, response, userClient);
	}

	@RequestMapping(value = "**/user/support", method = RequestMethod.POST)
	public ModelAndView sendSupportForm(@ModelAttribute("userClient") @Valid UserClient userClient,
						BindingResult result,
						HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Model model = initRequest(request, userClient);
		addMessageToModel(model, Model.TITLE, "msg.base.user.support.title");

		if ( !result.hasErrors() )
		{
			SingleClient client = model.getSingleClient();
	
			// See if we can look up the user by e-mail
			//
			User user = new User();
			user.setClient(client);
			user.setEmail(userClient.getEmail());
	
			userClient.setRequest(request);
			userClient.setClient(client);
			userClient.setLoggedInUser(model.getUser());

			// If the user is sending the form in a language that we support, we use that language
			// otherwise, the support e-mail gets sent in the default language
			//
			List<String> supportedLangs = model.getSingleClient().getLanguageList();
			String lang = supportedLangs.contains(model.getLanguage()) ? model.getLanguage() : supportedLangs.get(0);
	
			String subject = getMessageManager().buildMessage(client, "emails.base.support.subject", lang, UserClient.class.getSimpleName(), userClient);
			String to = getMessageManager().buildMessage(client, "emails.base.support.to", lang, UserClient.class.getSimpleName(), userClient);
	
			String htmlBody = getMessageManager().getMessage(client, "emails.base.support.htmlbody").getValue();
			String plainBody = getMessageManager().getMessage(client, "emails.base.support.textbody").getValue();

			HashMap<String, Object> supportVars = new HashMap<>();
			supportVars.put(UserClient.class.getSimpleName(), userClient);
	
			LegacyEmailTemplate template = new LegacyEmailTemplate(client, userClient.getEmail(), subject, htmlBody, plainBody);
			template.setBcc("<hamid@i4oneinteractive.com>");
			template.setReplyTo(userClient.getEmail());
			try
			{
				template.sendEmail(supportVars, to);
			}
			catch (MessagingException ex)
			{
				Errors errors = new Errors();
				errors.addError(new ErrorMessage("msg.base.user.support.emailError", "Could not send e-mail on behalf of '$UserClient.email': $ex.message", new Object[] { UserClient.class.getSimpleName(), userClient , "ex", ex}, ex));
	
				convertErrors(result, errors);
				fail(model, "msg.base.user.support.error", result, errors);
			}
		}
		else
		{
			result.addError(new ObjectError("userClient", new String[] {"msg.base.user.support.fieldErrors"}, new Object[] { UserClient.class.getSimpleName(), userClient}, "Please correct the following errors"));

			fail(model, "msg.base.user.support.error", result, Errors.NO_ERRORS);
		}

		return redirOnSuccess(model, result, null, "supportdone.html", "supportdone.html", request, response);
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
