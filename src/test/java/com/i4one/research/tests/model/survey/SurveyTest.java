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
package com.i4one.research.tests.model.survey;

import com.i4one.base.tests.model.BaseRecordTypeDelegatorTest;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.SurveyRecord;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class SurveyTest extends BaseRecordTypeDelegatorTest<SurveyRecord, Survey>
{
	@Override
	public Survey newItem()
	{
		return new Survey();
	}

	@Test
	public void testFromCSV()
	{
		String csv = "01-Jan-2016,0:0:0,31-Dec-2018,23:59:59,\"{\"\"en\"\": \"\"Test Title\"\"}\",Intro goes here,Outro goes here,0,0";

		Survey survey = new Survey();
		boolean imported = survey.fromCSV(csv);

		assertTrue(imported);
		assertEquals("Test Title", survey.getTitle().get("en"));
		assertEquals("Intro goes here", survey.getIntro().get("en"));
		assertEquals("Outro goes here", survey.getOutro().get("en"));
		assertEquals(0, survey.getPerPage());
	}
}
