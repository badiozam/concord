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
package com.i4one.research.model.survey.respondent;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.response.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface RespondentManager extends ActivityManager<RespondentRecord, Respondent, Survey>
{
	/**
	 * Determines whether a user is eligible to participate in the given survey or not
	 * 
	 * @param survey The survey to check
	 * @param user The user account
	 * 
	 * @return True if the user is eligible to participate, false otherwise
	 */
	public boolean isEligible(Survey survey, User user);

	/**
	 * Constructs information about a respondent given their user account. If no
	 * such respondent exists, creates a new record for that respondent.
	 * 
	 * @param survey The survey for which to get the info
	 * @param user The user account
	 * 
	 * @return The user's respondent record
	 */
	@Override
	public Respondent getActivity(Survey survey, User user);

	/**
	 * Record the responses to a survey. The list of answers will be checked against the survey
	 * and respondent's pagination parameters to ensure that the list of answers
	 * is complete.
	 * 
	 * @param respondent The respondent that is responding to the survey
	 * @param responses The answers to the current survey to record
	 * 
	 * @return The list of recorded responses
	 */
	public ReturnType<List<ReturnType<Response>>> respond(Respondent respondent, Map<Question, List<Response>> responses);

	/**
	 * Get a list of all respondents for the current survey with the given pagination limiter
	 * 
	 * @param survey The survey that the respondents belong to
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of all respondents for the given survey
	 */
	public Set<Respondent> getAllRespondents(Survey survey, PaginationFilter pagination);
}
