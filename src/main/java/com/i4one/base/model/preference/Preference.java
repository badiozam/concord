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

import com.i4one.base.model.BaseSiteGroupType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.SiteGroupType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class Preference extends BaseSiteGroupType<PreferenceRecord> implements SiteGroupType<PreferenceRecord>
{
	static final long serialVersionUID = 42L;

	public static final int TYPE_SINGLEANSWER_RADIO = 0;
	public static final int TYPE_SINGLEANSWER_SELECT = 1;
	public static final int TYPE_MULTIANSWER_CHECKBOX = 2;
	public static final int TYPE_OPENANSWER_SINGLE = 3;
	public static final int TYPE_OPENANSWER_MULTI = 4;

	private transient Set<PreferenceAnswer> preferenceAnswers;

	public Preference()
	{
		super(new PreferenceRecord());
	}

	protected Preference(PreferenceRecord delegate)
	{
		super(delegate);
	}

	@Override
	public void init()
	{
		super.init();

		if ( preferenceAnswers == null )
		{
			preferenceAnswers = new LinkedHashSet<>();
		}
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getTitle().isBlank() )
		{
			retVal.addError(new ErrorMessage("title", "msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".title.empty", "Title cannot be empty", new Object[]{"item", this}));
		}

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		int answerCount = 0;
		for (PreferenceAnswer answer : getPreferenceAnswers() )
		{
			if ( !answer.getAnswer().isBlank() )
			{
				// The answer count is only incremented for those
				// answers that exist
				//
				answerCount++;
			}
		}

		setAnswerCount(answerCount);
	}

	public boolean isOpenAnswer()
	{
		return getQuestionType() == TYPE_OPENANSWER_SINGLE || getQuestionType() == TYPE_OPENANSWER_MULTI;
	}

	public boolean isSingleAnswer()
	{
		return getQuestionType() == TYPE_SINGLEANSWER_RADIO || getQuestionType() == TYPE_SINGLEANSWER_SELECT;
	}

	public boolean isMultiAnswer()
	{
		return getQuestionType() == TYPE_MULTIANSWER_CHECKBOX;
	}

	public IString getTitle()
	{
		return getDelegate().getTitle();
	}

	public void setTitle(IString title)
	{
		getDelegate().setTitle(title);
	}

	public IString getDefaultValue()
	{
		return getDelegate().getDefaultvalue();
	}

	public void setDefaultValue(IString defVal)
	{
		getDelegate().setDefaultvalue(defVal);
	}

	public int getAnswerCount()
	{
		return getDelegate().getAnswercount();
	}

	public void setAnswerCount(int answerCount)
	{
		getDelegate().setAnswercount(answerCount);
	}

	public int getOrderWeight()
	{
		return getDelegate().getOrderweight();
	}

	public void setOrderWeight(int orderweight)
	{
		getDelegate().setOrderweight(orderweight);
	}

	public int getQuestionType()
	{
		return getDelegate().getQuestiontype();
	}

	public void setQuestionType(int questionType)
	{
		getDelegate().setQuestiontype(questionType);
	}

	public int getMinResponses()
	{
		return getDelegate().getMinresponses();
	}

	public void setMinResponses(int minResponses)
	{
		getDelegate().setMinresponses(minResponses);
	}
	
	public int getMaxResponses()
	{
		return getDelegate().getMaxresponses();
	}

	public void setMaxResponses(int maxResponses)
	{
		getDelegate().setMaxresponses(maxResponses);
	}

	public String getValidAnswer()
	{
		return getDelegate().getValidanswer();
	}

	public void setValidAnswer(String validAnswer)
	{
		getDelegate().setValidanswer(validAnswer);
	}

	public Set<PreferenceAnswer> getPreferenceAnswers()
	{
		return Collections.unmodifiableSet(preferenceAnswers);
	}

	public void setPreferenceAnswers(Collection<PreferenceAnswer> answers)
	{
		this.preferenceAnswers.clear();
		this.preferenceAnswers.addAll(answers);
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@Override
	protected void copyFromInternal(RecordTypeDelegator<PreferenceRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyFromInternal(right);
		if ( right instanceof Preference )
		{
			Preference rightPref = (Preference)right;

			preferenceAnswers.clear();
			for ( PreferenceAnswer answer : rightPref.getPreferenceAnswers() )
			{
				PreferenceAnswer newAnswer = new PreferenceAnswer();
				newAnswer.copyFrom(answer);
				newAnswer.setSer(0);

				preferenceAnswers.add(newAnswer);
			}
		}
	}

	@Override
	protected void copyOverInternal(RecordTypeDelegator<PreferenceRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyOverInternal(right);
		if ( right instanceof Preference )
		{
			Preference rightPreference = (Preference)right;

			// If the right preference's answers aren't empty, then regardless
			// of our answers we'll want to copy the right's preference's answers
			//
			if ( !rightPreference.getPreferenceAnswers().isEmpty() )
			{
				copyFromInternal(right);
			}
		}
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle().toString();
	}
}
