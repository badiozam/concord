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
package com.i4one.promotion.web.controller.user.trivias;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;
import com.i4one.promotion.model.trivia.Trivia;
import com.i4one.promotion.model.trivia.TriviaAnswer;
import com.i4one.promotion.model.trivia.TriviaAnswerManager;
import com.i4one.promotion.model.trivia.TriviaResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class WebModelTriviaResponse extends TriviaResponse implements WebModel
{
	private Model model;
	private transient TriviaAnswerManager triviaAnswerManager;
	
	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		getLogger().debug("Validating trivia response with trivia " + getTrivia());
		if ( !getTrivia().exists() )
		{
			errors.addError(new ErrorMessage("msg.promotion.TriviaResponse.trivia.invalidTrivia", "This trivia game does not exist", new Object[]{"item", this}));
		}
		else if ( !getTrivia().isLive(getModel().getTimeInSeconds()) )
		{
			errors.addError(new ErrorMessage("msg.promotion.TriviaResponse.trivia.notLive", "This trivia game is not currently available", new Object[]{"item", this}));
		}

		if ( !getTriviaAnswer().exists() )
		{
			errors.addError(new ErrorMessage("triviaAnswer", "msg.promotion.TriviaResponse.invalidTriviaAnswer", "Please select an answer", new Object[]{"item", this}));
		}

		// The user has to be set
		//
		if ( !getUser().exists() )
		{
			errors.addError(new ErrorMessage("msg.promotion.triviaResponseManager.processTrivia.userdne", "You must be logged in", new Object[] { "user", getUser()}, null));
		}

		return errors;
	}

	/**
	 * Get a mapping of all answers
	 * 
	 * @return The mapping of answerid =&gt; answer for the trivia
	 */
	public Map<Integer, String> getAnswerMap()
	{
		// Make sure we have the most up-to-date answers
		//
		getTrivia().setTriviaAnswers(getTriviaAnswerManager().getAnswers(getTrivia(), SimplePaginationFilter.NONE));

		Set<TriviaAnswer> answers = getTrivia().getTriviaAnswers();
		Map<Integer, String> answerMap = new LinkedHashMap<>(answers.size());

		// Need a "not-selected" answer
		//
		if ( getTrivia().getQuestionType() == Trivia.TYPE_SINGLEANSWER_SELECT )
		{
			answerMap.put(0, "--");
		}

		String lang = model.getLanguage();

		List<TriviaAnswer> listAnswers = new ArrayList<>(answers);
		if ( getTrivia().getRandomize() )
		{
			Collections.shuffle(listAnswers);
		}

		listAnswers.stream().forEach((answer) ->
		{
			answerMap.put(answer.getSer(), answer.getAnswer().get(lang));
		});

		return answerMap;
	}

	public TriviaAnswerManager getTriviaAnswerManager()
	{
		return triviaAnswerManager;
	}

	public void setTriviaAnswerManager(TriviaAnswerManager triviaAnswerManager)
	{
		this.triviaAnswerManager = triviaAnswerManager;
	}

	@Override
	public Model getModel()
	{
		return model;
	}

	@Override
	public void setModel(Model model)
	{
		this.model = model;
	}
}
