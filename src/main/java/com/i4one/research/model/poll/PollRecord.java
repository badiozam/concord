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
package com.i4one.research.model.poll;

import com.i4one.base.dao.categorizable.BaseCategorizableTerminableSiteGroupRecordType;
import com.i4one.base.dao.categorizable.CategorizableTerminableSiteGroupRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class PollRecord extends BaseCategorizableTerminableSiteGroupRecordType implements CategorizableTerminableSiteGroupRecordType
{
	static final long serialVersionUID = 42L;

	private Integer pollingstarttm;
	private Integer pollingendtm;

	private IString title;
	private Integer answercount;
	private Integer orderweight;
	private Integer questiontype;
	private Boolean randomize;
	private IString intro;
	private IString outro;

	public PollRecord()
	{
		super();

		pollingstarttm = 0;
		pollingendtm = 0;

		answercount = 0;
		orderweight = 0;
		questiontype = 0;
		randomize = false;

		title = new IString();
		intro = new IString();
		outro = new IString();
	}

	@Override
	public String getSchemaName()
	{
		return "research";
	}

	@Override
	public String getTableName()
	{
		return "polls";
	}

	@Override
	public IString getTitle()
	{
		return title;
	}

	@Override
	public void setTitle(IString title)
	{
		this.title = title;
	}

	public Boolean getRandomize()
	{
		return randomize;
	}

	public void setRandomize(Boolean randomize)
	{
		this.randomize = randomize;
	}

	public IString getIntro()
	{
		return intro;
	}

	public void setIntro(IString intro)
	{
		this.intro = intro;
	}

	public IString getOutro()
	{
		return outro;
	}

	public void setOutro(IString outro)
	{
		this.outro = outro;
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

	public Integer getPollingstarttm()
	{
		return pollingstarttm;
	}

	public void setPollingstarttm(Integer pollingstarttm)
	{
		this.pollingstarttm = pollingstarttm;
	}

	public Integer getPollingendtm()
	{
		return pollingendtm;
	}

	public void setPollingendtm(Integer pollingendtm)
	{
		this.pollingendtm = pollingendtm;
	}

}
