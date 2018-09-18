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
package com.i4one.base.web.controller.admin.messages;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.message.Message;
import java.util.List;
import org.springframework.util.AutoPopulatingList;
import org.springframework.util.AutoPopulatingList.ElementFactory;

/**
 * @author Hamid Badiozamani
 */
public class MessageList extends BaseLoggable implements ElementFactory<WebModelMessage>
{
	private String key;
	private PaginationFilter pagination;
	private final List<WebModelMessage> updateMessages;
	private Message newMessage;

	public MessageList()
	{
		updateMessages = new AutoPopulatingList<>(this);
		newMessage = new Message();
		pagination = new SimplePaginationFilter();

		key = "msg";
	}

	public List<WebModelMessage> getUpdateMessages()
	{
		return updateMessages;
	}

	public void setUpdateMessages(List<WebModelMessage> messages)
	{
		this.updateMessages.clear();
		this.updateMessages.addAll(messages);
	}

	public void setAllMessages(List<Message> messages)
	{
		this.updateMessages.clear();

		// We convert to the wrapped version for the remove option to become
		// available. This has the effect of making the items in the list above
		// our own since the delegates are not duplicated
		//
		//for ( Message currMessage : messages )
		messages.stream().forEach( (currMessage) ->
		{
			this.updateMessages.add(new WebModelMessage(currMessage));
		});
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public Message getNewMessage()
	{
		// This ensures that the new message is never overwriting anything
		//
		newMessage.setSer(0);

		return newMessage;
	}

	public void setNewMessage(Message newMessage)
	{
		this.newMessage = newMessage;
	}

	public PaginationFilter getPagination()
	{
		return pagination;
	}

	public void setPagination(PaginationFilter pagination)
	{
		this.pagination = pagination;
	}

	@Override
	public WebModelMessage createElement(int i) throws AutoPopulatingList.ElementInstantiationException
	{
		return new WebModelMessage();
	}
}
