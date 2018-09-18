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

import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.classifications.AgeClassification;
import com.i4one.base.model.report.classifier.classifications.DayOfWeekClassification;
import com.i4one.base.model.report.classifier.classifications.GenderClassification;
import com.i4one.base.model.report.classifier.classifications.HourOfDayClassification;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.respondent.Respondent;
import com.i4one.research.model.survey.respondent.RespondentRecord;
import com.i4one.research.model.survey.response.ResponseManager;
import java.util.Calendar;

/**
 *
 * @author Hamid Badiozamani
 */
public class SurveyResultsReport extends ClassificationReport<RespondentRecord, Respondent>
{
	private final Survey survey;
	private final ResponseManager responseManager;

	public SurveyResultsReport(Survey survey, Calendar cal, ResponseManager responseManager)
	{
		super("surveyresults");

		this.survey = survey;
		this.responseManager = responseManager;

		add(new SurveyCompletedClassification());

		add(new AgeClassification(cal));
		add(new HourOfDayClassification(cal));
		add(new DayOfWeekClassification(cal));
		add(new GenderClassification());

		for ( Question question : survey.getQuestions() )
		{
			add(new SurveyResponseClassification(question, responseManager));
		}

		addCombinations();
	}

	@Override
	protected Respondent convert(RespondentRecord record)
	{
		Respondent retVal = new Respondent();
		retVal.setOwnedDelegate(record);

		return retVal;
	}

	public Survey getItem()
	{
		return survey;
	}

	public ResponseManager getResponseManager()
	{
		return responseManager;
	}
}
