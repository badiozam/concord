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
package com.i4one.research.model.survey.reports;

import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.report.classifier.BaseSubclassification;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.research.model.survey.answer.Answer;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.respondent.Respondent;
import com.i4one.research.model.survey.response.ResponseManager;

/**
 * @author Hamid Badiozamani
 */
public class SurveyResponseSubclassification extends BaseSubclassification<Respondent>
{
	private final Answer surveyAnswer;
	private final transient ResponseManager responseManager;

	public SurveyResponseSubclassification(Classification<Respondent, ? extends Subclassification<Respondent>> parent, Answer surveyAnswer, ResponseManager responseManager)
	{
		super(parent, String.valueOf(surveyAnswer.getSer()));

		this.surveyAnswer = surveyAnswer;
		this.responseManager = responseManager;
	}

	@Override
	public IString getTitle()
	{
		return surveyAnswer.getAnswer();
	}

	@Override
	public boolean isExclusiveTo(Subclassification<Respondent> sub)
	{
		if ( !(sub instanceof SurveyResponseSubclassification))
		{
			// We only keep track of SurveyResponseSubclassifications
			// and not something like GenderSubclassification<Response>
			//
			return false;
		}
		else
		{
			if ( getParent().equals(sub.getParent()))
			{
				// Nothing is exclusive if the question can have multiple answers
				//
				return !(surveyAnswer.getQuestion().getQuestionType() == Question.TYPE_MULTIANSWER_CHECKBOX);
			}
			else
			{
				// We're not the same question and thus not exclusive
				//
				return false;
			}
		}
	}

	@Override
	public boolean belongs(Respondent item)
	{
		return responseManager.hasResponded(surveyAnswer, item);
	}
}
