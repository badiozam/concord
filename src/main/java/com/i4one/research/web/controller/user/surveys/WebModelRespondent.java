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
package com.i4one.research.web.controller.user.surveys;

import com.i4one.base.core.Utils;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;
import com.i4one.research.model.survey.answer.Answer;
import com.i4one.research.model.survey.answer.AnswerManager;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.respondent.Respondent;
import com.i4one.research.model.survey.response.Response;
import com.i4one.research.model.survey.response.ResponseManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Hamid Badiozamani
 */
public class WebModelRespondent extends Respondent implements WebModel
{
	static final long serialVersionUID = 42L;

	private Model model;
	
	private transient ResponseManager responseManager;
	private transient AnswerManager answerManager;

	private transient Map<Integer, List<Integer>> selectionResponses;
	private transient Map<Integer, String> openAnswerResponses;

	public WebModelRespondent()
	{
		super();

		setCurrentpage(0);
		setLastPlayedTimeSeconds(Utils.currentTimeSeconds());

		selectionResponses = null;
		openAnswerResponses = null;
	}

	public Map<Integer, List<Integer>> getSelectionResponses()
	{
		if ( selectionResponses == null )
		{
			selectionResponses = new HashMap<>();

			// We initialize the response map by retrieving all of the respondent's information from the database
			// and populating the serial numbers. Typically a PropertyEditor or Converter would be used to do this
			// step automatically but for some reason it's not working with complex types so we do it here
			// programmatically
			//
			getSurvey().getQuestions()
				.stream()
				.forEach( (currQuestion) ->
			{
				getLogger().debug("Getting all responses for question: " + currQuestion);
				Set<Response> responses = getResponseManager().getResponses(currQuestion, this);

				// Extract all of the answer serial numbers
				//
				List<Integer> answers = new ArrayList<>();
				responses.stream().forEach((currResponse) ->
				{
					answers.add(currResponse.getAnswer(false).getSer());
				});

				// Add them to the list for display
				//
				selectionResponses.put(currQuestion.getSer(), answers);
			});

			getLogger().debug("Setting selection responses from: " + getSurvey().getQuestions() + "\n to " + selectionResponses );
		}

		return selectionResponses;
	}

	public void setSelectionResponses(Map<Integer, List<Integer>> selectionResponses)
	{
		this.selectionResponses = selectionResponses;
	}

	public Map<Integer, String> getOpenAnswerResponses()
	{
		if ( openAnswerResponses == null )
		{
			openAnswerResponses = new HashMap<>();

			// We initialize the response map by retrieving all of the respondent's information from the database
			// and populating the serial numbers. Typically a PropertyEditor or Converter would be used to do this
			// step automatically but for some reason it's not working with complex types so we do it here
			// programmatically
			//
			//for ( Question currQuestion : getSurvey().getQuestions() )
			getSurvey().getQuestions().stream().forEach((currQuestion) ->
			{
				Set<Response> responses = getResponseManager().getResponses(currQuestion, this);
				if (!responses.isEmpty())
				{
					openAnswerResponses.put(currQuestion.getSer(), responses.iterator().next().getOpenAnswer());
				}
				else
				{
					openAnswerResponses.put(currQuestion.getSer(), "");
				}
			});
		}

		return openAnswerResponses;
	}

	public void setOpenAnswerResponses(Map<Integer, String> openAnswerResponses)
	{
		this.openAnswerResponses = openAnswerResponses;
	}

