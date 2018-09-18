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
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.report.classifier.display.BaseClassificationDisplay;
import com.i4one.base.model.report.classifier.display.DataPoint;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.respondent.Respondent;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class SurveyResponseClassificationDisplay extends BaseClassificationDisplay<Respondent, SurveyResponseSubclassification>
{
	private final Question question;

	public SurveyResponseClassificationDisplay(SurveyResponseClassification classification, ClassificationReport<?, Respondent> report)
	{
		super(classification, report);

		this.question = classification.getQuestion();
	}

	@Override
	public IString getTitle()
	{
		return question.getQuestion();
	}

	@Override
	public String getDisplayType()
	{
		if ( question.getQuestionType() == Question.TYPE_SINGLEANSWER_RADIO || question.getQuestionType() == Question.TYPE_SINGLEANSWER_SELECT )
		{
			return "pie";
		}
		else if ( question.getQuestionType() == Question.TYPE_MULTIANSWER_CHECKBOX )
		{
			return "bar";
		}
		else
		{
			return "none";
		}
	}

	@Override
	protected DataPoint<Respondent, Subclassification<Respondent>> makeDataPoint(SurveyResponseSubclassification inner, Set<Subclassification<Respondent>> key, Integer value)
	{
		return new DataPoint<>(inner.getTitle(), inner, key, value);
	}
}
