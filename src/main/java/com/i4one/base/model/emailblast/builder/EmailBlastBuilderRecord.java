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
package com.i4one.base.model.emailblast.builder;

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class EmailBlastBuilderRecord extends BaseClientRecordType implements ClientRecordType
{
	static final long serialVersionUID = 42L;

	private IString title;
	private Integer emailtemplateid;

	private IString target;
	private String targetsql;

	private String savedstate;

	public EmailBlastBuilderRecord()
	{
		super();

		title = new IString("");
		target = new IString("");

		targetsql = "";
		savedstate = "";

		emailtemplateid = 0;
	}

	@Override
	public String getTableName()
	{
		return "emailblastbuilders";
	}

	public IString getTitle()
	{
		return title;
	}

	public void setTitle(IString title)
	{
		this.title = title;
	}

	public Integer getEmailtemplateid()
	{
		return emailtemplateid;
	}

	public void setEmailtemplateid(Integer emailtemplateid)
	{
		this.emailtemplateid = emailtemplateid;
	}

	public IString getTarget()
	{
		return target;
	}

	public void setTarget(IString target)
	{
		this.target = target;
	}

	public String getTargetsql()
	{
		return targetsql;
	}

	public void setTargetsql(String targetsql)
	{
		this.targetsql = targetsql;
	}

	public String getSavedstate()
	{
		return savedstate;
	}

	public void setSavedstate(String savedstate)
	{
		this.savedstate = savedstate;
	}

}
