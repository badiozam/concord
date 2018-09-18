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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.tests.core.BaseManagerTest;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.SurveyManager;
import com.i4one.research.model.survey.category.SurveyCategory;
import com.i4one.research.model.survey.category.SurveyCategoryManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Hamid Badiozamani
 */
public class SurveyImportTest extends BaseManagerTest
{
	private SurveyManager surveyManager;
	private SurveyCategoryManager surveyCategoryManager;

	private SurveyCategory surveyCategory;
	private SurveyCategory exportSurveyCategory;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		surveyCategory = new SurveyCategory();
		surveyCategory.setClient(getFirstClient());
		surveyCategory.setSiteGroup(getFirstSiteGroup());
		surveyCategory.setTitle(new IString("Survey Category #1"));
		surveyCategory.setDescr(new IString("Test category"));

		ReturnType<SurveyCategory> createdCategory = getSurveyCategoryManager().create(surveyCategory);
		assertTrue(surveyCategory.exists());

		exportSurveyCategory = new SurveyCategory();
		exportSurveyCategory.setClient(getSecondClient());
		exportSurveyCategory.setSiteGroup(getSecondSiteGroup());
		exportSurveyCategory.setTitle(new IString("Survey Category #2"));
		exportSurveyCategory.setDescr(new IString("Test category"));

		logAdminIn(getSecondAdminRW());

		ReturnType<SurveyCategory> createdExportCategory = getSurveyCategoryManager().create(exportSurveyCategory);
		assertTrue(exportSurveyCategory.exists());

