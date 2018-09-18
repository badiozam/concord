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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseDataReport extends BaseReport<AggregateReportType> implements DataReportType
{
	private int id;

	@JsonProperty("s")
	public Set<AggregateReportType> getJSONSubReports()
	{
		// We're saving these reports to a database and the way we display
		// them right now means that only three levels are needed to show
		// a full page of charts. A top-level (or data filtered node),
		// followed by its aggregates, followed by those aggregates data nodes.
		//
		// Therefore, in case we're called to be serialized to JSON format, we
		// can copy the sub reports only two deep (this node would make the third
		// level) and return that collection
		//
		Set<AggregateReportType> jsonSubs = new LinkedHashSet<>();

		for ( AggregateReportType<DataReportType> sub : getSubReports() )
		{
			// First we copy the aggregate
			//
			AggregateReportType<DataReportType> aggregateClone = sub.cloneReport();
			aggregateClone.setTotal(sub.getTotal());

			// Then we copy the internal data nodes
			//
			for ( DataReportType subSub : sub.getSubReports() )
			{
				DataReportType dataClone = subSub.cloneReport();
				dataClone.setTotal(subSub.getTotal());

				aggregateClone.newReport(dataClone);
			}

			jsonSubs.add(aggregateClone);
		}

		return jsonSubs;
	}

	@JsonIgnore
	@Override
	public int getId()
	{
		return id;
	}

	@JsonIgnore
	@Override
	public void setId(int id)
	{
		this.id = id;
	}

}
