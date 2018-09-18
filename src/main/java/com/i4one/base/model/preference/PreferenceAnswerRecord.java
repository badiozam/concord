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
package com.i4one.base.model.preference;

import com.i4one.base.dao.BaseRecordType;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class PreferenceAnswerRecord extends BaseRecordType implements RecordType
{
	static final long serialVersionUID = 42L;

	private Integer preferenceid;
	private Integer orderweight;
	private IString answer;
	
	public PreferenceAnswerRecord()
	{
		super();

		preferenceid = 0;
		orderweight = 0;

		answer = new IString();
	}

	@Override
	public String getTableName()
	{
		return "preferenceanswers";
	}

	public Integer getPreferenceid()
	{
		return preferenceid;
	}

	public void setPreferenceid(Integer preferenceid)
	{
		this.preferenceid = preferenceid;
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
