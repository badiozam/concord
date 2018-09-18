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
package com.i4one.promotion.model.trivia;

import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ForeignKey;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class TriviaAnswer extends BaseRecordTypeDelegator<TriviaAnswerRecord>
{
	static final long serialVersionUID = 42L;

	private transient ForeignKey<TriviaRecord, Trivia> triviaFk;
	private transient boolean correct;


	public TriviaAnswer()
	{
		super(new TriviaAnswerRecord());

		correct = false;
	}

	protected TriviaAnswer(TriviaAnswerRecord delegate)
	{
		super(delegate);

		correct = false;
	}

	@Override
	protected void init()
	{
		triviaFk = new ForeignKey<>(this,
			getDelegate()::getTriviaid,
			getDelegate()::setTriviaid,
			() -> { return new Trivia(); });
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getAnswer().isBlank() )
		{
			retVal.addError("answer", new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".answer.empty", "Answer cannot be empty", new Object[]{"item", this}));
		}

		return retVal;
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		triviaFk.actualize();
	}

	public Trivia getTrivia()
	{
		return triviaFk.get(true);
	}

	public Trivia getTrivia(boolean doLoad)
	{
		return triviaFk.get(doLoad);
	}

	public void setTrivia(Trivia trivia)
	{
		triviaFk.set(trivia);
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

	public boolean isCorrect()
	{
		return correct;
	}

	public boolean getCorrect()
	{
		return correct;
	}

	public void setCorrect(boolean correct)
	{
		this.correct = correct;
	}
	@Override
	protected String uniqueKeyInternal()
	{
		return getTrivia(false).getSer() + " - " + getAnswer();
	}
}
