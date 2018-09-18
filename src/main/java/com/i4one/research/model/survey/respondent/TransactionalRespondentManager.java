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
import com.i4one.base.model.manager.BaseTransactionalActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.transaction.Transaction;
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
@Service("research.RespondentManager")
public class TransactionalRespondentManager extends BaseTransactionalActivityManager<RespondentRecord, Respondent, Survey> implements RespondentManager
{
	private RespondentManager respondentManager;

	public RespondentManager getRespondentManager()
	{
		return respondentManager;
	}

	@Autowired
	public void setRespondentManager(RespondentManager instantWinningRespondentManager)
	{
		this.respondentManager = instantWinningRespondentManager;
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
		ReturnType<List<ReturnType<Response>>> retVal = getRespondentManager().respond(respondent, responses);

		// The responses for each individual incoming question are created and their results stored in the ReturnType's
		// getPost(). However, the Respondent record itself is updated with the current page, last access time, etc.
		// and its status is stored in the respondentManager.update key. It is that updated respondent record we're
		// looking for to tie the transaction record to
		//
		Respondent dbRespondent = (Respondent) retVal.getChain(this, "update").getPost();

		Transaction t = newTransaction(respondent.getUser());

		setTransactionDescr(t, "msg.respondentManager.respond.xaction.descr", "respondent", dbRespondent, "responses", responses);

		// See if we can locate a user balance update transaction and set us
		// as the the parent transaction
		//
		t = createTransaction(retVal, t);

		// Set our master transaction record since we've created a new transaction
		//
		retVal.put("transaction", t);

		return retVal;
	}

	@Override
	public Set<Respondent> getAllRespondents(Survey survey, PaginationFilter pagination)
	{
		return getRespondentManager().getAllRespondents(survey, pagination);
	}
}
