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
package com.i4one.base.tests.model.report.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.i4one.base.core.Base;
import com.i4one.base.model.UserType;
import com.i4one.base.model.report.classifier.classifications.AgeClassification;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.GenericClassification;
import com.i4one.base.model.report.classifier.GenericClassificationReport;
import com.i4one.base.model.report.classifier.GenericSubclassification;
import com.i4one.base.model.report.classifier.classifications.GenderClassification;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.reports.UserCreationClassification;
import com.i4one.base.tests.core.BaseTest;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class SimpleClassificationReportTest extends BaseTest
{
	private ClassificationReport report;

	private AgeClassification<UserType> ages;
	private GenderClassification<UserType> genders;

	@Before
	public void setUp()
	{
		Calendar now = Calendar.getInstance();
		report = new UserCreationClassification(now);

		ages = new AgeClassification(now);
		genders = new GenderClassification();

		int currYear = now.get(Calendar.YEAR);

		User user = new User();
		user.setGender("m");
		user.setBirthDD(8);
		user.setBirthMM(8);
		user.setBirthYYYY(currYear - 36);
		user.setCreateTimeSeconds((int)(now.getTimeInMillis() / 1000l));

		report.processRecord(user.getDelegate());

		user.setBirthDD(2);
		user.setBirthMM(22);
		user.setBirthYYYY(currYear - 60);

		report.processRecord(user.getDelegate());

		user.setGender("f");
		user.setBirthDD(1);
		user.setBirthMM(7);
		user.setBirthYYYY(currYear - 32);

		report.processRecord(user.getDelegate());

		getLogger().debug("Report is now " + report);
	}

	@Test
	public void testClassification()
	{
		Set<Subclassification> m1824 = new HashSet<>();
		Set<Subclassification> m3544 = new HashSet<>();
		Set<Subclassification> males = new HashSet<>();

		for ( Subclassification gender : genders.getSubclassifications())
		{
			if ( gender.getKey().equals("m"))
			{
				m1824.add(gender);
				m3544.add(gender);
				males.add(gender);
			}
		}

		for ( Subclassification age : ages.getSubclassifications() )
		{
			if ( age.getKey().equals("18-24"))
			{
				m1824.add(age);
			}
			else if ( age.getKey().equals("35-44"))
			{
				m3544.add(age);
			}
		}

		assertEquals(2, m1824.size());
		assertEquals(2, m3544.size());
		assertEquals(1, males.size());

		Integer m1824Count = report.get(m1824);
		assertEquals(0, (int)m1824Count);

		Integer m3544Count = report.get(m3544);
		assertEquals(1, (int)m3544Count);

		Integer malesCount = report.get(males);
		assertEquals(2, (int)malesCount);
	}

	@Test
	public void testGenericSubclassification()
	{
		Set<Subclassification> m1824 = new HashSet<>();
		Set<Subclassification> m3544 = new HashSet<>();
		Set<Subclassification> males = new HashSet<>();

		GenericClassification age = new GenericClassification("age");
		m1824.add(new GenericSubclassification(age, "18-24"));

		GenericClassification gender = new GenericClassification("gender");
		m1824.add(new GenericSubclassification(gender, "m"));

		GenericClassification age2 = new GenericClassification("age");
		m3544.add(new GenericSubclassification(age2, "35-44"));

		GenericClassification gender2 = new GenericClassification("gender");
		m3544.add(new GenericSubclassification(gender2, "m"));

		GenericClassification gender3 = new GenericClassification("gender");
		males.add(new GenericSubclassification(gender3, "m"));

		assertEquals(2, m1824.size());
		assertEquals(2, m3544.size());
		assertEquals(1, males.size());

		Integer m1824Count = report.get(m1824);
		assertEquals(0, (int)m1824Count);

		Integer m3544Count = report.get(m3544);
		assertEquals(1, (int)m3544Count);

		Integer malesCount = report.get(males);
		assertEquals(2, (int)malesCount);
	}

	@Test
	public void testSerializeDeserialize() throws JsonProcessingException, IOException
	{
		String reportStr = Base.getInstance().getJacksonObjectMapper().writeValueAsString(report.getValues());
		assertNotNull(reportStr);

		GenericClassificationReport genericReport = new GenericClassificationReport("test");
		genericReport.setValues(Base.getInstance().getJacksonObjectMapper().readValue(reportStr, Map.class));

		report = genericReport;

		testClassification();
	}
}
