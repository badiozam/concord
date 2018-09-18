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

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.message.Message;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class MessageViewController extends BaseAdminViewController
{
	public static final String MESSAGELIST = "messageList";

	private SingleClientManager singleClientManager;

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof MessageList )
		{
			MessageList messageList = (MessageList)modelAttribute;
			SingleClient client = model.getSingleClient();

			addLanguageMap(model);

			// We need the client ancestry of our current leaf node since we'll
			// allow for setting the message's client that way
			//
			Set<SingleClient> clients = new LinkedHashSet<>();
			SingleClient currClient = client;
			while ( currClient.exists() )
			{
				clients.add(currClient);
				currClient = currClient.getParent();
			}

			model.put("clients", toSelectMapping(clients, SingleClient::getName));
		}

		return model;
	}

	@RequestMapping(value = "**/admin/messages/index", method = RequestMethod.GET)
	public Model displayMessages(@ModelAttribute("messageList") MessageList messageList, BindingResult result, HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, messageList);
		addMessageToModel(model, Model.TITLE, "msg.base.admin.messages.index.title");

		if ( !Utils.isEmpty(messageList.getKey()))
		{
			messageList.setAllMessages(getMessageManager().getAllMessages(model.getSingleClient(),
				messageList.getKey(),
				model.getLanguage(),
				messageList.getPagination()));

			messageList.getNewMessage().setLanguage(model.getLanguage());

			if ( messageList.getUpdateMessages().isEmpty() )
			{
				fail(model, "msg.base.admin.messages.index.listing.failure", result, Errors.NO_ERRORS);
			}

			model.put(MESSAGELIST, messageList);
		}

		return initResponse(model, response, messageList);
	}

	@RequestMapping(value = "**/admin/messages/index", method = RequestMethod.POST)
	public Model updateMessages(@ModelAttribute("messageList") MessageList messageList, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, messageList);
		addMessageToModel(model, Model.TITLE, "msg.base.admin.messages.index.title");

		try
		{
			SingleClient client = model.getSingleClient();
			getLogger().debug("Got messages: " + messageList.getUpdateMessages().toString());

			for (Iterator<WebModelMessage> it = messageList.getUpdateMessages().iterator(); it.hasNext(); )
			{
				WebModelMessage currMessage = it.next();

				// We don't allow the user to set the client to anything
				// else but the current client (for now)
				//
				currMessage.setClient(client);
				if ( currMessage.isRemove() )
				{
					getLogger().debug("Removing message " + currMessage);

					// Remove the message from the database as well as our list
					//
					getMessageManager().remove(currMessage);

					// We add the message that takes the place of this one (from a parent client) if it exists
					//
					WebModelMessage supercedingMessage = new WebModelMessage(getMessageManager().getMessage(currMessage.getClient(), currMessage.getKey(), currMessage.getLanguage()));
					if ( supercedingMessage.exists() )
					{
						currMessage.copyFrom(supercedingMessage);
					}
					else
					{
						it.remove();
					}
				}
				else
				{
					ReturnType<Message> updateResult = getMessageManager().update(currMessage);
					currMessage.copyFrom(updateResult.getPost());
				}
			}

			if ( !Utils.isEmpty(messageList.getNewMessage().getKey()) )
			{
				messageList.getNewMessage().setClient(client);
				getMessageManager().create(messageList.getNewMessage());
			}

			success(model, "msg.base.admin.messages.index.update.success");
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.admin.messages.index.update.failure", result, errors);
		}

		return initResponse(model, response,  messageList);
	}

	public SingleClientManager getSingleClientManager()
	{
		return singleClientManager;
	}

	@Autowired
	public void setSingleClientManager(SingleClientManager singleClientManager)
	{
		this.singleClientManager = singleClientManager;
	}
}
