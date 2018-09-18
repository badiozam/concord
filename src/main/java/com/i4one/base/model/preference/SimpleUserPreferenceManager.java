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
package com.i4one.base.model.preference;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.user.UserRecord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleUserPreferenceManager extends BaseActivityManager<UserPreferenceRecord, UserPreference, Preference> implements UserPreferenceManager
{
	private UserManager userManager;
	private PreferenceManager preferenceManager;

	@Override
	public UserPreference emptyInstance()
	{
		return new UserPreference();
	}

	@Override
	public boolean hasUserPreferences(Preference preference)
	{
		return getDao().hasActivity(preference.getSer());
	}

	@Override
	public UserPreference getUserPreference(Preference preference, User user)
	{
		UserPreference retVal = new UserPreference();

		UserPreferenceRecord delegate = getDao().get(preference.getSer(), user.getSer());
		if ( delegate != null )
		{
			retVal.setOwnedDelegate(delegate);
		}

		return retVal;
	}

	@Override
	public Set<UserPreference> getAllUserPreferences(Preference preference, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByItem(preference.getSer(), pagination));
	}

	@Override
	public Set<UserPreference> getAllUserPreferences(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByUser(user.getSer(), pagination));
	}

	@Override
	public Set<UserPreference> getAllUserPreferences(Preference preference, User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAll(preference.getSer(), user.getSer(), pagination));
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<List<ReturnType<UserPreference>>> setUserPreferences(User user, SingleClient client, Map<Preference, List<UserPreference>> userPreferences)
	{
		// Gather errors as we go through the survey
		//
		Errors errors = new Errors();

		// Lock the user record to prevent double writes
		//
		UserRecord dbUserRecord = getUserManager().lock(user);

		// First make sure all of the questions we need are in the incoming responses
		//
		Set<Preference> preferences = getPreferenceManager().getAllPreferences(client, SimplePaginationFilter.ORDERWEIGHT);
		int questionNo = 0;
		for (Preference preference : preferences)
		{
			questionNo++;

			String fieldName = "preferences[" + (questionNo - 1) + "]";
			getLogger().debug("Found response for {} w/ field name {}: {}", preference, fieldName, userPreferences.get(preference));

			// We check the multi-answer questions only if the min/max are set
			//
			List<UserPreference> currResponses = Utils.defaultIfNull(userPreferences.get(preference), Collections.EMPTY_LIST);
			if ( preference.isMultiAnswer() && preference.getMinResponses() > 0 && preference.getMaxResponses() > 0 )
			{
				if ( currResponses.size() < preference.getMinResponses() )
				{
					errors.addError(fieldName, new ErrorMessage(fieldName, "msg." + getInterfaceName() + ".setUserPreferences.minresponses", "Please select at least $question.minResponses responses to question #$questionNo", new Object[] { "questionNo", questionNo,"preference", preference, "user", user, "userpreferences", userPreferences }, null));
				}
				else if ( currResponses.size() > preference.getMaxResponses() )
				{
					errors.addError(fieldName, new ErrorMessage(fieldName, "msg." + getInterfaceName() + ".setUserPreferences.maxresponses", "Please select no more than $question.maxResponses responses to question #$questionNo", new Object[] { "questionNo", questionNo,"preference", preference, "user", user, "userpreferences", userPreferences }, null));
				}
			}
			else if ( preference.isOpenAnswer() )
			{
				// Check to make sure the open answer is qualified
				//
				String openAnswer = currResponses.isEmpty() ? "" : Utils.forceEmptyStr(currResponses.get(0).getOpenAnswer());
				if ( !openAnswer.matches(preference.getValidAnswer()) )
				{
					getLogger().debug("Open answer '" + openAnswer + "' does not match " + preference.getValidAnswer());

					errors.addError(fieldName, new ErrorMessage(fieldName, "msg." + getInterfaceName() + ".setUserPreferences.invalidanswer", "Please enter a valid response to question #$questionNo", new Object[] { "questionNo", questionNo,"preference", preference, "user", user, "userpreferences", userPreferences }, null));
				}
			}
			else if ( preference.isSingleAnswer() )
			{
				// Check to make sure the answer selected from the drop down is a valid one (and not the place-holder)
				//
				UserPreference userPreference = currResponses.isEmpty() ?  new UserPreference() : currResponses.get(0);

				if ( !userPreference.getPreference().exists() )
				{
					errors.addError(fieldName, new ErrorMessage(fieldName, "msg." + getInterfaceName() + ".setUserPreferences.noresponse", "You have not responded to question #$questionNo", new Object[] { "questionNo", questionNo,"preference", preference, "user", user, "userpreferences", userPreferences }, null));
				}
			}
			else // if ( question.getQuestionType() == Question.TYPE_SINGLEANSER_RADIO )
			{
				// Nothing to do with radio button selections
			}
		}

		// Only continue with the recording if all of the questions were properly answered
		//
		if ( errors.hasErrors() )
		{
			throw errors;
		}
		else
		{
			List<ReturnType<UserPreference>> responseCreateVals = recordUserPreferences(user, userPreferences);

			ReturnType<List<ReturnType<UserPreference>>> retVal = new ReturnType<>();

			retVal.setPre(new ArrayList<>());
			retVal.setPost(responseCreateVals);

			return retVal;
		}
	}

	protected List<ReturnType<UserPreference>> recordUserPreferences(User user, Map<Preference, List<UserPreference>> userPreferences)
	{
		ArrayList<ReturnType<UserPreference>> responseCreateVals = new ArrayList<>();

		for (Map.Entry<Preference, List<UserPreference>> userPreference : userPreferences.entrySet() )
		{
			Preference preference = userPreference.getKey();
			List<UserPreference> values = userPreference.getValue();

			if ( preference.isOpenAnswer() )
			{
				UserPreference currPreference = values.iterator().next();

				// Update the old answer if it exists
				//
				UserPreference oldPreference = getUserPreference(preference, user);
				if ( oldPreference.exists() )
				{
					oldPreference.setOpenAnswer(currPreference.getOpenAnswer());

					ReturnType<UserPreference> updateVal = update(oldPreference);
					responseCreateVals.add(updateVal);
				}
				else
				{
					// Override any inconsistencies
					//
					currPreference.setPreference(preference);
					currPreference.setUser(user);

					ReturnType<UserPreference> updateVal = create(currPreference);
					responseCreateVals.add(updateVal);
				}
			}
			else if ( preference.isMultiAnswer() )
			{
				// We remove all of the previous settings first and only set the ones that
				// were given to us.
				//
				Map<Integer, UserPreference> oldPreferences = getAllUserPreferences(preference, user, SimplePaginationFilter.NONE)
					.stream()
					.collect(
						Collectors.toMap(
							(oldPref) -> { return oldPref.getPreferenceAnswer(false).getSer(); },
							(oldPref) -> { return oldPref;})
					);

				for ( UserPreference currUserPreference : values )
				{
					// Override any inconsistencies
					//
					currUserPreference.setPreference(userPreference.getKey());
					currUserPreference.setOpenAnswer(null);
					currUserPreference.setUser(user);

					Integer answerId = currUserPreference.getPreferenceAnswer(false).getSer();
					if ( oldPreferences.containsKey(answerId) )
					{
						// No need to create another one
						//
						getLogger().debug("Skipping creation of {} due to previous existence", currUserPreference);
						oldPreferences.remove(answerId);
					}
					else
					{
						ReturnType<UserPreference> createVal = create(currUserPreference);
						responseCreateVals.add(createVal);
					}
				}

				// At this point any new preference have been created, and any existing
				// preferences have been removed from the oldPreferences set. The remainder
				// needs to be removed
				//
				for ( UserPreference removeUserPreference : oldPreferences.values() )
				{
					UserPreference oldUserPreference = remove(removeUserPreference);
					getLogger().debug("Removed {}", oldUserPreference);

					ReturnType<UserPreference> removeVal = new ReturnType<>();
					removeVal.setPre(oldUserPreference);
					removeVal.setPost(new UserPreference());

					responseCreateVals.add(removeVal);
				}
			}
			else if ( preference.isSingleAnswer() )
			{
				UserPreference currPreference = values.iterator().next();

				UserPreference oldPreference = getUserPreference(preference, user);
				if ( oldPreference.exists() )
				{
					oldPreference.setPreferenceAnswer(currPreference.getPreferenceAnswer(false));
					oldPreference.setOpenAnswer(null);

					ReturnType<UserPreference> updateVal = update(oldPreference);
					responseCreateVals.add(updateVal);
				}
				else
				{
					// Override any inconsistencies
					//
					currPreference.setPreference(preference);
					currPreference.setOpenAnswer(null);
					currPreference.setUser(user);

					// Consider updating this to addChain(..) sequences. The only problem right now
					// is that addChain(..) overwrites the current value instead of keeping a list
					// of method calls
					//
					ReturnType<UserPreference> createVal = create(currPreference);
					responseCreateVals.add(createVal);
				}
			}
			else
			{
				getLogger().info("Unknown preference type {} when recording user preferences.", preference);
			}
		}

		return responseCreateVals;
	}
	
	@Override
	public UserPreferenceRecordDao getDao()
	{
		return (UserPreferenceRecordDao) super.getDao();
	}

	public PreferenceManager getPreferenceManager()
	{
		return preferenceManager;
	}

	@Autowired
	public void setPreferenceManager(PreferenceManager preferenceManager)
	{
		this.preferenceManager = preferenceManager;
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
