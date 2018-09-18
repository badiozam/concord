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
package com.i4one.base.model.accesscode.reports;

import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.classifications.AgeClassification;
import com.i4one.base.model.report.classifier.classifications.DayOfWeekClassification;
import com.i4one.base.model.report.classifier.classifications.GenderClassification;
import com.i4one.base.model.report.classifier.classifications.HourOfDayClassification;
import com.i4one.base.model.accesscode.AccessCode;
import com.i4one.base.model.accesscode.AccessCodeResponse;
import com.i4one.base.model.accesscode.AccessCodeResponseRecord;
import java.util.Calendar;

/**
 *
 * @author Hamid Badiozamani
 */
public class AccessCodeResultsReport extends ClassificationReport<AccessCodeResponseRecord, AccessCodeResponse>
{
	private final AccessCode item;

	public AccessCodeResultsReport(AccessCode accessCode, Calendar cal)
	{
		super("accesscoderesults");

		this.item = accessCode;

		add(new AgeClassification(cal));
		add(new HourOfDayClassification(cal));
		add(new DayOfWeekClassification(cal));
		add(new GenderClassification());

		addCombinations();
	}

	@Override
	protected AccessCodeResponse convert(AccessCodeResponseRecord record)
	{
		AccessCodeResponse retVal = new AccessCodeResponse();
		retVal.setOwnedDelegate(record);

		return retVal;
	}

	public AccessCode getItem()
	{
		return item;
	}
}
