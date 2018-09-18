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

import com.i4one.base.model.UserType;
import com.i4one.base.model.report.classifier.BaseClassification;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.display.AgeClassificationDisplay;
import com.i4one.base.model.report.classifier.display.ClassificationDisplay;
import java.util.Calendar;

/**
 * @author Hamid Badiozamani
 */
public class AgeClassification<T extends UserType> extends BaseClassification<T, AgeSubclassification<T>> implements Classification<T, AgeSubclassification<T>>
{
	public AgeClassification(Calendar cal)
	{
		super();

		add(new AgeSubclassification(this, cal, 12, 17));
		add(new AgeSubclassification(this, cal, 18, 24));
		add(new AgeSubclassification(this, cal, 25, 34));
		add(new AgeSubclassification(this, cal, 35, 44));
		add(new AgeSubclassification(this, cal, 45, 54));
		add(new AgeSubclassification(this, cal, 55, 64));
		add(new AgeSubclassification(this, cal, 65, 100));
	}

	@Override
	public String getKey()
	{
		return "age";
	}

	@Override
	public ClassificationDisplay<T, AgeSubclassification<T>> getDefaultDisplay(ClassificationReport<?, T> report)
	{
		return new AgeClassificationDisplay(this, report);
	}
}
