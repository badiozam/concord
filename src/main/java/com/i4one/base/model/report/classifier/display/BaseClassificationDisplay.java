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
package com.i4one.base.model.report.classifier.display;

import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.Subclassification;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class BaseClassificationDisplay<T extends Object, SUB extends Subclassification<T>> implements ClassificationDisplay<T, SUB>
{
	private final Classification<T, SUB> classification;
	private final ClassificationReport<?, T> report;

	public BaseClassificationDisplay(Classification<T, SUB> classification, ClassificationReport<?,T> report)
	{
		this.classification = classification;
		this.report = report;
	}

	@Override
	public boolean hasData(Set<Subclassification<T>> lineage)
	{
		for ( SUB subclass : classification.getSubclassifications() )
		{
			Set<Subclassification<T>> dpKey = new HashSet<>();
			dpKey.addAll(lineage);
			dpKey.add(subclass);

			Integer value = report.get(dpKey);
			if ( value != null && value != 0)
			{
				return true;
			}
		}

		// No data in any of the subclasses
		//
		return false;
	}

	@Override
	public Set<DataPoint<T, Subclassification<T>>> getDataPoints()
	{
		return getDataPoints(Collections.emptySet());
	}

	@Override
	public Set<DataPoint<T, Subclassification<T>>> getDataPoints(Set<Subclassification<T>> lineage)
	{
		DataPointSet<T, Subclassification<T>> retVal = makeDataPointSet();

		for ( SUB subclass : classification.getSubclassifications() )
		{
			Set<Subclassification<T>> dpKey = new HashSet<>();
			dpKey.addAll(lineage);
			dpKey.add(subclass);

			// The key in this case could be something like gender#m|age#18-24. We pass
			// subclass title as the name since in the set it may take any position.
			//
			Integer value = report.get(dpKey);
			if ( value != null )
			{
				DataPoint dp = makeDataPoint(subclass, dpKey, value);

				retVal.add(dp);
			}
		}

		return retVal;
	}

	protected DataPointSet<T, Subclassification<T>> makeDataPointSet()
	{
		return new DataPointSet<>(new LinkedHashSet<>());
	}

	protected DataPoint<T, Subclassification<T>> makeDataPoint(SUB inner, Set<Subclassification<T>> key, Integer value)
	{
		return new DataPoint<>(inner, key, value);
	}

	@Override
	public Classification<T, SUB> getClassification()
	{
		return classification;
	}

	@Override
	public String getKey()
	{
		return classification.getKey();
	}

	@Override
	public IString getTitle()
	{
		return IString.BLANK;
	}

	@Override
	public String getDisplayType()
	{
		return "pie";
	}

	@Override
	public int getGroupingCount()
	{
		return 1;
	}

	@Override
	public String getSeriesName()
	{
		return report.getTitle();
	}

	@Override
	public String getSeriesName(int i)
	{
		return getSeriesName();
	}

	@Override
	public boolean getShowChart()
	{
		return !getDisplayType().equals("none");
	}

	protected ClassificationReport<?, T> getReport()
	{
		return report;
	}
}
