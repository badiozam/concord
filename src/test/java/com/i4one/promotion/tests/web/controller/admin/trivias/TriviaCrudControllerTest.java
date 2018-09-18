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
package com.i4one.promotion.tests.web.controller.admin.trivias;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balancetrigger.BalanceTriggerManager;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.instantwin.InstantWinManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.tests.web.controller.admin.AdminViewControllerTest;
import com.i4one.base.web.controller.BaseViewController;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseCrudController;
import com.i4one.promotion.model.trivia.Trivia;
import com.i4one.promotion.model.trivia.TriviaAnswer;
import com.i4one.promotion.model.trivia.TriviaAnswerManager;
import com.i4one.promotion.model.trivia.TriviaManager;
import com.i4one.promotion.model.trivia.category.TriviaCategory;
import com.i4one.promotion.model.trivia.category.TriviaCategoryManager;
import com.i4one.promotion.web.controller.admin.trivias.TriviaCrudController;
import com.i4one.promotion.web.controller.admin.trivias.WebModelTrivia;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
public class TriviaCrudControllerTest extends AdminViewControllerTest
{
	private BalanceManager balanceManager;
	private BalanceTriggerManager balanceTriggerManager;
	private InstantWinManager instantWinManager;
	private TriviaManager triviaManager;
	private TriviaAnswerManager triviaAnswerManager;
	private TriviaCategoryManager triviaCategoryManager;
	
	private TriviaCategory firstTriviaCategory;

	private WebModelTrivia trivia;
	private TriviaCrudController triviaCrudController;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		firstTriviaCategory = new TriviaCategory();
		firstTriviaCategory.setClient(getFirstClient());
		firstTriviaCategory.setSiteGroup(getFirstSiteGroup());
		getTriviaCategoryManager().create(firstTriviaCategory);

		trivia = new WebModelTrivia();
		trivia.setClient(getFirstClient());
		trivia.setSiteGroup(getFirstSiteGroup());
		trivia.setCategory(firstTriviaCategory);
		trivia.setTitle(new IString("First Trivia"));
		trivia.setStartTimeString("12/8/14 12:00 AM");
		trivia.setEndTimeString("12/8/24 11:59 PM");
		trivia.setQuestionType(Trivia.TYPE_SINGLEANSER_RADIO);
		trivia.setIntro(new IString("What was today's Song of the Day?"));
		trivia.setOutro(new IString("Thanks for playing!"));

		trivia.getAnswers().get(0).setAnswer(new IString("Answer 1"));
		trivia.getAnswers().get(1).setAnswer(new IString("Answer 2"));
		trivia.getAnswers().get(2).setAnswer(new IString("Answer 3"));

		trivia.setCorrectIndex(1);
		trivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setSer(0);
		trivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setAmount(100);
		trivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setBalance(getFirstClientDefaultBalance());

		trivia.getInstantWinning().getExclusiveInstantWins().get(0).setPercentWin(0.0f);
		trivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerLimit(0);
		trivia.getInstantWinning().getExclusiveInstantWins().get(0).getUser().setUsername("");
		trivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerMessage(new IString());
		trivia.getInstantWinning().getExclusiveInstantWins().get(0).setLoserMessage(new IString());

		initRequest(new MockHttpServletRequest("POST", getBaseURL() + "/promotion/admin/trivias/update.html"));

