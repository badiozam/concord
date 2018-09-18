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
package com.i4one.research.model.survey.answer;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.research.model.survey.question.Question;
import java.util.Collections;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("answerManager")
public class CachedAnswerManager extends SimpleAnswerManager implements AnswerManager
{
	@Cacheable(value = "surveyAnswerManager", key = "target.makeKey(#question, 'getAnswers', #pagination)")
	@Override
	public Set<Answer> getAnswers(Question question, PaginationFilter pagination)
	{
		return Collections.unmodifiableSet(super.getAnswers(question, pagination));
	}

	@CacheEvict(value = "surveyAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Answer> clone(Answer item)
	{
		return super.clone(item);
	}

	@CacheEvict(value = "surveyAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Answer> create(Answer item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "surveyAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Answer> update(Answer item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "surveyAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public Answer remove(Answer item)
	{
		return super.remove(item);
	}
}
