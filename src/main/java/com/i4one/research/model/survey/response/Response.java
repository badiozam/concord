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

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.UsageType;
import com.i4one.base.model.user.User;
import com.i4one.research.model.survey.answer.Answer;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.respondent.Respondent;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public class Response extends BaseRecordTypeDelegator<ResponseRecord> implements UsageType
{
	static final long serialVersionUID = 42L;

	private transient Respondent respondent;
	private transient Question question;
	private transient Answer answer;

	public Response()
	{
		super(new ResponseRecord());
	}

	protected Response(ResponseRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		if ( question == null )
		{
			question = new Question();
		}
		question.resetDelegateBySer(getDelegate().getQuestionid());

		if ( respondent == null )
		{
			respondent = new Respondent();
		}
		respondent.resetDelegateBySer(getDelegate().getRespondentid());

		if ( answer == null )
		{
			answer = new Answer();
		}

		// Open ended answers wouldn't have an answerid
		//
		if ( getDelegate().getAnswerid() != null )
		{
			answer.resetDelegateBySer(getDelegate().getAnswerid());
		}
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setQuestion(getQuestion());
		setRespondent(getRespondent());
		setAnswer(getAnswer());
	}

	public String getOpenAnswer()
	{
		return getDelegate().getOpenanswer();
	}

	public void setOpenAnswer(String openAnswer)
	{
		getDelegate().setOpenanswer(openAnswer);
	}

	public Question getQuestion()
	{
		return getQuestion(true);
	}

	public Question getQuestion(boolean doLoad)
	{
		if ( doLoad )
		{
			question.loadedVersion();
		}

		return question;
	}

	public void setQuestion(Question question)
	{
		this.question = question;
		getDelegate().setQuestionid(question.getSer());
	}

	public Answer getAnswer()
	{
		return getAnswer(true);
	}

	public Answer getAnswer(boolean doLoad)
	{
		if ( doLoad )
		{
			answer.loadedVersion();
		}

		return answer;
	}

	public void setAnswer(Answer answer)
	{
		this.answer = answer;
		if ( answer.exists() )
		{
			getDelegate().setAnswerid(answer.getSer());
		}
		else
		{
			getDelegate().setAnswerid( null );
		}
	}

	public Respondent getRespondent()
	{
		return getRespondent(true);
	}

	public Respondent getRespondent(boolean doLoad)
	{
		if ( doLoad )
		{
			respondent.loadedVersion();
		}

		return respondent;
	}

	public void setRespondent(Respondent respondent)
	{
		this.respondent = respondent;
		getDelegate().setRespondentid(respondent.getSer());
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getRespondentid() + "-" + getDelegate().getQuestionid() + "-" + getDelegate().getAnswerid() + "-" + getDelegate().getTimestamp();
	}

	@Override
	public void setUser(User user)
	{
		throw new UnsupportedOperationException("Can't set user directly.");
	}

	@Override
	public User getUser()
	{
		return getRespondent().getUser();
	}

	@Override
	public Date getTimeStamp()
	{
		return Utils.toDate(getTimeStampSeconds());
	}

	@Override
	public int getTimeStampSeconds()
	{
		return getDelegate().getTimestamp();
	}

	@Override
	public void setTimeStampSeconds(int timeStamp)
	{
		getDelegate().setTimestamp(timeStamp);
	}
}
