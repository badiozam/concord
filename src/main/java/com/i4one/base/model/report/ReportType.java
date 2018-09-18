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
package com.i4one.base.model.report;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Collection;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This interface represents an activity report. Each report can contain other
 * sub-reports with each sub-report guaranteed to reflect information about a
 * subset of the parent report.
 * 
 * @author Hamid Badiozamani
 * @param <T> The type of sub-reports this report will contain
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@c")
/*
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@c")
@JsonSubTypes({
    @JsonSubTypes.Type(value=AgeGenderAggregateReport.class, name="aga"),
    @JsonSubTypes.Type(value=AgeGenderReport.class, name="ag")
})
*/
public interface ReportType<T extends ReportType>
{
	/**
	 * Returns the schema name. The schema name is the "simplified" package 
	 * name (i.e. com.i4one.base.* =&gt; base, or com.i4one.promotion.* =&gt; promotion).
	 * 
	 * @return The schema name
	 */
	public String getSchemaName();

	/**
	 * Returns the model name. The model name uniquely identifies a report type.
	 * The format of the model name is &lt;schema name&gt;.&lt;simple class name&gt;
	 * 
	 * @return 
	 */
	public String getModelName();

	/**
	 * Get the parent report of this report. A top level report would not have
	 * a parent and would thus return null.
	 * 
	 * @return The parent report of this report
	 */
	public ReportType getParent();

	/**
	 * Set the parent report of this report.
	 * 
	 * @param parent The parent report of this report
	 */
	public void setParent(ReportType parent);

	/**
	 * Get the title chain for this report. The title chain consists of the
	 * titles of the ancestry of this report in CSV format. As such,
	 * the top level report is always the first item.
	 * 
	 * @return The title chain for this report
	 */
	public String getTitleChain();

	/**
	 * Get the display name chain for this report. The display name chain
	 * consists of the display names of the ancestry of this report in CSV
	 * format. As such, the top level report is always the first element.
	 * 
	 * @return The title chain for this report
	 */
	public String getDisplayNameChain();

	/**
	 * Get the ancestry stack of this report. The stack has this report
	 * as its top-most element, followed by the parent, grandparent, etc.
	 * 
	 * @return The ancestry stack of this report
	 */
	public Stack<ReportType> getAncestry();

	/**
	 * Find a child report by title chain. The title chain is the set of titles
	 * in the format [parent title],[child title],[grandchild title],...
	 * 
	 * @param titleChain The title chain identifying the report
	 * 
	 * @return The report or null if not found
	 */
	public ReportType findReport(String titleChain);

	/**
	 * The total count for this report.
	 * 
	 * @return The total count for this report.
	 */
	public int getTotal();

	public void setTotal(int total);

	/**
	 * The groupings of each sub-report. For example, if the report has 10 reports
	 * which are of type age/gender in order of [age1gender1][age1gender2][age2gender1]...
	 * then the grouping number would be two since every two elements are considered
	 * to be in the same group. Typically though, this value is 1.
	 * 
	 * @return The total number of groupings per sub-report
	 */
	public int getGroupingCount();

	/**
	 * Return a clone of this report. The clone must always have a zero
	 * total count.
	 * 
	 * @return A cloned version of this report
	 */
	public ReportType<T> cloneReport();

	/**
	 * Initialize the report. This method is called immediately prior to
	 * the processing of records.
	 */
	public void initReport();

	/**
	 * Get the name of the report. This name should uniquely identify
	 * the report taking into account its current state as well.
	 * 
	 * @return The name of the report
	 */
	public String getTitle();

	/**
	 * Get the display name of the report. This is the name to use when
	 * displaying just this report's value.
	 * 
	 * @return The display name of the report
	 */
	public String getDisplayName();

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

	/**
	 * The type of chart to display for the sub-report data. Some possible
	 * values could be "bar", "pie" or "line".
	 * 
	 * @return The type of chart to display for the sub-reports
	 */
	public String getDisplayType();

	/**
	 * Get the set of sub reports that belong to this report.
	 * 
	 * @return The set of sub reports for this report
	 */
	public Set<T> getSubReports();

	/**
	 * Set the sub reports for this report.
	 * 
	 * @param reports The sub reports for this report
	 */
	public void setSubReports(Collection<T> reports);

	/**
	 * Adds a copy of a given report to the current list.
	 * 
	 * @param report The report to copy and add
	 */
	public void addReport(T report);

	/**
	 * Adds a new report to the current list, taking ownership of the incoming
	 * report.
	 * 
	 * @param report The new report to add
	 * 
	 * @return True if the report was added, false otherwise
	 */
	public boolean newReport(T report);

	/**
	 * Remove a given sub-report
	 * 
	 * @param report The report to remove
	 * 
	 * @return True if the report was successfully removed, false if the report
	 * 	was not found
	 */
	public boolean removeReport(T report);

	/**
	 * Tests to see whether this report or any of its sub-reports contains the
	 * given report.
	 * 
	 * @param report The report to check
	 * 
	 * @return True if this or any sub-report contains the incoming value, false otherwise
	 */
	public boolean hasReport(T report);

	/**
	 * Determines whether the usages in this report are exclusive to those of another.
	 * This typically happens in cases where the eligibility is determined by an
	 * exclusive field (e.g. gender).
	 * 
	 * @param report The report to test exclusivity against
	 * 
	 * @return True if this report's usages are exclusive to the given report,
	 * 	false otherwise
	 */
	public boolean isExclusiveTo(T report);

	public boolean isClassCompatible(Class clazz);

	/**
	 * Tests to see if this report is the same as another. The reports are compared
	 * only to each. This is different behavior than the .equals(..) method which
	 * considers parental lineage as well.
	 * 
	 * @param report The report to compare to
	 * 
	 * @return True if the reports are the same, false otherwise
	 */
	public boolean isSameReport(ReportType<T> report);

	/**
	 * Clear all of the calculations of the report.
	 */
	public void clear();

	/**
	 * Remove redundant sub-reports.
	 */
	public void prune();

	/**
	 * Tests to see if the given usage is eligible for addition to this report
	 * 
	 * @param usage The usage to test
	 * 
	 * @return True if the usage is eligible, false otherwise
	 */
	public boolean isEligible(ReportUsageType usage);

	/**
	 * Perform an action on all nodes in depth-first fashion.
	 * 
	 * @param function The action to be performed. If the function returns
	 * 	true the DFS continues, otherwise the traversal stops.
	 */
	public void forAllProgeny(Function<ReportType, Boolean> function);

	/**
	 * Processes a usage type. If the type meets the eligibility requirements
	 * the total is incremented, and the type is passed down to its sub-reports.
	 * 
	 * @param usage The usage type to test
	 */
	public void process(ReportUsageType usage);

	/**
	 * Stores the report as JSON string.
	 * 
	 * @return The JSON string representation of this report
	 */
	public String toJSONString();

	/**
	 * Attach a callback method to be invoked each time a record is processed.
	 * The method is only invoked if the eligibility requirements have been
	 * met and after the record has been processed.
	 * 
	 * @param processCallback The method to call back after processing. The first
	 * 	argument is a reference to this report and the second argument is
	 * 	the record being processed.
	 */
	public void setProcessCallback(BiConsumer<ReportType<T>, ReportUsageType> processCallback);
}