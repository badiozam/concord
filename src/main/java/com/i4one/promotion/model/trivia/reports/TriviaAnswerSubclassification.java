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
package com.i4one.promotion.model.trivia.reports;

import com.i4one.base.model.report.classifier.BaseSubclassification;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.promotion.model.trivia.TriviaAnswer;
import com.i4one.promotion.model.trivia.TriviaResponse;
import java.util.Objects;

/**
 * @author Hamid Badiozamani
 */
public class TriviaAnswerSubclassification extends BaseSubclassification<TriviaResponse>
{
	private final boolean correct;
	private final TriviaAnswer triviaAnswer;

	public TriviaAnswerSubclassification(Classification<TriviaResponse, ? extends Subclassification<TriviaResponse>> parent, TriviaAnswer correctAnswer, boolean correct)
	{
		super(parent, String.valueOf(correct));

		this.triviaAnswer = correctAnswer;
		this.correct = correct;
	}

	@Override
	public boolean belongs(TriviaResponse item)
	{
		if ( correct )
		{
			return Objects.equals(triviaAnswer.getSer(), item.getTriviaAnswer(false).getSer());
		}
		else
		{
			return !Objects.equals(triviaAnswer.getSer(), item.getTriviaAnswer(false).getSer());
		}
	}
	
}
