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

import com.i4one.base.core.Utils;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.report.classifier.classifications.AgeClassification;
import com.i4one.base.model.report.classifier.classifications.GenderClassification;
import com.i4one.base.model.report.classifier.display.ClassificationDisplay;
import com.i4one.base.model.report.classifier.display.GenderAgeClassificationDisplay;
import com.i4one.base.web.controller.Model;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public class HighChartsClassificationReportDisplay<U extends RecordType, T extends Object>
{
	private final ClassificationReport<U, T> report;
	private final Set<Subclassification<T>> lineage;
	private final Model model;

	public HighChartsClassificationReportDisplay(ClassificationReport<U, T> report, Set<Subclassification<T>> lineage, Model model)
	{
		this.model = model;
		this.report = report;
		this. lineage = lineage;
	}

	public String getTitle()
	{
		return model.buildMessage("msg.classification." + report.getTitle() + ".title", "report", report);
	}

	public String getSubTitle()
	{
		return model.buildMessage("msg.classification." + report.getTitle() + ".subtitle", "report", report);
	}

	public String getHTML() throws IOException
	{
		StringBuilder retVal = new StringBuilder();

		int i = 0;
		for ( HighChartsClassificationDisplay display : getHighChartsClassificationDisplays() )
		{
			i++;
			String idName = "chart" + i;
			retVal.append("<script>$(function () {    $('#").append(idName).append("').highcharts(");
				retVal.append(display.toJSONString());
			retVal.append("); });</script>\n");
			retVal.append("<div class='chart' id='").append(idName).append("'></div>\n");

		}

		return retVal.toString();
	}

	public Set<HighChartsClassificationDisplay> getHighChartsClassificationDisplays()
	{
		return getClassificationDisplays()
			.stream()
			.map( (display) -> { return getHighChartsClassificationDisplay(display); })
			.collect(Collectors.toSet());
	}

	public HighChartsClassificationDisplay getHighChartsClassificationDisplay(ClassificationDisplay<T, ? extends Subclassification<T>> display)
	{
		return new HighChartsClassificationDisplay(lineage, display, model);
	}

	/**
	 * Converts the set of classifications included in the report to displays.
	 * 
	 * @return The set of classification displays to present to the user.
	 */
	public Set<ClassificationDisplay<T, ? extends Subclassification<T>>> getClassificationDisplays()
	{
		GenderClassification genderClass = null;
		AgeClassification ageClass = null;

		Set<ClassificationDisplay<T,? extends Subclassification<T>>> retVal = new LinkedHashSet<>();
		for ( Classification<T, ? extends Subclassification<T>> classification : report.getClassifications() )
		{
			ClassificationDisplay<T, ? extends Subclassification<T>> classDisplay = classification.getDefaultDisplay(report);
			retVal.add(classDisplay);
				
			if ( classification instanceof GenderClassification )
			{
				genderClass = (GenderClassification) classification;
			}
			else if ( classification instanceof AgeClassification )
			{
				ageClass = (AgeClassification) classification;
			}
		}

		// Adding the age-gender combination only if both age and gender exist 
		//
		if ( ageClass != null && genderClass != null )
		{
			ClassificationDisplay<T, ? extends Subclassification<T>> display = new GenderAgeClassificationDisplay(genderClass, ageClass, report);
			retVal.add(display);
		}

		return retVal;
	}

	public String getLineageKey()
	{
		return Utils.toCSV(lineage);
	}

	public Set<Subclassification<T>> getLineage()
	{
		return Collections.unmodifiableSet(lineage);
	}
}
