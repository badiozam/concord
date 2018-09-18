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
package com.i4one.research.model.survey.response;

import com.i4one.base.model.manager.pagination.PaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.research.model.survey.answer.Answer;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.respondent.Respondent;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface ResponseManager extends PaginableManager<ResponseRecord, Response>
{
	/**
	 * Retrieves all of answers for a particular respondent
	 *
	 * @param respondent The respondent whose answers to retrieve
	 * @param pagination The pagination/sort ordering information
	 *
	 * @return A list of all responses for the given respondent
	 */
	public Set<Response> getAllResponses(Respondent respondent, PaginationFilter pagination);

	/**
	 * Retrieves all of the responses given for a particular question
	 *
	 * @param question The question
	 * @param pagination The pagination/sort ordering information
	 *
	 * @return A list of all of the responses for the given question
	 */
	public Set<Response> getAllResponsesByQuestion(Question question, PaginationFilter pagination);

	/**
	 * Retrieves all of the responses given for a particular answer
	 *
	 * @param answer The answer
	 * @param pagination The pagination/sort ordering information
	 *
	 * @return A list of all of the responses for the given answer
	 */
	public Set<Response> getAllResponsesByAnswer(Answer answer, PaginationFilter pagination);

	/**
	 * Retrieves all of the responses given for a particular question by a particular respondent
	 * 
	 * @param question The question whose responses to retrieve
	 * @param respondent The respondent whose responses we're to retrieve
	 * 
	 * @return The (potentially empty) list of responses for the given question by the given respondent
	 */
	public Set<Response> getResponses(Question question, Respondent respondent);

	/**
	 * Determines whether the given respondent has selected a particular answer or not.
	 * 
	 * @param answer The answer that the user may have selected
	 * @param respondent The respondent
	 * 
	 * @return True if the answer was selected by the user, false otherwise
	 */
	public boolean hasResponded(Answer answer, Respondent respondent);
}
