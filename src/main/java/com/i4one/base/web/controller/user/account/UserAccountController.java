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

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.pagination.UserPagination;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.transaction.TransactionManager;
import com.i4one.base.model.transaction.TransactionPagination;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Authenticator;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.user.BaseUserViewController;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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

/**
 * @author Hamid Badiozamani
 */
@Controller
public class UserAccountController extends BaseUserViewController
{
	private UserManager userManager;
	private TransactionManager transactionManager;

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
	@ModelAttribute("user")
	public User initUser(HttpServletRequest request)
	{
		return getRequestState().getModel().getUser();
	}

	@RequestMapping(value = "**/user/account/activity")
	public Model doGetTransactions(@ModelAttribute("pagination") TransactionPagination pagination, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, pagination);
		addMessageToModel(model, Model.TITLE, "msg.base.user.account.activity.title");

		User user = model.getUser();

		Set<Transaction> transactions = getTransactionManager().getRootTransactions(user, pagination);

		// Some transactions (e.g. friend referral trickle transactions) may belong to a different user
		// we're only interested in displaying this user's transactions which is why we use the
		// UserPagination filter when retrieving child transactions
		//
		UserPagination userPagination = new UserPagination(user, SimplePaginationFilter.NONE);
		transactions.stream().forEach( (currTransaction) ->
		{
			currTransaction.setChildren(getTransactionManager().getTransactions(currTransaction, userPagination));
		});

		model.put("transactions", transactions);

		return initResponse(model, response, pagination);
	}

	@RequestMapping(value = { "**/user/account/profile", "**/user/account/updatepersonalinfo", "**/user/account/updatecontactinfo"}, method = RequestMethod.GET)
	public Model getUserUpdateProfileForm(@ModelAttribute("user") User user, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		// Initialize the form backing object to our currently logged in user
		//
		user.copyFrom(getRequestState().getModel().getUser());

		Model model = initRequest(request, user);
		addMessageToModel(model, Model.TITLE, "msg.base.user.account.profile.title");

		return initResponse(model, response, user);
	}

	@RequestMapping(value = { "**/user/account/profile", "**/user/account/updatepersonalinfo", "**/user/account/updatecontactinfo"}, method = RequestMethod.POST)
	public Model doUserUpdateProfile(@ModelAttribute("user") @Valid User user,
						@RequestParam(value = "redirURL", required = false) String redirURL,
						@RequestParam(value = "redirView", required = false) String redirView,
						BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
	{
		Model model = initRequest(request, user);
		addMessageToModel(model, Model.TITLE, "msg.base.user.account.profile.title");

		try
		{
			getLogger().debug("Updating user " + user);

			// Need to set the permission to update
			//
			user.setSer(model.getUser().getSer());

			// Perform the update, the manager will take care of merging the two profiles
			//
			ReturnType<User> retVal = getUserManager().update(user);

			// Login to get the most up-to-date profile
			//
			getUserAuthenticator().relogin(retVal.getPost(), request, response);
			user.copyOver(retVal.getPost());

			// If we got this far, we know the object was updated successfully
			//
			success(model, "msg.userManager.update.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.userManager.update.failure", result, errors);
		}

		return initResponse(model, response, user);
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

	public TransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Autowired
	public void setTransactionManager(TransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
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
