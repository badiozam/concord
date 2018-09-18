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
package com.i4one.promotion.tests.model.trivia;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.tests.model.BaseRecordTypeDelegatorTest;
import com.i4one.promotion.model.trivia.Trivia;
import com.i4one.promotion.model.trivia.TriviaAnswer;
import com.i4one.promotion.model.trivia.TriviaManager;
import com.i4one.promotion.model.trivia.TriviaRecord;
import com.i4one.promotion.model.trivia.category.TriviaCategory;
import com.i4one.promotion.model.trivia.category.TriviaCategoryManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Hamid Badiozamani
 */
public class TriviaTest extends BaseRecordTypeDelegatorTest<TriviaRecord, Trivia>
{
	private TriviaManager simpleTriviaManager;
	private TriviaCategoryManager triviaCategoryManager;

	private Trivia trivia;
	private TriviaCategory triviaCategory;
	private TriviaAnswer answer1;
	private TriviaAnswer answer2;

	@Override
	public Trivia newItem()
	{
		Trivia retVal = new Trivia();

		retVal.setStartTimeSeconds(5);
		retVal.setEndTimeSeconds(10);

		return retVal;
	}

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		triviaCategory = new TriviaCategory();
		triviaCategory.setClient(getFirstClient());
		triviaCategory.setSiteGroup(getFirstSiteGroup());
		triviaCategory.setDescr(new IString("Description of trivia category #1"));
		triviaCategory.setDetailPicURL("http://www.detailpicurl.com/");
		triviaCategory.setThumbnailURL("http://www.thumbnailurl.com/");
		triviaCategory.setTitle(new IString("Trivia Category #1"));

		ReturnType<TriviaCategory> categoryCreate = getTriviaCategoryManager().create(triviaCategory);
		assertTrue(categoryCreate.getPost().exists());

		trivia = new Trivia();
		trivia.setTitle(new IString("Trivia Title"));
		trivia.setIntro(new IString("Intro"));
		trivia.setOutro(new IString("Outro"));
		trivia.setQuestionType(Trivia.TYPE_SINGLEANSER_RADIO);
		trivia.setStartTimeSeconds(Utils.currentTimeSeconds() - 5);
		trivia.setEndTimeSeconds(Utils.currentTimeSeconds() + 5);

		trivia.setClient(getFirstClient());
		trivia.setSiteGroup(getFirstSiteGroup());
		trivia.setCategory(triviaCategory);

		answer1 = new TriviaAnswer();
		answer1.setAnswer(new IString("answer1"));

		answer2 = new TriviaAnswer();
		answer2.setAnswer(new IString("answer2"));

		Set<TriviaAnswer> answers = new HashSet<>();
		answers.add(answer1);
		answers.add(answer2);

		trivia.setTriviaAnswers(answers);
		trivia.setCorrectAnswer(answer1);

		ReturnType<Trivia> triviaCreate = getTriviaManager().create(trivia);
		assertTrue(triviaCreate.getPost().exists());

		assertTrue(answer1.exists());
		assertTrue(answer2.exists());
	}

	@Test
	public void testEquality()
	{
		TriviaAnswer triviaAnswer1 = new TriviaAnswer();
		TriviaAnswer triviaAnswer2 = new TriviaAnswer();
		
		IString answerStr1 = new IString();
		answerStr1.set("en", "First Answer");
		answerStr1.set("es", "Primera respuesta");
		answerStr1.set("fa", "");

		IString answerStr2 = new IString();
		answerStr2.set("en", "Second Answer");
		answerStr2.set("es", "Segunda respuesta");
		answerStr2.set("fa", "");

		assertNotEquals(answerStr1, answerStr2);

		triviaAnswer1.setAnswer(answerStr1);
		triviaAnswer2.setAnswer(answerStr2);

		assertNotEquals(triviaAnswer1, triviaAnswer2);

		triviaAnswer1.setTrivia(trivia);
		triviaAnswer2.setTrivia(trivia);

		assertNotEquals(triviaAnswer1, triviaAnswer2);
	}

	@Test
	public void testUpdate()
	{
		int prevEndTime = trivia.getEndTimeSeconds();

		List<TriviaAnswer> answers = new ArrayList<>();
		for ( TriviaAnswer currAnswer : trivia.getTriviaAnswers() )
		{
			TriviaAnswer answer = new TriviaAnswer();
			answer.setSer(currAnswer.getSer());
			answer.setAnswer(new IString(currAnswer.getAnswer()));
			answer.setTrivia(trivia);
		}

		int triviaSer = trivia.getSer();
		trivia.setSer(0);
		trivia.setSer(triviaSer);

		trivia.setEndTimeSeconds(prevEndTime + 10);
		trivia.setTriviaAnswers(answers);

		ReturnType<Trivia> triviaUpdate = getTriviaManager().update(trivia);
		assertEquals(prevEndTime + 10, trivia.getEndTimeSeconds());
	}

	public TriviaManager getTriviaManager()
	{
		return getSimpleTriviaManager();
	}

	public TriviaManager getSimpleTriviaManager()
	{
		return simpleTriviaManager;
	}

	@Autowired
	public void setSimpleTriviaManager(TriviaManager simpleTriviaManager)
	{
		this.simpleTriviaManager = simpleTriviaManager;
	}

	public TriviaCategoryManager getTriviaCategoryManager()
	{
		return triviaCategoryManager;
	}

	@Autowired
	public void setTriviaCategoryManager(TriviaCategoryManager triviaCategoryManager)
	{
		this.triviaCategoryManager = triviaCategoryManager;
	}

}
