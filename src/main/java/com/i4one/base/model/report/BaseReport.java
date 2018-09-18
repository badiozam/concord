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
import com.i4one.base.core.Base;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Hamid Badiozamani
 * @param <R>
 */
public abstract class BaseReport<R extends ReportType> extends BaseLoggable implements ReportType<R>
{
	private Set<R> subReports;

	private int total;
	private String displayName;
	private String title;

	private transient ReportType parent;
	private transient boolean processingStarted;

	private transient BiConsumer<ReportType<R>, ReportUsageType> processCallback;

	public BaseReport()
	{
		subReports = new LinkedHashSet<>();
		processingStarted = false;
	}

	@JsonIgnore
	@Override
	public String getSchemaName()
	{
		return "base";
	}

	@JsonIgnore
	@Override
	public String getModelName()
	{
		return getSchemaName() + "." + getClass().getSimpleName();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return cloneReport();
	}

	@Override
	public final boolean isExclusiveTo(ReportType right)
	{
		return isExclusiveToInternal(right);
	}

	protected abstract boolean isExclusiveToInternal(ReportType right);

	protected abstract void initReportInternal();

	public void init()
	{
		// Initialize all of the sub-reports as well
		//
		if ( subReports == null )
		{
			subReports = new LinkedHashSet<>();
		}
	}

	@Override
	public final void initReport()
	{
		// Create all of the subclass specific sub-reports
		//
		initReportInternal();

		for ( ReportType report : subReports )
		{
			report.initReport();
		}

		// This flag indicates that any changes to the state of the report
		// will throw IllegalArgumentExceptions
		//
		processingStarted = true;


		// Remove any unnecessary branches. This has to be the last call
		// we make because sub reports may alter their behavior after init
		//
		//prune();
	}

	@Override
	public void prune()
	{
		getLogger().trace("Pruning " + this.getTitle());
		Iterator<R> it = subReports.iterator();

		while ( it.hasNext() )
		{
			boolean pruneSubReports = true;

			ReportType subReport = it.next();
			ReportType currParent = this;
			while ( currParent != null )
			{
				if ( currParent.isExclusiveTo(subReport) || currParent.equals(subReport) ||
					subReport.isExclusiveTo(currParent) || subReport.equals(currParent))
				{
					getLogger().debug("Removing " + subReport.getTitle() + " from " + this.getTitle() + " since parent " + currParent.getTitle() + " is either exlusive to or equal to it.");

					it.remove();
					pruneSubReports = false;

					break;
				}
				else
				{
					currParent = currParent.getParent();
				}
			}

			// We only prune this branch if it wasn't already disconnected
			//
			if ( pruneSubReports )
			{
				subReport.prune();
			}
		}

		getLogger().trace("Done pruning " + this.getTitle());
	}

	@JsonProperty("t")
	public String getJSONTitle()
	{
		// This method is here to allow subclasses to override whether
		// this member is serialized through JSON while allowing the
		// getTitle() method to remain final
		//
		return getTitle();
	}

	@Override
	public final String getTitle()
	{
		return title;
	}

	@JsonProperty("t")
	public final void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Sets the display name to a standard format of
	 * &lt;model name&gt;.&lt;displayName&gt;.
	 * 
	 * @param displayName The new display name to append and set
	 */
	public final void newDisplayName(String displayName)
	{
		this.displayName = getModelName() + "." + displayName;
	}

	@JsonProperty("n")
	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	@JsonProperty("n")
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	@JsonProperty("d")
	@Override
	public abstract String getDisplayType();

	@JsonProperty("d")
	public void setDisplayType(String displayType)
	{
	}

	@JsonIgnore
	@Override
	public String getSeriesName()
	{
		return (getParent() != null ? getParent().getSeriesName() : getDisplayName());
	}

	@Override
	public String getSeriesName(int i)
	{
		return getSeriesName();
	}

	@JsonIgnore
	@Override
	public int getGroupingCount()
	{
		return 1;
	}

	@JsonProperty("c")
	@Override
	public final int getTotal()
	{
		return total;
	}

	@JsonProperty("c")
	@Override
	public final void setTotal(int total)
	{
		this.total = total;
	}

	@Override
	public ReportType getParent()
	{
		return parent;
	}

	@JsonIgnore
	@Override
	public void setParent(ReportType parent)
	{
		this.parent = parent;
	}

	@JsonIgnore
	@Override
	public String getTitleChain()
	{
		return Utils.toCSV(getAncestry(), ReportType::getTitle);
	}

	@JsonIgnore
	@Override
	public String getDisplayNameChain()
	{
		return Utils.toCSV(getAncestry(), ReportType::getDisplayName);
	}

