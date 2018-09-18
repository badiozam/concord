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
package com.i4one.base.model;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public abstract class MessageKeySettings extends BaseSettings implements Settings
{
	public MessageKeySettings()
	{
	}

	protected List<Message> getMessages(SingleClient client, String key, IString istr)
	{
		return Message.messagesFromIString(client, key, istr)
			.entrySet()
			.stream()
			.map( (item) -> { return item.getValue(); } )
			.collect(Collectors.toList());
	}

	protected IString messagesToIString(List<Message> messages)
	{
		IString retVal = new IString();
		messages.forEach((Message message) ->
		{
			retVal.put(message.getLanguage(), message.getValue());
		});
		return retVal;
	}
}
