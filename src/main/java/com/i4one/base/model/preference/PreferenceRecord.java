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

import com.i4one.base.dao.categorizable.BaseCategorizableTerminableSiteGroupRecordType;
import com.i4one.base.dao.categorizable.CategorizableTerminableSiteGroupRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class PreferenceRecord extends BaseCategorizableTerminableSiteGroupRecordType implements CategorizableTerminableSiteGroupRecordType
{
	static final long serialVersionUID = 42L;

	private IString title;

	private Integer answercount;
	private Integer orderweight;
	private Integer questiontype;

	private Integer minresponses;
	private Integer maxresponses;
	private String validanswer;

	private IString defaultvalue;

	public PreferenceRecord()
	{
		super();

		answercount = 0;
		orderweight = 0;
		questiontype = 0;

		minresponses = 0;
		maxresponses = 0;
		validanswer = "";

		title = new IString();
		defaultvalue = new IString();
	}

	@Override
	public String getTableName()
	{
		return "preferences";
	}

	public IString getTitle()
	{
		return title;
	}

	public void setTitle(IString title)
	{
		this.title = title;
	}

	public Integer getAnswercount()
	{
		return answercount;
	}

	public void setAnswercount(Integer answercount)
	{
		this.answercount = answercount;
	}

	public Integer getOrderweight()
	{
		return orderweight;
	}

	public void setOrderweight(Integer orderweight)
	{
		this.orderweight = orderweight;
	}

	public Integer getQuestiontype()
	{
		return questiontype;
	}

	public void setQuestiontype(Integer questiontype)
	{
		this.questiontype = questiontype;
	}

	public IString getDefaultvalue()
	{
		return defaultvalue;
	}

	public void setDefaultvalue(IString defaultvalue)
	{
		this.defaultvalue = defaultvalue;
	}

	public Integer getMinresponses()
	{
		return minresponses;
	}

	public void setMinresponses(Integer minresponses)
	{
		this.minresponses = minresponses;
	}

	public Integer getMaxresponses()
	{
		return maxresponses;
	}

	public void setMaxresponses(Integer maxresponses)
	{
		this.maxresponses = maxresponses;
	}

	public String getValidanswer()
	{
		return validanswer;
	}

	public void setValidanswer(String validanswer)
	{
		this.validanswer = validanswer;
	}

}
