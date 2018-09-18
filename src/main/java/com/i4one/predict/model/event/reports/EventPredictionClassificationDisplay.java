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
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.report.classifier.display.BaseClassificationDisplay;
import com.i4one.base.model.report.classifier.display.DataPoint;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.eventprediction.EventPrediction;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class EventPredictionClassificationDisplay extends BaseClassificationDisplay<EventPrediction, EventPredictionSubclassification>
{
	private final Event event;

	public EventPredictionClassificationDisplay(EventPredictionClassification classification, ClassificationReport<?, EventPrediction> report)
	{
		super(classification, report);

		this.event = classification.getEvent();
	}

	@Override
	public IString getTitle()
	{
		return event.getTitle();
	}

	@Override
	protected DataPoint<EventPrediction, Subclassification<EventPrediction>> makeDataPoint(EventPredictionSubclassification inner, Set<Subclassification<EventPrediction>> key, Integer value)
	{
		return new DataPoint<>(inner.getTitle(), inner, key, value);
	}
}