		logAdminIn();
		preHandle(getMockRequest(), getMockResponse());
	}

	@Test
	public void createTriviaTest() throws Exception
	{

		/*
		getMockRequest().setParameter("ser=", "0");
		getMockRequest().setParameter("client=", "" + getFirstClient().getSer() );
		getMockRequest().setParameter("siteGroup=", "" + getFirstSiteGroup().getSer() );
		getMockRequest().setParameter("category=", "" + firstTriviaCategory.getSer());
		getMockRequest().setParameter("title['en']=", "Second Trivia");
		getMockRequest().setParameter("title['es']=", "");
		getMockRequest().setParameter("title['fa']=", "");
		getMockRequest().setParameter("startTimeString=", "12/8/14 12:00 AM");
		getMockRequest().setParameter("endTimeString=", "12/8/14 11:59 PM");
		getMockRequest().setParameter("questionType=", "0");
		getMockRequest().setParameter("intro['en']=", "What was today's Song of the Day?");
		getMockRequest().setParameter("intro['es']=", "");
		getMockRequest().setParameter("intro['fa']=", "");
		getMockRequest().setParameter("outro['en']=", "Thanks for playing! Listen again tomorrow for more opportunities.");
		getMockRequest().setParameter("outro['es']=", "");
		getMockRequest().setParameter("outro['fa']=", "");
		getMockRequest().setParameter("answers[0].ser=", "0");
		getMockRequest().setParameter("answers[0].answer['en']=", "Answer 1");
		getMockRequest().setParameter("answers[0].answer['es']=", "");
		getMockRequest().setParameter("answers[0].answer['fa']=", "");
		getMockRequest().setParameter("answers[1].ser=", "0");
		getMockRequest().setParameter("answers[1].answer['en']=", "Answer 2");
		getMockRequest().setParameter("answers[1].answer['es']=", "");
		getMockRequest().setParameter("answers[1].answer['fa']=", "");
		getMockRequest().setParameter("answers[2].ser=", "0");
		getMockRequest().setParameter("answers[2].answer['en']=", "Answer 3");
		getMockRequest().setParameter("answers[2].answer['es']=", "");
		getMockRequest().setParameter("answers[2].answer['fa']=", "");
		getMockRequest().setParameter("correctIndex=", "2");
		getMockRequest().setParameter("answers[3].ser=", "0");
		getMockRequest().setParameter("answers[3].answer['en']=", "");
		getMockRequest().setParameter("answers[3].answer['es']=", "");
		getMockRequest().setParameter("answers[3].answer['fa']=", "");
		getMockRequest().setParameter("triggerable.exclusiveBalanceTriggers[0].ser=", "0");
		getMockRequest().setParameter("triggerable.exclusiveBalanceTriggers[0].amount=", "100");
		getMockRequest().setParameter("triggerable.exclusiveBalanceTriggers[0].balance=", "4");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].ser=", "0");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].percentWin=", "0.0");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].winnerLimit=", "0");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].ser=", "0");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].user.username=", "");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].ser=", "0");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].winnerMessage['en']=", "");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].winnerMessage['es']=", "");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].winnerMessage['fa']=", "");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].ser=", "0");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].loserMessage['en']=", "");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].loserMessage['es']=", "");
		getMockRequest().setParameter("instantWinning.exclusiveInstantWins[0].loserMessage['fa']=", "");

		/getDispatcherServlet().service(getMockRequest(), getMockResponse());
		*/

		BindingResult result = new BeanPropertyBindingResult(trivia, "trivia");
		Model model = getTriviaCrudController().doUpdate(trivia, result, getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertFalse(result.hasErrors());
		assertEquals(3, trivia.getAnswerCount());
		assertEquals(trivia.getAnswerCount(), trivia.getTriviaAnswers().size());

		Trivia createdTrivia = new Trivia();
		createdTrivia.setSer(trivia.getSer());
		createdTrivia.loadedVersion();

		assertEquals(createdTrivia, trivia);
		createdTrivia.setTriviaAnswers(getTriviaAnswerManager().getAnswers(createdTrivia, SimplePaginationFilter.NONE));
		assertFalse(createdTrivia.getTriviaAnswers().isEmpty());
		assertEquals(createdTrivia.getTriviaAnswers().size(), trivia.getTriviaAnswers().size());

		assertEquals(createdTrivia.getCorrectAnswer(), trivia.getCorrectAnswer());
	}

	@Test
	public void updateTriviaTest() throws Exception
	{
		ReturnType<Trivia> createdTrivia = getTriviaManager().create(trivia);
		assertTrue(createdTrivia.getPost().exists());

		WebModelTrivia updateTrivia = new WebModelTrivia();
		updateTrivia.setSer(trivia.getSer());

		updateTrivia.setClient(getFirstClient());
		updateTrivia.setSiteGroup(getFirstSiteGroup());
		updateTrivia.setCategory(firstTriviaCategory);
		updateTrivia.setTitle(new IString("First Trivia"));
		updateTrivia.setStartTimeString("12/8/14 12:00 AM");
		updateTrivia.setEndTimeString("12/8/24 11:59 PM");
		updateTrivia.setQuestionType(Trivia.TYPE_SINGLEANSER_RADIO);
		updateTrivia.setIntro(new IString("What was today's Song of the Day?"));
		updateTrivia.setOutro(new IString("Thanks for playing!"));

		// The answers each have their own serial numbers as well
		//
		updateTrivia.getAnswers().get(0).setSer(trivia.getAnswers().get(0).getSer());
		updateTrivia.getAnswers().get(0).setAnswer(new IString("Answer 1"));

		updateTrivia.getAnswers().get(1).setSer(trivia.getAnswers().get(1).getSer());
		updateTrivia.getAnswers().get(1).setAnswer(new IString("Answer 2"));

		updateTrivia.getAnswers().get(2).setSer(trivia.getAnswers().get(2).getSer());
		updateTrivia.getAnswers().get(2).setAnswer(new IString("Answer 3"));


		updateTrivia.setCorrectIndex(1);
		int triggerid = trivia.getTriggerable().getExclusiveBalanceTriggers().get(0).getSer();
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setSer(triggerid);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setAmount(100);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setBalance(getFirstClientDefaultBalance());

		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setPercentWin(0.0f);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerLimit(0);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).getUser().setUsername("");
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerMessage(new IString());
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setLoserMessage(new IString());

		// Update a different object with the same serial number
		//
		BindingResult result = new BeanPropertyBindingResult(updateTrivia, "trivia");
		Model model = getTriviaCrudController().doUpdate(updateTrivia, result, getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertFalse(result.hasErrors());

		assertEquals(3, updateTrivia.getAnswerCount());
		assertEquals(trivia.getAnswerCount(), updateTrivia.getTriviaAnswers().size());

		assertEquals(1, updateTrivia.getCorrectIndex());
		assertEquals(new IString("Answer 2"), updateTrivia.getCorrectAnswer().getAnswer());
	}

	@Test
	public void setCorrectAnswerTest() throws Exception
	{
		ReturnType<Trivia> createdTrivia = getTriviaManager().create(trivia);
		assertTrue(createdTrivia.getPost().exists());

		WebModelTrivia updateTrivia = new WebModelTrivia();
		updateTrivia.setSer(trivia.getSer());

		updateTrivia.setClient(getFirstClient());
		updateTrivia.setSiteGroup(getFirstSiteGroup());
		updateTrivia.setCategory(firstTriviaCategory);
		updateTrivia.setTitle(new IString("First Trivia"));
		updateTrivia.setStartTimeString("12/8/14 12:00 AM");
		updateTrivia.setEndTimeString("12/8/24 11:59 PM");
		updateTrivia.setQuestionType(Trivia.TYPE_SINGLEANSER_RADIO);
		updateTrivia.setIntro(new IString("What was today's Song of the Day?"));
		updateTrivia.setOutro(new IString("Thanks for playing!"));

		// The answers each have their own serial numbers as well
		//
		updateTrivia.getAnswers().get(0).setSer(trivia.getAnswers().get(0).getSer());
		updateTrivia.getAnswers().get(0).setAnswer(new IString("Answer 1"));

		updateTrivia.getAnswers().get(1).setSer(trivia.getAnswers().get(1).getSer());
		updateTrivia.getAnswers().get(1).setAnswer(new IString("Answer 2"));

		updateTrivia.getAnswers().get(2).setSer(trivia.getAnswers().get(2).getSer());
		updateTrivia.getAnswers().get(2).setAnswer(new IString("Answer 3"));


		updateTrivia.setCorrectIndex(0);
		int triggerid = trivia.getTriggerable().getExclusiveBalanceTriggers().get(0).getSer();
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setSer(triggerid);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setAmount(100);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setBalance(getFirstClientDefaultBalance());

		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setPercentWin(0.0f);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerLimit(0);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).getUser().setUsername("");
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerMessage(new IString());
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setLoserMessage(new IString());

		// Update a different object with the same serial number
		//
		BindingResult result = new BeanPropertyBindingResult(updateTrivia, "trivia");
		Model model = getTriviaCrudController().doUpdate(updateTrivia, result, getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertFalse(result.hasErrors());

		assertEquals(3, updateTrivia.getAnswerCount());
		assertEquals(trivia.getAnswerCount(), updateTrivia.getTriviaAnswers().size());

		assertEquals(0, updateTrivia.getCorrectIndex());
		assertEquals(new IString("Answer 1"), updateTrivia.getCorrectAnswer().getAnswer());
	}

	@Test
	public void removeTriviaAnswerTest() throws Exception
	{
		ReturnType<Trivia> createdTrivia = getTriviaManager().create(trivia);
		assertTrue(createdTrivia.getPost().exists());

		WebModelTrivia updateTrivia = new WebModelTrivia();
		updateTrivia.setSer(trivia.getSer());

		updateTrivia.setClient(getFirstClient());
		updateTrivia.setSiteGroup(getFirstSiteGroup());
		updateTrivia.setCategory(firstTriviaCategory);
		updateTrivia.setTitle(new IString("First Trivia"));
		updateTrivia.setStartTimeString("12/8/14 12:00 AM");
		updateTrivia.setEndTimeString("12/8/24 11:59 PM");
		updateTrivia.setQuestionType(Trivia.TYPE_SINGLEANSER_RADIO);
		updateTrivia.setIntro(new IString("What was today's Song of the Day?"));
		updateTrivia.setOutro(new IString("Thanks for playing!"));

		// The answers each have their own serial numbers as well
		//
		updateTrivia.getAnswers().get(0).setSer(trivia.getAnswers().get(0).getSer());
		updateTrivia.getAnswers().get(0).setAnswer(new IString("Answer 1"));

		updateTrivia.getAnswers().get(1).setSer(trivia.getAnswers().get(1).getSer());
		updateTrivia.getAnswers().get(1).setAnswer(new IString("Answer 2"));

		updateTrivia.getAnswers().get(2).setSer(trivia.getAnswers().get(2).getSer());
		updateTrivia.getAnswers().get(2).setAnswer(new IString(""));

		updateTrivia.setCorrectIndex(1);
		int triggerid = trivia.getTriggerable().getExclusiveBalanceTriggers().get(0).getSer();
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setSer(triggerid);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setAmount(100);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setBalance(getFirstClientDefaultBalance());

		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setPercentWin(0.0f);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerLimit(0);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).getUser().setUsername("");
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerMessage(new IString());
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setLoserMessage(new IString());

		// Removed the third answer
		//
		BindingResult result = new BeanPropertyBindingResult(updateTrivia, "trivia");
		Model model = getTriviaCrudController().doUpdate(updateTrivia, result, getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertFalse(result.hasErrors());
		assertEquals(2, updateTrivia.getAnswerCount());
		assertEquals(trivia.getAnswerCount() - 1, updateTrivia.getTriviaAnswers().size());

		assertEquals(1, updateTrivia.getCorrectIndex());
		assertEquals(new IString("Answer 2"), updateTrivia.getCorrectAnswer().getAnswer());
	}

	@Test
	public void addTriviaAnswerTest() throws Exception
	{
		ReturnType<Trivia> createdTrivia = getTriviaManager().create(trivia);
		assertTrue(createdTrivia.getPost().exists());

		WebModelTrivia updateTrivia = new WebModelTrivia();
		updateTrivia.setSer(trivia.getSer());

		updateTrivia.setClient(getFirstClient());
		updateTrivia.setSiteGroup(getFirstSiteGroup());
		updateTrivia.setCategory(firstTriviaCategory);
		updateTrivia.setTitle(new IString("First Trivia"));
		updateTrivia.setStartTimeString("12/8/14 12:00 AM");
		updateTrivia.setEndTimeString("12/8/24 11:59 PM");
		updateTrivia.setQuestionType(Trivia.TYPE_SINGLEANSER_RADIO);
		updateTrivia.setIntro(new IString("What was today's Song of the Day?"));
		updateTrivia.setOutro(new IString("Thanks for playing!"));

		// The answers each have their own serial numbers as well
		//
		updateTrivia.getAnswers().get(0).setSer(trivia.getAnswers().get(0).getSer());
		updateTrivia.getAnswers().get(0).setAnswer(new IString("Answer 1"));

		updateTrivia.getAnswers().get(1).setSer(trivia.getAnswers().get(1).getSer());
		updateTrivia.getAnswers().get(1).setAnswer(new IString("Answer 2"));

		updateTrivia.getAnswers().get(2).setSer(trivia.getAnswers().get(2).getSer());
		updateTrivia.getAnswers().get(2).setAnswer(new IString("Answer 3"));

		updateTrivia.getAnswers().get(3).setSer(0);
		updateTrivia.getAnswers().get(3).setAnswer(new IString("Answer 4"));

		updateTrivia.setCorrectIndex(1);
		int triggerid = trivia.getTriggerable().getExclusiveBalanceTriggers().get(0).getSer();
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setSer(triggerid);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setAmount(100);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setBalance(getFirstClientDefaultBalance());

		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setPercentWin(0.0f);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerLimit(0);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).getUser().setUsername("");
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerMessage(new IString());
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setLoserMessage(new IString());

		// Update a different object with the same serial number
		//
		BindingResult result = new BeanPropertyBindingResult(updateTrivia, "trivia");
		Model model = getTriviaCrudController().doUpdate(updateTrivia, result, getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertFalse(result.hasErrors());

		assertEquals(4, updateTrivia.getAnswerCount());
		assertEquals(trivia.getAnswerCount() + 1, updateTrivia.getTriviaAnswers().size());

		assertEquals(1, updateTrivia.getCorrectIndex());
		assertEquals(new IString("Answer 2"), updateTrivia.getCorrectAnswer().getAnswer());
	}

	@Test
	public void addCorrectAnswerTest() throws Exception
	{
		ReturnType<Trivia> createdTrivia = getTriviaManager().create(trivia);
		assertTrue(createdTrivia.getPost().exists());

		WebModelTrivia updateTrivia = new WebModelTrivia();
		updateTrivia.setSer(trivia.getSer());

		updateTrivia.setClient(getFirstClient());
		updateTrivia.setSiteGroup(getFirstSiteGroup());
		updateTrivia.setCategory(firstTriviaCategory);
		updateTrivia.setTitle(new IString("First Trivia"));
		updateTrivia.setStartTimeString("12/8/14 12:00 AM");
		updateTrivia.setEndTimeString("12/8/24 11:59 PM");
		updateTrivia.setQuestionType(Trivia.TYPE_SINGLEANSER_RADIO);
		updateTrivia.setIntro(new IString("What was today's Song of the Day?"));
		updateTrivia.setOutro(new IString("Thanks for playing!"));

		// The answers each have their own serial numbers as well
		//
		updateTrivia.getAnswers().get(0).setSer(trivia.getAnswers().get(0).getSer());
		updateTrivia.getAnswers().get(0).setAnswer(new IString("Answer 1"));

		updateTrivia.getAnswers().get(1).setSer(trivia.getAnswers().get(1).getSer());
		updateTrivia.getAnswers().get(1).setAnswer(new IString("Answer 2"));

		updateTrivia.getAnswers().get(2).setSer(trivia.getAnswers().get(2).getSer());
		updateTrivia.getAnswers().get(2).setAnswer(new IString("Answer 3"));

		updateTrivia.getAnswers().get(3).setSer(0);
		updateTrivia.getAnswers().get(3).setAnswer(new IString("Answer 4"));

		updateTrivia.setCorrectIndex(3);
		int triggerid = trivia.getTriggerable().getExclusiveBalanceTriggers().get(0).getSer();
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setSer(triggerid);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setAmount(100);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setBalance(getFirstClientDefaultBalance());

		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setPercentWin(0.0f);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerLimit(0);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).getUser().setUsername("");
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerMessage(new IString());
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setLoserMessage(new IString());

		// Update a different object with the same serial number
		//
		BindingResult result = new BeanPropertyBindingResult(updateTrivia, "trivia");
		Model model = getTriviaCrudController().doUpdate(updateTrivia, result, getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertFalse(result.hasErrors());

		assertEquals(4, updateTrivia.getAnswerCount());
		assertEquals(trivia.getAnswerCount() + 1, updateTrivia.getTriviaAnswers().size());

		assertEquals(3, updateTrivia.getCorrectIndex());
		assertEquals(new IString("Answer 4"), updateTrivia.getCorrectAnswer().getAnswer());
	}

	@Test
	public void updateAndSetCorrectAnswerTest() throws Exception
	{
		ReturnType<Trivia> createdTrivia = getTriviaManager().create(trivia);
		assertTrue(createdTrivia.getPost().exists());

		WebModelTrivia updateTrivia = new WebModelTrivia();
		updateTrivia.setSer(trivia.getSer());

		updateTrivia.setClient(getFirstClient());
		updateTrivia.setSiteGroup(getFirstSiteGroup());
		updateTrivia.setCategory(firstTriviaCategory);
		updateTrivia.setTitle(new IString("First Trivia"));
		updateTrivia.setStartTimeString("12/8/14 12:00 AM");
		updateTrivia.setEndTimeString("12/8/24 11:59 PM");
		updateTrivia.setQuestionType(Trivia.TYPE_SINGLEANSER_RADIO);
		updateTrivia.setIntro(new IString("What was today's Song of the Day?"));
		updateTrivia.setOutro(new IString("Thanks for playing!"));

		// The answers each have their own serial numbers as well
		//
		updateTrivia.getAnswers().get(0).setSer(trivia.getAnswers().get(0).getSer());
		updateTrivia.getAnswers().get(0).setAnswer(new IString("Answer One"));

		updateTrivia.getAnswers().get(1).setSer(trivia.getAnswers().get(1).getSer());
		updateTrivia.getAnswers().get(1).setAnswer(new IString("Answer Two"));

		updateTrivia.getAnswers().get(2).setSer(trivia.getAnswers().get(2).getSer());
		updateTrivia.getAnswers().get(2).setAnswer(new IString("Answer 3"));


		updateTrivia.setCorrectIndex(0);
		int triggerid = trivia.getTriggerable().getExclusiveBalanceTriggers().get(0).getSer();
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setSer(triggerid);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setAmount(100);
		updateTrivia.getTriggerable().getExclusiveBalanceTriggers().get(0).setBalance(getFirstClientDefaultBalance());

		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setPercentWin(0.0f);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerLimit(0);
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).getUser().setUsername("");
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setWinnerMessage(new IString());
		updateTrivia.getInstantWinning().getExclusiveInstantWins().get(0).setLoserMessage(new IString());

		// Update a different object with the same serial number
		//
		BindingResult result = new BeanPropertyBindingResult(updateTrivia, "trivia");
		Model model = getTriviaCrudController().doUpdate(updateTrivia, result, getMockRequest(), getMockResponse());

		assertNotNull(model);
		assertFalse(result.hasErrors());

		assertEquals(3, updateTrivia.getAnswerCount());
		assertEquals(trivia.getAnswerCount(), updateTrivia.getTriviaAnswers().size());

		assertEquals(0, updateTrivia.getCorrectIndex());
		assertEquals(new IString("Answer One"), updateTrivia.getCorrectAnswer().getAnswer());

		TriviaAnswer[] triviaAnswers = new TriviaAnswer[updateTrivia.getTriviaAnswers().size()];
		triviaAnswers = updateTrivia.getTriviaAnswers().toArray(triviaAnswers);

		assertEquals(new IString("Answer One"), triviaAnswers[0].getAnswer());
		assertEquals(new IString("Answer Two"), triviaAnswers[1].getAnswer());
	}

	@Test
	public void cloneTriviaTest() throws Exception
	{
		ReturnType<Trivia> createdTrivia = getTriviaManager().create(trivia);
		assertTrue(createdTrivia.getPost().exists());

		// Update a different object with the same serial number
		//
		ModelAndView modelAndView = getTriviaCrudController().clone(trivia.getSer(), getMockRequest(), getMockResponse());
		ReturnType<Trivia> clonedResult = (ReturnType<Trivia>) modelAndView.getModel().get(BaseCrudController.RESULT);
		Trivia clonedTrivia = clonedResult.getPost();

		assertNotNull(clonedTrivia);
		assertNotSame(trivia, clonedTrivia);
		assertNotEquals(trivia, clonedTrivia);

		assertEquals(trivia.getAnswerCount(), clonedTrivia.getAnswerCount());

		// Make sure the answers are equal but not the same records
		//
		TriviaAnswer[] triviaAnswers = new TriviaAnswer[trivia.getAnswerCount()];
		triviaAnswers = trivia.getTriviaAnswers().toArray(triviaAnswers);

		TriviaAnswer[] clonedTriviaAnswers = new TriviaAnswer[clonedTrivia.getAnswerCount()];
		clonedTriviaAnswers = clonedTrivia.getTriviaAnswers().toArray(clonedTriviaAnswers);

		for ( int i = 0; i < triviaAnswers.length; i++ )
		{
			assertNotSame(triviaAnswers[i], clonedTriviaAnswers[i]);
			assertNotEquals(triviaAnswers[i].getSer(), clonedTriviaAnswers[i].getSer());
			assertEquals(triviaAnswers[i].getAnswer(), clonedTriviaAnswers[i].getAnswer());
		}

		// Ensure the correct answer is also properly set
		//
		TriviaAnswer correctAnswer = trivia.getCorrectAnswer();
		TriviaAnswer clonedCorrectAnswer = clonedTrivia.getCorrectAnswer();

		assertNotSame(correctAnswer, clonedCorrectAnswer);
		assertNotEquals(correctAnswer, clonedCorrectAnswer);
		assertEquals(correctAnswer.getAnswer(), clonedCorrectAnswer.getAnswer());
	}

	@Override
	public BaseViewController getViewController()
	{
		return getTriviaCrudController();
	}

	public TriviaCrudController getTriviaCrudController()
	{
		return triviaCrudController;
	}

	@Autowired
	public void setTriviaCrudController(TriviaCrudController triviaCrudController)
	{
		this.triviaCrudController = triviaCrudController;
		setViewController(triviaCrudController);
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

	public BalanceTriggerManager getBalanceTriggerManager()
	{
		return balanceTriggerManager;
	}

	@Autowired
	public void setBalanceTriggerManager(BalanceTriggerManager balanceTriggerManager)
	{
		this.balanceTriggerManager = balanceTriggerManager;
	}

	public InstantWinManager getInstantWinManager()
	{
		return instantWinManager;
	}

	@Autowired
	public void setInstantWinManager(InstantWinManager instantWinManager)
	{
		this.instantWinManager = instantWinManager;
	}

	public TriviaManager getTriviaManager()
	{
		return triviaManager;
	}

	@Autowired
	public void setTriviaManager(TriviaManager triviaManager)
	{
		this.triviaManager = triviaManager;
	}

	public TriviaAnswerManager getTriviaAnswerManager()
	{
		return triviaAnswerManager;
	}

	@Autowired
	public void setTriviaAnswerManager(TriviaAnswerManager triviaAnswerManager)
	{
		this.triviaAnswerManager = triviaAnswerManager;
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
