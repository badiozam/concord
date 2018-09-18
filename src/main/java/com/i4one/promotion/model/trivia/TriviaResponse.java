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

import com.i4one.base.model.BaseActivityType;
import com.i4one.base.model.SiteGroupActivityType;
import com.i4one.base.model.UsageType;

/**
 * @author Hamid Badiozamani
 */
public class TriviaResponse extends BaseActivityType<TriviaResponseRecord, TriviaRecord, Trivia> implements UsageType<TriviaResponseRecord>,SiteGroupActivityType<TriviaResponseRecord, Trivia>
{
	static final long serialVersionUID = 42L;

	protected transient TriviaAnswer triviaAnswer;

	public TriviaResponse()
	{
		super(new TriviaResponseRecord());
	}

	protected TriviaResponse(TriviaResponseRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( triviaAnswer == null )
		{
			triviaAnswer = new TriviaAnswer();
		}
		triviaAnswer.resetDelegateBySer(getDelegate().getAnswerid());
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setTriviaAnswer(getTriviaAnswer());
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getItemid() + "-" + getDelegate().getUserid();
	}

	public Trivia getTrivia()
	{
		return getActionItem();
	}

	public Trivia getTrivia(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	public void setTrivia(Trivia trivia)
	{
		setActionItem(trivia);
	}

	public TriviaAnswer getTriviaAnswer()
	{
		return getTriviaAnswer(true);
	}

	public TriviaAnswer getTriviaAnswer(boolean doLoad)
	{
		if ( doLoad )
		{
			triviaAnswer.loadedVersion();
		}

		return triviaAnswer;
	}

	public void setTriviaAnswer(TriviaAnswer triviaAnswer)
	{
		this.triviaAnswer = triviaAnswer;
		getDelegate().setAnswerid(triviaAnswer.getSer());
	}

	public boolean isCorrect()
	{
		TriviaAnswer correctAnswer = getTrivia().getCorrectAnswer();
		return correctAnswer.equals(triviaAnswer);
	}

	@Override
	protected Trivia newActionItem()
	{
		return new Trivia();
	}
}