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

import com.i4one.base.core.Base;
import com.i4one.base.core.Utils;
import static com.i4one.base.core.Utils.trimString;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balance.BalanceSelectStringifier;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.transaction.TransactionRecord;
import com.i4one.base.model.transaction.TransactionRecordDao;
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
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class CreditDebitController extends BaseAdminViewController
{
	private UserManager userManager;
	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		// List all of the balances available for the admin to credit
		//
		Set<Balance> balances = getBalanceManager().getAllBalances(model.getSingleClient(), SimplePaginationFilter.NONE);
		model.put("balances", toSelectMapping(balances, new BalanceSelectStringifier(model.getSingleClient(), getBalanceManager()), model.getLanguage()));

		return model;
	}

	@RequestMapping(value = "**/admin/members/creditdebit", method = RequestMethod.GET)
	public Model showForm(@ModelAttribute("users") CreditDebitUsers users, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, users);

		return initResponse(model, response, users);
	}

	@RequestMapping(value = "**/admin/members/creditdebit", method = RequestMethod.POST)
	//XXX: Needs fixing @Transactional(readOnly = false)
	public Model doCreditDebit(@ModelAttribute("users") @Valid CreditDebitUsers users, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Model model = initRequest(request, users);

		//@NotBlank(message = "msg.base.CreditDebitUsers.invalidReason")
		users.getReason().forEach( (lang, value) -> { if ( Utils.isEmpty(value)) {result.addError(new FieldError("users", "reason", users.getReason(), false, new String[]{ "msg.base.CreditDebitUsers.invalidReason"}, new String[]{"lang", lang}, "msg.base.CreditDebitUsers.invalidReason"));} } );

		// Add the failure message first in case our database connection refuses further queries
		// later on due to a query failure
		//
		if ( !result.hasErrors() )
		{
			try
			{
				String[] usernames = users.getUsernames().split("\n");
				for ( String currUsername : usernames )
				{
					User currUser = new User();
					currUser.setUsername(trimString(currUsername));
					currUser.setClient(model.getSingleClient());

					getLogger().debug("Looking up user: " + currUsername);
					currUser = getUserManager().lookupUser(currUser);

					if ( currUser.exists() )
					{
						/*
						UserBalance userBalance = getUserBalanceManager().getUserBalanceForUpdate(currUser, users.getBalance());
						userBalance.incTotal(users.getAmount());
						*/

						ReturnType<UserBalance> updateResult = getUserBalanceManager().increment(
							new UserBalance(currUser, users.getBalance(), model.getTimeInSeconds()),
							users.getAmount());

						if ( updateResult.getPost().exists() )
						{
							Transaction t = (Transaction) updateResult.get("transaction");
							t.setDescr(users.getReason());
							getTransactionRecordDao().updateBySer(t.getDelegate());
						}
						else
						{
							result.addError(new ObjectError(result.getObjectName(), new String[] { "msg.base.admin.members.creditdebit.update.nsf" }, new Object[] {"userBalance", updateResult.getPost(), "user", currUser}, "Could not credit user $User.username since the user's balance of $userBalance.total would reduce below zero"));
						}
					}
					else
					{
						getLogger().debug("User not found: " + result.getObjectName());

						// Add an error message for this user
						//
						result.addError(new ObjectError(result.getObjectName(), new String[] { "msg.base.admin.members.creditdebit.update.dne" }, new Object[] {"username", currUsername}, "Could not credit user with username $username: user not found"));
					}
				}

				if ( result.hasErrors() )
				{
					fail(model, "msg.base.admin.members.creditdebit.update.failure", result, Errors.NO_ERRORS);
				}
				else
				{
					success(model, "msg.base.admin.members.creditdebit.update.success");

					// Successful completion, clear the users portion of the form so
					// they don't get credited twice accidentally
					//
					users.setUsernames("");
				}
			}
			catch (Errors errors)
			{
				fail(model, "msg.base.admin.members.creditdebit.update.failure", result, errors);
			}
		}
		else
		{
			fail(model, "msg.base.admin.members.creditdebit.update.failure", result, Errors.NO_ERRORS);
		}

		return initResponse(model, response, users);
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

	public TransactionRecordDao getTransactionRecordDao()
	{
		return (TransactionRecordDao) Base.getInstance().getDaoManager().getNewDao(new TransactionRecord());
	}
}
