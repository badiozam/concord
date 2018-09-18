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
package com.i4one.predict.model.eventprediction;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.usermessage.UserMessage;
import com.i4one.base.model.usermessage.UserMessageManager;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.term.TermManager;
import com.i4one.predict.model.term.TermSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class UserMessageEventPredictionActualizer extends BaseLoggable implements EventPredictionActualizer
{
	private static final String CORRECT_KEY = "predict.termManager.correctUserMessage";

	private MessageManager messageManager;
	private UserMessageManager userMessageManager;

	private TermManager termManager;
	private EventPredictionActualizer eventPredictionActualizer;

	@Transactional(readOnly = false)
	@Override
	public ReturnType<EventPrediction> actualize(EventPrediction eventPrediction, EventOutcome actualOutcome)
	{
		ReturnType<EventPrediction> retVal = getEventPredictionActualizer().actualize(eventPrediction, actualOutcome);

		EventPrediction prediction = retVal.getPost();
		if ( !prediction.exists() )
		{
			prediction = retVal.getPre();
		}

		if ( prediction.exists() && prediction.getCorrect() )
		{
			SingleClient client = prediction.getEvent().getClient(false);

			TermSettings settings = getTermManager().getSettings(client);
			if ( settings.isInjectPrivateMessages() )
			{
				User user = prediction.getUser(false);
				UserMessage item = new UserMessage();

				item.setUser(user);
				item.setClient(client);
				item.setStartTimeSeconds(prediction.getPostedTimeSeconds());
				item.setEndTimeSeconds(prediction.getPostedTimeSeconds() + 86400);
				item.setSiteGroup(prediction.getEvent().getSiteGroup(false));
	
				item.setMessage(getMessageManager().buildIStringMessage(client, CORRECT_KEY, "prediction", prediction));
				getLogger().debug("Attempting to create user message {}", item);
	
				if ( !getUserMessageManager().exists(item))
				{
					ReturnType<UserMessage> createdMessage = getUserMessageManager().create(item);
					retVal.addChain(getUserMessageManager(), "create", createdMessage);
				}
				else
				{
					getLogger().debug("Message {} already exists, skipping.");
				}
			}
		}

		return retVal;
	}

	public EventPredictionActualizer getEventPredictionActualizer()
	{
		return eventPredictionActualizer;
	}

	@Autowired
	public void setEventPredictionActualizer(EventPredictionActualizer simpleEventPredictionActualizer)
	{
		this.eventPredictionActualizer = simpleEventPredictionActualizer;
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public UserMessageManager getUserMessageManager()
	{
		return userMessageManager;
	}

	@Autowired
	public void setUserMessageManager(UserMessageManager userMessageManager)
	{
		this.userMessageManager = userMessageManager;
	}

	public TermManager getTermManager()
	{
		return termManager;
	}

	@Autowired
	public void setTermManager(TermManager termManager)
	{
		this.termManager = termManager;
	}
}
