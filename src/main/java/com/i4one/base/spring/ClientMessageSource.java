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
package com.i4one.base.spring;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.ui.context.Theme;

/**
 * A message source that uses a given client to resolve message keys.
 * 
 * @author Hamid Badiozamani
 */
public class ClientMessageSource extends BaseLoggable implements MessageSource, Theme
{
	private SingleClient client;
	private MessageManager messageManager;

	@Override
	public String getMessage(String key, Object[] args, String defaultMessage, Locale locale)
	{
		Message currMessage = getMessageManager().getMessage(getClient(), key, locale.getLanguage());
		if ( currMessage.exists() )
		{
			return getMessageManager().buildMessage(currMessage, args);
		}
		else
		{
			// Only if the key and the default message are the same can we
			// assume that the default message is not a valid key
			//
			if ( key.equals(defaultMessage))
			{
				currMessage = new Message();
				currMessage.setKey(key);
				currMessage.setLanguage(locale.getLanguage());
				currMessage.setClient(getClient());
				currMessage.setValue(defaultMessage);

				return getMessageManager().buildMessage(currMessage, args);
			}
			else
			{
				// Attempt to resolve the default message as a key
				//
				return getMessage(defaultMessage, args, defaultMessage, locale);
			}
		}
	}

	@Override
	public String getMessage(String key, Object[] args, Locale locale) throws NoSuchMessageException
	{
		Message currMessage = getMessageManager().getMessage(getClient(), key, locale.getLanguage());
		if ( currMessage.exists() )
		{
			return getMessageManager().buildMessage(currMessage, args);
		}
		else
		{
			throw new NoSuchMessageException(key, locale);
		}
	}

	@Override
	public String getMessage(MessageSourceResolvable msr, Locale locale) throws NoSuchMessageException
	{
		getLogger().debug("Get message called for: " + msr);

		for ( String currCode : msr.getCodes() )
		{
			Message currMessage = getMessageManager().getMessage(getClient(), currCode, locale.getLanguage());
			if ( currMessage.exists() )
			{
				return getMessageManager().buildMessage(currMessage, msr.getArguments());
			}
		}

		if ( msr.getDefaultMessage() != null )
		{
			getLogger().debug("Falling back to default message: " + msr.getDefaultMessage());

			// We attempt to resolve the default message as a key, this is done mainly for JSR-303
			// where we want to declaratively set error codes
			//
			return getMessage(msr.getDefaultMessage(), msr.getArguments(), msr.getDefaultMessage(), locale);
		}
		else
		{
			throw new NoSuchMessageException(msr.toString(), locale);
		}
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	@Override
	public String getName()
	{
		return getClient().getName();
	}

	@Override
	public MessageSource getMessageSource()
	{
		return this;
	}

	public SingleClient getClient()
	{
		return client;
	}

	public void setClient(SingleClient client)
	{
		this.client = client;
	}

}
