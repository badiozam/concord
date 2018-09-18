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
package com.i4one.research.model.survey.response;

import com.i4one.research.model.ResearchRecordType;

/**
 * @author Hamid Badiozamani
 */
public class ResponseRecord extends ResearchRecordType
{
	static final long serialVersionUID = 42L;

	private Integer respondentid;
	private Integer questionid;
	private Integer answerid;
	private String openanswer;

	private Integer timestamp;

	public ResponseRecord()
	{
		respondentid = 0;
		questionid = 0;
		answerid = 0;

		timestamp = 0;
	}

	@Override
	public String getTableName()
	{
		return "surveyresponses";
	}

	public Integer getRespondentid()
	{
		return respondentid;
	}

	public void setRespondentid(Integer respondentid)
	{
		this.respondentid = respondentid;
	}

	public Integer getQuestionid()
	{
		return questionid;
	}

	public void setQuestionid(Integer questionid)
	{
		this.questionid = questionid;
	}

	public Integer getAnswerid()
	{
		return answerid;
	}

	public void setAnswerid(Integer answerid)
	{
		this.answerid = answerid;
	}

	public String getOpenanswer()
	{
		return openanswer;
	}

	public void setOpenanswer(String openanswer)
	{
		this.openanswer = openanswer;
	}

	public Integer getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Integer timestamp)
	{
		this.timestamp = timestamp;
	}

}
