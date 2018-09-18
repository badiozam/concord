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

import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.report.classifier.display.DataPoint;
import com.i4one.base.web.controller.Model;

/**
 * @author Hamid Badiozamani
 */
public class HighChartsDataPoint<T extends Object, SUB extends Subclassification<T>>
{
	private final DataPoint<T, SUB> dataPoint;
	private final String name;

	public HighChartsDataPoint(DataPoint<T, SUB> dataPoint, Model model)
	{
		if ( dataPoint == null )
		{
			throw new IllegalArgumentException("Data point cannot be a null reference");
		}

		this.dataPoint = dataPoint;
		if ( dataPoint.getTitle().isBlank() )
		{
			this.name = model.buildMessage("msg.classification." + dataPoint.getTitleKey());
		}
		else
		{
			this.name = dataPoint.getTitle().get(model.getLanguage());
		}
	}

	public String getKey()
	{
		// This returns the full lineage
		//
		//return Utils.toCSV(dataPoint.getKey());
		return dataPoint.getTitleKey();
	}

	public String getName()
	{
		return name;
	}

	public int getY()
	{
		return dataPoint.getValue();
	}

}
