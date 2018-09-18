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
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.report.AggregateReport;
import com.i4one.base.model.report.ReportType;
import com.i4one.base.web.controller.Model;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Hamid Badiozamani
 */
public abstract class HighChartsReport extends BaseLoggable
{
	private transient final Model model;
	private transient final ReportType report;
	private transient final List<HighChartsBreadCrumb> breadCrumbs;

	public HighChartsReport(ReportType report, Model model)
	{
		this.report = report;
		this.model = model;

		Stack<ReportType> ancestors = report.getAncestry();

		breadCrumbs = new ArrayList<>();
		while ( !ancestors.isEmpty() )
		{
			ReportType ancestor = ancestors.pop();
			String builtDisplayName = buildMessage(ancestor, ancestor.getDisplayName());

			HighChartsBreadCrumb crumb = new HighChartsBreadCrumb();
			crumb.setValue(builtDisplayName);
			crumb.setCount(ancestor.getTotal());

			if ( ancestor instanceof AggregateReport )
			{
				crumb.setKey("");
			}
			else
			{
				crumb.setKey(ancestor.getTitleChain());
			}

			breadCrumbs.add(crumb);
		}
	}

	public final String buildMessage(ReportType report, String message)
	{
		return getModel().buildMessage("msg." + message, "report", report);
	}

	@JsonIgnore
	public Model getModel()
	{
		return model;
	}

	@JsonIgnore
	public ReportType getReport()
	{
		return report;
	}

	/**
	 * Returns a list of name-value pairs that reflects the ancestry of this
	 * report. The names correspond to the title of the report and the values
	 * correspond to the display-name for each report.
	 * 
	 * @return The ancestry list starting with the root node
	 */
	public List<HighChartsBreadCrumb> getBreadCrumbs()
	{
		return breadCrumbs;
	}

	public String getTitleChain()
	{
		return getReport().getTitleChain();
	}

}