		logAdminIn(getFirstAdmin());
	}

	@Test
	public void testImport()
	{
		String importSurveyCSV =
			 "###Start Date (dd-MMM-yyyy),Start Time (HH:mm:ss),End Date (dd-MMM-yyyy),End Time (HH:mm:ss),Title,Intro,Outro,# of Triggers,1 ###Exclusive,Amount,Balance ID,\n"
			+ "01-Jan-2016,0:0:0,31-Dec-2018,23:59:59,\"{\"\"en\"\": \"\"Test Title\"\"}\",Intro goes here,Outro goes here,0,1,true,1000,\n"
			+ "QUESTIONS\n"
			+ "### Serial, Question Type, Question, Min Responses, Max Responses, Valid Answer Regex\n"
			+ "1,0,Do you like going out?,1,1,\n"
			+ "2,0,Do you like staying up late?,1,1,\n"
			+ "ANSWERS\n"
			+ "#### Question ID, Answer\n"
			+ "1,Yes\n"
			+ "1,No\n"
			+ "2,Yes\n"
			+ "2,No\n"
			+ "SURVEY\n"
			+ "01-Jan-2016,0:0:0,31-Dec-2018,23:59:59,\"{\"\"en\"\": \"\"Test Title 2\"\"}\",Intro goes here,Outro goes here,0,0\n"
			+ "QUESTIONS\n"
			+ "### Serial, Question Type, Question, Min Responses, Max Responses, Valid Answer Regex\n"
			+ "1,0,Do you like making out?,1,1,\n"
			+ "2,0,How's your breath?,1,1,\n"
			+ "ANSWERS\n"
			+ "#### Question ID, Answer\n"
			+ "1,Yes\n"
			+ "1,No\n"
			+ "2,Ok, I guess\n"
			+ "2,I don't know\n"
			+ "2,I don't floss";

		List<ReturnType<Survey>> created = getSurveyManager().importCSV(new ByteArrayInputStream(importSurveyCSV.getBytes()),
			() ->
			{
				Survey retVal = new Survey();
				retVal.setClient(getFirstClient());
				retVal.setSiteGroup(getFirstSiteGroup());
				retVal.setCategory(surveyCategory);

				return retVal;
			},
			(survey) -> { return true; },
			(survey) -> {});

		assertNotNull(created);
		assertFalse(created.isEmpty());
		assertEquals(2, created.size());

		Survey firstSurvey = created.get(0).getPost();
		assertTrue(firstSurvey.exists());
		assertFalse(firstSurvey.getQuestions().isEmpty());

		Survey firstSurveyFromDb = getSurveyManager().getById(firstSurvey.getSer());
		assertFalse(firstSurveyFromDb.getBalanceTriggers().isEmpty());

		Survey secondSurvey = created.get(1).getPost();
		assertTrue(secondSurvey.exists());
		assertFalse(secondSurvey.getQuestions().isEmpty());

		Survey secondSurveyFromDb = getSurveyManager().getById(secondSurvey.getSer());
		assertTrue(secondSurveyFromDb.getBalanceTriggers().isEmpty());
	}

	@Test
	public void testExport() throws IOException, Exception
	{
		String importSurveyCSV =
			 "###Start Date (dd-MMM-yyyy),Start Time (HH:mm:ss),End Date (dd-MMM-yyyy),End Time (HH:mm:ss),Title,Intro,Outro,# of Triggers,1 ###Exclusive,Amount,Balance ID,\n"
			+ "01-Jan-2016,0:0:0,31-Dec-2018,23:59:59,\"{\"\"en\"\": \"\"Test Title\"\"}\",Intro goes here,Outro goes here,0,1,true,1000,\n"
			+ "QUESTIONS\n"
			+ "### Serial, Question Type, Question, Min Responses, Max Responses, Valid Answer Regex\n"
			+ "1,0,Do you like going out?,1,1,\n"
			+ "2,0,Do you like staying up late?,1,1,\n"
			+ "ANSWERS\n"
			+ "#### Question ID, Answer\n"
			+ "1,Yes\n"
			+ "1,No\n"
			+ "2,Yes\n"
			+ "2,No\n";

		List<ReturnType<Survey>> created = getSurveyManager().importCSV(new ByteArrayInputStream(importSurveyCSV.getBytes()),
			() ->
			{
				Survey retVal = new Survey();
				retVal.setClient(getFirstClient());
				retVal.setSiteGroup(getFirstSiteGroup());
				retVal.setCategory(surveyCategory);

				return retVal;
			},
			(survey) -> { return true; },
			(survey) -> {});

		assertNotNull(created);
		assertFalse(created.isEmpty());

		Survey firstSurvey = created.get(0).getPost();
		assertTrue(firstSurvey.exists());

		Survey firstSurveyFromDb = getSurveyManager().getById(firstSurvey.getSer());
		assertFalse(firstSurveyFromDb.getBalanceTriggers().isEmpty());

		Set<Survey> forExport = new HashSet<>();
		forExport.add(firstSurveyFromDb);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		getSurveyManager().exportCSV(forExport, outStream);
		outStream.close();

		String exportedCSV =  outStream.toString();
		assertNotNull(exportedCSV);
		assertFalse(exportedCSV.isEmpty());

		logAdminIn(getSecondAdminRW(), getSecondClient());

		List<ReturnType<Survey>> fromExport = getSurveyManager().importCSV(new ByteArrayInputStream(exportedCSV.getBytes()),
			() ->
			{
				Survey retVal = new Survey();
				retVal.setClient(getSecondClient());
				retVal.setSiteGroup(getSecondSiteGroup());
				retVal.setCategory(exportSurveyCategory);

				return retVal;
			},
			(survey) -> { return true; },
			(survey) -> {});

		assertNotNull(fromExport);
		assertFalse(fromExport.isEmpty());

		Survey surveyFromExport = fromExport.get(0).getPost();
		assertNotNull(surveyFromExport);
		assertTrue(surveyFromExport.exists());
		assertFalse(surveyFromExport.getQuestions().isEmpty());

		Survey surveyFromExportDb = getSurveyManager().getById(surveyFromExport.getSer());
		assertFalse(surveyFromExportDb.getBalanceTriggers().isEmpty());
		assertNotEquals(firstSurvey.getSer(), surveyFromExportDb.getSer());
		assertNotEquals(firstSurvey.getClient(), surveyFromExportDb.getClient());
		assertNotEquals(firstSurvey.getSiteGroup(), surveyFromExportDb.getSiteGroup());
	}

	public SurveyManager getSurveyManager()
	{
		return surveyManager;
	}

	@Autowired
	public void setSurveyManager(SurveyManager surveyManager)
	{
		this.surveyManager = surveyManager;
	}

	public SurveyCategoryManager getSurveyCategoryManager()
	{
		return surveyCategoryManager;
	}

	@Autowired
	public void setSurveyCategoryManager(SurveyCategoryManager surveyCategoryManager)
	{
		this.surveyCategoryManager = surveyCategoryManager;
	}

}
