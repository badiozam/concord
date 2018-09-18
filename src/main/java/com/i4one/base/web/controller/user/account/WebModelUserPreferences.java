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

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.preference.Preference;
import com.i4one.base.model.preference.PreferenceAnswer;
import com.i4one.base.model.preference.PreferenceAnswerManager;
import com.i4one.base.model.preference.PreferenceManager;
import com.i4one.base.model.preference.UserPreference;
import com.i4one.base.model.preference.UserPreferenceManager;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A web model object that accepts the custom user preferences and provides a map
 * the manager expects.
 * 
 * Note that this is not a UserType as the user object's e-mail preference is also
 * modified and so the user object must be preserved.
 * 
 * @author Hamid Badiozamani
 */
public class WebModelUserPreferences extends BaseLoggable implements WebModel
{
	private Model model;
	private WebModelUserPreferenceUser user;

	private transient PreferenceManager preferenceManager;
	private transient UserPreferenceManager userPreferenceManager;
	private transient PreferenceAnswerManager preferenceAnswerManager;

	private transient Map<Integer, List<Integer>> selectionUserPreferences;
	private transient Map<Integer, String> openAnswerPreferences;

	private transient Set<Preference> preferences;

	public WebModelUserPreferences()
	{
		super();

		selectionUserPreferences = null;
		openAnswerPreferences = null;
	}

	public Map<Integer, List<Integer>> getSelectionUserPreferences()
	{
		if ( selectionUserPreferences == null )
		{
			selectionUserPreferences = new HashMap<>();

			// We initialize the response map by retrieving all of the respondent's information from the database
			// and populating the serial numbers. Typically a PropertyEditor or Converter would be used to do this
			// step automatically but for some reason it's not working with complex types so we do it here
			// programmatically
			//
			getPreferences().stream().forEach( (currPreference) ->
			{
				getLogger().debug("Getting all responses for question: " + currPreference);
				Set<UserPreference> userPreferences = getUserPreferenceManager().getAllUserPreferences(currPreference, getUser(), SimplePaginationFilter.NONE);

				// Extract all of the answer serial numbers
				//
				List<Integer> answers = new ArrayList<>();
				userPreferences.stream().forEach((currUserPreference) ->
				{
					answers.add(currUserPreference.getPreferenceAnswer(false).getSer());
				});

				// Add them to the list for display
				//
				selectionUserPreferences.put(currPreference.getSer(), answers);
			});

			getLogger().debug("Setting selection responses from: " + preferences + "\n to " + selectionUserPreferences );
		}

		return selectionUserPreferences;
	}

	public void setSelectionUserPreferences(Map<Integer, List<Integer>> selectionUserPreferences)
	{
		this.selectionUserPreferences = selectionUserPreferences;
	}

	public Map<Integer, String> getOpenAnswerPreferences()
	{
		if ( openAnswerPreferences == null )
		{
			openAnswerPreferences = new HashMap<>();

			// We initialize the response map by retrieving all of the respondent's information from the database
			// and populating the serial numbers. Typically a PropertyEditor or Converter would be used to do this
			// step automatically but for some reason it's not working with complex types so we do it here
			// programmatically
			//
			//for ( Preference currPreference : getSurvey().getPreferences() )
			getPreferences().forEach((currPreference) ->
			{
				getLogger().debug("Getting user preference {} for user {}", currPreference, getUser());

				Set<UserPreference> userPreferences = getUserPreferenceManager().getAllUserPreferences(currPreference, getUser(), SimplePaginationFilter.NONE);
				if (!userPreferences.isEmpty())
				{
					openAnswerPreferences.put(currPreference.getSer(), userPreferences.iterator().next().getOpenAnswer());
				}
				else
				{
					openAnswerPreferences.put(currPreference.getSer(), "");
				}
			});
		}

		return openAnswerPreferences;
	}

	public void setOpenAnswerPreferences(Map<Integer, String> openAnswerUserPreferences)
	{
		this.openAnswerPreferences = openAnswerUserPreferences;
	}

