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

import com.i4one.base.core.Utils;
import com.i4one.base.model.i18n.IString;
import com.i4one.promotion.model.PromotionRecordType;

/**
 * @author Hamid Badiozamani
 */
public class TriviaAnswerRecord extends PromotionRecordType
{
	static final long serialVersionUID = 42L;

	private Integer triviaid;
	private Integer orderweight;
	private IString answer;
	
	public TriviaAnswerRecord()
	{
		super();

		triviaid = 0;
		orderweight = 0;

		answer = new IString();
	}

	@Override
	public String getTableName()
	{
		return "triviaanswers";
	}

	public Integer getTriviaid()
	{
		return triviaid;
	}

	public void setTriviaid(Integer triviaid)
	{
		this.triviaid = triviaid;
	}

	public Integer getOrderweight()
	{
		return orderweight;
	}

	public void setOrderweight(Integer orderweight)
	{
		this.orderweight = orderweight;
	}

	public IString getAnswer()
	{
		return answer;
	}

	public void setAnswer(IString answer)
	{
		this.answer = answer;
	}

}
