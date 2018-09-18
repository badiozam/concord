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

import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.research.model.survey.answer.Answer;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.respondent.Respondent;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleResponseManager extends BasePaginableManager<ResponseRecord,Response> implements ResponseManager
{
	@Override
	public void init()
	{
		super.init();

		getLogger().debug("Initializing " + this);
	}

	@Override
	public Set<Response> getAllResponses(Respondent respondent, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAllResponses(respondent.getSer(), pagination));
	}

	@Override
	public Set<Response> getAllResponsesByQuestion(Question question, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAllResponsesByQuestionid(question.getSer(), pagination));
	}

	@Override
	public Set<Response> getAllResponsesByAnswer(Answer answer, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAllResponsesByAnswerid(answer.getSer(), pagination));
	}

	@Override
	public Set<Response> getResponses(Question question, Respondent respondent)
	{
		return convertDelegates(getDao().getResponses(question.getSer(), respondent.getSer()));
	}

	@Override
	public boolean hasResponded(Answer answer, Respondent respondent)
	{
		return getDao().hasResponded(answer.getSer(), respondent.getSer());
	}

	@Override
	public <R extends Object> ClassificationReport<ResponseRecord, R> getReport(Response item, ClassificationReport<ResponseRecord, R> report, PaginationFilter pagination)
	{
		getDao().processReport(item.getDelegate(), report, pagination);
		return report;
	}

	@Override
	public ResponseRecordDao getDao()
	{
		return (ResponseRecordDao) super.getDao();
	}

	@Override
	public Response emptyInstance()
	{
		return new Response();
	}

}
