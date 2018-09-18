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

import com.i4one.base.model.i18n.IString;
import com.i4one.research.model.ResearchRecordType;

/**
 * @author Hamid Badiozamani
 */
public class QuestionRecord extends ResearchRecordType
{
	static final long serialVersionUID = 42L;

	private Integer surveyid;
	private Integer answercount;
	private Integer orderweight;
	private Integer minresponses;
	private Integer maxresponses;
	private Integer questiontype;
	private IString question;
	private String validanswer;

	public QuestionRecord()
	{
		super();

		surveyid = 0;

		answercount = 0;
		orderweight = 0;
		questiontype = 0;
		minresponses = 0;
		maxresponses = 0;

		question = new IString();
	}

	@Override
	public String getTableName()
	{
		return "surveyquestions";
	}

	public Integer getSurveyid()
	{
		return surveyid;
	}

	public void setSurveyid(Integer surveyid)
	{
		this.surveyid = surveyid;
	}

	public Integer getAnswercount()
	{
		return answercount;
	}

	public void setAnswercount(Integer answercount)
	{
		this.answercount = answercount;
	}

	public Integer getOrderweight()
	{
		return orderweight;
	}

	public void setOrderweight(Integer orderweight)
	{
		this.orderweight = orderweight;
	}

	public Integer getQuestiontype()
	{
		return questiontype;
	}

	public void setQuestiontype(Integer questiontype)
	{
		this.questiontype = questiontype;
	}

	public Integer getMinresponses()
	{
		return minresponses;
	}

	public void setMinresponses(Integer minresponses)
	{
		this.minresponses = minresponses;
	}

	public Integer getMaxresponses()
	{
		return maxresponses;
	}

	public void setMaxresponses(Integer maxresponses)
	{
		this.maxresponses = maxresponses;
	}

	public IString getQuestion()
	{
		return question;
	}

	public void setQuestion(IString question)
	{
		this.question = question;
	}

	public String getValidanswer()
	{
		return validanswer;
	}

	public void setValidanswer(String validanswer)
	{
		this.validanswer = validanswer;
	}
}
