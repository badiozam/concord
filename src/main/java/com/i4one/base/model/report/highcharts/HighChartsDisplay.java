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
package com.i4one.base.model.report.highcharts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.model.report.AggregateReportType;
import com.i4one.base.model.report.DataReportType;
import com.i4one.base.model.report.ReportType;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.web.controller.Model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for displaying a HighCharts compatible HTML block
 * given a DataReportType.
 * 
 * @author Hamid Badiozamani
 */
public class HighChartsDisplay extends HighChartsReport
{
	private final List<HighChartsAggregateReport> subReports;

	public HighChartsDisplay(DataReportType report, Model model)
	{
		super(report, model);

		subReports = new ArrayList<>();
		for ( AggregateReportType subReport : report.getSubReports() )
		{
			subReports.add(new HighChartsAggregateReport(subReport, model));
		}
	}

	public String getTitle()
	{
		if ( getReport() instanceof TopLevelReport )
		{
			return buildMessage(getReport(), getReport().getDisplayName());
		}
		else
		{
			// We search for the parent since the message being built relies on the
			// top level report
			//
			ReportType parent = getReport().getParent();
			while ( !(parent instanceof TopLevelReport) )
			{
				parent = parent.getParent();
			}

			return buildMessage(parent, getReport().getDisplayName());
		}
	}

	public String getSubTitle()
	{
		if ( getReport() instanceof TopLevelReport )
		{
			return "";
		}
		else
		{
			//return getModel().buildMessage("msg.base." + getReport().getClass().getSimpleName() + "." + getReport().getTitle(), "item", getReport());
			return buildMessage(getReport(), getReport().getDisplayName());
		}
	}

	public List<HighChartsAggregateReport> getSubReports()
	{
		return Collections.unmodifiableList(subReports);
	}

	@JsonIgnore
	public String getHTML() throws IOException
	{
		StringBuilder retVal = new StringBuilder();

		int i = 0;
		for ( HighChartsAggregateReport report : subReports )
		{
			i++;
			String idName = "chart" + i;
			retVal.append("<script>$(function () {    $('#").append(idName).append("').highcharts(");
				retVal.append(report.toJSONString());
			retVal.append("); });</script>\n");
			retVal.append("<div id='").append(idName).append("'></div>\n");
		}

		return retVal.toString();
	}
}