	@JsonIgnore
	@Override
	public Stack<ReportType> getAncestry()
	{
		Stack<ReportType> ancestors = new Stack<>();
		ReportType ancestor = this;
		while (ancestor != null )
		{
			ancestors.push(ancestor);
			ancestor = ancestor.getParent();
		}

		return ancestors;
	}

	@Override
	public ReportType findReport(String titleChain)
	{
		getLogger().debug("Searching for titleChain " + titleChain + " in " + getTitle());

		String currTitle = getTitle();
		if ( currTitle.equals(titleChain) )
		{
			getLogger().debug(titleChain + " matches our title!");

			// Direct match
			//
			return this;
		}
		else if ( titleChain.startsWith(currTitle) && titleChain.contains(","))
		{
			// This is a BFS algorithm based off of currTitle strings with O(log_m n)
			// where m is the maximum number of elements per level:
			//
			// e.g. (w/ x,o = searched, x = match, and * = skipped)
			//
			//                          x
			//                        / | \
			//                       o  o  x
			//                     / | / \ | \
			//                    *  **   *o  x
			//
			//
			// The input chain starts with our report which means somewhere
			// in our subreports, we may find the chain with the exact currTitle
			// match. We split off everything after the input titleChains
			// portion that matches our currTitle and continue the search for
			// subreports. For example, if our currTitle is "title1" and
			// the displayNameChain is "title1,title2,title4" then we end up with
			//
			//	title1,title2,title4 => title2,title4
			//
			// And we look in our subreports to match "title2,title4". If none
			// of our subreports are "title2" we know we don't have it
			//
			String[] titles = titleChain.split(",", 2);
			ReportType retVal = null;

			getLogger().debug("Testing subreports " + Utils.toCSV(subReports));
			for ( R report : subReports )
			{
				retVal = report.findReport(titles[1]);
				if ( retVal != null )
				{
					getLogger().debug("We're a match for " + titleChain + "!");
					break;
				}
			}

			return retVal;
		}
		else
		{
			getLogger().debug(titleChain + " does not match our title " + currTitle );
			return null;
		}
	}

	@JsonIgnore
	@Override
	public Set<R> getSubReports()
	{
		return Collections.unmodifiableSet(subReports);
	}

	@JsonProperty("s")
	@Override
	public void setSubReports(Collection<R> reports)
	{
		if ( isProcessingStarted() )
		{
			throw new IllegalArgumentException("Can't set subreports after processing has started.");
		}
		else
		{
			subReports.clear();
			for ( R report : reports )
			{
				newReport(report);
			}
		}
	}

	@Override
	public boolean newReport(R report)
	{
		if ( isProcessingStarted() )
		{
			throw new IllegalArgumentException("Can't set subreports after processing has started.");
		}
		else
		{
			if ( isSameReport(report) || report.isSameReport(this) )
			{
				getLogger().trace("newReport: Skipping the addition of {} to {}  due to equality", report.getTitle(), this.getTitle());
				return false;
			}
			else if ( isExclusiveTo(report) || report.isExclusiveTo(this) )
			{
				getLogger().trace("newReport: Skipping the addition of {} to {} due to exclusive sets", report.getTitle(), this.getTitle());
				return false;
			}
			else if ( existsInLineage(report) )
			{
				getLogger().trace("newReport: Skipping the addition of {} to {} due to existence in the call chain", report.getTitle(), this.getTitle());
				return false;
			}
			else if ( existsInSubReports(report ))
			{
				getLogger().trace("newReport: Skipping the addition of {} to {} due to existence in the sub reports", report.getTitle(), this.getTitle());
				return false;
			}
			else
			{
				report.setParent(this);
				boolean retVal = subReports.add(report);
	
				if ( !retVal )
				{
					getLogger().trace("newReport: Did not add duplicate report " + report);
				}
				else
				{
					getLogger().trace("newReport: Successful: {} is neither exclusive to {} nor does it exists in its subreports", report.getTitle(), this.getTitle());
				}
	
				return retVal;
			}
		}
	}

	@Override
	public boolean removeReport(R report)
	{
		report.setParent(null);
		return subReports.remove(report);
	}

