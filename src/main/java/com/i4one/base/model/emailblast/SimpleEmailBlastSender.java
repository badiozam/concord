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
package com.i4one.base.model.emailblast;

import com.i4one.base.model.targeting.TargetListPagination;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.email.EmailManager;
import com.i4one.base.model.email.SimpleEmailManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.model.user.UserManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEmailBlastSender extends BaseLoggable implements EmailBlastSender
{
	private EmailManager emailManager;
	private UserManager userManager;
	private UserBalanceManager userBalanceManager;
	private BalanceManager balanceManager;
	private EmailBlastManager emailBlastManager;

	public static final String OPENCOUNT = "opencount";
	public static final String ID = "id";

	@Override
	public void sendEmailBlast(EmailBlast emailBlast)
	{
		Set<User> users;
		if ( Utils.isEmpty(emailBlast.getTargetSQL()) )
		{
			Balance defaultBalance = getBalanceManager().getDefaultBalance(emailBlast.getClient());

			Set<UserBalance> userBalances = getUserBalanceManager().getUserBalances(defaultBalance, SimplePaginationFilter.NONE);
			users = userBalances
				.stream()
				.map(UserBalance::getUser)
				.filter(User::getCanEmail)
				.collect(Collectors.toSet());
		}
		else
		{
			users = getEmailBlastManager().getTargetUsers(emailBlast, TargetListPagination.NONE());
		}

		emailBlast.setSendStartTimeSeconds(Utils.currentTimeSeconds());
		emailBlast.setTotalCount(users.size());
		emailBlast.setStatus(EmailBlastStatus.SENDING);
		getEmailBlastManager().updateAttributes(emailBlast);

		// Here we send all of the e-mails
		//
		// XXX: Needs to be reworked for fragmenting
		//
		int totalSent = sendEmailBlast(users, emailBlast);

		emailBlast.setTotalSent(totalSent);
		emailBlast.setSendEndTimeSeconds(Utils.currentTimeSeconds());

		// If there is a exist, then we go back to setting this to PENDING
		// since we'll need to send this out again sometime. Note that
		// setting the schedule to an empty string after it has been sent
		// out a number of times would require the admin to update this status
		// to COMPLETED at that time, otherwise, the e-mail blast will go out
		// one more time before it's done.
		//
		if ( emailBlast.isRecurrent() )
		{
			// Recurring e-mail blast, we'll send out again soon
			//
			emailBlast.setStatus(EmailBlastStatus.PENDING);
		}
		else
		{
			emailBlast.setStatus(EmailBlastStatus.COMPLETED);
		}

		getEmailBlastManager().updateAttributes(emailBlast);
	}

	@Override
	public void sendEmailBlast(User user, EmailBlast emailBlast)
	{
		sendEmailBlast(Arrays.asList(user), emailBlast);
	}

	@Override
	public int sendEmailBlast(Collection<User> users, EmailBlast emailBlast)
	{
		Map<String, Object> model = new HashMap<>();

		buildEmailBlastModel(model, emailBlast);

		int totalSent = 0;
		for ( User currUser : users )
		{
			// Ensure we have complete and up-to-date data before sending
			//
			getUserManager().loadById(currUser, currUser.getSer());

			if ( currUser.getCanEmail() )
			{
				buildUserEmailBlastModel(model, currUser, emailBlast);
				getEmailManager().sendEmail(currUser, emailBlast.getEmailTemplate(), model);

				totalSent++;
			}
		}

		return totalSent;
	}

	@Override
	public Map<String, Object> getEmailBlastModel(User user, EmailBlast emailBlast)
	{
		Map<String, Object> retVal = new HashMap<>();
		
		buildEmailBlastModel(retVal, emailBlast);
		buildUserEmailBlastModel(retVal, user, emailBlast);

		return retVal;
	}

	protected Map<String, Object> buildEmailBlastModel(Map<String, Object> model, EmailBlast emailBlast)
	{
		int emailBlastID = emailBlast.getSer();
		model.put(ID, emailBlastID);

		return getEmailManager().buildEmailModel(emailBlast.getClient(), model);
	}

	protected Map<String, Object> buildUserEmailBlastModel(Map<String, Object> model, User user, EmailBlast emailBlast)
	{
		SingleClient client = emailBlast.getClient();
		model = getEmailManager().buildUserEmailModel(client, user, model);

		String openCountURL = "<img src=\"" + model.get(SimpleEmailManager.BASEURL) + "emails/" + emailBlast.getSer() + "-" + user.getSer() + ".pngg\" width=\"1\" height=\"1\">";
		model.put(OPENCOUNT, openCountURL);

		return model;
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

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
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

	public EmailManager getEmailManager()
	{
		return emailManager;
	}

	@Autowired
	public void setEmailManager(EmailManager emailManager)
	{
		this.emailManager = emailManager;
	}

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}

}
