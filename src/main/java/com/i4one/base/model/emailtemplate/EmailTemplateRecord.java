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
package com.i4one.base.model.emailtemplate;

import com.i4one.base.dao.BaseClientRecordType;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class EmailTemplateRecord extends BaseClientRecordType implements ClientRecordType
{
	static final long serialVersionUID = 42L;

	private String key;

	private IString fromaddr;
	private IString bcc;

	private IString replyto;
	private IString subject;

	private IString htmlbody;
	private IString textbody;

	public EmailTemplateRecord()
	{
		super();

		key = "";

		fromaddr = new IString();
		bcc = new IString();

		replyto = new IString();
		subject = new IString();

		htmlbody = new IString();
		textbody = new IString();
	}

	@Override
	public String getTableName()
	{
		return "emailtemplates";
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public IString getFromaddr()
	{
		return fromaddr;
	}

	public void setFromaddr(IString fromaddr)
	{
		this.fromaddr = fromaddr;
	}

	public IString getSubject()
	{
		return subject;
	}

	public void setSubject(IString subject)
	{
		this.subject = subject;
	}

	public IString getHtmlbody()
	{
		return htmlbody;
	}

	public void setHtmlbody(IString htmlbody)
	{
		this.htmlbody = htmlbody;
	}

	public IString getTextbody()
	{
		return textbody;
	}

	public void setTextbody(IString textbody)
	{
		this.textbody = textbody;
	}

	public IString getBcc()
	{
		return bcc;
	}

	public void setBcc(IString bcc)
	{
		this.bcc = bcc;
	}

	public IString getReplyto()
	{
		return replyto;
	}

	public void setReplyto(IString replyto)
	{
		this.replyto = replyto;
	}

}
