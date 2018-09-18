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
import com.i4one.base.model.adminhistory.AdminHistory;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.BaseHistoricalManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.PrivilegedManager;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.MessageManager")
public class HistoricalMessageManager extends BaseHistoricalManager<MessageRecord,Message> implements MessageManager
{
	private PrivilegedManager<MessageRecord,Message> privilegedMessageManager;

	@Override
	public PrivilegedManager<MessageRecord,Message> getImplementationManager()
	{
		return getPrivilegedMessageManager();
	}

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

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<Message>> updateMessages(Set<Message> messages)
	{
		List<ReturnType<Message>> retVal = getMessageManager().updateMessages(messages);

		for ( ReturnType<Message> result : retVal )
		{
			AdminHistory adminHistory = newAdminHistory(result.getPre(), result.getPost(), "update");
			setHistoryChainOwnership(result, adminHistory);

			result.put(ATTR_ADMINHISTORY, adminHistory);
		}

		return retVal;
	}

	@Override
	public MessageManager getMessageManager()
	{
		// We ignore the typical ReadOnlyMessageManager that is autowired
		// in the superclass in favor of using the same PrivilegedMessageManager
		// instance for everything.
		//
		return (MessageManager) getPrivilegedMessageManager();
	}

	public PrivilegedManager<MessageRecord,Message> getPrivilegedMessageManager()
	{
		return privilegedMessageManager;
	}

	@Autowired
	public <P extends MessageManager & PrivilegedManager<MessageRecord, Message>>
	 void setPrivilegedMessageManager(P privilegedMessageManager)
	{
		this.privilegedMessageManager = privilegedMessageManager;
	}
}
