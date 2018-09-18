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
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.activity.BaseSiteGroupActivityPrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.user.User;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.response.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * XXX: Not being used
 * @author Hamid Badiozamani
 */
public class PrivilegedRespondentManager extends BaseSiteGroupActivityPrivilegedManager<RespondentRecord, Respondent, Survey> implements RespondentManager
{
	private RespondentManager respondentManager;

	@Override
	public User getUser(Respondent item)
	{
		return item.getUser();
	}

	@Override
	public SingleClient getClient(Respondent item)
	{
		return item.getUser().getClient();
	}

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

	@Transactional(readOnly = false)
	@Override
	public ReturnType<List<ReturnType<Response>>> respond(Respondent respondent, Map<Question, List<Response>> responses)
	{
		checkWrite(respondent, getUser(respondent), "update");
		return getRespondentManager().respond(respondent, responses);
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

	public void setRespondentManager(RespondentManager respondentManager)
	{
		this.respondentManager = respondentManager;
	}

	@Override
	public ActivityReport getReport(Respondent item, TopLevelReport report, PaginationFilter pagination)
	{
		return getRespondentManager().getReport(item, report, pagination);
	}

}
