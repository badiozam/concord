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

import com.i4one.base.dao.SimpleSortableRecordType;
import com.i4one.base.dao.SortableRecordType;
import com.i4one.base.dao.categorizable.BaseCategorizableTerminableSiteGroupRecordType;
import com.i4one.base.dao.categorizable.CategorizableTerminableSiteGroupRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class TriviaRecord extends BaseCategorizableTerminableSiteGroupRecordType implements CategorizableTerminableSiteGroupRecordType,SortableRecordType
{
	static final long serialVersionUID = 42L;

	private final SortableRecordType sortableRecord;

	private IString title;
	private IString descr;

	private Integer answercount;
	private Integer questiontype;
	private Integer correctanswerid;

	private Boolean randomize;

	private IString intro;
	private IString outro;

	public TriviaRecord()
	{
		super();

		answercount = 0;
		questiontype = 0;
		randomize = true;

		title = new IString();
		descr = new IString();
		intro = new IString();
		outro = new IString();

		correctanswerid = 0;

		sortableRecord = new SimpleSortableRecordType(this);
	}

	@Override
	public String getSchemaName()
	{
		return "promotion";
	}

	@Override
	public String getTableName()
	{
		return "trivias";
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

	@Override
	public Integer getOrderweight()
	{
		return sortableRecord.getOrderweight();
	}

	@Override
	public void setOrderweight(Integer orderweight)
	{
		sortableRecord.setOrderweight(orderweight);
	}

	public Integer getQuestiontype()
	{
		return questiontype;
	}

	public void setQuestiontype(Integer questiontype)
	{
		this.questiontype = questiontype;
	}

	public Integer getCorrectanswerid()
	{
		return correctanswerid;
	}

	public void setCorrectanswerid(Integer correctanswerid)
	{
		this.correctanswerid = correctanswerid;
	}

	public IString getDescr()
	{
		return descr;
	}

	public void setDescr(IString descr)
	{
		this.descr = descr;
	}
}
