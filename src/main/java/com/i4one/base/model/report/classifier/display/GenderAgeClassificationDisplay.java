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

import com.i4one.base.model.UserType;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.ExclusiveSubclassificationComparator;
import com.i4one.base.model.report.classifier.SetComparator;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.report.classifier.classifications.AgeClassification;
import com.i4one.base.model.report.classifier.classifications.AgeSubclassification;
import com.i4one.base.model.report.classifier.classifications.GenderClassification;
import com.i4one.base.model.report.classifier.classifications.GenderSubclassification;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Hamid Badiozamani
 */
public class GenderAgeClassificationDisplay<T extends UserType> extends BaseClassificationDisplay<T, AgeSubclassification<T>>
{
	// Used for O(1) access when retrieving grouping counts and series names
	//
	private final GenderSubclassification[] genderCache;
	private final Set<AgeSubclassification> ageCache;

	private final Set<Set<Subclassification<T>>> dataPointKeys;

	public GenderAgeClassificationDisplay(GenderClassification<T> genderClass, AgeClassification<T> ageClass, ClassificationReport<?, T> report)
	{
		super(ageClass, report);

		this.dataPointKeys = new TreeSet<>(new SetComparator<>());

		// We use this comparator to ensure mutually exclusive subclassifications
		// aren't added to the data point keys.
		//
		ExclusiveSubclassificationComparator<Subclassification<T>> exclusiveSubs = new ExclusiveSubclassificationComparator<>();

                for ( GenderSubclassification gender : genderClass.getSubclassifications() )
                {
                        for ( AgeSubclassification<T> age : ageClass.getSubclassifications() )
                        {
				Set<Subclassification<T>> ageGenderSub = new TreeSet<>(exclusiveSubs);

                                ageGenderSub.add(age);
                                ageGenderSub.add(gender);

				dataPointKeys.add(ageGenderSub);
                        }
                }

                genderCache = genderClass.getSubclassifications()
                        .toArray(new GenderSubclassification[genderClass.getSubclassifications().size()]);

                ageCache = new LinkedHashSet<>();
		ageCache.addAll(ageClass.getSubclassifications());
		
		//ageCache = ageClass.getSubclassifications().toArray(new AgeSubclassification[ageClass.getSubclassifications().size()]);
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

		for ( AgeSubclassification<T> age : ageCache )
                {
			for ( GenderSubclassification gender : genderCache )
			{
				Set<Subclassification<T>> dpKey = new HashSet<>();
				dpKey.addAll(lineage);
				dpKey.add(gender);
				dpKey.add(age);

				// The key for these data points are going to always be the gender since
				// that is the "inner" component
				//
				DataPoint<T, Subclassification<T>> dp = makeDataPoint(age, gender, dpKey, getReport().get(dpKey));

				retVal.add(dp);
			}
		}

		return retVal;
	}

	@Override
	protected DataPoint<T, Subclassification<T>> makeDataPoint(AgeSubclassification<T> inner, Set<Subclassification<T>> key, Integer value)
	{
		throw new UnsupportedOperationException("Data points should be instantiated with both age and gender components");
	}

	protected DataPoint<T, Subclassification<T>> makeDataPoint(AgeSubclassification age, GenderSubclassification gender, Set<Subclassification<T>> key, Integer value)
	{
		return new GenderAgeDataPoint<>(gender, age, key, value);
	}


	@Override
	public String getKey()
	{
		return "genderage";
	}

	@Override
	public String getDisplayType()
	{
		return "column";
	}

	@Override
	public int getGroupingCount()
	{
		return genderCache.length;
	}

	@Override
	public String getSeriesName(int i)
	{
		return (genderCache[i % genderCache.length].getUniqueID());
	}
}
