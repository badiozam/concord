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
package com.i4one.base.model.accesscode;

import com.i4one.base.model.MessageKeySettings;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class AccessCodeSettings extends MessageKeySettings
{
	private SingleClient client;

	private IString nameSingle;
	private IString namePlural;

	private IString defaultDescr;
	private IString defaultAccessDenied;

	public AccessCodeSettings()
	{
		nameSingle = new IString();
		namePlural = new IString();

		defaultDescr = new IString();
		defaultAccessDenied = new IString();
	}

	public SingleClient getClient()
	{
		return client;
	}

	public void setClient(SingleClient client)
	{
		this.client = client;
	}

	public List<Message> getNameSingleMessages(SingleClient client, String key)
	{
		return getMessages(client, key, nameSingle);
	}

	public List<Message> getNamePluralMessages(SingleClient client, String key)
	{
		return getMessages(client, key, namePlural);
	}

	public List<Message> getDefaultAccessDeniedMessages(SingleClient client, String key)
	{
		return getMessages(client, key, defaultAccessDenied);
	}


	public List<Message> getDescr(SingleClient client, String key)
	{
		return getMessages(client, key, defaultDescr);
	}

	public void setNames(List<Message> singleNames, List<Message> pluralNames)
	{
		setNameSingle(messagesToIString(singleNames));
		setNamePlural(messagesToIString(pluralNames));
	}

	public void setDescr(List<Message> instructions)
	{
		setDescr(messagesToIString(instructions));
	}

	public void setOutro(List<Message> outros)
	{
		setDefaultAccessDenied(messagesToIString(outros));
	}

	@Override
	public IString getNameSingle()
	{
		return nameSingle;
	}

	public void setNameSingle(IString nameSingle)
	{
		this.nameSingle = nameSingle;
	}

	@Override
	public IString getNamePlural()
	{
		return namePlural;
	}

	public void setNamePlural(IString namePlural)
	{
		this.namePlural = namePlural;
	}

	public IString getDefaultAccessDenied()
	{
		return defaultAccessDenied;
	}

	public void setDefaultAccessDenied(IString defaultOutro)
	{
		this.defaultAccessDenied = defaultOutro;
	}

	public IString getDescr()
	{
		return defaultDescr;
	}

	public void setDescr(IString instructions)
	{
		this.defaultDescr = instructions;
	}
}
