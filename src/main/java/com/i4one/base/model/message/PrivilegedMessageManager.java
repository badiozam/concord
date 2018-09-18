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
package com.i4one.base.model.message;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.BaseClientTypePrivilegedManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedMessageManager extends BaseClientTypePrivilegedManager<MessageRecord,Message> implements MessageManager
{
	private MessageManager messageManager;

	@Override
	public Message getMessage(SingleClient client, String key)
	{
		return getMessageManager().getMessage(client, key);
	}

	@Override
	public Message getMessage(SingleClient client, String key, String language)
	{
		return getMessageManager().getMessage(client, key, language);
	}

	@Override
	public List<Message> getAllMessages(SingleClient client, String key, String language, PaginationFilter pagination)
	{
		return getMessageManager().getAllMessages(client, key, language, pagination);
	}

	@Override
	public List<Message> getAllMessages(SingleClient client, String key)
	{
		return getMessageManager().getAllMessages(client, key);
	}

	@Override
	public String buildMessage(SingleClient client, String key, String language, Object... args)
	{
		return getMessageManager().buildMessage(client, key, language, args);
	}

	@Override
	public String buildMessage(Message message, Object... args)
	{
		return getMessageManager().buildMessage(message, args);
	}

	@Override
	public String buildMessage(Message message, Map<String, Object> args)
	{
		return getMessageManager().buildMessage(message, args);
	}

	@Override
	public IString buildIStringMessage(SingleClient client, String key, Object... args)
	{
		return getMessageManager().buildIStringMessage(client, key, args);
	}

	@Override
	public List<ReturnType<Message>> updateMessages(Set<Message> messages)
	{
		for (Message message : messages)
		{
			checkWrite(getClient(message), "update");
		}

		return getMessageManager().updateMessages(messages);
	}


	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager cachedMessageManager)
	{
		this.messageManager = cachedMessageManager;
	}

	@Override
	public Manager<MessageRecord,Message> getImplementationManager()
	{
		return getMessageManager();
	}

	@Override
	public SingleClient getClient(Message item)
	{
		return item.getClient();
	}
}