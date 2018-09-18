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

import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.report.classifier.BaseSubclassification;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.eventprediction.EventPrediction;
import java.util.Objects;

/**
 * @author Hamid Badiozamani
 */
public class EventPredictionSubclassification extends BaseSubclassification<EventPrediction>
{
	private final EventOutcome eventOutcome;

	public EventPredictionSubclassification(Classification<EventPrediction, ? extends Subclassification<EventPrediction>> parent, EventOutcome eventOutcome)
	{
		super(parent, String.valueOf(eventOutcome.getSer()));

		this.eventOutcome = eventOutcome;
	}

	@Override
	public IString getTitle()
	{
		return eventOutcome.getDescr();
	}

	@Override
	public boolean belongs(EventPrediction item)
	{
		return Objects.equals(eventOutcome.getSer(), item.getEventOutcome(false).getSer());
	}
	
}
