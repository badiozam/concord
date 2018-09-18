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
package com.i4one.base.web.controller.admin.members;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.transaction.TransactionManager;
import com.i4one.base.model.transaction.TransactionPagination;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class MemberLookupController extends BaseAdminViewController
{
	private TransactionManager transactionManager;
	private UserManager userManager;
	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		return model;
	}

	@Override
	public Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);
		if ( modelAttribute instanceof LookupUser )
		{
			LookupUser lookup = (LookupUser)modelAttribute;

			TransactionPagination pagination = new TransactionPagination(model.getSingleClient(), lookup.getTransactionPagination());
			Set<Transaction> transactions = getTransactionManager().getTransactions(lookup.getUser(), pagination);
			model.put("transactions", transactions);

			Balance defBalance = getBalanceManager().getDefaultBalance(model.getSingleClient());
			lookup.setDefaultBalance(getUserBalanceManager().getUserBalance(lookup.getUser(), defBalance));
		}

		return retVal;
	}

	@ModelAttribute("lookup")
	public LookupUser getLookupUser()
	{
		LookupUser retVal = new LookupUser();

		retVal.getUser().setClient(getRequestState().getSingleClient());

		return retVal;
	}

	@RequestMapping(value = "**/admin/members/index", method = RequestMethod.GET)
	public Model doLookup(@ModelAttribute("lookup") LookupUser lookup, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, lookup);

		if ( !Utils.isEmpty(lookup.getLookupUsername()) )
		{
			try
			{
				// Only attempt this if there was some sort of form input
				//
				User dbUser = getUserManager().lookupUser(lookup.buildLookupUser());
				if ( dbUser.exists() )
				{
					lookup.setUser(dbUser);
				}
				else
				{
					result.addError(new ObjectError("lookup", new String[] {"msg.userManager.lookupUser.notfound"}, new Object[] { "lookup", lookup}, "No user with username '$lookup.lookupUsername' or e-mail '$lookup.lookupEmail' found."));
					fail(model, "msg.base.admin.members.index.lookup.failure", result, Errors.NO_ERRORS);
				}
			}
			catch (Errors errors)
			{
				fail(model, "msg.base.admin.members.index.lookup.failure", result, errors);
			}
		}
		else if ( lookup.getSelectRandom() )
		{
			try
			{
				// We get a random user that is a member of this client
				//
				Balance defBalance = getBalanceManager().getDefaultBalance(model.getSingleClient());
				UserBalance userBalance = getUserBalanceManager().randomUserBalance(defBalance);

				User dbUser = getUserManager().getById(userBalance.getUser(false).getSer());
				if ( dbUser.exists() )
				{
					lookup.setUser(dbUser);
				}
				else
				{
					result.addError(new ObjectError("lookup", new String[] {"msg.userManager.lookupUser.notfound"}, new Object[] { "lookup", lookup}, "No user with username '$lookup.lookupUsername' or e-mail '$lookup.lookupEmail' found."));
					fail(model, "msg.base.admin.members.index.lookup.failure", result, Errors.NO_ERRORS);
				}
			}
			catch (Errors errors)
			{
				fail(model, "msg.base.admin.members.index.lookup.failure", result, errors);
			}
		}
		else if ( lookup.getUser().exists() )
		{
			lookup.getUser().loadedVersion();
		}

		return initResponse(model, response, lookup);
	}

	@RequestMapping(value = "**/admin/members/index", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("lookup") LookupUser lookup, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
	{
		Model model = initRequest(request, lookup);

		getLogger().debug("Received model object " + lookup);
		User forUpdate;

		if ( !Utils.isEmpty(lookup.getLookupUsername()) )
		{
			forUpdate = getUserManager().lookupUser(lookup.buildLookupUser());
			getLogger().debug("Database lookup returned " + forUpdate);
		}
		else if ( lookup.getUser().exists() )
		{
			forUpdate = getUserManager().getById(lookup.getUser().getSer());
			getLogger().debug("Database lookup returned " + forUpdate);
		}
		else
		{
			// Obviously we don't have a user to update
			//
			fail(model, "msg.base.admin.members.index.update.failure", result, Errors.NO_ERRORS);
			return initResponse(model, response, lookup);
		}
	
		try
		{
			forUpdate.copyOver(lookup.getUser());
			getLogger().debug("After copy over we have " + forUpdate);

			ReturnType<User> updateRetval = getUserManager().update(forUpdate);

			lookup.getUser().copyFrom(updateRetval.getPost());

			success(model, "msg.base.admin.members.index.update.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.members.index.update.failure", result, errors);
			lookup.getUser().copyFrom(forUpdate);
		}

		return initResponse(model, response, lookup);
	}

	@RequestMapping(value = "**/admin/members/list", method = RequestMethod.GET)
	public Model doListing(HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, null);

		Set<User> users = getUserManager().getAllUsers(model.getSingleClient(), SimplePaginationFilter.NONE);
		model.put("users", users);

		model.put(MODELSTATUS, null);

		return initResponse(model, response, null);
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

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

}
