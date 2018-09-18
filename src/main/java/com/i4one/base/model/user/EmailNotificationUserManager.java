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
package com.i4one.base.model.user;

import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.manager.BaseEmailNotificationManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.UserManager")
public class EmailNotificationUserManager extends BaseEmailNotificationManager<UserRecord, User> implements UserManager
{
	private UserManager userManager;
	
	public static final String BIRTHDAY_EMAIL_KEY = "emails.base.processBirthday";
	public static final String FORGOTPW_EMAIL_KEY = "emails.base.forgotPassword";
	public static final String REGISTRATION_EMAIL_KEY = "emails.base.registration";

	@Override
	public Manager<UserRecord, User> getImplementationManager()
	{
		return getUserManager();
	}

	@Override
	public String generateVerificationCode(User user)
	{
		return getUserManager().generateVerificationCode(user);
	}

	@Override
	public ReturnType<Boolean> verify(User user, String code)
	{
		return getUserManager().verify(user, code);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> create(User item)
	{
		try
		{
			ReturnType<User> retVal = getUserManager().create(item);

			if ( retVal.getPost().exists() )
			{
				// Bypass the opt-out field, since we always want the user to receive this e-mail
				//
				boolean oldCanEmail = retVal.getPost().getCanEmail();
				retVal.getPost().setCanEmail(Boolean.TRUE);

				SingleClient client = getRequestState().getSingleClient();

				EmailTemplate emailTemplate = getEmailTemplateManager().getEmailTemplate(client, REGISTRATION_EMAIL_KEY);
				Map<String, Object> model = new HashMap<>();
				model = getEmailManager().buildEmailModel(client, model);
				model = getEmailManager().buildUserEmailModel(client, item, model);
				model.put("request", getRequestState().getRequest());

				getEmailManager().sendEmail(item.getEmail(), getRequestState().getLanguage(), emailTemplate, model);

				// Revert the flag back for others in the chain to use
				//
				retVal.getPost().setCanEmail(oldCanEmail);
			}

			return retVal;
		}
		catch (Exception ex)
		{
			throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".create.emailError", "Could not send e-mail to $user.email", new Object[] { "user", item }, ex));
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> resetPassword(User user)
	{
		try
		{
			ReturnType<User> retVal = getUserManager().resetPassword(user);

			if ( retVal.getPost().exists() )
			{
				// Bypass the opt-out field, since we always want the user to receive this e-mail
				//
				boolean oldCanEmail = retVal.getPost().getCanEmail();
				retVal.getPost().setCanEmail(Boolean.TRUE);

				SingleClient client = getRequestState().getSingleClient();

				EmailTemplate emailTemplate = getEmailTemplateManager().getEmailTemplate(client, FORGOTPW_EMAIL_KEY);
				Map<String, Object> model = new HashMap<>();
				model = getEmailManager().buildEmailModel(client, model);
				model = getEmailManager().buildUserEmailModel(client, user, model);
				model.put("reset", retVal.get("resetPassword"));
				model.put("request", getRequestState().getRequest());

				getEmailManager().sendEmail(user.getEmail(), getRequestState().getLanguage(), emailTemplate, model);

				// Revert the flag back for others in the chain to use
				//
				retVal.getPost().setCanEmail(oldCanEmail);
			}

			return retVal;
		}
		catch (Exception ex)
		{
			throw new Errors(getInterfaceName() + ".resetPassword", new ErrorMessage("msg." + getInterfaceName() + ".resetPassword.emailError", "Could not send e-mail to $user.email", new Object[] { "user", user }, ex));
		}
	}

	@Override
	public ReturnType<User> processBirthday(User user, int forYear)
	{
		try
		{
			ReturnType<User> retVal = getUserManager().processBirthday(user, forYear);

			if ( retVal.getPost().exists() )
			{
				SingleClient client = getRequestState().getSingleClient();

				EmailTemplate emailTemplate = getEmailTemplateManager().getEmailTemplate(client, BIRTHDAY_EMAIL_KEY);

				Map<String, Object> model = new HashMap<>();
				model = getEmailManager().buildEmailModel(client, model);
				model = getEmailManager().buildUserEmailModel(client, user, model);

				// XXX: This needs to be changed to the user's preferred language
				//
				getEmailManager().sendEmail(user.getEmail(), client.getLanguageList().get(0), emailTemplate, model);
			}

			return retVal;
		}
		catch (Exception ex)
		{
			throw new Errors(getInterfaceName() + ".birthday", new ErrorMessage("msg." + getInterfaceName() + ".birthday.emailError", "Could not send e-mail to $user.email", new Object[] { "user", user }, ex));
		}
	}

	@Override
	public UserSettings getSettings(SingleClient client)
	{
		UserSettings retVal = getUserManager().getSettings(client);
		retVal.setBirthdayEmailTemplate(getEmailTemplateManager().getEmailTemplate(client, BIRTHDAY_EMAIL_KEY));

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<UserSettings> updateSettings(UserSettings settings)
	{
		ReturnType<UserSettings> retVal = getUserManager().updateSettings(settings);

		EmailTemplate template = getEmailTemplateManager().getEmailTemplate(settings.getClient(), BIRTHDAY_EMAIL_KEY);
		if ( template.exists() )
		{
			ReturnType<EmailTemplate> updatedTemplate = getEmailTemplateManager().update(template);
			retVal.addChain(getEmailTemplateManager(), "update", updatedTemplate);

			template = updatedTemplate.getPost();
		}
		else
		{
			template = settings.getBirthdayEmailTemplate();
			template.setClient(settings.getClient());
			template.setKey(BIRTHDAY_EMAIL_KEY);

			ReturnType<EmailTemplate> createdTemplate = getEmailTemplateManager().create(template);
			retVal.addChain(getEmailTemplateManager(), "create", createdTemplate);

			template = createdTemplate.getPost();
		}

		retVal.getPost().setBirthdayEmailTemplate(template);

		return retVal;
	}

	@Override
	public boolean updatePassword(User user)
	{
		return getUserManager().updatePassword(user);
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	@Qualifier("base.TransactionalUserManager")
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	@Override
	public boolean optout(User user)
	{
		return getUserManager().optout(user);
	}

	@Override
	public Set<User> search(UserSearchCriteria criteria)
	{
		return getUserManager().search(criteria);
	}
		
	@Override
	public User lookupUser(User user)
	{
		return getUserManager().lookupUser(user);
	}

	@Override
	public boolean existsUser(User user)
	{
		return getUserManager().existsUser(user);
	}

	@Override
	public Set<User> getAllUsers(SingleClient client, PaginationFilter pagination)
	{
		return getUserManager().getAllUsers(client, pagination);
	}

	@Override
	public Set<User> getAllMembers(SingleClient client, PaginationFilter pagination)
	{
		return getUserManager().getAllMembers(client, pagination);
	}

	@Override
	public User authenticate(User item, SingleClient client)
	{
		return getUserManager().authenticate(item, client);
	}

	@Override
	public PaginableRecordTypeDao<UserRecord> getDao()
	{
		return getUserManager().getDao();
	}
}