	/**
	 * Flatten the map for use in recording the userPreferences. This differs from the selection userPreferences
 since the selection userPreferences display all of the possible response whereas this field contains
 only the userPreferences that the user selected.
	 * 
	 * @return The flattened list of userPreferences containing all question <=> userPreferences mappings
	 */
	public Map<Preference, List<UserPreference>> getUserPreferences()
	{
		// This method may be called multiple times and its results should be cached
		//
		getLogger().debug("getUserPreferences() called");

		// Note that this is done because the PropertyEditor classes are not being called
		// for some reason so we have to do the conversion manually
		//
		Map<Preference, List<UserPreference>> retVal = new LinkedHashMap<>();

		getLogger().debug("Selection preferences: " + getSelectionUserPreferences().keySet());

		getSelectionUserPreferences().keySet()
			.stream()
			.filter((selectionPreferenceId) -> ( selectionPreferenceId != null ))
			.forEach((selectionPreferenceId) ->
		{
			// Note that it's important for the question to be loaded entirely
			// since the map makes use of the hashCode() method which relies
			// on the unique key generation of the question. That in turn relies
			// on the question field as well as the survey field.
			//
			Preference currPreference = new Preference();
			currPreference.resetDelegateBySer(selectionPreferenceId);
			currPreference.loadedVersion();
			
			if ( currPreference.isMultiAnswer() || currPreference.isSingleAnswer() )
			{
				List<Integer> responseList = getSelectionUserPreferences().get(selectionPreferenceId);
				List<UserPreference> currUserPreferences = new ArrayList<>();
			
				// Add all of the userPreferences for this question
				//
				if ( responseList != null )
				{
					responseList.stream().forEach( (answerId) ->
					{
						UserPreference currUserPreference = new UserPreference();
						currUserPreference.getPreferenceAnswer(false).resetDelegateBySer(answerId);
			
						currUserPreference.setPreference(currPreference);
						currUserPreference.setPreferenceAnswer(currUserPreference.getPreferenceAnswer());
						currUserPreference.setOpenAnswer(null);

						getLogger().debug("Found answer id {} for {} yielding {}", answerId, currPreference, currUserPreference);
						currUserPreferences.add(currUserPreference);
					});
				}
				else
				{
					getLogger().debug("Response list for {} was null.", currPreference);
				}

				getLogger().debug("Preference {} to have user preferences {}", currPreference, currUserPreferences);
				retVal.put(currPreference, currUserPreferences);
			}
		});

		getOpenAnswerPreferences().keySet()
			.stream()
			.filter((openPreferenceId) -> ( openPreferenceId != null ))
			.forEach((openPreferenceId) ->
		{
			// Note that it's important for the question to be loaded entirely
			// since the map makes use of the hashCode() method which relies
			// on the unique key generation of the question. That in turn relies
			// on the question field as well as the survey field.
			//
			Preference currPreference = new Preference();
			currPreference.resetDelegateBySer(openPreferenceId);
			currPreference.loadedVersion();
			
			if ( currPreference.isOpenAnswer() )
			{
				String response = getOpenAnswerPreferences().get(openPreferenceId);
			
				UserPreference currUserPreference = new UserPreference();
	
				currUserPreference.setOpenAnswer(Utils.trimString(response));
				currUserPreference.setPreference(currPreference);
	
				// There will always only be one open answer but we need to conform
				//
				List<UserPreference> currUserPreferences = new ArrayList<>(1);
				currUserPreferences.add(currUserPreference);
	
				retVal.put(currPreference, currUserPreferences);
			}
		});

		return retVal;
	}

	/**
	 * Get a mapping of all questions and a map of their possible answers for use
	 * by the form
	 * 
	 * @return The mapping of questionid => (answerid => answer) for the survey
	 */
	public Map<Integer, Map<Integer, String>> getAnswerListMap()
	{
		HashMap<Integer, Map<Integer, String>> retVal = new HashMap<>();

		getPreferences().stream().forEach((currPreference) ->
		{
			Set<PreferenceAnswer> answers = getPreferenceAnswerManager().getAnswers(currPreference);
			Map<Integer, String> answerMap = new LinkedHashMap<>(answers.size());

			// Need a "not-selected" answer
			//
			if ( currPreference.getQuestionType() == Preference.TYPE_SINGLEANSWER_SELECT )
			{
				answerMap.put(0, "--");
			}

			answers.stream().forEach((answer) ->
			{
				answerMap.put(answer.getSer(), answer.getAnswer().get(model.getLanguage()));
			});

			retVal.put(currPreference.getSer(), answerMap);
		});

		return retVal;
	}

	@Override
	public Model getModel()
	{
		return model;
	}

	@Override
	public void setModel(Model model)
	{
		this.model = model;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(WebModelUserPreferenceUser user)
	{
		this.user = user;
	}

	public Set<Preference> getPreferences()
	{
		if ( preferences == null )
		{
			preferences = Collections.unmodifiableSet(getPreferenceManager().getAllPreferences(model.getSingleClient(), SimplePaginationFilter.ORDERWEIGHT));
		}

		return preferences;
	}

	public PreferenceManager getPreferenceManager()
	{
		return preferenceManager;
	}

	public void setPreferenceManager(PreferenceManager preferenceManager)
	{
		this.preferenceManager = preferenceManager;
	}

	public UserPreferenceManager getUserPreferenceManager()
	{
		return userPreferenceManager;
	}

	public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager)
	{
		this.userPreferenceManager = userPreferenceManager;
	}

	public PreferenceAnswerManager getPreferenceAnswerManager()
	{
		return preferenceAnswerManager;
	}

	public void setPreferenceAnswerManager(PreferenceAnswerManager preferenceAnswerManager)
	{
		this.preferenceAnswerManager = preferenceAnswerManager;
	}

}
