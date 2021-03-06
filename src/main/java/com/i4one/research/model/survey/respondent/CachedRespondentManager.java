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
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.response.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedRespondentManager extends SimpleRespondentManager implements RespondentManager
{
	@Cacheable(value = "surveyRespondentManager", key = "target.makeKey(#survey, #user)")
	@Override
	public boolean isEligible(Survey survey, User user)
	{
		return super.isEligible(survey, user);
	}

	@Cacheable(value = "surveyRespondentManager", key = "target.makeKey(#survey, #pagination)")
	@Override
	public Set<Respondent> getAllRespondents(Survey survey, PaginationFilter pagination)
	{
		return Collections.unmodifiableSet(super.getAllRespondents(survey, pagination));
	}

	@CacheEvict(value = "surveyRespondentManager", key = "target.makeKeyFromRespondent(#respondent)")
	@Override
	public ReturnType<List<ReturnType<Response>>> respond(Respondent respondent, Map<Question, List<Response>> responses)
	{
		return super.respond(respondent, responses);
	}

	@CacheEvict(value = "surveyRespondentManager", key = "target.makeKeyFromRespondent(#respondent)")
	@Override
	public ReturnType<Respondent> create(Respondent respondent)
	{
		return super.create(respondent);
	}

	@CacheEvict(value = "surveyRespondentManager", key = "target.makeKeyFromRespondent(#respondent)")
	@Override
	public ReturnType<Respondent> update(Respondent respondent)
	{
		return super.update(respondent);
	}

	@CacheEvict(value = "surveyRespondentManager", key = "target.makeKeyFromRespondent(#respondent)")
	@Override
	public Respondent remove(Respondent respondent)
	{
		return super.remove(respondent);
	}

	/**
	 * Make a key that matches the isEligible scheme but is based off of the respondent
	 * 
	 * @param respondent The respondent to generate the key for
	 * 
	 * @return A unique key identifying the respondent
	 */
	public String makeKeyFromRespondent(Respondent respondent)
	{
		return makeKey(respondent.getSurvey(), respondent.getUser());
	}
}
