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

import com.i4one.base.dao.activity.ActivityRecordType;
import com.i4one.base.dao.activity.BaseActivityRecordType;

/**
 * @author Hamid Badiozamani
 */
public class RespondentRecord extends BaseActivityRecordType implements ActivityRecordType
{
	static final long serialVersionUID = 42L;

	private Integer startpage;
	private Integer currentpage;
	private Integer updatetime;
	private Boolean hasfinished;

	public RespondentRecord()
	{
		super();

		startpage = 0;
		currentpage = 0;
		updatetime = 0;
		hasfinished = false;
	}
	
	@Override
	public String getSchemaName()
	{
		return "research";
	}

	@Override
	public String getTableName()
	{
		return "surveyrespondents";
	}

	public Integer getStartpage()
	{
		return startpage;
	}

	public void setStartpage(Integer startpage)
	{
		this.startpage = startpage;
	}

	public Integer getCurrentpage()
	{
		return currentpage;
	}

	public void setCurrentpage(Integer currentpage)
	{
		this.currentpage = currentpage;
	}

	public Integer getUpdatetime()
	{
		return updatetime;
	}

	public void setUpdatetime(Integer updatetime)
	{
		this.updatetime = updatetime;
	}

	public Boolean getHasfinished()
	{
		return hasfinished;
	}

	public void setHasfinished(Boolean hasfinished)
	{
		this.hasfinished = hasfinished;
	}

}
