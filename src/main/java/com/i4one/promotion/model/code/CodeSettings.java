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
package com.i4one.promotion.model.code;

import com.i4one.base.model.MessageKeySettings;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class CodeSettings extends MessageKeySettings
{
	private SingleClient client;

	private IString nameSingle;
	private IString namePlural;

	private IString instructions;
	private IString defaultOutro;

	public CodeSettings()
	{
		nameSingle = new IString();
		namePlural = new IString();

		defaultOutro = new IString();
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

	public List<Message> getDefaultOutroMessages(SingleClient client, String key)
	{
		return getMessages(client, key, defaultOutro);
	}


	public List<Message> getInstructions(SingleClient client, String key)
	{
		return getMessages(client, key, instructions);
	}

	public void setNames(List<Message> singleNames, List<Message> pluralNames)
	{
		setNameSingle(messagesToIString(singleNames));
		setNamePlural(messagesToIString(pluralNames));
	}

	public void setInstructions(List<Message> instructions)
	{
		setInstructions(messagesToIString(instructions));
	}

	public void setOutro(List<Message> outros)
	{
		setDefaultOutro(messagesToIString(outros));
	}

	public IString getNameSingle()
	{
		return nameSingle;
	}

	public void setNameSingle(IString nameSingle)
	{
		this.nameSingle = nameSingle;
	}

	public IString getNamePlural()
	{
		return namePlural;
	}

	public void setNamePlural(IString namePlural)
	{
		this.namePlural = namePlural;
	}

	public IString getDefaultOutro()
	{
		return defaultOutro;
	}

	public void setDefaultOutro(IString defaultOutro)
	{
		this.defaultOutro = defaultOutro;
	}

	public IString getInstructions()
	{
		return instructions;
	}

	public void setInstructions(IString instructions)
	{
		this.instructions = instructions;
	}
}
