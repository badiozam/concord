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
package com.i4one.promotion.model.trivia;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.BaseTerminableActivityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleTriviaResponseManager extends BaseTerminableActivityManager<TriviaResponseRecord, TriviaResponse, Trivia> implements TriviaResponseManager
{
	private TriviaManager triviaManager;

	@Override
	public TriviaResponse emptyInstance()
	{
		return new TriviaResponse();
	}

	@Override
	public ReturnType<TriviaResponse> update(TriviaResponse triviaResponse)
	{
		throw new UnsupportedOperationException("Can't update a trivia response.");
	}

	@Override
	public TriviaResponseRecordDao getDao()
	{
		return (TriviaResponseRecordDao) super.getDao();
	}

	public TriviaManager getTriviaManager()
	{
		return triviaManager;
	}

	@Autowired
	public void setTriviaManager(TriviaManager triviaManager)
	{
		this.triviaManager = triviaManager;
	}
}
