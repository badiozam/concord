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
package com.i4one.predict.model.event.reports;

import com.i4one.base.model.report.classifier.BaseSubclassification;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.eventprediction.EventPrediction;
import java.util.Objects;

/**
 * @author Hamid Badiozamani
 */
public class CorrectPredictionSubclassification extends BaseSubclassification<EventPrediction>
{
	private final Event event;
	private final boolean correct;

	public CorrectPredictionSubclassification(Classification<EventPrediction, ? extends Subclassification<EventPrediction>> parent, Event event, boolean correct)
	{
		super(parent, String.valueOf(correct));

		this.event = event;
		this.correct = correct;
	}

	public Event getEvent()
	{
		return event;
	}

	public boolean isCorrect()
	{
		return correct;
	}

	@Override
	public boolean belongs(EventPrediction item)
	{
		if (correct)
		{
			return Objects.equals(item.getEventOutcome(), item.getEvent().getActualOutcome());
		}
		else
		{
			return !Objects.equals(item.getEventOutcome(), item.getEvent().getActualOutcome());
		}
	}
	
}
