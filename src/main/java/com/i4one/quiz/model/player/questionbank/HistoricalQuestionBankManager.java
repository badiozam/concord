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
package com.i4one.quiz.model.player.questionbank;

import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.HistoricalManager;
import com.i4one.base.model.manager.PrivilegedManager;

/**
 * @author Hamid Badiozamani
 */
public class HistoricalQuestionBankManager extends BaseHistoricalManager<QuestionBankRecord, QuestionBank> implements HistoricalManager<QuestionBankRecord, QuestionBank>, QuestionBankManager
{
	private PrivilegedManager<QuestionBankRecord, QuestionBank> privilegedQuestionBankManager;

	public QuestionBankManager getQuestionBankManager()
	{
		return (QuestionBankManager) getPrivilegedQuestionBankManager();
	}

	public PrivilegedManager<QuestionBankRecord, QuestionBank> getPrivilegedQuestionBankManager()
	{
		return privilegedQuestionBankManager;
	}

	public <P extends QuestionBankManager & PrivilegedManager<QuestionBankRecord, QuestionBank>>
	 void setPrivilegedQuestionBankManager(P privilegedQuestionBankManager)
	{
		this.privilegedQuestionBankManager = privilegedQuestionBankManager;
	}

	@Override
	public PrivilegedManager<QuestionBankRecord, QuestionBank> getImplementationManager()
	{
		return getPrivilegedQuestionBankManager();
	}
}
