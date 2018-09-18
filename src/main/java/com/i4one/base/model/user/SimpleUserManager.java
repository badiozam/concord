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
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.friendref.FriendRef;
import com.i4one.base.model.friendref.FriendRefManager;
import com.i4one.base.model.manager.BaseSimpleClientTypeManager;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.data.UserDatum;
import com.i4one.base.model.user.data.UserDatumManager;
import com.i4one.base.model.userlogin.UserLogin;
import com.i4one.base.model.userlogin.UserLoginManager;
import com.i4one.base.web.RequestState;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the user manager interface using the supplied
 * underlying DAO classes
 *
 * @author Hamid Badiozamani
 */
@Service
public class SimpleUserManager extends BaseSimpleClientTypeManager<UserRecord,User> implements UserManager
{
	private UserLoginManager userLoginManager;
	private UserDatumManager userDatumManager;
	private FriendRefManager friendRefManager;

	private RequestState requestState;

	private static final String VERIFICATION_CODE = "userManager.verificationCode";
	private static final String VERIFICATION_CODE_LENGTH = "userManager.verificationCodeLength";
	private static final String VERIFICATION_CODE_COOLDOWN_SECONDS = "userManager.verificationCodeCooldownSeconds";

	private static final String BIRTHDAYJOB_ENABLED = "userManager.birthdayJob.enabled";
	private static final String BIRTHDAY_PROCESSED = "birthdayProcessed-{0}";

	@Transactional(readOnly = false)
	@Override
	public User authenticate(User user, SingleClient client)
	{
		// Get the user by username
		//
		User dbUser = getUserForLogin(user);

		// See if we have a match in the database
		//
		if ( dbUser.exists() )
		{
			try
			{
				// XXX: Major security problem as anyone can just set an Admin token
				//
				Admin currAdmin = UserAdminModelInterceptor.getAdmin(getRequestState().getRequest());

				// Use the latest information from the database. We compare clear text passwords first, then
				// we compare the database's md5 against the input object's clear version
				//
				if ( dbUser.getPassword().equalsIgnoreCase(user.getPassword()) ||
					dbUser.getMD5Password().equals(user.getMD5Password()) ||
					currAdmin.exists() )
				{
					// Set the last login time
					//
					/*
					dbUser.setLastLoginTimeSeconds(Utils.currentTimeSeconds());
					getDao().updateBySer(dbUser.getDelegate(), "lastlogintime");
					*/
					UserLogin userLogin = new UserLogin();
					userLogin.setUser(dbUser);
					userLogin.setClient(client);

					getUserLoginManager().create(userLogin);

					// Use the latest information from the database
					//
					return dbUser;
				}
				else
				{
					getLogger().debug("Non-matching password comparison");
					getLogger().debug("Admin: '" + currAdmin);
					getLogger().debug("Clear: '" + dbUser.getPassword() + "' vs " + user.getPassword());
					getLogger().debug("MD5: '" + dbUser.getMD5Password() + "' vs " + user.getMD5Password());

					return new User();
				}
			}
			catch (NoSuchAlgorithmException ex)
			{
				getLogger().error("Could not perform MD5 match", ex);
				return new User();
			}
		}
		else
		{
			getLogger().debug("User with username " + user.getUsername() + " does not exist");
			return dbUser;
		}
	}

	public User getUserForLogin(User user)
	{
		UserRecord record = getUserRecord(user, false);

		if ( record == null )
		{
			return new User();
		}
		else
		{
			return new User(record);
		}
	}

	@Override
	public User lookupUser(User user)
	{
		return getUserForLogin(user);
	}

	@Override
	public boolean existsUser(User user)
	{
		return lookupUser(user).exists();
	}

