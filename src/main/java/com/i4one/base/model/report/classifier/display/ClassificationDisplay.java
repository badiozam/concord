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
import com.i4one.base.model.report.classifier.Subclassification;
import java.util.Set;

/**
 * This interface describes how to display a particular classification and its
 * subclassification data points.
 * 
 * @author Hamid Badiozamani
 * @param <T> The type that has been compiled into the report
 * @param <SUB> The type of subclassification that this object's classification manages.
 */
public interface ClassificationDisplay<T extends Object, SUB extends Subclassification<T>>
{
	/**
	 * Whether this classification has any data or not at the given lineage
	 * point.
	 * 
	 * @param lineage The lineage to prepend to each data point.
	 * 
	 * @return True if the classification has any data, false otherwise.
	 */
	public boolean hasData(Set<Subclassification<T>> lineage);

	/**
	 * Whether to display a chart or only tables.
	 * 
	 * @return True if we're to display a chart as well
	 */
	public boolean getShowChart();

	/**
	 * A key that uniquely describes the display being rendered. These are keys
	 * that are to be converted into i18n messages. Typical values might be
	 * "age" or "hourofday" which might be converted to "Age Distribution" or
	 * "Hour of Day Breakdown".
	 * 
	 * @return The message key of the classification.
	 */
	public String getKey();

	/**
	 * The i18n title to display for a particular classification. This is a
	 * secondary method for getting the title of a display and is necessary
	 * in cases where the title is known or is retrieved from a different
	 * source than MessageManager.
	 * 
	 * @return The title
	 */
	public IString getTitle();

	/**
	 * The underlying classification to be displayed.
	 * 
	 * @return The underlying classification.
	 */
	public Classification<T, SUB> getClassification();

	/**
	 * Return the individual data points that comprise this report.
	 * 
	 * @return The data points to display
	 */
	public Set<DataPoint<T, Subclassification<T>>> getDataPoints();

	/**
	 * Return the individual data points that comprise this report with a
	 * particular lineage.
	 * 
	 * @param lineage The lineage to prepend to each data point.
	 * 
	 * @return The data points to display
	 */
	public Set<DataPoint<T, Subclassification<T>>> getDataPoints(Set<Subclassification<T>> lineage);

	/**
	 * The type of chart to display for the sub-report data. Some possible
	 * values could be "bar", "pie" or "line".
	 * 
	 * @return The type of chart to display the subclassifications in.
	 */
	public String getDisplayType();

	/**
	 * How to group the subclassifications. For example, if the classificaiton
	 * has 10 subclassifications which are of type age/gender in order of
	 * [age1gender1][age1gender2][age2gender1]... then the grouping number
	 * would be two since every two elements are considered to be in the same
	 * group. Typically though, this value is 1.
	 * 
	 * @return The total number of groupings per sub-report
	 */
	public int getGroupingCount();

	/**
	 * Get the series name of the report. This name is the same as
	 * display name, but also factors in the parent reports as well.
	 * 
	 * The return value will always be in the following form:
	 * 
	 * msg.[class name].seriesName
	 * 
	 * @return The display name of the report
	 */
	public String getSeriesName();

	/**
	 * Get the series name of the report with respect to the grouping count.
	 * The return value will always be in the following form:
	 * 
	 * msg.[class name].[series name]
	 * 
	 * Where [series name] is the name of the specific series as identified
	 * by the given index parameter i.
	 * 
	 * @param i The series index number in the set [0,getGroupingCount)
	 * 
	 * @return The display name of the report
	 */
	public String getSeriesName(int i);

}
