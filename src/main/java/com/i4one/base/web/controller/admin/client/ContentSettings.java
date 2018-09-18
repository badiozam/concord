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
package com.i4one.base.web.controller.admin.client;

import com.i4one.base.model.MessageKeySettings;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class ContentSettings extends MessageKeySettings
{
	private IString head;

	private IString header;
	private IString footer;

	private IString mainStart;
	private IString mainEnd;
	
	private final IString name;

	public ContentSettings()
	{
		head = new IString();

		header = new IString();
		footer = new IString();

		mainStart = new IString();
		mainEnd = new IString();

		name = new IString("Content");
	}

	@Override
	public IString getNameSingle()
	{
		return name;
	}

	@Override
	public IString getNamePlural()
	{
		return name;
	}

	public IString getHead()
	{
		return head;
	}

	public void setHead(IString head)
	{
		this.head = head;
	}

	public IString getHeader()
	{
		return header;
	}

	public void setHeader(IString header)
	{
		this.header = header;
	}

	public IString getFooter()
	{
		return footer;
	}

	public void setFooter(IString footer)
	{
		this.footer = footer;
	}

	public IString getMainStart()
	{
		return mainStart;
	}

	public void setMainStart(IString mainStart)
	{
		this.mainStart = mainStart;
	}

	public IString getMainEnd()
	{
		return mainEnd;
	}

	public void setMainEnd(IString mainEnd)
	{
		this.mainEnd = mainEnd;
	}

	public List<Message> getHeadMessages(SingleClient client, String key)
	{
		return getMessages(client, key, head);
	}

	public void setHeadMessages(List<Message> messages)
	{
		this.head = messagesToIString(messages);
	}

	public List<Message> getHeaderMessages(SingleClient client, String key)
	{
		return getMessages(client, key, header);
	}

	public void setHeaderMessages(List<Message> messages)
	{
		this.header = messagesToIString(messages);
	}

	public List<Message> getFooterMessages(SingleClient client, String key)
	{
		return getMessages(client, key, footer);
	}

	public void setFooterMessages(List<Message> messages)
	{
		this.footer = messagesToIString(messages);
	}

	public List<Message> getMainStartMessages(SingleClient client, String key)
	{
		return getMessages(client, key, mainStart);
	}

	public void setMainStartMessages(List<Message> messages)
	{
		this.mainStart = messagesToIString(messages);
	}

	public List<Message> getMainEndMessages(SingleClient client, String key)
	{
		return getMessages(client, key, mainEnd);
	}

	public void setMainEndMessages(List<Message> messages)
	{
		this.mainEnd = messagesToIString(messages);
	}
}
