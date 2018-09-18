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
package com.i4one.base.model.report.classifier.classifications;

import com.i4one.base.model.TemporalType;
import com.i4one.base.model.report.classifier.BaseClassification;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.display.ClassificationDisplay;
import com.i4one.base.model.report.classifier.display.DayOfWeekClassificationDisplay;
import java.util.Calendar;

/**
 * @author Hamid Badiozamani
 */
public class DayOfWeekClassification<T extends TemporalType> extends BaseClassification<T, DayOfWeekSubclassification<T>>
{
	public DayOfWeekClassification(Calendar cal)
	{
		super();

		for (int day = 1; day < 8; day++ )
		{
			add(new DayOfWeekSubclassification(this, cal, day));
		}
	}

	@Override
	public String getKey()
	{
		return "dayofweek";
	}

	@Override
	public ClassificationDisplay<T, DayOfWeekSubclassification<T>> getDefaultDisplay(ClassificationReport<?, T> report)
	{
		return new DayOfWeekClassificationDisplay(this, report);
	}
}
