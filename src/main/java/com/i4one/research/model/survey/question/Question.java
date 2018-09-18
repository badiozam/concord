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
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.i18n.IString;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.answer.Answer;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class Question extends BaseRecordTypeDelegator<QuestionRecord>
{
	static final long serialVersionUID = 42L;

	public static final int TYPE_SINGLEANSWER_RADIO = 0;
	public static final int TYPE_SINGLEANSWER_SELECT = 1;
	public static final int TYPE_MULTIANSWER_CHECKBOX = 2;
	public static final int TYPE_OPENANSWER_SINGLE = 3;
	public static final int TYPE_OPENANSWER_MULTI = 4;

	private transient Survey survey;
	private transient Set<Answer> answers;

	public Question()
	{
		super(new QuestionRecord());
	}

	protected Question(QuestionRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		if ( survey == null )
		{
			survey = new Survey();
		}
		survey.resetDelegateBySer(getDelegate().getSurveyid());

		if ( answers == null )
		{
			answers = new LinkedHashSet<>();
		}
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getQuestion().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".answer.empty", "Question cannot be empty", new Object[]{"item", this}));
		}


		return retVal;
	}

	@Override
	public void setOverrides()
	{
		if ( getQuestionType() == TYPE_SINGLEANSWER_RADIO || getQuestionType() == TYPE_SINGLEANSWER_SELECT )
		{
			setMinResponses(1);
			setMaxResponses(1);
			setValidAnswer("");
		}
		else if ( getQuestionType() == TYPE_OPENANSWER_SINGLE || getQuestionType() == TYPE_OPENANSWER_MULTI )
		{
			setMinResponses(0);
			setMaxResponses(0);
		}
		else // Multi
		{
			setValidAnswer("");
		}
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setSurvey(Question.this.getSurvey());
	}

	public Survey getSurvey()
	{
		return getSurvey(true);
	}

	public Survey getSurvey(boolean doLoad)
	{
		if ( doLoad )
		{
			survey.loadedVersion();
		}

		return survey;
	}

	public void setSurvey(Survey survey)
	{
		this.survey = survey;
		getDelegate().setSurveyid(survey.getSer());
	}

	public int getAnswerCount()
	{
		return getDelegate().getAnswercount();
	}

	public void setAnswerCount(int answercount)
	{
		getDelegate().setAnswercount(answercount);
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

	public void setQuestionType(int answerType)
	{
		getDelegate().setQuestiontype(answerType);
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
	
	public IString getQuestion()
	{
		return getDelegate().getQuestion();
	}

	public void setQuestion(IString answer)
	{
		getDelegate().setQuestion(answer);
	}

	public Set<Answer> getAnswers()
	{
		return answers;
	}

	public void setAnswers(Collection<Answer> answers)
	{
		this.answers.clear();
		this.answers.addAll(answers);

		setAnswerCount(answers.size());
	}

	@Override
	protected void copyFromInternal(RecordTypeDelegator<QuestionRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyFromInternal(right);
		if ( right instanceof Question )
		{
			Question rightQuestion = (Question)right;

			answers.clear();
			for ( Answer answer : rightQuestion.getAnswers() )
			{
				Answer newAnswer = new Answer();
				newAnswer.copyFrom(answer);
				newAnswer.setSer(0);

				answers.add(newAnswer);
			}
		}
	}

	@Override
	protected void copyOverInternal(RecordTypeDelegator<QuestionRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyOverInternal(right);
		if ( right instanceof Question )
		{
			Question rightQuestion = (Question)right;

			if ( !rightQuestion.getAnswers().isEmpty() )
			{
				copyFromInternal(right);
			}
		}
	}

	public String getValidAnswer()
	{
		return getDelegate().getValidanswer();
	}

	public void setValidAnswer(String validAnswer)
	{
		getDelegate().setValidanswer(validAnswer);
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		// question no, type, question, minresponses, maxresponses,validanswer
		if  ( super.fromCSVInternal(csv) )
		{
			setSer(Integer.valueOf(csv.get(0))); csv.remove(0);
			setQuestionType(Integer.valueOf(csv.get(0))); csv.remove(0);
			setQuestion(new IString(csv.get(0))); csv.remove(0);
			setMinResponses(Integer.valueOf(csv.get(0))); csv.remove(0);
			setMaxResponses(Integer.valueOf(csv.get(0))); csv.remove(0);
			setValidAnswer(csv.get(0)); csv.remove(0);

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
			retVal.append(Utils.csvEscape("ID")).append(",");
			retVal.append(Utils.csvEscape("Type")).append(",");
			retVal.append(Utils.csvEscape("Question")).append(",");
			retVal.append(Utils.csvEscape("Min Responses")).append(",");
			retVal.append(Utils.csvEscape("Max Responses")).append(",");
			retVal.append(Utils.csvEscape("Valid Answer Regex")).append(",");
		}
		else
		{
			retVal.append("\"").append(getSer()).append("\",");
			retVal.append("\"").append(getQuestionType()).append("\",");
			retVal.append("\"").append(Utils.csvEscape(getQuestion().toString())).append("\",");
			retVal.append("\"").append(getMinResponses()).append("\",");
			retVal.append("\"").append(getMaxResponses()).append("\",");
			retVal.append("\"").append(Utils.csvEscape(getValidAnswer())).append("\",");
		}

		return retVal;
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getSurvey().getSer() + " - " + getQuestion();
	}

	@Override
	protected boolean equalsInternal(RecordTypeDelegator<QuestionRecord> right)
	{
		if ( right instanceof Question )
		{
			Question rightQuestion = (Question)right;
			return Objects.equals(getSurvey(false).getSer(), rightQuestion.getSurvey(false).getSer()) &&
				getQuestion().equals(rightQuestion.getQuestion());
		}
		else
		{
			return false;
		}
	}
}
