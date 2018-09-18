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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.i4one.base.core.Utils;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * @author Hamid Badiozamani
 * @param <T>
 */
@JsonTypeName("a")
public class AggregateReport<T extends DataReportType> extends BaseReport<T> implements AggregateReportType<T>
{
	private String displayType;
	private Class classType;
	
	public AggregateReport()
	{
		super();

		initInternal("default", "default");
	}

	public AggregateReport(String name, String type)
	{
		super();

		initInternal(name, type);
	}

	private void initInternal(String displayName, String type)
	{
		if ( Utils.isEmpty(displayName) )
		{
			throw new IllegalArgumentException("Display name cannot be empty");
		}

		newDisplayName(Utils.forceEmptyStr(displayName));
		setDisplayType(type);

		// This is the default title, subclasses might want to
		// override this
		//
		setTitle(getDisplayName());
		classType = null;
	}

	@Override
	protected void initReportInternal()
	{
	}

	@Override
	public AggregateReport cloneReport()
	{
		AggregateReport retVal = new AggregateReport("clone", getDisplayType());

		// The display name the constructor receives gets modified, we just
		// use the pure setter method to reset it properly
		//
		retVal.setTitle(getTitle());
		retVal.setDisplayName(getDisplayName());
		retVal.setClassType(getClassType());

		return retVal;
	}

	@JsonProperty("s")
	public Set<T> getJSONSubReports()
	{
		return super.getSubReports();
	}

	@Override
	public String getDisplayType()
	{
		return displayType;
	}

	@Override
	public void setDisplayType(String displayType)
	{
		this.displayType = displayType;
	}

	@JsonIgnore
	public Class getClassType()
	{
		return classType;
	}

	protected void setClassType(Class classType)
	{
		this.classType = classType;
	}

	@Override
	public boolean newReport(T report)
	{
		if ( classType == null )
		{
			// Attempt to add the report first and if successful, set the class
			//
			if ( super.newReport(report) )
			{
				classType = report.getClass();
				return true;
			}
			else
			{
				return false;
			}
		}
		else if ( report.isClassCompatible(classType) )
		{
			return super.newReport(report);
		}
		else
		{
			getLogger().trace("Ignoring newReport request for " + this + " since " + classType + " is not equal to " + report.getClass());
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		int hash = Objects.hash(getTitle(), getClassType());
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		return super.equals(o);
	}

	@Override
	public boolean isSameReport(ReportType<T> report)
	{
		if ( this == report )
		{
			return true;
		}
		else if ( getClass() == report.getClass() )
		{
			// If either the titles are the same or the aggregate reports are the same we regard the two reports to be the same.
			//
			if ( getSubReports().isEmpty() )
			{
				// Empty aggregates are regarded as different if they don't have any sub-reports but do have different titles
				//
				return getTitle().equals(report.getTitle());
			}
			else
			{
				//return getTitle().equals(report.getTitle()) || aggregateComparisonSatisfies((AggregateReport) report, (selfSub, rightSub) -> { return selfSub.isSameReport(rightSub); } );
				AggregateReport right = (AggregateReport)report;
				return getTitle().equals(report.getTitle()) && getClassType() == right.getClassType();
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	protected boolean isExclusiveToInternal(ReportType right)
	{
		if ( isClassCompatible(right.getClass()) )
		{
			// If all of our subreports are exclusive to the right's aggregates, we are exclusive
			//
			return aggregateComparisonSatisfies((AggregateReport) right, (selfSub, rightSub) -> { return selfSub.isExclusiveTo(rightSub); } );
		}
		else
		{
			return false;
		}
	}

	protected boolean aggregateComparisonSatisfies(AggregateReport rightAggregate, BiPredicate<ReportType,ReportType> op)
	{
		if ( getSubReports().size() == rightAggregate.getSubReports().size() )
		{
			Iterator<T> selfIt = getSubReports().iterator();
			Iterator<ReportType> rightIt = rightAggregate.getSubReports().iterator();

			while ( selfIt.hasNext() )
			{
				ReportType selfSubReport = selfIt.next();
				ReportType rightSubReport = rightIt.next();
				if ( !op.test(selfSubReport, rightSubReport) )
				{
					return false;
				}
			}

			// All of our sub-reports were exclusive to their counterparts
			// which means we're exclusive to the incoming aggregate report
			//
			return true;
		}

		return false;
	}
}