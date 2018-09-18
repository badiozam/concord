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
package com.i4one.research.model.poll.reports;

import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.report.classifier.display.BaseClassificationDisplay;
import com.i4one.base.model.report.classifier.display.DataPoint;
import com.i4one.research.model.poll.Poll;
import com.i4one.research.model.poll.PollResponse;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class PollAnswerClassificationDisplay extends BaseClassificationDisplay<PollResponse, PollAnswerSubclassification>
{
	private final Poll poll;

	public PollAnswerClassificationDisplay(PollAnswerClassification classification, ClassificationReport<?, PollResponse> report)
	{
		super(classification, report);

		this.poll = classification.getPoll();
	}

	@Override
	public IString getTitle()
	{
		return poll.getTitle();
	}

	@Override
	protected DataPoint<PollResponse, Subclassification<PollResponse>> makeDataPoint(PollAnswerSubclassification inner, Set<Subclassification<PollResponse>> key, Integer value)
	{
		return new DataPoint<>(inner.getTitle(), inner, key, value);
	}
}
