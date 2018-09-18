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

import com.i4one.base.core.Base;
import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.BaseSimpleManager;
import java.io.IOException;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("base.ReportManager")
public class SimpleReportManager extends BaseSimpleManager<ReportRecord, Report> implements ReportManager
{
	@Override
	public Set<Report> getReportsByTitle(String title)
	{
		return convertDelegates(getDao().getReportsByTitle(title));
	}

	@Override
	public Report getReport(String title, int starttm, int endtm)
	{
		ReportRecord record = getDao().getReport(title, starttm, endtm);
		if ( record != null )
		{
			Report retVal = new Report(record);
			initModelObject(retVal);

			return retVal;
		}
		else
		{
			return new Report();
		}
	}

	@Override
	public Report getSubReport(String title, Report parent)
	{
		return getSubReport(title, parent.getSer());
	}

	protected Report getSubReport(String title, int parentid)
	{
		ReportRecord record = getDao().getSubReport(title, parentid);
		if ( record != null )
		{
			Report retVal = new Report(record);
			initModelObject(retVal);

			return retVal;
		}
		else
		{
			return new Report();
		}
	}

	@Override
	public void saveReport(TopLevelReport topLevelReport)
	{
		if ( topLevelReport.isCacheable() )
		{
			Report report = getReportFromTopLevelReport(topLevelReport);
			
			// We should be saving TopLevelReports instead of ManagerReports so that the cutoff times
			// can be properly set and queried for trending purposes. Furtheremore, it reduces overhead
			// since you don't have to load all of the subreports of a manager class
	
			report.setTitle(topLevelReport.getTitle());
			report.setTimeStampSeconds(Utils.currentTimeSeconds());
			report.setStartTimeSeconds((int)(topLevelReport.getStartCalendar().getTimeInMillis() / 1000l));
			report.setEndTimeSeconds((int)(topLevelReport.getEndCalendar().getTimeInMillis() / 1000l));
			report.setProperties(topLevelReport.toJSONString());
			report.setTotal(topLevelReport.getTotal());
	
			// NOTE: We can only assume that the last serial number method works if we know
			// for a fact that records that have already been processed cannot be updated
			// or deleted.
			//
			report.setLastSer(topLevelReport.getLastSer());
	
			if ( report.exists() )
			{
				getDao().updateBySer(report.getDelegate());
			}
			else
			{
				getDao().insert(report.getDelegate());
			}
			topLevelReport.setId(report.getSer());

			// NullTopLevelReports save all of their children in the properties tag
			//
			if ( !(topLevelReport instanceof NullTopLevelReport ) )
			{
				// At this point, the TopLevelReport is stored one level deep. That is, the JSON representation
				// is TopLevelReport -> AggregateReports 1 .. n -> DataReports 1 .. m, which means we need
				// to drill down to each level and store the JSONs for each data level one at a time.
				//
				saveSubReports(report, topLevelReport);
			}
		}
	}

	protected void saveSubReports(Report parent, DataReportType dataReport)
	{
		for ( AggregateReportType<?> aggregateReport : dataReport.getSubReports() )
		{
			for ( DataReportType currDataReport : aggregateReport.getSubReports() )
			{
				if ( !currDataReport.getSubReports().isEmpty() )
				{
					getLogger().trace("Saving sub report {} w/ chain {} and parent {} ", currDataReport.getTitle(), currDataReport.getTitleChain(), parent.getSer());
	
					Report report = new Report();
	
					report.setTitle(currDataReport.getTitleChain());
					report.setProperties(currDataReport.toJSONString());
					report.setTotal(currDataReport.getTotal());
	
					// Lot of duplication here, consider moving the subreports
					// to their own table
					//
					report.setTimeStampSeconds(parent.getTimeStampSeconds());
					report.setStartTimeSeconds(parent.getStartTimeSeconds());
					report.setEndTimeSeconds(parent.getEndTimeSeconds());
					report.setLastSer(parent.getLastSer());
	
					report.setParent(parent);
	
					getDao().insert(report.getDelegate());
					getLogger().trace("Done saving sub report {} w/ ser = {} and title = {}", dataReport.getTitleChain(), report.getSer(), report.getTitle());
	
					saveSubReports(report, currDataReport);
				}
				else
				{
					getLogger().trace("No need to save {} since it contains no sub reports and its count is stored in the properties of its parent", currDataReport.getTitleChain());
				}
			}
		}

		getLogger().trace("Done processing children for {}", dataReport.getTitleChain());
	}

