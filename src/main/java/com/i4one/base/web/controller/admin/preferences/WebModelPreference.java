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
package com.i4one.base.web.controller.admin.preferences;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.preference.Preference;
import com.i4one.base.model.preference.PreferenceAnswer;
import com.i4one.base.model.preference.PreferenceRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.AutoPopulatingList;
import org.springframework.util.AutoPopulatingList.ElementFactory;

/**
 * @author Hamid Badiozamani
 */
public class WebModelPreference extends Preference implements ElementFactory<PreferenceAnswer>
{
	public static final int DEFAULT_PREFERENCE_ANSWER_SIZE = 4;

	private transient List<PreferenceAnswer> answers;

	public WebModelPreference()
	{
		super();

		answers = new AutoPopulatingList<>(this);
	}

	protected WebModelPreference(PreferenceRecord delegate)
	{
		super(delegate);

		answers = new AutoPopulatingList<>(this);
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		// Since we're managing the answers as well we need to ensure that
		// their overrides are also set
		//
		List<PreferenceAnswer> filteredAnswers = getFilteredAnswers();
		filteredAnswers.forEach( (preferenceAnswer) ->
		{
			preferenceAnswer.setPreference(this);

			// No need to set the overrides of the answers here since
			// that's taken care of by the manager when it creates/updates
			// each answer
			//
			// preferenceAnswer.setOverrides();
		});

		setPreferenceAnswers(filteredAnswers);

		// We have to set the overrides here after the balance triggers have been
		// set to our filtered results
		//
		super.setOverrides();
	}

	protected List<PreferenceAnswer> getFilteredAnswers()
	{
		return getAnswers().stream()
			.filter( (answer) -> { return answer != null; } )

			// Blank answers are to be handled by the manager (i.e. either removed or ignored)
			//
			//.filter( (answer) -> { return answer.exists() || !answer.getAnswer().isBlank(); } )
			.collect(Collectors.toList());
	}

	@Override
	public void setPreferenceAnswers(Collection<PreferenceAnswer> newAnswers)
	{
		getLogger().debug("Setting preference answers to " + Utils.toCSV(newAnswers) + " called from ", new Throwable());
		super.setPreferenceAnswers(newAnswers);

		// We have to force a reload of the answers since they may have
		// changed in the database
		//
		answers.clear();
	}

	public List<PreferenceAnswer> getAnswers()
	{
		if ( answers == null || answers.isEmpty() )
		{
			getLogger().debug("getAnswers() initializing answers from " + Utils.toCSV(getPreferenceAnswers()));

			Collection<PreferenceAnswer> preferenceAnswers = getPreferenceAnswers();
			List<PreferenceAnswer> backingList = new ArrayList<>(preferenceAnswers);

			answers = new AutoPopulatingList<>(backingList, this);
		}

		// This creates the elements up to this point if they didn't already exist
		//
		answers.get(DEFAULT_PREFERENCE_ANSWER_SIZE - 1);

		return answers;
	}

	public void setAnswers(List<PreferenceAnswer> answers)
	{
		this.answers = answers;
	}

	@Override
	public PreferenceAnswer createElement(int index) throws AutoPopulatingList.ElementInstantiationException
	{
		return new WebModelPreferenceAnswer();
	}
}