	@Override
	public boolean isSameReport(ReportType<R> report)
	{
		if ( this == report )
		{
			return true;
		}
		else if ( getClass() == report.getClass() )
		{
			return report.getTitle().equals(getTitle());
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		int hash = Objects.hashCode(this.title);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if ( getClass() != obj.getClass() )
		{
			return false;
		}
		final ReportType other = (ReportType) obj;
		return isSameReport(other);
	}

	@Override
	public void addReport(R report)
	{
		if ( isProcessingStarted() )
		{
			throw new IllegalArgumentException("Can't set subreports after processing has started.");
		}
		else
		{
			getLogger().trace("addReport: Adding " + report.getTitle() + " to " + this.getTitle());
	
			// We can't be sure if the incoming subReport is being used somewhere else or not
			// so we clone it and all of its sub-children
			//
			R cloned = (R) report.cloneReport();
	
			getLogger().trace("addReport: Adding cloned " + cloned.getTitle() + " to " + this.getTitle());
	
			if ( newReport(cloned) )
			{
				// Clone all of the incoming parameter's child reports as well
				//
				report.getSubReports().forEach( (subReport) -> { cloned.addReport((ReportType)subReport); });
			}
	
			getLogger().trace("addReport: Done adding " + report.getTitle() + " as "+ cloned.getTitle() + " to " + this.getTitle());
		}
	}

	private String lineage()
	{
		StringBuilder lineageStr = new StringBuilder();

		if ( getParent() instanceof BaseReport )
		{
			lineageStr.append(getParent() == null ? "" : ((BaseReport)getParent()).lineage()).append(" -> ");
		}
		else
		{
			lineageStr.append("Non-BaseReport parent -> ");
		}

		lineageStr.append(this.getTitle());

		return lineageStr.toString();
	}

	protected boolean existsInLineage(ReportType report)
	{
		ReportType currReport = this.getParent();
		while ( currReport != null )
		{
			getLogger().trace("Testing " + report.getTitle() + " ancestor " + currReport.getTitle() );

			// It's important that the incoming parameter subReport test equality and exclusivity
			// since it could be a ReferenceReport which would delegate its behavior
			//
			if ( currReport.isSameReport(report) || report.isSameReport(currReport) )
			{
				getLogger().trace(report.getTitle() + " is the same report as " + currReport.getTitle() );
				return true;
			}
			else if ( report.isExclusiveTo(currReport) || report.isExclusiveTo(currReport) )
			{
				getLogger().trace(report.getTitle() + " is exclusive to " + currReport.getTitle() );
				return true;
			}

			currReport = currReport.getParent();
		}

		return false;
	}

	protected boolean existsInSubReports(R report)
	{
		for ( ReportType currReport : subReports)
		{
			if ( currReport.isSameReport(report))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasReport(R report)
	{
		if ( subReports.contains(report))
		{
			return true;
		}
		else
		{
			return (subReports.stream().anyMatch((currReport) -> ( currReport.hasReport(report))));
		}
	}

	@Override
	public boolean isClassCompatible(Class clazz)
	{
		getLogger().trace("Testing whether " + clazz + " is a super-class (or the same as) " + getClass() + ": " + clazz.isAssignableFrom(getClass()));

		// If the given class is a super-class (or the same class) as us,
		// then we're compatible
		//
		return clazz.isAssignableFrom(getClass());
	}

	@Override
	public void clear()
	{
		setTotal(0);
		subReports.stream().forEach( (report) -> { report.clear(); });
	}

	@Override
	public boolean isEligible(ReportUsageType usage)
	{
		return true;
	}

	@Override
	public final void forAllProgeny(Function<ReportType, Boolean> function)
	{
		for ( ReportType sub : subReports )
		{
			if (!function.apply(sub) )
			{
				break;
			}
			else
			{
				sub.forAllProgeny(function);
			}
		}
	}

	@Override
	public final void process(ReportUsageType usage)
	{
		// Make sure we at least load the user
		//
		usage.getUser().loadedVersion();

		if ( isEligible(usage) )
		{
			// Increment the total
			//
			total++;

			// Any other stats that need to be updated
			//
			processInternal(usage);

			// Only process to the sub-reports if we have an eligible record
			//
			subReports.stream().forEach( (report) ->
			{
				report.process(usage);
			});
		}
		else
		{
			getLogger().trace("Skipping processing of " + usage + " due to ineligibility");
		}
	}

	protected void processInternal(ReportUsageType usage)
	{
		if ( processCallback != null )
		{
			processCallback.accept(this, usage);
		}
	}

	@JsonIgnore
	public boolean isProcessingStarted()
	{
		return processingStarted;
	}

	@Override
	public String toJSONString()
	{
		try
		{
			return Base.getInstance().getJacksonObjectMapper().writeValueAsString(this);
		}
		catch (IOException ex)
		{
			return ex.getLocalizedMessage();
		}
	}

	@Override
	public String toString()
	{
		return super.toString() + "(" + title + ")";
	}

	@JsonIgnore
	public final BiConsumer<ReportType<R>, ReportUsageType> getProcessCallback()
	{
		return processCallback;
	}

	@Override
	public final void setProcessCallback(BiConsumer<ReportType<R>, ReportUsageType> processCallback)
	{
		this.processCallback = processCallback;
	}
}