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
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.question.QuestionRecord;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Hamid Badiozamani
 */
public class SurveyQuestionTest extends BaseRecordTypeDelegatorTest<QuestionRecord, Question>
{
	@Override
	public Question newItem()
	{
		return new Question();
	}

	@Test
	public void testFromCSV()
	{
		String csv = "1,0,Question goes here,1,2,.*";

		Question question = new Question();
		boolean imported = question.fromCSV(csv);

		assertTrue(imported);
		assertEquals((int)1, (int)question.getSer());
		assertEquals((int)0, (int)question.getQuestionType());
		assertEquals("Question goes here", question.getQuestion().get("en"));
		assertEquals((int)1, (int)question.getMinResponses());
		assertEquals((int)2, (int)question.getMaxResponses());
		assertEquals(".*", question.getValidAnswer());
	}
}
