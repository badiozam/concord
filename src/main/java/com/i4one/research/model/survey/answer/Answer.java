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
package com.i4one.research.model.survey.answer;

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.i18n.IString;
import com.i4one.research.model.survey.question.Question;
import java.util.List;
import java.util.Objects;

/**
 * @author Hamid Badiozamani
 */
public class Answer extends BaseRecordTypeDelegator<AnswerRecord>
{
	static final long serialVersionUID = 42L;

	private transient Question question;

	public Answer()
	{
		super(new AnswerRecord());
	}

	protected Answer(AnswerRecord delegate)
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
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getAnswer().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".answer.empty", "Answer cannot be empty", new Object[]{"item", this}));
		}


		return retVal;
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setQuestion(getQuestion());
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

	public int getOrderWeight()
	{
		return getDelegate().getOrderweight();
	}

	public void setOrderWeight(int orderweight)
	{
		getDelegate().setOrderweight(orderweight);
	}

	public IString getAnswer()
	{
		return getDelegate().getAnswer();
	}

	public void setAnswer(IString answer)
	{
		getDelegate().setAnswer(answer);
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		// question id, answer
		if  ( super.fromCSVInternal(csv) )
		{
			getQuestion(false).setSer(Integer.valueOf(csv.get(0))); csv.remove(0);
			setAnswer(new IString(csv.get(0))); csv.remove(0);

			return true;
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
			retVal.append(Utils.csvEscape("Question ID")).append(",");
			retVal.append(Utils.csvEscape("Answer")).append(",");
		}
		else
		{
			retVal.append("\"").append(getQuestion(false).getSer()).append("\",");
			retVal.append("\"").append(Utils.csvEscape(getAnswer().toString())).append("\",");
		}

		return retVal;
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getQuestion().getSer() + " - " + getAnswer();
	}

	@Override
	protected boolean equalsInternal(RecordTypeDelegator<AnswerRecord> right)
	{
		if ( right instanceof Answer )
		{
			Answer rightAnswer = (Answer)right;
			return Objects.equals(getQuestion(false).getSer(), rightAnswer.getQuestion(false).getSer()) &&
				getAnswer().equals(rightAnswer.getAnswer());
		}
		else
		{
			return false;
		}
	}
}
