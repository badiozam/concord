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
package com.i4one.research.model.survey.question;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.answer.Answer;
import com.i4one.research.model.survey.answer.AnswerManager;
import com.i4one.research.model.survey.SurveyManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleQuestionManager extends BasePaginableManager<QuestionRecord, Question> implements QuestionManager
{
	private SurveyManager surveyManager;
	private AnswerManager answerManager;

	@Override
	public Question emptyInstance()
	{
		return new Question();
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Question> clone(Question item)
	{
		try
		{
			if ( item.exists() )
			{
				String currTimeStamp = String.valueOf(Utils.currentDateTime());
				IString workingTitle = new IString(item.getQuestion()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				Question question = new Question();
				question.copyFrom(item);
	
				question.setSer(0);
				question.setQuestion(workingTitle);

				// Create will take care of creating the answers as well
				//
				ReturnType<Question> retVal = create(question);

				List<ReturnType<Answer>> createdAnswers = new ArrayList<>();
				for ( Answer currAnswer : getAnswerManager().getAnswers(item, SimplePaginationFilter.NONE) )
				{
					Answer answerCopy = new Answer();
					answerCopy.copyFrom(currAnswer);
					answerCopy.setSer(0);
					answerCopy.setQuestion(question);

					ReturnType<Answer> answerCreate = getAnswerManager().create(answerCopy);
					question.getAnswers().add(answerCreate.getPost());

					createdAnswers.add(answerCreate);
				}
	
				retVal.addChain(getAnswerManager(), "create", new ReturnType<>(createdAnswers));
				return retVal;
			}
			else
			{
				throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.dne", "You are attempting to clone a non-existent item: $item", new Object[] { "item", item }));
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
		{
			Errors errors = new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".clone.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));

			throw errors;
		}
	}

	@Override
	protected ReturnType<Question> createInternal(Question question)
	{
		Survey currSurvey = question.getSurvey();
		getSurveyManager().lock(currSurvey);

		ReturnType<Question> retVal = super.createInternal(question);

		question.getAnswers().stream().forEach( (currAnswer) ->
		{
			currAnswer.setSer(0);
			currAnswer.setQuestion(retVal.getPost());

			ReturnType<Answer> answerCreate = getAnswerManager().create(currAnswer);
			retVal.addChain(getAnswerManager(), "create", answerCreate);
		});

		/*
		 * This is poor design, the question count should be calculated
		 * when the survey is loaded
		 *
		*/
		// Update the survey's question count, if we're creating a question
		// we can assume that it didn't exist before and therefore the
		// count must necessarily increase by 1
		//
		currSurvey.setQuestionCount(currSurvey.getQuestionCount() + 1);
		//retVal.addChain(surveyManager, "update", getSurveyManager().update(currSurvey));

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public Question remove(Question question)
	{
		Survey currSurvey = question.getSurvey();
		getSurveyManager().lock(currSurvey);

		Question retVal = super.remove(question);
		currSurvey.setQuestionCount(currSurvey.getQuestionCount() - 1);

		return retVal;
	}

	@Override
	public Set<Question> getQuestions(Survey survey, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getQuestions(survey.getSer(), pagination));
	}

	@Override
	public QuestionRecordDao getDao()
	{
		return (QuestionRecordDao) super.getDao();
	}

	public AnswerManager getAnswerManager()
	{
		return answerManager;
	}

	@Autowired
	public void setAnswerManager(AnswerManager answerManager)
	{
		this.answerManager = answerManager;
	}

	public SurveyManager getSurveyManager()
	{
		return surveyManager;
	}

	@Autowired
	public void setSurveyManager(SurveyManager surveyManager)
	{
		this.surveyManager = surveyManager;
	}

	@Override
	protected Question initModelObject(Question item)
	{
		item.setAnswers(getAnswerManager().getAnswers(item, SimplePaginationFilter.NONE));

		return item;
	}
}