	@Override
	public boolean optout(User user)
	{
		User dbUser = lookupUser(user);
		if ( dbUser.exists() )
		{
			if ( dbUser.getCanEmail() )
			{
				dbUser.setCanEmail(false);
				super.update(dbUser);

				// Convenient to have the serial number so callers won't have to
				// look up the user again
				//
				user.setSer(dbUser.getSer());

				return true;
			}
			else
			{
				Errors errors = new Errors("userManager.optout", new ErrorMessage("msg.userManager.optout.collision", "You are already opted out!", new Object[] { "user", user}));
				throw errors;
			}
		}
		else
		{
			// The user does not exist!
			//
			return false;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> resetPassword(User user) throws Exception
	{
		ReturnType<User> retVal = new ReturnType<>();

		// Since this just like any create/update method, we allow the model object to do any
		// updates it needs to
		//
		user.setOverrides();

		// Attempt to look up the user by username, and then e-mail address
		//
		UserRecord userRecord = getUserRecord(user, true);

		if ( userRecord == null || !userRecord.exists() )
		{
			// Couldn't find the user by username or e-mail
			//
			retVal.setPost(new User());
		}
		else
		{
			User dbUser = new User(userRecord);

			// Set the password to the MD5 hex version with the time stamp appended and send that
			// to the user for their next login and also force them to update their password
			//
			String newPwd = Utils.getMD5Hex(Utils.abbrStr(userRecord.getPassword(), 32) + Utils.currentTimeSeconds());
			dbUser.setPassword(newPwd);
			dbUser.setForceUpdate(true);

			// We use the super class update to bypass unnecessary checking but also to not trigger
			// the forceUpdate reset
			//
			getDao().updatePassword(userRecord, dbUser.getPassword());

			// Need to update the force update field here, note that it needs to happen after
			// the updatePassword(..) method is called since that method resets the force
			// update field to true
			//
			getDao().updateBySer(userRecord);

			// Convenient to have the serial number so callers won't have to
			// look up the user again
			//
			user.setSer(userRecord.getSer());

			retVal.setPost(dbUser);
			retVal.put("resetPassword", newPwd);
		}

		return retVal;
	}

	@Override
	public Set<User> search(UserSearchCriteria criteria)
	{
		return convertDelegates(getDao().getAllUsers(criteria));
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> processBirthday(User user, int forYear)
	{
		ReturnType<User> retVal = new ReturnType<>();
		retVal.setPre(user);
		retVal.setPost(new User());

		UserRecord record = getUserRecord(user, true);
		if ( record != null )
		{
			SingleClient singleClient = getRequestState().getSingleClient();
			String birthdayKey = MessageFormat.format(BIRTHDAY_PROCESSED, singleClient.getName());

			UserDatum lastBirthdayProcessed = getUserDatumManager().getUserDatum(user, birthdayKey);
			if ( !lastBirthdayProcessed.exists() ||
				lastBirthdayProcessed.hasExpired(getRequestState().getRequest().getTimeInSeconds()) ||
				Utils.defaultIfNaN(lastBirthdayProcessed.getValue(), 0) < forYear )
			{
				getLogger().debug("processBirthday: Processing birthday for user {} and year {}", user, forYear);

				// The only processing that happens is that we
				// mark the user's birthday as having been
				// processed for this year.
				//
				lastBirthdayProcessed.setUser(user);
				lastBirthdayProcessed.setKey(birthdayKey);
				lastBirthdayProcessed.setValue(String.valueOf(forYear));
				lastBirthdayProcessed.setDurationSeconds(86400 * 365);
				lastBirthdayProcessed.setTimeStampSeconds(getRequestState().getRequest().getTimeInSeconds());
	
				if ( lastBirthdayProcessed.exists() )
				{
					ReturnType<UserDatum> processedBirthday = getUserDatumManager().update(lastBirthdayProcessed);
					retVal.addChain(getUserDatumManager(), "update", processedBirthday);
				}
				else
				{
					ReturnType<UserDatum> processedBirthday = getUserDatumManager().create(lastBirthdayProcessed);
					retVal.addChain(getUserDatumManager(), "create", processedBirthday);
				}

				// We were successfull
				//
				retVal.setPost(user);
			}
			else
			{
				getLogger().debug("processBirthday: User {} has already been processed for year {}", user, forYear);
			}
		}
		else
		{
			getLogger().debug("processBirthday: Couldn't find user {}", user);
		}

		return retVal;
	}

	@Override
	public UserSettings getSettings(SingleClient client)
	{
		UserSettings retVal = new UserSettings();

		retVal.setClient(client);

		boolean enabled = Utils.defaultIfNaB(getClientOptionManager().getOption(client, BIRTHDAYJOB_ENABLED).getValue(), true);
		retVal.setBirthdayEnabled(enabled);

		return retVal;
	}

	@Override
	public ReturnType<UserSettings> updateSettings(UserSettings settings)
	{
		ReturnType<UserSettings> retVal = new ReturnType<>(settings);
		retVal.setPre(getSettings(settings.getClient()));

		// Birthday job enable/disable
		//
		updateOption(settings.getClient(), BIRTHDAYJOB_ENABLED, String.valueOf(settings.isBirthdayEnabled()), retVal);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> create(User user)
	{
		try
		{
			ReturnType<User> retVal = super.create(user);

			// By this point the user has been validated
			//
			doUpdatePassword(user);

			// We could possibly put this in a FriendRefUserManager but it might be overkill for
			// just two lines of code in here. If we do need to decouple this at some point down
			// the line we can just extract this portion of the method
			//
			ReturnType<FriendRef> refResult = getFriendRefManager().processReferral(user);
			retVal.addChain(getFriendRefManager(), "processReferral", refResult);

			return retVal;
		}
		catch (Errors e)
		{
			throw e;
		}
		/*
		catch (NoSuchAlgorithmException ex)
		{
			Errors errors = new Errors("userManager.create", new ErrorMessage("msg.userManager.create.emailError", "Could not send e-mail to user $user: $ex.message", new Object[] { "user", user , "ex", ex}, ex));
			throw errors;
		}
		*/
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<User> update(User user)
	{
		// We're overriding this to make sure users can't be moved
		// from one client over to another
		//
		User dbUser = new User();
		UserRecord record = getUserRecord(user, true);
		if ( record != null )
		{
			dbUser.setOwnedDelegate(record);
		}

		// These are columns that cannot be overwritten by anyone
		//
		user.setClient(dbUser.getClient());
		user.setSer(dbUser.getSer());

		// We no longer need the user to update his/her profile
		// so we set this flag to false
		//
		user.setForceUpdate(false);
		return super.update(user);
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<User>> importCSV(InputStream stream, Supplier<User> instantiator, Function<User,Boolean> preprocessor, Consumer<ReturnType<User>> postprocessor)
	{
		return importCSVInternal(stream, instantiator, preprocessor, postprocessor);
	}


	@Transactional(readOnly = false)
	@Override
	public boolean updatePassword(User user)
	{
		// Since this just like any create/update method, we allow the model object to do any
		// updates it needs to
		//
		user.setOverrides();

		Errors errors = user.validate();
		if ( errors.hasErrors() )
		{
			throw errors;
		}
		else
		{
			user.actualizeRelations();
			return doUpdatePassword(user);
		}
	}

	private boolean doUpdatePassword(User validUser)
	{
		// Lock the record to prevent other updates
		//
		UserRecord dbUserRecord = getUserRecord(validUser, true);

		if ( dbUserRecord != null )
		{
			getDao().updatePassword(dbUserRecord, validUser.getPassword());

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public UserRecordDao getDao()
	{
		return (UserRecordDao) super.getDao();
	}

	@Override
	public User emptyInstance()
	{
		return new User();
	}

	@Override
	public Set<User> getAllUsers(SingleClient client, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAllUsers(new ClientPagination(client, pagination)));
	}

	@Override
	public Set<User> getAllMembers(SingleClient client, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAllMembers(client, pagination));
	}

	/**
	 * Attempt to look up a user by username, then e-mail address
	 * 
	 * @param user A user object which contains the user's username, and e-mail address
	 * @param forUpdate Whether to lock the record or not
	 * 
	 * @return The (potentially locked) database record of the user or null if not found
	 */
	private UserRecord getUserRecord(User user, boolean forUpdate)
	{
		UserRecord record = null;

		if ( user.exists() )
		{
			record = getDao().getBySer(user.getSer(), forUpdate);
		}
		else
		{
			// First attempt to get the user by username
			//
			if ( !Utils.isEmpty(user.getUsername()))
			{
				record = getDao().getUserByUsername(user.getUsername().toLowerCase());
	
				// They may have also put in their e-mail address insetad of username
				//
				if ( record == null )
				{
					record = getDao().getUserByEmail(user.getUsername().toLowerCase());
				}
			}
	
			// If the user is not found by username, attempt to get by e-mail address
			//
			if ( record == null && !Utils.isEmpty(user.getEmail()))
			{
				record = getDao().getUserByEmail(user.getEmail().toLowerCase());
			}

			// If the user is not found by username nor e-mail, attempt to get by cell phone
			// 
			if ( record == null && !Utils.isEmpty(user.getCellPhone()))
			{
				record = getDao().getUserByCellphone(user.getCellPhone());
			}
	
			if ( forUpdate && record != null )
			{
				record = getDao().getBySer(record.getSer(), true);
			}
		}

		return record;
	}

	private String generateRandomCode(int length)
	{
		StringBuilder retVal = new StringBuilder(length);

		Random random = new Random();
		for ( int i = 0; i < length; i++ )
		{
			// 0 - 9, then 26 capital letters [0, 36)
			//
			char c;
			int n = random.nextInt(36);
			if ( n < 10 )
			{
				c = '0';
				c += n;
			}
			else
			{
				c = 'A';
				c += (n - 10);
			}

			if ( c != '0' && c != 'O' && c != '1' && c != 'I' )
			{
				retVal.append(c);
			}
			else
			{
				// Skip 0s and Os, and Is and 1s because they look too similar
				//
				i--;
			}
		}

		return retVal.toString();
	}

	@Transactional(readOnly = false)
	@Override
	public String generateVerificationCode(User user)
	{
		User dbUser = lookupUser(user);

		if ( dbUser.exists() )
		{
			if ( dbUser.isVerified() )
			{
				throw new Errors(getInterfaceName() + ".generateVerificationCode", new ErrorMessage("msg." + getInterfaceName() + ".generateVerificationCode.alreadyVerified", "This account has already been verified: $user", new Object[] { "user", user }));
			}
			else
			{
				// So we don't multiple generate codes
				//
				lock(user);
	
				SingleClient client = dbUser.getClient();
				int codeLength = Utils.defaultIfNaN(client.getOptionValue(VERIFICATION_CODE_LENGTH), 4);
				int cooldownSeconds = Utils.defaultIfNaN(client.getOptionValue(VERIFICATION_CODE_COOLDOWN_SECONDS), 60 * 60);
	
				// First check to make sure we're not on cooldown
				//
				UserDatum userDatum = getUserDatumManager().getUserDatum(user, VERIFICATION_CODE);
				if ( userDatum.exists() && (Utils.currentTimeSeconds() + cooldownSeconds) < userDatum.getTimeStampSeconds() )
				{
					// Haven't reached cooldown yet
					//
					throw new Errors(getInterfaceName() + ".generateVerificationCode", new ErrorMessage("msg." + getInterfaceName() + ".generateVerificationCode.onCooldown", "A code has already been generated for this account: $user", new Object[] { "user", user }));
				}
				else
				{
					// Generate a random code of length "codeLength" and associate it with this user account
					// overwriting any previous such code
					//
					userDatum.setValue(generateRandomCode(codeLength));
					userDatum.setTimeStampSeconds(Utils.currentTimeSeconds());
	
					if ( userDatum.exists() )
					{
						getUserDatumManager().update(userDatum);
					}
					else
					{
						userDatum.setUser(user);
						userDatum.setKey(VERIFICATION_CODE);
	
						getUserDatumManager().create(userDatum);
					}
				}
	
				return userDatum.getValue();
			}
		}
		else
		{
			throw new Errors(getInterfaceName() + ".generateVerificationCode", new ErrorMessage("msg." + getInterfaceName() + ".generateVerificationCode.dne", "The user being does not exist: $user", new Object[] { "user", user }));
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Boolean> verify(User user, String code)
	{
		User dbUser = lookupUser(user);

		if ( dbUser.exists() )
		{
			lock(user);

			UserDatum userDatum = getUserDatumManager().getUserDatum(user, VERIFICATION_CODE);

			if ( userDatum.exists() )
			{
				// If the code given matches, the user is considered verified
				//
				if ( userDatum.getValue().equalsIgnoreCase(code))
				{
					user.setStatus(User.STATUS_VERIFIED);
					super.update(user);

					return new ReturnType<>(true);
				}
				else
				{
					return new ReturnType<>(false);
				}
			}
			else
			{
				return new ReturnType<>(false);
			}
		}
		else
		{
			return new ReturnType<>(false);
		}
	}

	public UserDatumManager getUserDatumManager()
	{
		return userDatumManager;
	}

	@Autowired
	public void setUserDatumManager(UserDatumManager userDatumManager)
	{
		this.userDatumManager = userDatumManager;
	}

	public FriendRefManager getFriendRefManager()
	{
		return friendRefManager;
	}

	@Autowired
	public void setFriendRefManager(FriendRefManager friendRefManager)
	{
		this.friendRefManager = friendRefManager;
	}

	public UserLoginManager getUserLoginManager()
	{
		return userLoginManager;
	}

	@Autowired
	public void setUserLoginManager(UserLoginManager userLoginManager)
	{
		this.userLoginManager = userLoginManager;
	}

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}
}