	/**
	 * Flatten the map for use in recording the responses. This differs from the selection responses
	 * since the selection responses display all of the possible response whereas this field contains
	 * only the responses that the user selected.
	 * 
	 * @return The flattened list of responses containing all question <=> responses mappings
	 */
	public Map<Question, List<Response>> getResponses()
	{
		// This method may be called multiple times and its results should be cached
		//
		getLogger().debug("getResponses() called");

		// Note that this is done because the PropertyEditor classes are not being called
		// for some reason so we have to do the conversion manually
		//
		Map<Question, List<Response>> retVal = new LinkedHashMap<>();

		getLogger().debug("Selection responses: " + getSelectionResponses().keySet());

		getSelectionResponses().keySet()
			.stream()
			.filter((selectionQuestionId) -> ( selectionQuestionId != null ))
			.forEach((selectionQuestionId) ->
		{
			// Note that it's important for the question to be loaded entirely
			// since the map makes use of the hashCode() method which relies
			// on the unique key generation of the question. That in turn relies
			// on the question field as well as the survey field.
			//
			Question currQuestion = new Question();
			currQuestion.resetDelegateBySer(selectionQuestionId);
			currQuestion.loadedVersion();
			
			List<Integer> responseList = getSelectionResponses().get(selectionQuestionId);
			List<Response> currResponses = new ArrayList<>();
			
			// Add all of the responses for this question
			//
			if ( responseList != null )
			{
				responseList.stream().forEach( (answerId) ->
				{
					Response currResponse = new Response();
					currResponse.getAnswer(false).resetDelegateBySer(answerId);
					
					currResponse.setRespondent(this);
					currResponse.setQuestion(currQuestion);
					currResponse.setAnswer(currResponse.getAnswer());
					
					currResponses.add(currResponse);
				});
			}

			retVal.put(currQuestion, currResponses);
		});

		getOpenAnswerResponses().keySet()
			.stream()
			.filter((openQuestionId) -> ( openQuestionId != null ))
			.forEach((openQuestionId) ->
		{
			// Note that it's important for the question to be loaded entirely
			// since the map makes use of the hashCode() method which relies
			// on the unique key generation of the question. That in turn relies
			// on the question field as well as the survey field.
			//
			Question currQuestion = new Question();
			currQuestion.resetDelegateBySer(openQuestionId);
			currQuestion.loadedVersion();
			
			String response = getOpenAnswerResponses().get(openQuestionId);
			
			Response currResponse = new Response();
			
			currResponse.setRespondent(this);
			currResponse.setOpenAnswer(Utils.trimString(response));
			currResponse.setQuestion(currQuestion);
			
			// There will always only be one open answer but we need to conform
			//
			List<Response> currResponses = new ArrayList<>(1);
			currResponses.add(currResponse);
			
			retVal.put(currQuestion, currResponses);
		});

		return retVal;
	}

	/**
	 * Get a mapping of all questions and a map of their possible answers for use
	 * by the form
	 * 
	 * @return The mapping of questionid => (answerid => answer) for the survey
	 */
	public Map<Integer, Map<Integer, String>> getAnswerListMap()
	{
		HashMap<Integer, Map<Integer, String>> retVal = new HashMap<>();

		getSurvey().getQuestions().stream().forEach((currQuestion) ->
		{
			Set<Answer> answers = getAnswerManager().getAnswers(currQuestion, SimplePaginationFilter.NONE);
			Map<Integer, String> answerMap = new LinkedHashMap<>(answers.size());

			// Need a "not-selected" answer
			//
			if ( currQuestion.getQuestionType() == Question.TYPE_SINGLEANSWER_SELECT )
			{
				answerMap.put(0, "--");
			}

			answers.stream().forEach((answer) ->
			{
				answerMap.put(answer.getSer(), answer.getAnswer().get(model.getLanguage()));
			});

			retVal.put(currQuestion.getSer(), answerMap);
		});

		return retVal;
	}

	public AnswerManager getAnswerManager()
	{
		return answerManager;
	}

	@Autowired
	public void setAnswerManager(AnswerManager answerManager)
	{
		this.answerManager = answerManager;
	}

	public ResponseManager getResponseManager()
	{
		return responseManager;
	}

	@Autowired
	public void setResponseManager(ResponseManager responseManager)
	{
		this.responseManager = responseManager;
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
