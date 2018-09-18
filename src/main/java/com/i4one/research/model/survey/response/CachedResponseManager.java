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

import com.i4one.base.model.ReturnType;
import com.i4one.research.model.survey.answer.Answer;
import com.i4one.research.model.survey.respondent.Respondent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("research.ResponseManager")
public class CachedResponseManager extends SimpleResponseManager implements ResponseManager
{
	@Cacheable(value = "surveyResponseManager", key = "target.makeKeyForResponse(#answer, #respondent)")
	@Override
	public boolean hasResponded(Answer answer, Respondent respondent)
	{
		return super.hasResponded(answer, respondent);
	}


	@CacheEvict(value = "surveyResponseManager", key = "target.makeKeyFromResponse(#response)")
	@Override
	public ReturnType<Response> create(Response response)
	{
		return super.create(response);
	}

	@CacheEvict(value = "surveyResponseManager", key = "target.makeKeyFromResponse(#response)")
	@Override
	public ReturnType<Response> update(Response response)
	{
		return super.update(response);
	}

	@CacheEvict(value = "surveyResponseManager", key = "target.makeKeyFromResponse(#response)")
	@Override
	public Response remove(Response response)
	{
		return super.remove(response);
	}

	/**
	 * Make a key that matches the isEligible scheme but is based off of the response
	 * 
	 * @param response The response to generate the key for
	 * 
	 * @return A unique key identifying the response
	 */
	public String makeKeyFromResponse(Response response)
	{
		return makeKey(response.getAnswer(false).getSer(), response.getRespondent(false));
	}

	public String makeKeyForResponse(Answer answer, Respondent respondent)
	{
		return answer.getSer() + "-" + respondent.getSer();
	}
}
