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

import com.i4one.base.core.Base;
import com.i4one.base.model.report.AggregateReportType;
import com.i4one.base.model.report.Demo;
import com.i4one.base.web.controller.Model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hamid Badiozamani
 */
public class HighChartsAggregateReport extends HighChartsReport
{
	private final Map<String, Object> title;
	private final Map<String, Object> chart;
	private final Map<String, Object> xAxis;
	private final List<Map<String, Object>> series;

	public HighChartsAggregateReport(AggregateReportType<Demo> report, Model model)
	{
		super(report, model);

		chart = new HashMap<>();
		chart.put("type", report.getDisplayType());

		xAxis = new HashMap<>();
		xAxis.put("type", "category");

		title = new HashMap<>();
		//title.put("text", getModel().buildMessage("msg." + report.getModelName() + "." + report.getDisplayName(), "item", report));
		title.put("text", buildMessage(report, report.getDisplayName())); 

		series = new ArrayList<>();
		for ( int i = 0; i < report.getGroupingCount(); i++ )
		{
			Map<String, Object> dataSeries = new HashMap<>();
			dataSeries.put("name", buildMessage(getReport(), getReport().getSeriesName(i)));
			dataSeries.put("data", new ArrayList<>());

			series.add(dataSeries);
		}

		int i = 0;
		for ( Demo currDemo : report.getSubReports() )
		{
			List<HighChartsDemoReport> data = (List<HighChartsDemoReport>) series.get(i % report.getGroupingCount()).get("data");
			data.add(new HighChartsDemoReport(currDemo, model));

			i++;
		}
	}

	public String getType()
	{
		return "aggregate";
	}

	public Map<String, Object> getChart()
	{
		return chart;
	}

	public Map<String, Object> getTitle()
	{
		return title;
	}

	public Map<String, Object> getxAxis()
	{
		return xAxis;
	}

	public List<Map<String, Object>> getSeries()
	{
		return series;
	}

	public String toJSONString() throws IOException
	{
		return Base.getInstance().getJacksonObjectMapper().writeValueAsString(this);
	}
}
