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
package com.i4one.research.web.controller.user.polls;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;
import com.i4one.research.model.poll.Poll;
import com.i4one.research.model.poll.PollAnswer;
import com.i4one.research.model.poll.PollAnswerManager;
import com.i4one.research.model.poll.PollResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class WebModelPollResponse extends PollResponse implements WebModel
{
	private Model model;
	private transient PollAnswerManager pollAnswerManager;
	
	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		getLogger().debug("Validating poll response with poll " + getPoll());
		if ( !getPoll().exists() )
		{
			errors.addError(new ErrorMessage("msg.research.PollResponse.poll.invalidPoll", "This poll does not exist", new Object[]{"item", this}));
		}
		else if ( !getPoll().isLive(getModel().getTimeInSeconds()) )
		{
			errors.addError(new ErrorMessage("msg.research.PollResponse.poll.notLive", "This poll is not currently available", new Object[]{"item", this}));
		}

		if ( !getPollAnswer().exists() )
		{
			errors.addError(new ErrorMessage("pollAnswer", "msg.research.PollResponse.invalidPollAnswer", "Please select an answer", new Object[]{"item", this}));
		}

		// The user has to be set
		//
		if ( !getUser().exists() )
		{
			errors.addError(new ErrorMessage("msg.research.pollResponseManager.processPoll.userdne", "You must be logged in", new Object[] { "user", getUser()}, null));
		}

		return errors;
	}

	/**
	 * Get a mapping of all answers
	 * 
	 * @return The mapping of answerid =&gt; answer for the poll
	 */
	public Map<Integer, String> getAnswerMap()
	{
		// Make sure we have the most up-to-date answers
		//
		getPoll().setPollAnswers(getPollAnswerManager().getAnswers(getPoll()));

		Set<PollAnswer> answers = getPoll().getPollAnswers();
		Map<Integer, String> answerMap = new LinkedHashMap<>(answers.size());

		// Need a "not-selected" answer
		//
		if ( getPoll().getQuestionType() == Poll.TYPE_SINGLEANSWER_SELECT )
		{
			answerMap.put(0, "--");
		}

		String lang = model.getLanguage();

		List<PollAnswer> listAnswers = new ArrayList<>(answers);
		if ( getPoll().getRandomize() )
		{
			Collections.shuffle(listAnswers);
		}

		listAnswers.stream().forEach((answer) ->
		{
			answerMap.put(answer.getSer(), answer.getAnswer().get(lang));
		});

		return answerMap;
	}

	public PollAnswerManager getPollAnswerManager()
	{
		return pollAnswerManager;
	}

	public void setPollAnswerManager(PollAnswerManager pollAnswerManager)
	{
		this.pollAnswerManager = pollAnswerManager;
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
