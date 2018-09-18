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
package com.i4one.base.model.email;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.web.interceptor.model.UserBalanceModelInterceptor;
import java.text.NumberFormat;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author Hamid Badiozamani
 */
@Service("base.EmailManager")
public class UserBalanceEmailManager extends BaseLoggable implements EmailManager
{
	private EmailManager emailManager;

	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;

	public static final String DEFAULT_USERBALANCE = UserBalanceModelInterceptor.DEFAULT_USERBALANCE;
	public static final String POINTS = "points";

	@Override
	public boolean sendEmail(SingleClient client, User user, String template, Map<String, Object> subVars) throws Exception
	{
		return getEmailManager().sendEmail(client, user, template, subVars);
	}

	@Override
	public boolean sendEmail(SingleClient client, String email, String template, Map<String, Object> subVars) throws Exception
	{
		return getEmailManager().sendEmail(client, email, template, subVars);
	}

	@Override
	public boolean sendEmail(User user, EmailTemplate template, Map<String, Object> model)
	{
		return getEmailManager().sendEmail(user, template, model);
	}

	@Override
	public boolean sendEmail(String to, String lang, EmailTemplate template, Map<String, Object> model)
	{
		return getEmailManager().sendEmail(to, lang, template, model);
	}

	@Override
	public Map<String, Object> buildEmailModel(SingleClient client, Map<String, Object> model)
	{
		Map<String, Object> retVal = getEmailManager().buildEmailModel(client, model);

		return retVal;
	}

	@Override
	public Map<String, Object> buildUserEmailModel(SingleClient client, User user, Map<String, Object> model)
	{
		Map<String, Object> retVal = getEmailManager().buildUserEmailModel(client, user, model);

		NumberFormat numberFormat = NumberFormat.getNumberInstance(client.getLocale());
		String defaultLang = client.getLanguageList().get(0);
		String lang = defaultLang; // currUser.getLanguage()

		// We look up and inject the user's balance into the model
		//
		Balance defaultBalance = getBalanceManager().getDefaultBalance(client);
		UserBalance defaultUserBalance = getUserBalanceManager().getUserBalance(user, defaultBalance);
		int amount = defaultUserBalance.getTotal();

		model.put(DEFAULT_USERBALANCE, defaultUserBalance);
		model.put(POINTS, numberFormat.format(amount) + " " + defaultUserBalance.getBalance().formatName(amount).get(lang) );

		return retVal;
	}

	public EmailManager getEmailManager()
	{
		return emailManager;
	}

	@Autowired
	@Qualifier("base.SimpleEmailManager")
	public void setEmailManager(EmailManager emailManager)
	{
		this.emailManager = emailManager;
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
