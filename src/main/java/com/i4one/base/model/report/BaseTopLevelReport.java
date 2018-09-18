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
import com.i4one.base.core.Utils;
import com.i4one.base.dao.UsageRecordType;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseTopLevelReport extends BaseDataReport implements TopLevelReport
{
	private transient boolean cacheable;

	private transient Calendar startCalendar;
	private transient Calendar endCalendar;

	private transient int startTimestamp;
	private transient int endTimestamp;

	private int lastSer;

	public BaseTopLevelReport()
	{
		super();

		// XXX: This ignores the time zone of the incoming requests when loading
		// off of the database
		initInternal(Calendar.getInstance());
	}

	public BaseTopLevelReport(Calendar calendar)
	{
		super();

		initInternal(calendar);
	}

	@Override
	public String getDisplayType()
	{
		return "listing";
	}

	@Override
	public void setDisplayType(String displayType)
	{
		if ( !displayType.equals("listing"))
		{
			throw new IllegalArgumentException("Invalid display type " + displayType + " for TopLevelReport");
		}
	}

	@Override
	public boolean newReport(AggregateReportType report)
	{
		getLogger().debug("Adding report " + report.getClass());

		return super.newReport(report);
	}

	private void initInternal(Calendar cal)
	{
		Calendar endCalCopy = Calendar.getInstance(cal.getTimeZone());
		Calendar startCalCopy = Calendar.getInstance(cal.getTimeZone());

		// By default the start calendar starts at the beginning of time
		//
		startCalCopy.set(Calendar.YEAR, 1970);
		startCalCopy.set(Calendar.MONTH, 0);
		startCalCopy.set(Calendar.DAY_OF_MONTH, 1);
		startCalCopy.set(Calendar.HOUR_OF_DAY, 0);
		startCalCopy.set(Calendar.MINUTE, 0);
		startCalCopy.set(Calendar.SECOND, 0);
		startCalCopy.set(Calendar.MILLISECOND, 0);


		// By default stop processing records at midnight today in the timezone
		// of the given client
		//
		endCalCopy.setTimeInMillis(cal.getTimeInMillis());
		endCalCopy.set(Calendar.HOUR_OF_DAY, 0);
		endCalCopy.set(Calendar.MINUTE, 0);
		endCalCopy.set(Calendar.SECOND, 0);
		endCalCopy.set(Calendar.MILLISECOND, 0);

		setStartCalendar(startCalCopy);
		setEndCalendar(endCalCopy);

		cacheable = true;
		lastSer = 0;

		newDisplayName("title");

		// This is the default title, subclasses might want to
		// override this
		//
		setTitle(getDisplayName());
	}

	@Override
	public ReportType getParent()
	{
		return null;
	}

	@Override
	public void setParent(ReportType parent)
	{
		if ( parent != null )
		{
			throw new IllegalArgumentException("Top Level reports can't have parents.");
		}
	}

	@Override
	public String getSeriesName()
	{
		return getModelName() + ".seriesName";
	}

	protected void initDemos()
	{
		// The demos might have been set earlier, for example when wanting to attach callbacks for displaying
		// each individual record, the report might be initialized before being run. In either case we 
		// want to ensure the subreports are only populated once in order not to overwrite the references
		// to subreports that might have callbacks attached to them.
		//
		if ( getSubReports().isEmpty() )
		{
			AggregateReport<Demo> genderReport = new AggregateReport<>("gender", "pie");
			genderReport.newReport(new GenderReport("m"));
			genderReport.newReport(new GenderReport("f"));
			genderReport.newReport(new GenderReport("o"));
	
			AggregateReport<Demo> hoursOfDay = new AggregateReport<>("hoursOfDay", "line");
			for (int i = 0; i < 24; i++ )
			{
				hoursOfDay.newReport(new HourOfDayReport((i + 5) % 24, () -> { return getEndCalendar(); } ));
			}
	
			AggregateReport<Demo> daysOfWeek = new AggregateReport<>("daysOfWeek", "bar");
			for (int i = 1; i < 8; i++ )
			{
				daysOfWeek.newReport(new DayOfWeekReport(i, () -> { return getEndCalendar(); } ));
			}
	
			AggregateReport<Demo> ageRanges = new AggregateReport<>("age", "pie");
			ageRanges.newReport(new AgeRangeReport(12, 17, () -> { return getEndCalendar(); } ));
			ageRanges.newReport(new AgeRangeReport(18, 24, () -> { return getEndCalendar(); } ));
			ageRanges.newReport(new AgeRangeReport(25, 34, () -> { return getEndCalendar(); } ));
			ageRanges.newReport(new AgeRangeReport(35, 44, () -> { return getEndCalendar(); } ));
			ageRanges.newReport(new AgeRangeReport(45, 54, () -> { return getEndCalendar(); } ));
			ageRanges.newReport(new AgeRangeReport(55, 64, () -> { return getEndCalendar(); } ));
			ageRanges.newReport(new AgeRangeReport(65, 100, () -> { return getEndCalendar(); } ));

			newReport(genderReport);
			newReport(hoursOfDay);
			newReport(daysOfWeek);
			newReport(ageRanges);

			AggregateReport<Demo> ageGenderReport = new AgeGenderAggregateReport( () -> { return getEndCalendar(); });
			newReport(ageGenderReport);
	
			getLogger().debug("Initialized new standard demos adding permutations");

			// We have to make two passes at this in order to ensure
			// that the first leaf demos get the fleshed out permutations
			// from the last set of demos
			//
			addPermutations(this, 5);
			addPermutations(this, 5);
		}
	}

	@Override
	protected void initReportInternal()
	{
		getLogger().debug("Initializing " + getTitle());

		initDemos();

		getLogger().debug("Done initializng " + getTitle());
		//printSubReports(0, this);
	}

	private void printSubReports(int tabCount, ReportType<ReportType> report)
	{
		if ( tabCount > 7 )
		{
			getLogger().debug("Can't recurse any deeper than 7 levels");
			return;
		}

		int i = 0;
		for ( ReportType currReport : report.getSubReports() )
		{
			i++;
			getLogger().debug(Utils.repeatStr("-", tabCount) + "" + i + ". " + currReport.getTitle());
			printSubReports(tabCount + 1, currReport);
		}

	}

	@Override
	protected boolean isExclusiveToInternal(ReportType report)
	{
		// We regard all top-level reports to be exclusive to each other
		//
		return (report instanceof TopLevelReport);
	}
	
	@Override
	public final void process(UsageRecordType usageRecord)
	{
		ReportUsageType usage = getUsage(usageRecord);

		process(usage);
		lastSer = usageRecord.getSer();
	}

	@Override
	public boolean isEligible(ReportUsageType usage)
	{
		if ( usage.getTimeStampSeconds() >= startTimestamp && usage.getTimeStampSeconds() <= endTimestamp )
		{
			return super.isEligible(usage);
		}
		else
		{
			getLogger().debug("Record " + usage.getDelegate() + " is outside of the processing time interval " + startTimestamp + " - " + endTimestamp);
			return false;
		}
	}

	@Override
	public final Calendar getEndCalendar()
	{
		return endCalendar;
	}

	public final void setEndTimeSeconds(int endTimeSeconds)
	{
		if ( isProcessingStarted() )
		{
			throw new IllegalArgumentException("Can't set a new calendar after processing has started.");
		}
		else
		{
			endCalendar.setTimeInMillis(endTimeSeconds * 1000l);
			endTimestamp = endTimeSeconds;
		}
	}

	@Override
	public final void setEndCalendar(Calendar calendar)
	{
		if ( isProcessingStarted() )
		{
			throw new IllegalArgumentException("Can't set a new calendar after processing has started.");
		}
		else
		{
			this.endCalendar = calendar;

			// Calculate the new cut-off
			//
			endTimestamp = (int)(endCalendar.getTimeInMillis() / 1000l);
		}
	}

	@Override
	public Calendar getStartCalendar()
	{
		return startCalendar;
	}

	public void setStartTimeSeconds(int startTimeSeconds)
	{
		if ( isProcessingStarted() )
		{
			throw new IllegalArgumentException("Can't set a new calendar after processing has started.");
		}
		else
		{
			startCalendar.setTimeInMillis(startTimeSeconds * 1000l);
			startTimestamp = startTimeSeconds;
		}
	}

	@Override
	public void setStartCalendar(Calendar calendar)
	{
		if ( isProcessingStarted() )
		{
			throw new IllegalArgumentException("Can't set a new calendar after processing has started.");
		}
		else
		{
			this.startCalendar = calendar;

			// Calculate the new cut-off
			//
			startTimestamp = (int)(startCalendar.getTimeInMillis() / 1000l);
		}
	}

	public void addPermutations(ReportType report, int depth)
	{
		if ( depth == 0 )
		{
			return;
		}

		// Go all the way down the tree and work up adding references. Since we're
		// modifying the sub-reports of this report as we add the permutations we
		// need to save the original list. Furthermore, the list's order has to
		// be preserved since recreating IDs depends on it.
		//
		for ( Object subReport : report.getSubReports() )
		{
			addPermutations((ReportType) subReport, depth - 1);
		}

		getLogger().debug(report.getTitle() + " - Adding all permutations at depth " + depth);

		ReportType currParent = report.getParent();
		while ( currParent != null )
		{
			// Need to save the current sub report list because we'll be adding to it
			//
			Set<ReportType> currParentSubReports = new LinkedHashSet<>();
			currParentSubReports.addAll(currParent.getSubReports());

			getLogger().debug(report.getTitle() + " - Adding sub-reports of parent " + currParent.getTitle());

			if ( report instanceof TopLevelReport || report instanceof DataReportType )
			{
				for ( ReportType parentSubReport : currParentSubReports )
				{
					if ( parentSubReport instanceof AggregateReportType )
					{
						report.addReport(parentSubReport);
					}
				}
			}
			else if ( report instanceof AggregateReportType )
			{
				for ( ReportType parentSubReport : currParentSubReports )
				{
					if ( parentSubReport instanceof DataReportType )
					{
						report.addReport(parentSubReport);
					}
				}
			}

			getLogger().debug(report.getTitle() + " - Done adding sub-reports of parent " + currParent.getTitle());

			currParent = currParent.getParent();
		}

		getLogger().debug(report.getTitle() + " - Done adding all permutations at depth " + depth);
	}

	@JsonIgnore
	@Override
	public boolean isCacheable()
	{
		return cacheable;
	}

	@Override
	public void setCacheable(boolean cacheable)
	{
		this.cacheable = cacheable;
	}

	@JsonProperty("lastSer")
	@Override
	public int getLastSer()
	{
		return lastSer;
	}

	@JsonProperty("lastSer")
	public void setLastSer(int lastSer)
	{
		this.lastSer = lastSer;
	}
}