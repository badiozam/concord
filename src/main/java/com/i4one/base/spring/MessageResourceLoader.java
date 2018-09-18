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

import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * This class loads VTL templates using a message manager. Keys must be formatted
 * in the following pattern:
 *
 * 	[client]/[language]/[key]
 *
 * @author Hamid Badiozamani
 */
public class MessageResourceLoader extends ResourceLoader
{
	private Log logger;

	private MessageManager messageManager;
	private SingleClientManager clientManager;

	private ResourceLoader fallbackResourceLoader;

	private MessageResourceLoader()
	{
		logger = LogFactory.getLog(getClass());
	}

	public void init()
	{
		init(null);
	}

	@Override
	public void init(ExtendedProperties ep)
	{
		setCachingOn(true);
		setModificationCheckInterval(10);
	}

	@Override
	public InputStream getResourceStream(String key) throws ResourceNotFoundException
	{
		getLogger().debug("getResourceStream(..) called w/ " + key);

		if ( Utils.isEmpty(key))
		{
			//throw new ResourceNotFoundException(getClass().getSimpleName() + ": Template key is empty");
			return getFallbackResourceLoader().getResourceStream(key);
		}
		else
		{
			// The input key is split into three parts: client/language/key
			//
			String[] keyOpts = key.split("/", 3);

			if ( keyOpts.length < 3)
			{
				//throw new ResourceNotFoundException(getClass().getSimpleName() + ": Template key " + key + " does not specify client and language");
				return getFallbackResourceLoader().getResourceStream(key);
			}
			else
			{
				String clientName = keyOpts[0];
				String language = keyOpts[1];
				String messageKey = keyOpts[2];

				getLogger().trace("Parsed key as: client = " + clientName + ", language = " + language + " messageKey = " + messageKey);

				SingleClient client = getClientManager().getClient(clientName);
				if ( !client.exists() )
				{
					//throw new ResourceNotFoundException(getClass().getSimpleName() + ": Client " + clientName + " was not found");
					return getFallbackResourceLoader().getResourceStream(key);
				}
				else
				{
					Message message = getMessageByKey(key);

					if ( message.exists())
					{
						return new ByteArrayInputStream(message.getValue().getBytes());
					}
					else
					{
						getLogger().trace("Message " + message + " not found, falling back to file system.");
						//throw new ResourceNotFoundException(getClass().getSimpleName() + ": could not find resource " + key);

						return getFallbackResourceLoader().getResourceStream(key);
					}
				}
			}
		}
	}

	@Override
	public boolean isSourceModified(Resource rsrc)
	{
		return (rsrc.getLastModified() != getMessageByKey(rsrc.getName()).getUpdateTimeSeconds());
	}

	@Override
	public long getLastModified(Resource rsrc)
	{
		return getMessageByKey(rsrc.getName()).getUpdateTimeSeconds();
	}

	protected Message getMessageByKey(String key)
	{
		String[] keyOpts = key.split("/", 3);

		String clientName = keyOpts[0];
		String language = keyOpts[1];
		String messageKey = keyOpts[2];

		SingleClient client = getClientManager().getClient(clientName);
		return getMessageManager().getMessage(client, messageKey, language);
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public SingleClientManager getClientManager()
	{
		return clientManager;
	}

	public void setClientManager(SingleClientManager clientManager)
	{
		this.clientManager = clientManager;
	}

	public ResourceLoader getFallbackResourceLoader()
	{
		return fallbackResourceLoader;
	}

	public void setFallbackResourceLoader(ResourceLoader fallbackResourceLoader)
	{
		this.fallbackResourceLoader = fallbackResourceLoader;
	}

	public Log getLogger()
	{
		return logger;
	}

	public void setLogger(Log logger)
	{
		this.logger = logger;
	}

}