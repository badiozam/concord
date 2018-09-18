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
public class FrontPageContentSettings extends MessageKeySettings
{
	private final IString name;
	private IString body;

	public FrontPageContentSettings()
	{
		body = new IString();

		name = new IString("name");
	}

	public IString getBody()
	{
		return body;
	}

	public void setBody(IString body)
	{
		this.body = body;
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

	public List<Message> getBodyMessages(SingleClient client, String key)
	{
		return getMessages(client, key, body);
	}

	public void setBodyMessages(List<Message> messages)
	{
		this.body = messagesToIString(messages);
	}
}