	@Override
	public TopLevelReport loadReport(TopLevelReport topLevelReport)
	{
		Report report = getReportFromTopLevelReport(topLevelReport);
		if ( report.exists() )
		{
			TopLevelReport retVal;
			try
			{
				retVal = Base.getInstance().getJacksonObjectMapper().readValue(report.getProperties(), TopLevelReport.class);
				retVal.setStartCalendar(topLevelReport.getStartCalendar());
				retVal.setEndCalendar(topLevelReport.getEndCalendar());
				retVal.setId(report.getSer());

				return retVal;
			}
			catch (IOException ex)
			{
				throw new Errors(getInterfaceName() + ".loadReport", new ErrorMessage("msg." + getInterfaceName() + ".loadReport.parseError", "Could not parse the report from cache: $item.title", new Object[] { "item", report }, ex));
			}
		}
		else
		{
			//throw new Errors(getInterfaceName() + ".loadReport", new ErrorMessage("msg." + getInterfaceName() + ".loadReport.notfound", "Could not find any report with the name: $title", new Object[] { "title", title }));
			return new NullTopLevelReport();
		}
	}

	@Override
	public DataReportType loadSubReport(TopLevelReport parent, String titleChain)
	{
		Report report = getSubReport(titleChain, parent.getId());
		if ( report.exists() )
		{
			DataReportType retVal;
			try
			{
				retVal = Base.getInstance().getJacksonObjectMapper().readValue(report.getProperties(), DataReportType.class);
				retVal.setId(report.getSer());

				return retVal;
			}
			catch (IOException ex)
			{
				throw new Errors(getInterfaceName() + ".loadSubReport", new ErrorMessage("msg." + getInterfaceName() + ".loadSubReport.parseError", "Could not parse subreport from cache: $item.title", new Object[] { "item", report }, ex));
			}
		}
		else
		{
			return new NullTopLevelReport();
		}
	}

	@Override
	public void populateReport(TopLevelReport report)
	{
		TopLevelReport defReport = new NullTopLevelReport();

		Report dbReport = getReportFromTopLevelReport(defReport);
		if ( !dbReport.exists() )
		{
			defReport.initReport();
			defReport.setCacheable(true);

			saveReport(defReport);
		}
		else
		{
			try
			{
				defReport = Base.getInstance().getJacksonObjectMapper().readValue(dbReport.getProperties(), TopLevelReport.class);
			}
			catch (IOException ex)
			{
				throw new Errors(getInterfaceName() + ".populateReport", new ErrorMessage("msg." + getInterfaceName() + ".populateReport.parseError", "Could not parse the report from cache: $item.title", new Object[] { "item", report }, ex));
			}
		}

		report.setSubReports(defReport.getSubReports());
		report.forAllProgeny( (sub) ->
		{
			if ( sub instanceof BaseCalendarDemo )
			{
				BaseCalendarDemo calendarDemo = (BaseCalendarDemo)sub;
				calendarDemo.setCalendarSupplier( () -> { return report.getEndCalendar(); } );
			}

			return true;
		});
	}

	private Report getReportFromTopLevelReport(TopLevelReport topLevelReport)
	{
		if (topLevelReport.isCacheable())
		{
			return getReport(
				topLevelReport.getTitle(),
				(int)(topLevelReport.getStartCalendar().getTimeInMillis() / 1000l),
				(int)(topLevelReport.getEndCalendar().getTimeInMillis() / 1000l));
		}
		else
		{
			getLogger().debug("Top level report is not cacheable returning empty report");
			return new Report();
		}
	}

	@Override
	public Report emptyInstance()
	{
		return new Report();
	}

	@Override
	public ReportRecordDao getDao()
	{
		return (ReportRecordDao) super.getDao();
	}
}