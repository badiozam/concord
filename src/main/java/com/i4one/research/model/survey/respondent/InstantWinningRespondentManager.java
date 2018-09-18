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

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.instantwin.UserInstantWin;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.instantwinnable.BaseInstantWinningManager;
import com.i4one.base.model.user.User;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.response.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class InstantWinningRespondentManager extends BaseInstantWinningManager<RespondentRecord, Respondent, Survey> implements RespondentManager
{
	private RespondentManager respondentManager;

	@Override
	public RespondentManager getImplementationManager()
	{
		return getRespondentManager();
	}

	@Override
	public boolean isEligible(Survey survey, User user)
	{
		return getRespondentManager().isEligible(survey, user);
	}

	@Override
	public User getUser(Respondent item)
	{
		return item.getUser(false);
	}

	@Override
	public RecordTypeDelegator<?> getAttachee(Respondent item)
	{
		return item.getSurvey(false);
	}

	@Override
	protected void processAttached(ReturnType<Respondent> processedItem, String methodName) 
	{
		// Ignore instant win processing requests for creation/update of a respondent record
		// since we'll take care of this only in the respond(..) implementation
		//
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<List<ReturnType<Response>>> respond(Respondent respondent, Map<Question, List<Response>> responses)
	{
		ReturnType<List<ReturnType<Response>>> retVal = getRespondentManager().respond(respondent, responses);

		// We only process the instant win only on the last page of the survey
		//
		ReturnType<Respondent> dbRespondent = (ReturnType<Respondent>) retVal.getChain(getInterfaceName() + ".update");
		if ( dbRespondent != null && dbRespondent.getPost().getHasFinished() )
		{
			List<ReturnType<UserInstantWin>> processedInstantWins = getUserInstantWinManager().processInstantWins(getUser(respondent),
				this,
				"respond",
				respondent.getSurvey(false),
				SimplePaginationFilter.NONE);


			retVal.put("processedInstantWins", processedInstantWins);
			getLogger().debug("Processed instantWins = " + processedInstantWins);
		}

		return retVal;
	}

	@Override
	public Set<Respondent> getAllRespondents(Survey survey, PaginationFilter pagination)
	{
		return getRespondentManager().getAllRespondents(survey, pagination);
	}

	public RespondentManager getRespondentManager()
	{
		return respondentManager;
	}

	@Autowired
	public void setRespondentManager(RespondentManager triggeredRespondentManager)
	{
		this.respondentManager = triggeredRespondentManager;
	}
}
