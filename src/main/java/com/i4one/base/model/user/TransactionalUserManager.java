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

import com.i4one.base.core.Utils;
import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.adminhistory.AdminHistory;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.BaseTransactionalManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class TransactionalUserManager extends BaseTransactionalManager<UserRecord, User> implements UserManager
{
	/** What an item looks like before a given operation */
	public static final String ATTR_PRE = "pre";

	/** What an item looks like after a given operation */
	public static final String ATTR_POST = "post";

	private UserManager userManager;

	@Override
	public User authenticate(User user, SingleClient client)
	{
		return getUserManager().authenticate(user, client);
	}

	@Transactional(readOnly = false)
	@Override
	public String generateVerificationCode(User user)
	{
		String retVal = getUserManager().generateVerificationCode(user);

		SingleClient client = user.getClient();
		try
		{

			// Create a new transaction with the details of what was done
			//
			Transaction t = newTransaction(user);
	
			setTransactionDescr(t, "msg.userManager.generateVerificationCode.xaction.descr", "user", user);
	
			t = createTransaction(null, t);

			//retVal.put("transaction", t);
		}
		catch (Exception e)
		{
			Errors errors = new Errors("userManager.generateVerificationCode", new ErrorMessage("msg.userManager.generateVerificationCode.txError", "Could not record transaction for $user", new Object[] { "user", user }, e));
			throw errors;
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Boolean> verify(User user, String code)
	{
		ReturnType<Boolean> retVal =  getUserManager().verify(user, code);

		if ( retVal.getPost() )
		{
			SingleClient client = user.getClient();
			try
			{
				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(user);
	
				setTransactionDescr(t, "msg.userManager.verify.xaction.descr", "user", user);
	
				t = createTransaction(retVal, t);

				retVal.put("transaction", t);
			}
			catch (Exception e)
			{
				Errors errors = new Errors("userManager.verify", new ErrorMessage("msg.userManager.verify.txError", "Could not record transaction for $user", new Object[] { "user", user }, e));
				throw errors;
			}
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> processBirthday(User user, int forYear)
	{
		ReturnType<User> retVal =  getUserManager().processBirthday(user, forYear);

		if ( retVal.getPost().exists() )
		{
			SingleClient client = user.getClient();
			try
			{
				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(user);
	
				setTransactionDescr(t, "msg.userManager.processBirthday.xaction.descr", "user", user);
	
				t = createTransaction(retVal, t);

				retVal.put("transaction", t);
			}
			catch (Exception e)
			{
				Errors errors = new Errors("userManager.processBirthday", new ErrorMessage("msg.userManager.processBirthday.txError", "Could not record transaction for $user", new Object[] { "user", user }, e));
				throw errors;
			}
		}

		return retVal;
	}

	@Override
	public ReturnType<User> resetPassword(User user) throws Exception
	{
		ReturnType<User> retVal = getUserManager().resetPassword(user);

		if ( retVal.getPost().exists() )
		{
			User currUser = retVal.getPost();
			SingleClient client = currUser.getClient();

			try
			{

				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(user);
	
				setTransactionDescr(t, "msg.userManager.resetPassword.xaction.descr", "user", user);
	
				t = createTransaction(retVal, t);

				retVal.put("transaction", t);
			}
			catch (Exception e)
			{
				Errors errors = new Errors("userManager.resetPassword", new ErrorMessage("msg.userManager.resetPassword.txError", "Could not record transaction for $user", new Object[] { "user", currUser }, e));
				throw errors;
			}
		}

		return retVal;
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

	@Transactional(readOnly = false)
	@Override
	public boolean optout(User user)
	{
		boolean retVal = getUserManager().optout(user);

		if ( retVal )
		{
			SingleClient client = user.getClient();

			try
			{
				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(user);
	
				setTransactionDescr(t, "msg.userManager.optout.xaction.descr", "user", user);
	
				t = createTransaction(null, t);
			}
			catch (Exception e)
			{
				Errors errors = new Errors("userManager.optout", new ErrorMessage("msg.userManager.optout.txError", "Could not record transaction for $user", new Object[] { "user", user }, e));
				throw errors;
			}
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public boolean updatePassword(User user)
	{
		boolean retVal = getUserManager().updatePassword(user);

		if ( retVal )
		{
			SingleClient client = user.getClient();

			try
			{
				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(user);
	
				setTransactionDescr(t, "msg.userManager.updatePassword.xaction.descr", "user", user);

				t = createTransaction(null, t);
			}
			catch (Exception e)
			{
				Errors errors = new Errors("userManager.updatePassword", new ErrorMessage("msg.userManager.updatePassword.txError", "Could not record transaction for $user", new Object[] { "user", user }, e));
				throw errors;
			}
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> create(User user)
	{
		ReturnType<User> retVal = getUserManager().create(user);

		if ( user.exists() )
		{
			SingleClient client = user.getClient();
			try
			{
				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(user);
	
				setTransactionDescr(t, "msg.userManager.create.xaction.descr", "user", user);
	
				t = createTransaction(retVal, t);

				retVal.put("transaction", t);
			}
			catch (Exception ex)
			{
				Errors errors = new Errors("userManager.create", new ErrorMessage("msg.userManager.create.txError", "Could not record transaction for $user: $ex.message", new Object[] { "user", user, "ex", ex }, ex));
				throw errors;
			}
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> update(User user)
	{
		ReturnType<User> retVal = getUserManager().update(user);
		AdminHistory adminHistory = (AdminHistory)retVal.get(BaseHistoricalManager.ATTR_ADMINHISTORY);

		// We only record if an administrator record wasn'retVal also recorded (likely indicating that this action
		// wasn'retVal performed by an administrator)
		//
		if ( adminHistory == null || !adminHistory.exists() )
		{
			SingleClient client = user.getClient();
			try
			{
				List<String> updatedItems = new ArrayList<>();

				User preUser = retVal.getPre();
				User postUser = retVal.getPost();

				// Generate the updated items list
				//
				if ( postUser.getBirthDD() != preUser.getBirthDD() )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.birthDD", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( postUser.getBirthMM() != preUser.getBirthMM() )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.birthMM", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( postUser.getBirthYYYY() != preUser.getBirthYYYY() )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.birthYYYY", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getCellPhone) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.cellPhone", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getCity) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.city", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getEmail) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.email", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getFirstName) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.firstName", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getGender) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.gender", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getHomePhone) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.homePhone", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getLastName) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.lastName", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getState) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.state", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getStreet) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.street", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getUsername) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.username", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getZipcode) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.zipcode", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getCanCall) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.canCall", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getCanEmail) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.canEmail", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getCanSMS) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.canSMS", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}

				if ( !Utils.equalsOrNull(preUser, postUser, User::getIsMarried) )
				{
					updatedItems.add(getMessageManager().buildMessage(client, "msg.base.User.isMarried", getRequestState().getLanguage(), preUser, ATTR_POST, postUser));
				}


				// Create a new transaction with the details of what was done
				//
				Transaction t = newTransaction(user);

				setTransactionDescr(t, "msg.userManager.update.xaction.descr", "updated", updatedItems, "pre", preUser, "post", postUser);

				t = createTransaction(retVal, t);

				retVal.put("transaction", t);
			}
			catch (Exception e)
			{
				Errors errors = new Errors("userManager.update", new ErrorMessage("msg.userManager.update.txError", "Could not record transaction for $user", new Object[] { "user", user }, e));
				throw errors;
			}
		}
		else
		{
			getLogger().debug("Skipping transaction creation since admin history record was created:" + adminHistory);
		}

		return retVal;
	}

	@Override
	public Set<User> getAllUsers(SingleClient client, PaginationFilter daoConfigurer)
	{
		return getUserManager().getAllUsers(client, daoConfigurer);
	}

	@Override
	public Set<User> getAllMembers(SingleClient client, PaginationFilter pagination)
	{
		return getUserManager().getAllMembers(client, pagination);
	}

	@Override
	public UserSettings getSettings(SingleClient client)
	{
		return getUserManager().getSettings(client);
	}

	@Override
	public ReturnType<UserSettings> updateSettings(UserSettings settings)
	{
		return getUserManager().updateSettings(settings);
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	@Qualifier("base.HistoricalUserManager")
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	@Override
	public Manager<UserRecord, User> getImplementationManager()
	{
		return getUserManager();
	}

	@Override
	public PaginableRecordTypeDao<UserRecord> getDao()
	{
		return getUserManager().getDao();
	}
}
