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
package com.i4one.base.model.report.classifier.highcharts;

import com.i4one.base.core.Base;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.report.classifier.display.ClassificationDisplay;
import com.i4one.base.model.report.classifier.display.DataPoint;
import com.i4one.base.web.controller.Model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public class HighChartsClassificationDisplay<T extends Object> extends BaseLoggable
{
	private final ClassificationDisplay<T, Subclassification<T>> display;
	private final Set<Subclassification<T>> lineage;
	private final Model model;

	private final Map<String, Object> title;
	private final Map<String, Object> chart;
	private final Map<String, Object> xAxis;
	private final List<Map<String, Object>> series;

	/**
	 * Constructs at the root node.
	 * 
	 * @param display The classification to display (i.e. "Genders")
	 * @param model The model object for the request being serviced
	 */
	public HighChartsClassificationDisplay(ClassificationDisplay<T,Subclassification<T>> display, Model model)
	{
		this.display = display;
		this.lineage = Collections.emptySet();
		this.model = model;

		chart = new HashMap<>();
		xAxis = new HashMap<>();
		title = new HashMap<>();
		series = new ArrayList<>();

		init();
	}

	/**
	 * Constructs at the given point in the lineage. The lineage is prepended
	 * to all subclassifications when retrieving data. For example, the lineage
	 * might be "gender#m|age#18-24" in which case all data for subclassifications
	 * that fall under those would be displayed.
	 * 
	 * @param lineage The subclass lineage chain
	 * @param display The specific classification to display (i.e. "ZIP Codes")
	 * @param model The model object for the request being serviced
	 */
	public HighChartsClassificationDisplay(Set<Subclassification<T>> lineage,
		ClassificationDisplay<T, Subclassification<T>> display,
		Model model)
	{
		this.display = display;
		this.lineage = lineage;
		this.model = model;

		chart = new HashMap<>();
		xAxis = new HashMap<>();
		title = new HashMap<>();
		series = new ArrayList<>();

		init();
	}

	private void init()
	{
		chart.put("type", display.getDisplayType());
		xAxis.put("categories", display.getClassification().getSubclassifications()
			.stream()
			.map( (subclass) ->
			{
				if ( subclass.getTitle().isBlank() )
				{
					return model.buildMessage("msg.classification." + subclass.getUniqueID(), "subclassification", subclass);
				}
				else
				{
					return subclass.getTitle().get(model.getLanguage());
				}
			})
			.collect(Collectors.toCollection(LinkedHashSet::new)));

		if ( display.getTitle().isBlank() )
		{
			title.put("text", model.buildMessage("msg.classification." + display.getKey(), "classification", display.getClassification()));
		}
		else
		{
			title.put("text", display.getTitle().get(model.getLanguage()));
		}

		// Typically, grouping count is 1 so we just have a single element map.
		// For example,
		//	series = [{"name": "gender", "data": []}]; or
		//	series = [{"name": "age", "data": []}];
		//
		// But for aggregate displays such as age-gender we would get something like
		// this:
		//	series = [{"name": "m", "data":[]},
		//			{"name":"f", "data":[]},
		//			{"name":"o", "data":[]}]
		//
		for ( int i = 0; i < display.getGroupingCount(); i++ )
		{
			Map<String, Object> dataSeries = new HashMap<>();
			dataSeries.put("name", model.buildMessage("msg.classification." + display.getSeriesName(i), "classification", display.getClassification()));
			dataSeries.put("data", new ArrayList<>());

			series.add(dataSeries);
		}

		int i = 0;
		for ( DataPoint currDp : display.getDataPoints(lineage) )
		{
			List<HighChartsDataPoint> data = (List<HighChartsDataPoint>) series.get(i % display.getGroupingCount()).get("data");
			data.add(new HighChartsDataPoint(currDp, model));

			i++;
		}
	}

	public boolean hasData()
	{
		return display.hasData(lineage);
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

	public Set<Subclassification<T>> getLineage()
	{
		return Collections.unmodifiableSet(lineage);
	}

	public boolean getShowChart()
	{
		return display.getShowChart();
	}

	public String toJSONString() throws IOException
	{
		return Base.getInstance().getJacksonObjectMapper().writeValueAsString(this);
	}
}
