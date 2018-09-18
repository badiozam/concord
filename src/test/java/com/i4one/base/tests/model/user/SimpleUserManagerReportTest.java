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
package com.i4one.base.tests.model.user;

import com.i4one.base.core.Base;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.report.AgeGenderAggregateReport;
import com.i4one.base.model.report.AggregateReport;
import com.i4one.base.model.report.DayOfWeekReport;
import com.i4one.base.model.report.Demo;
import com.i4one.base.model.report.HourOfDayReport;
import com.i4one.base.model.report.NullTopLevelReport;
import com.i4one.base.model.report.ReportManager;
import com.i4one.base.model.report.ReportType;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.user.reports.UserLastLoginReport;
import com.i4one.base.model.userlogin.UserLoginManager;
import com.i4one.base.tests.core.BaseUserManagerTest;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SimpleUserManagerReportTest extends BaseUserManagerTest
{
	private ReportManager reportManager;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		getUserManager().init();

		NullTopLevelReport ntp = new NullTopLevelReport();

			AggregateReport<Demo> hoursOfDay = new AggregateReport<>("hoursOfDay", "line");
			for (int i = 0; i < 24; i++ )
			{
				hoursOfDay.newReport(new HourOfDayReport((i + 5) % 24, () -> { return ntp.getEndCalendar(); } ));
			}
	
			AggregateReport<Demo> daysOfWeek = new AggregateReport<>("daysOfWeek", "bar");
			for (int i = 1; i < 8; i++ )
			{
				daysOfWeek.newReport(new DayOfWeekReport(i, () -> { return ntp.getEndCalendar(); } ));
			}
	
			ntp.newReport(hoursOfDay);
			ntp.newReport(daysOfWeek);

			AggregateReport<Demo> ageGenderReport = new AgeGenderAggregateReport( () -> { return ntp.getEndCalendar(); });
			ntp.newReport(ageGenderReport);
	
			getLogger().debug("Initialized new standard demos adding permutations");

			// We have to make two passes at this in order to ensure
			// that the first leaf demos get the fleshed out permutations
			// from the last set of demos
			//
			ntp.addPermutations(ntp, 3);
			ntp.addPermutations(ntp, 3);

		ntp.initReport();

		String origJSON = ntp.toJSONString();
		getReportManager().saveReport(ntp);

		TopLevelReport dbNtp = getReportManager().loadReport(new NullTopLevelReport());

		assertNotNull(dbNtp);
		assertFalse(dbNtp.getSubReports().isEmpty());

		String convJSON = dbNtp.toJSONString();
		assertEquals(origJSON, convJSON);
		assertNotSame(ntp, dbNtp);
	}

	@Test
	public void testPopulateReport()
	{
		TopLevelReport ntp = new NullTopLevelReport();

		assertEquals(0, ntp.getSubReports().size());

		TopLevelReport dbNtp = getReportManager().loadReport(new NullTopLevelReport());
		String origJSON = dbNtp.toJSONString();

		getReportManager().populateReport(ntp);
		String populatedJSON = ntp.toJSONString();

		assertEquals(origJSON, populatedJSON);
	}

	@Test
	public void testReportHash()
	{
		AggregateReport first = new AggregateReport("none", "bar");
		AggregateReport second = new AggregateReport("none", "bar");

		assertNotSame(first, second);
		assertEquals(first, second);

		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	public void testReport() throws IOException
	{
		User u = new User();
		u.setClient(getFirstClient());

		TopLevelReport topLevelReport = getUserManager().getReport(u, new UserLastLoginReport(getFirstClient().getCalendar()), SimplePaginationFilter.NONE);
		assertFalse(topLevelReport.getSubReports().isEmpty());

		String origJSON = topLevelReport.toJSONString();
		getLogger().debug(origJSON);

		TopLevelReport readReport = Base.getInstance().getJacksonObjectMapper().readValue(origJSON, TopLevelReport.class);
		String convJSON = readReport.toJSONString();
		getLogger().debug(convJSON);

		assertEquals(origJSON, convJSON);
		assertNotSame(origJSON, convJSON);
		assertNotSame(topLevelReport, readReport);

		// Test finding of reports
		//
		ReportType foundManagerReport = readReport.findReport(topLevelReport.getTitle());
		assertNotSame(topLevelReport, foundManagerReport);
		assertEquals(topLevelReport, foundManagerReport);

		ReportType foundHourOfDayReport = readReport.findReport(topLevelReport.getTitle() + ",base.AggregateReport.hoursOfDay,base.HourOfDayReport.22");
		assertNotNull(foundHourOfDayReport);
		assertTrue(foundHourOfDayReport instanceof HourOfDayReport);

		getLogger().debug("\n" + convJSON + "\n");
	}

	@Test
	public void testReportSaveLoad() throws IOException
	{
		User u = new User();
		u.setClient(getFirstClient());

		UserLastLoginReport userLastLoginReport = new UserLastLoginReport(getFirstClient().getCalendar());
		TopLevelReport topLevelReport = getUserManager().getReport(u, userLastLoginReport, SimplePaginationFilter.NONE);
		assertSame(userLastLoginReport, topLevelReport);
		assertFalse(topLevelReport.getSubReports().isEmpty());

		TopLevelReport readReport = getUserManager().getReport(u, new UserLastLoginReport(getFirstClient().getCalendar()), SimplePaginationFilter.NONE);
		assertFalse(readReport.getSubReports().isEmpty());
		assertNotSame(topLevelReport, readReport);
		assertEquals(topLevelReport, readReport);
		assertEquals(topLevelReport.getSubReports().size(), readReport.getSubReports().size());

		// Test finding of reports
		//
		ReportType foundManagerReport = readReport.findReport(topLevelReport.getTitle());
		assertNotSame(topLevelReport, foundManagerReport);
		assertEquals(topLevelReport, foundManagerReport);

		ReportType foundHourOfDayReport = readReport.findReport(topLevelReport.getTitle() + ",base.AggregateReport.hoursOfDay,base.HourOfDayReport.22");
		assertNotNull(foundHourOfDayReport);
		assertTrue(foundHourOfDayReport instanceof HourOfDayReport);
	}

	public UserManager getUserManager()
	{
		return getSimpleUserManager();
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
