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

import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.classifications.AgeClassification;
import com.i4one.base.model.report.classifier.classifications.DayOfWeekClassification;
import com.i4one.base.model.report.classifier.classifications.GenderClassification;
import com.i4one.base.model.report.classifier.classifications.HourOfDayClassification;
import com.i4one.base.web.controller.admin.ActivityReportSettings;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.eventprediction.EventPrediction;
import com.i4one.predict.model.eventprediction.EventPredictionRecord;
import java.util.Calendar;

/**
 *
 * @author Hamid Badiozamani
 */
public class EventResultsReport extends ClassificationReport<EventPredictionRecord, EventPrediction>
{
	private final Event item;

	public EventResultsReport(Event event, Calendar cal, ActivityReportSettings settings)
	{
		super("eventresults");

		this.item = event;

		// If the settings call for unique users, then we become an activity
		// report and displaying classifications doesn't make sense. Otherwise,
		// we display statistics on transactions.
		//
		if ( !settings.isUniqueUsers() )
		{
			// Displaying activity
			//
			add(new EventPredictionClassification(event));

			// Only add the correct prediction display if the event has
			// an actual outcome
			//
			if (event.getActualOutcome().exists())
			{
				add(new CorrectPredictionClassification(event));
			}

			add(new HourOfDayClassification(cal));
			add(new DayOfWeekClassification(cal));
		}
		else
		{
			add(new AgeClassification(cal));
			add(new HourOfDayClassification(cal));
			add(new DayOfWeekClassification(cal));
			add(new GenderClassification());
		}

		addCombinations();
	}

	@Override
	protected EventPrediction convert(EventPredictionRecord record)
	{
		EventPrediction retVal = new EventPrediction();
		retVal.setOwnedDelegate(record);

		return retVal;
	}

	public Event getItem()
	{
		return item;
	}
}
