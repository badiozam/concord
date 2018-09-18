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
package com.i4one.base.web.controller.admin;

import com.i4one.base.core.BlockingSingleItemCollection;
import com.i4one.base.core.Utils;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.UsageType;
import com.i4one.base.model.report.DataReportType;
import com.i4one.base.model.report.NullTopLevelReport;
import com.i4one.base.model.report.ReportManager;
import com.i4one.base.model.report.ReportType;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.report.classifier.highcharts.HighChartsClassificationReportDisplay;
import com.i4one.base.model.report.highcharts.HighChartsDisplay;
import com.i4one.base.web.controller.Model;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Hamid Badiozamani
 */
public class BaseAdminReportViewController extends BaseAdminViewController
{
	private ReportManager reportManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model retVal = super.initRequest(request, modelAttribute);

		// We know we'll be spawning threads in here so we ensure that the
		// request scope is maintained in newly spawned threads
		//
		RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);

		return retVal;
	}
	
	/**
	 * Initializes the report settings model attribute.
	 * 
	 * @param request The incoming request.
	 * @param reportSettings The report settings model object.
	 * @param item The item to set in the report settings.
	 * 
	 * @return The initialized model.
	 */
	protected Model initRequest(HttpServletRequest request, ReportSettings reportSettings, RecordTypeDelegator<?> item)
	{
		Model retVal = initRequest(request, reportSettings);

		// This is set for the view to use for generating where to navigate
		// to when the back button is pressed for terminable items.
		//
		reportSettings.setItem(item);

		return retVal;
	}
	
	@Deprecated
	protected Model report(Model model, String titleChain, TopLevelReport emptyReport, Function<TopLevelReport, TopLevelReport> getReport, HttpServletResponse response) throws IOException
	{
		ReportSettings settings = new ReportSettings();
		settings.setTitleChain(titleChain);

		return report(model, settings, emptyReport, getReport, response);
	}

	/**
	 * Runs a report. If the raw data option is selected, spawns a thread and feeds
	 * data to the model on-demand in order to minimize memory usage 
	 * 
	 * @param model The model to set either the "report" or "data" attribute
	 * @param reportSettings The settings that determine whether to show the raw data or the charts
	 * @param emptyReport An empty instance of the report to populate
	 * @param getReport The method to call that populates the given empty report
	 * @param response The response parameter to initialize before returning the model
	 * 
	 * @return The model with either "data" or "report" depending on the raw data flag.
	 * @throws java.io.IOException If there was an error writing to the response output
	 * 	stream in CSV mode.
	 * 
	 * @deprecated In favor of new Classification reports.
	 */
	protected Model report(Model model,
		ReportSettings reportSettings,
		TopLevelReport emptyReport,
		Function<TopLevelReport, TopLevelReport> getReport,
		HttpServletResponse response) throws IOException
	{
		DataReportType forDisplay;
		if ( reportSettings.isShowRawData() )
		{
			// The initialization can also take place by the DAO running the report.
			// We do it here when we need to attach a callback for displaying
			// each individual record. Note that the report must not be cacheable
			// otherwise the emptyReport and the report we get back from the manager
			// may not be the same
			//
			getReportManager().populateReport(emptyReport);
			emptyReport.setCacheable(false);
	
			forDisplay = findReportFromTitle(emptyReport, reportSettings.getTitleChain());

			if ( reportSettings.isCsv() )
			{
				CSVExportUsageType csvUsageType = reportSettings.getCSVExportUsageType();

				DataOutputStream out = new DataOutputStream(response.getOutputStream());
				out.writeChars(csvUsageType.toCSV(true));

				forDisplay.setProcessCallback((report, row) ->
				{
					// This method will get called for each row that is being processed by the
					// forDisplay report demographic. We simply output it as CSV to the response
					//
					try
					{
						getLogger().debug("Processing record " + row + " for " + report);
						csvUsageType.setDelegate(row);

						out.write('\n');
						out.writeChars(csvUsageType.toCSV(false));
					}
					catch (Exception e)
					{
						getLogger().debug("Exception", e);
					}
				});

				TopLevelReport report = getReport.apply(emptyReport);
			}
			else
			{
				BlockingSingleItemCollection recordBuffer = new BlockingSingleItemCollection();
				forDisplay.setProcessCallback((report, row) ->
				{
					// This method will get called for each row that is being processed by the
					// forDisplay report demographic. By adding that row to the record buffer
					// we can then display it in the view on demand
					//
					try
					{
						getLogger().debug("Processing record " + row + " for " + report);
						recordBuffer.add(row);
					}
					catch (Exception e)
					{
						getLogger().debug("Exception", e);
					}
				});
	
				// We start a new thread to do the data retrieval since it can be very large and
				// as such memory-consuming. As the report is being built, individual records are
				// being added to the recordBuffer. The recordBuffer can block until the next record
				// is consumed by the view, thus opening up space and also block on the view side
				// of things until a record becomes available or there are no more records.
				//
				Runnable rawDataTask = ( () -> 
				{
					try
					{
						getLogger().debug("Beginning report processing");
	
						TopLevelReport report = getReport.apply(emptyReport);
						recordBuffer.close();
	
						getLogger().debug("Report processing completed");
					}
					catch (Exception e)
					{
						getLogger().debug("Exception while getting raw data", e);
					}
				});
	
				Thread rawDataThread = new Thread(rawDataTask);
				rawDataThread.start();
	
				model.put("data", recordBuffer);
			}
		}
		else
		{
			TopLevelReport report = getReport.apply(emptyReport);
			forDisplay = findReportFromTitle(report, reportSettings.getTitleChain());
		}

		model.put("report", new HighChartsDisplay(forDisplay, model));
		return initResponse(model, response, reportSettings);
	}

	/**
	 * Runs a classification report. If the raw data option is selected, spawns a
	 * thread and feeds data to the model on-demand in order to minimize memory usage 
	 * 
	 * @param model The model to set either the "report" or "data" attribute
	 * @param reportSettings The settings that determine whether to show the raw data or the charts
	 * @param report An empty instance of the report to populate
	 * @param getReport The method to call that populates the given empty report
	 * @param response The response parameter to initialize before returning the model
	 * 
	 * @return The model with either "data" or "report" depending on the raw data flag.
	 * @throws java.io.IOException If there was an error writing to the response output
	 * 	stream in CSV mode.
	 */
	protected <U extends RecordType, T extends UsageType<?>> Model classificationReport(Model model,
		ReportSettings reportSettings,
		ClassificationReport<U,T> report,
		Supplier<ClassificationReport<U,T>> getReport,
		HttpServletResponse response) throws IOException
	{
		Set<Subclassification<T>> forDisplay = reportSettings.getLineageSet();
		if ( reportSettings.isShowRawData() )
		{
			getLogger().debug("Showing raw data");

			if ( reportSettings.isCsv() )
			{
				CSVExportUsageType csvUsageType = reportSettings.getCSVExportUsageType();

				DataOutputStream out = new DataOutputStream(response.getOutputStream());
				out.writeChars(csvUsageType.toCSV(true));

				report.setProcessCallback(forDisplay, (row) ->
				{
					// This method will get called for each row that is being processed by the
					// forDisplay report demographic. We simply output it as CSV to the response
					//
					try
					{
						csvUsageType.setDelegate(row);

						out.write('\n');
						out.writeChars(csvUsageType.toCSV(false));
					}
					catch (Exception e)
					{
						getLogger().debug("Exception", e);
					}

					return true;
				});

				// Process the report
				//
				ClassificationReport<U, T> processedReport = getReport.get();
				model.put("display", new HighChartsClassificationReportDisplay(processedReport, forDisplay, model));
			}
			else
			{
				BlockingSingleItemCollection recordBuffer = new BlockingSingleItemCollection();
				report.setProcessCallback(forDisplay, (row) ->
				{
					// This method will get called for each row that is being processed by the
					// forDisplay report demographic. By adding that row to the record buffer
					// we can then display it in the view on demand
					//
					try
					{
						recordBuffer.add(row);
					}
					catch (Exception e)
					{
						getLogger().debug("Exception", e);
					}

					return true;
				});
	
				// We start a new thread to do the data retrieval since it can be very large and
				// as such memory-consuming. As the report is being built, individual records are
				// being added to the recordBuffer. The recordBuffer can block until the next record
				// is consumed by the view, thus opening up space and also block on the view side
				// of things until a record becomes available or there are no more records.
				//
				Runnable rawDataTask = ( () -> 
				{
					try
					{
						getLogger().debug("Beginning report processing");
	
						// Process the report
						//
						ClassificationReport<U, T> processedReport = getReport.get();
						recordBuffer.close();
	
						getLogger().debug("Report processing completed");
					}
					catch (Exception e)
					{
						getLogger().debug("Exception while getting raw data", e);
					}
				});
	
				Thread rawDataThread = new Thread(rawDataTask);
				rawDataThread.start();
	
				model.put("data", recordBuffer);
				model.put("display", new HighChartsClassificationReportDisplay(report, forDisplay, model));
			}
		}
		else
		{
			ClassificationReport<U,T> processedReport = getReport.get();
			model.put("display", new HighChartsClassificationReportDisplay(processedReport, forDisplay, model));
		}

		return initResponse(model, response, reportSettings);
	}

	private DataReportType findReportFromTitle(TopLevelReport parent, String titleChain)
	{
		if ( Utils.isEmpty(titleChain ))
		{
			getLogger().debug("Title chain is empty, returning");
			return parent;
		}
		else
		{
			ReportType sub = parent.findReport(titleChain);

			if ( sub != null &&
				sub.getSubReports().isEmpty() &&		// Has no subreports (i.e. leaf node) and,
				titleChain.contains(",")			// We're actually searching for a subreport
				)
			{
				// We can check the database to see if we found one
				//
				DataReportType subReport = getReportManager().loadSubReport(parent, titleChain);
				if ( !(subReport instanceof NullTopLevelReport ) )
				{
					// XXX: When loading from the database, need to hook up ancestry to get
					// bread crumbs working properly
					//
					return subReport;
				}


				// We need to go up two levels to display a parent parent since this one doesn't contain
				// any data to render
				//
				String parentTitleChain = titleChain.substring(0, titleChain.lastIndexOf(","));
				if ( parentTitleChain.contains(","))
				{
					parentTitleChain = titleChain.substring(0, parentTitleChain.lastIndexOf(","));
					getLogger().debug("Reverting to " + parentTitleChain);

					sub = parent.findReport(parentTitleChain);
				}
				else
				{
					sub = null;
				}
			}

			if ( sub != null )
			{
				getLogger().debug("Found report " + sub + " with subs: " + sub.getSubReports());
				return (DataReportType)sub;
			}
			else
			{
				getLogger().debug("No suitable sub found for " + titleChain + " falling back to parent");
				return parent;
			}
		}
	}

	public ReportManager getReportManager()
	{
		return reportManager;
	}

	@Autowired
	public void setReportManager(ReportManager reportManager)
	{
		this.reportManager = reportManager;
	}
}
