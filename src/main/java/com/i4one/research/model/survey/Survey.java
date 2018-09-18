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
package com.i4one.research.model.survey;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.manager.instantwinnable.SimpleTerminableInstantWinnable;
import com.i4one.base.model.manager.triggerable.SimpleTerminableTriggerable;
import com.i4one.base.model.manager.instantwinnable.TerminableInstantWinnableClientType;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import com.i4one.base.model.manager.categorizable.BaseCategorizableTerminableSiteGroupType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.research.model.survey.category.SurveyCategory;
import com.i4one.research.model.survey.question.Question;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class Survey extends BaseCategorizableTerminableSiteGroupType<SurveyRecord, SurveyCategory> implements TerminableTriggerableClientType<SurveyRecord, Survey>, TerminableInstantWinnableClientType<SurveyRecord, Survey>
{
	static final long serialVersionUID = 42L;

	private transient Set<Question> questions;
	private final transient SimpleTerminableTriggerable<SurveyRecord, Survey> triggerable;
	private final transient SimpleTerminableInstantWinnable<SurveyRecord, Survey> instantWinning;

	public Survey()
	{
		super(new SurveyRecord());

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinning = new SimpleTerminableInstantWinnable<>(this);
	}

	protected Survey(SurveyRecord delegate)
	{
		super(delegate);

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinning = new SimpleTerminableInstantWinnable<>(this);
	}

	@Override
	public void init()
	{
		super.init();

		if ( questions == null )
		{
			questions = new LinkedHashSet<>();
		}
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal.merge(triggerable.validate());
		retVal.merge(instantWinning.validate());

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		triggerable.setOverrides();
	}

	public IString getIntro()
	{
		return getDelegate().getIntro();
	}

	public void setIntro(IString intro)
	{
		getDelegate().setIntro(intro);
	}

	public IString getOutro()
	{
		return getDelegate().getOutro();
	}

	public void setOutro(IString intro)
	{
		getDelegate().setOutro(intro);
	}

	public boolean getRandomize()
	{
		return getDelegate().getRandomize();
	}

	public void setRandomize(boolean randomize)
	{
		getDelegate().setRandomize(randomize);
	}

	public int getPerPage()
	{
		return getDelegate().getPerpage();
	}

	public void setPerPage(int perPage)
	{
		getDelegate().setPerpage(perPage);
	}

	public int getQuestionCount()
	{
		return getDelegate().getQuestioncount();
	}

	public void setQuestionCount(int questionCount)
	{
		getDelegate().setQuestioncount(questionCount);
	}

	public int getTotalPages()
	{
		if ( getPerPage() == 0 )
		{
			// All on one page
			//
			return 1;
		}
		else
		{
			int totalPages = getQuestionCount() / getPerPage();
			if ( getQuestionCount() % getPerPage() > 0 )
			{
				totalPages++;
			}

			return totalPages;
		}
	}

	public Set<Question> getQuestions()
	{
		return questions;
	}

	public void setQuestions(Collection<Question> questions)
	{
		this.questions.clear();
		this.questions.addAll(questions);

		setQuestionCount(questions.size());
	}

	@Override
	protected void copyFromInternal(RecordTypeDelegator<SurveyRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyFromInternal(right);
		if ( right instanceof Survey )
		{
			Survey rightSurvey = (Survey)right;

			questions.clear();
			for ( Question question : rightSurvey.getQuestions() )
			{
				Question newQuestion = new Question();
				newQuestion.copyFrom(question);
				newQuestion.setSer(0);

				questions.add(newQuestion);
			}
		}
	}

	@Override
	protected void copyOverInternal(RecordTypeDelegator<SurveyRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyOverInternal(right);
		if ( right instanceof Survey )
		{
			Survey rightSurvey = (Survey)right;

			if ( !rightSurvey.getQuestions().isEmpty() )
			{
				copyFromInternal(right);
			}
		}
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		// [Start time, end time, title,] intro, outro, perpage
		if  ( super.fromCSVInternal(csv) )
		{
			setIntro(new IString(csv.get(0))); csv.remove(0);
			setOutro(new IString(csv.get(0))); csv.remove(0);
			setPerPage(Integer.valueOf(csv.get(0))); csv.remove(0);

			boolean retVal = true;

			retVal &= triggerable.fromCSVList(csv);

			return retVal;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected StringBuilder toCSVInternal(boolean header)
	{
		StringBuilder retVal = super.toCSVInternal(header);

		if ( header )
		{
			// XXX: Needs to be i18n
			retVal.append(Utils.csvEscape("Intro")).append(",");
			retVal.append(Utils.csvEscape("Outro")).append(",");
			retVal.append(Utils.csvEscape("Per Page")).append(",");
		}
		else
		{
			retVal.append("\"").append(Utils.csvEscape(getIntro().toString())).append("\",");
			retVal.append("\"").append(Utils.csvEscape(getOutro().toString())).append("\",");
			retVal.append("\"").append(getPerPage()).append("\",");
		}

		retVal.append(triggerable.toCSV(header));

		return retVal;
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
	}

	@Override
	protected boolean equalsInternal(RecordTypeDelegator<SurveyRecord> right)
	{
		if ( right instanceof Survey )
		{
			Survey rightSurvey = (Survey)right;
			return getStartTimeSeconds() == rightSurvey.getStartTimeSeconds() &&
				getEndTimeSeconds() == rightSurvey.getEndTimeSeconds() &&
				getTitle().equals(rightSurvey.getTitle());
		}
		else
		{
			return false;
		}
	}

	@Override
	protected SurveyCategory initCategory(int categoryid)
	{
		SurveyCategory retVal = new SurveyCategory();
		retVal.setSer(categoryid);
		retVal.loadedVersion();

		return retVal;
	}

	@Override
	public Set<BalanceTrigger> getBalanceTriggers()
	{
		return triggerable.getBalanceTriggers();
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> balanceTriggers)
	{
		triggerable.setBalanceTriggers(balanceTriggers);
	}

	@Override
	public Set<ExclusiveBalanceTrigger<Survey>> getExclusiveBalanceTriggers()
	{
		return triggerable.getExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getNonExclusiveBalanceTriggers()
	{
		return triggerable.getNonExclusiveBalanceTriggers();
	}

	@Override
	public Set<InstantWin> getInstantWins()
	{
		return instantWinning.getInstantWins();
	}

	@Override
	public void setInstantWins(Collection<InstantWin> instantWins)
	{
		instantWinning.setInstantWins(instantWins);
	}

	@Override
	public Set<ExclusiveInstantWin<Survey>> getExclusiveInstantWins()
	{
		return instantWinning.getExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getNonExclusiveInstantWins()
	{
		return instantWinning.getNonExclusiveInstantWins();
	}
}
