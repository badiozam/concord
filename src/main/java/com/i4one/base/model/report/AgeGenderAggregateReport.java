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

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Calendar;
import java.util.function.Supplier;

/**
 * @author Hamid Badiozamani
 */
@JsonTypeName("aga")
public class AgeGenderAggregateReport extends AggregateReport<Demo>
{
	private Supplier<Calendar> calendarSupplier;
	private GenderReport males;
	private GenderReport females;
	private GenderReport others;

	public AgeGenderAggregateReport()
	{
		super("ageGender", "column");

		initInternal(() -> { return Calendar.getInstance(); });
	}

	public AgeGenderAggregateReport(Supplier<Calendar> calSupplier)
	{
		super("ageGender", "column");

		initInternal(calSupplier);
	}

	private void initInternal(Supplier<Calendar> calSupplier)
	{
		this.calendarSupplier = calSupplier;

		// Needed for series names
		//
		males = new GenderReport("m");
		females = new GenderReport("f");
		others = new GenderReport("o");

	}

	@Override
	protected void initReportInternal()
	{
		super.initReportInternal();

		if ( getSubReports().isEmpty() )
		{
			AggregateReport<GenderReport> genderReport = new AggregateReport<>("gender", "pie");
			genderReport.newReport(males);
			genderReport.newReport(females);
			genderReport.newReport(others);
	
			AggregateReport<AgeRangeReport> ageRanges = new AggregateReport<>("age", "pie");
			ageRanges.newReport(new AgeRangeReport(12, 17, calendarSupplier ));
			ageRanges.newReport(new AgeRangeReport(18, 24, calendarSupplier ));
			ageRanges.newReport(new AgeRangeReport(25, 34, calendarSupplier ));
			ageRanges.newReport(new AgeRangeReport(35, 44, calendarSupplier ));
			ageRanges.newReport(new AgeRangeReport(45, 54, calendarSupplier ));
			ageRanges.newReport(new AgeRangeReport(55, 64, calendarSupplier ));
			ageRanges.newReport(new AgeRangeReport(65, 100, calendarSupplier ));

			for ( AgeRangeReport ageRange : ageRanges.getSubReports() )
			{
				for ( GenderReport gender : genderReport.getSubReports() )
				{
					newReport(new AgeGenderReport(gender.getGender(), ageRange.getFromAge(), ageRange.getToAge(), calendarSupplier));
				}
			}
		}

		/*
		 * We don't actually use these for processing
		 *
		males.initReport();
		females.initReport();
		others.initReport();
		 */
	}

	@Override
	protected void processInternal(ReportUsageType usage)
	{
		super.processInternal(usage);

		/*
		 * We only use these to generate the series names
		 *
		males.process(usage);
		females.process(usage);
		others.process(usage);
		 */
	}

	@Override
	public int getGroupingCount()
	{
		// Males, Females and Others
		//
		return 3;
	}

	@Override
	public String getSeriesName(int i)
	{
		String seriesName = others.getGender();

		switch ( i )
		{
			case 0: { seriesName = males.getGender(); break; }
			case 1: { seriesName = females.getGender(); break; }
			case 2: //{ seriesName = others.getGender(); break; }
			default: {  break; }
		}

		//String seriesName = (i % 2 == 0) ? males.getGender() : females.getGender();

		return getModelName() + "." + seriesName;
	}

	@Override
	public AgeGenderAggregateReport cloneReport()
	{
		AgeGenderAggregateReport retVal = new AgeGenderAggregateReport(calendarSupplier);

		return retVal;
	}

}
