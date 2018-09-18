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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.spring.VelocityConfigurer;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleMessageManager extends BaseSimpleManager<MessageRecord,Message> implements MessageManager
{
	private SingleClientManager singleClientManager;
	private VelocityConfig velocityConfig;

	@Override
	public Message getMessage(SingleClient client, String key)
	{
		return getMessage(client, key, client.getLocale().getLanguage());
	}

	@Override
	public Message getMessage(SingleClient client, String key, String language)
	{
		if ( key == null || client == null )
		{
			throw new IllegalArgumentException("Key " + key + ", client " + client);
		}
		
		Message retVal = getMessageInternal(client, key, language);

		if ( retVal.exists() )
		{
			String value = retVal.getValue();
			if ( value.startsWith("[[") && value.endsWith("]]"))
			{
				String refKey = value.substring(2, value.length() - 2);

				if ( refKey.equals(key) )
				{
					retVal.setValue(refKey);
					return retVal;
				}
				else
				{
					// Return the reference message's value
					//
					getLogger().trace("Returning the reference message " + refKey + " instead of " + retVal.getKey());
					return getMessage(client, refKey, language);
				}
			}
			else
			{
				return retVal;
			}
		}
		else
		{
			// No such message exists in the database, return the key as the value
			//
			getLogger().trace("Message " + key + " not found for client " + client + " using language " + language);
			MessageRecord dne = new MessageRecord();
			dne.setClientid(1);
			dne.setKey(key);
			dne.setLanguage(language);
			dne.setValue(key);

			return new Message(dne);
		}
	}

	private Message getMessageInternal(SingleClient client, String key, String language)
	{
		MessageRecord messageRecord;
		SingleClient currClient = client;

		// Walk the tree upwards in the database until we find the message record
		//
		while ( currClient.exists() )
		{
			messageRecord = getDao().getMessage(currClient.getSer(), language, key);
			if ( messageRecord == null )
			{
				currClient = currClient.getParent();
			}
			else
			{
				return new Message(messageRecord);
			}
		}

		return new Message();
	}

	@Override
	public List<Message> getAllMessages(SingleClient client, String key, String language, PaginationFilter pagination)
	{
		// Walk the tree upwards in the database until we find the message record
		//
		List<MessageRecord> matches;

		Stack<SingleClient> clientStack = new Stack<>();
		for (SingleClient currClient = client; currClient.exists(); currClient = currClient.getParent() )
		{
			clientStack.push(currClient);
		}

		// Start with a set of messages at the ROOT node
		//
		matches = getDao().searchMessagesByKey(SingleClient.getRoot().getSer(), key + "%", pagination);

		// Then overwrite each one with the overridden settings for the lower levels
		//
		HashMap<String,MessageRecord> messageRecords = new HashMap<>();
		while ( !clientStack.empty() )
		{
			SingleClient currClient = clientStack.pop();
			matches.stream().forEach( (currMatch) ->
			{
				MessageRecord overrideRecord = getDao().getMessage(currClient.getSer(), language, currMatch.getKey());
				if ( overrideRecord != null )
				{
					messageRecords.put(overrideRecord.getKey() + "[" + overrideRecord.getLanguage() + "]", overrideRecord);
				}
			});
		}

		// Flatten the map which was just being used for fast lookup/overwrite
		//
		ArrayList<Message> retVal = new ArrayList<>();
		messageRecords.values().stream().forEach((match) ->
		{
			retVal.add(new Message(match));
		});

		return retVal;
	}

	@Override
	public List<Message> getAllMessages(SingleClient client, String key)
	{
		// Walk the tree upwards in the database until we find the message record
		//
		List<MessageRecord> matches;

		// Ensure we have the latest info
		//
		client = getSingleClientManager().getById(client.getSer());

		Stack<SingleClient> clientStack = new Stack<>();
		for (SingleClient currClient = client; currClient.exists(); currClient = currClient.getParent() )
		{
			clientStack.push(currClient);
		}

		// Start with a set of messages at the ROOT node
		//
		matches = getDao().searchMessagesByKey(SingleClient.getRoot().getSer(), key, SimplePaginationFilter.NONE);

		// Then overwrite each one with the overridden settings for the lower levels
		//
		HashMap<String,MessageRecord> messageRecords = new HashMap<>();
		while ( !clientStack.empty() )
		{
			SingleClient currClient = clientStack.pop();

			for ( MessageRecord currMatch : matches)
			{
				/*
				client.getLanguageList().forEach( (language) ->
				{
					MessageRecord overrideRecord = getDao().getMessage(currClient.getSer(), language, currMatch.getKey());
					if ( overrideRecord != null )
					{
						messageRecords.put(overrideRecord.getKey() + "[" + overrideRecord.getLanguage() + "]", overrideRecord);
					}
				});
				*/

				List<MessageRecord> overrideRecords = getDao().getMessagesByKey(currClient.getSer(), currMatch.getKey());
				for ( MessageRecord overrideRecord : overrideRecords )
				{
					if ( overrideRecord != null )
					{
						messageRecords.put(overrideRecord.getKey() + "[" + overrideRecord.getLanguage() + "]", overrideRecord);
					}
				}
			}
		}

		// Flatten the map which was just being used for fast lookup/overwrite
		//
		ArrayList<Message> retVal = new ArrayList<>();
		messageRecords.values().stream().forEach((match) ->
		{
			retVal.add(new Message(match));
		});

		return retVal;
	}

	@Override
	public String buildMessage(SingleClient client, String key, String language, Object... args)
	{
		return buildMessage(getMessage(client, key, language), args);
	}

	@Override
	public IString buildIStringMessage(SingleClient client, String key, Object... args)
	{
		IString retVal = new IString();

		List<Message> messages = getAllMessages(client, key);
		for (Message message : messages)
		{
			retVal.set(message.getLanguage(), buildMessage(message, args));
		}

		return retVal;
	}

	@Override
	public String buildMessage(Message message, Object... args)
	{
		try
		{
			if ( args != null )
			{
				getLogger().trace("buildMessage(..) adding {} arguments to {}" , args.length, message.getKey());

				Map<String, Object> argMap;
				if ( args.length == 1 && args[0] instanceof Map)
				{
					argMap = (Map)args[0];
				}
				else
				{
					argMap = Utils.makeMap(args);
				}

				return buildMessage(message, argMap);
			}
			else
			{
				getLogger().trace("buildMessage(..) called with null args");
				return buildMessage(message, Collections.EMPTY_MAP);
			}
		}
		catch (Exception e)
		{
			getLogger().debug("buildMessage problem for: " + Utils.toCSV(Arrays.asList(args)), e);
			throw e;
		}
	}

	@Override
	public String buildMessage(Message message, Map<String, Object> args)
	{
		VelocityContext context = new VelocityContext(args, VelocityConfigurer.getVelocityToolContext());

		return buildMessage(message, context);
	}

	private String buildMessage(Message message, VelocityContext context)
	{
		StringWriter writer = new StringWriter();

		// Regardless we want to add the language of the message so that it's available for IString's
		//
		context.put("language", message.getLanguage());

		String value = Utils.defaultIfNull(message.getValue(), message.getKey());
		getVelocityConfig().getVelocityEngine().evaluate(context, writer, getClass().getSimpleName() + "-" + message.getKey(), value);

		return writer.toString();
	}

	/**
	 * Updates a message, overriding a parent client's value if necessary.
	 *
	 * @param message The message to update
	 */
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Message> update(Message message)
	{
		getLogger().debug("Updating message " + message);

		Message currMessage = getMessageInternal(message.getClient(), message.getKey(), message.getLanguage());
		ReturnType<Message> retVal = new ReturnType<>();
		retVal.setPre(currMessage);

		message.setUpdateTimeSeconds(Utils.currentTimeSeconds());
		if ( !currMessage.exists() )
		{
			getLogger().trace("Option " + message + " is a brand new key");

			// This is the first time this key is being created, we create the key
			// at the root node so that all clients of the application have access
			// to the key
			//
			message.setClient(SingleClient.getRoot());
			getDao().insert(message.getDelegate());

			retVal.setPost(message);
		}
		else
		{
			getLogger().trace("Message " + message + " is an existing key: " + currMessage);

			// Check to see if the input message's client is the same as the one we already have
			//
			if ( message.getClient().equals(currMessage.getClient()) )
			{
				// The message blongs to the same client which means it override's a parent's value somwhere
				//
				getLogger().debug("Message " + message + " updates current value for client " + message.getClient() + " via " + currMessage);

				// Find the parent's value to see how we should proceed
				//
				SingleClient parentClient = message.getClient().getParent();
				Message parentMessage = getMessage(parentClient, message.getKey(), message.getLanguage());

				if (
				        parentMessage.exists() &&		// Does the parent client have the same option?
				        parentMessage.getValue() != null &&	// Does the parent option's value match our value?
				        parentMessage.getValue().equals(message.getValue())
				        )
				{
					// The value is the same as the parent's value, removing to allow
					// inheritance to take over
					//
					removeInternal(currMessage);

					// Update the parent message's time so that the child which just defaulted the key
					// is able to get the latest value
					//
					parentMessage.setUpdateTimeSeconds(Utils.currentTimeSeconds());
					getDao().updateBySer(parentMessage.getDelegate(), "updatetime");

					retVal.setPost(parentMessage);
				}
				else if ( !message.getValue().equals(currMessage.getValue()))
				{
					// This is a key for the same client, we only need to update the value
					//
					message.setSer(currMessage.getSer());
					getDao().updateBySer(message.getDelegate());

					retVal.setPost(message);
				}
				else
				{
					// No update needed since the values are the same
					//
					retVal.setPost(currMessage);
				}
			}
			else
			{
				// The messages are not equal by serial number. There are two cases here:
				//
				// 1. The message is being set for a lower child client
				// 2. THe message is being updated for a higher parent client
				//
				if ( message.getClient().belongsTo(currMessage.getClient()))
				{
					// The incoming message belongs to the database message's client,
					// which means that this is a child override
					//
					if ( !currMessage.getValue().equals(message.getValue()))
					{
						getLogger().debug("Message " + message + " overrides parent's value for client " + message.getClient() + " via " + currMessage);
		
						// The key is for a different client so we need to create a new key
						//
						message.setSer(0);
						getDao().insert(message.getDelegate());
		
						retVal.setPost(message);
					}
					else // if ( currMessage.getValue().equals(message.getValue())
					{
						getLogger().debug("Message " + message + " has the same value as its parent for client " + message.getClient() + " via " + currMessage);
						if ( message.exists() )
						{
							// Our message is redundant and can be safely removed
							//
							Message removedMessage = removeInternal(message);

							retVal.setPre(removedMessage);
						}

						retVal.setPost(currMessage);
					}
				}
				else if ( currMessage.getClient().belongsTo(message.getClient()) )
				{
					getLogger().debug("Message " + message + " updates parent's value");

					// Our message belongs to the incoming message's parent, which means this message
					// is being removed in favor of updating the parent message
					//
					Message removedMessage = removeInternal(message);

					// Nice to have for historical purposes
					//
					ReturnType<Message> removedRet = new ReturnType<>();
					removedRet.setPre(removedMessage);
					removedRet.setPost(new Message());
					retVal.addChain("remove", removedRet);

					currMessage.setValue(message.getValue());
					getDao().updateBySer(currMessage.getDelegate());

					retVal.setPre(removedMessage);
					retVal.setPost(currMessage);
				}
				else
				{
					throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".update.dne", "You are attempting to update an item outside of the client hierarchy: $item", new Object[] { "item", message }));
				}
			}
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public Message remove(Message message)
	{
		getLogger().trace("Removing message " + message);

		//Message currMessage = getMessage(message.getClient(), message.getLanguage(), message.getKey());
		Message currMessage = getMessageInternal(message.getClient(), message.getKey(), message.getLanguage());
		if ( !currMessage.exists() )
		{
			getLogger().trace("Message " + message + " does not specify a serial number");
			return new Message();
		}
		else
		{
			getLogger().trace("Message " + message + " exists, checking client");

			if ( currMessage.getClient().equals(message.getClient()) )
			{
				return removeInternal(currMessage);
			}
			else
			{
				getLogger().trace("Message " + message + " is inherited from the parent, ignoring remove request");
				return new Message();
			}
		}
	}

	private Message removeInternal(Message message)
	{
		// This is a key for the same client
		//
		MessageRecord dbRecord = getDao().getBySer(message.getSer(), true);
		if ( dbRecord != null )
		{
			getLogger().debug("Removing message " + message);
			getDao().deleteBySer(message.getSer());

			return new Message(dbRecord);
		}
		else
		{
			getLogger().debug("Message " + message + " does not exist in the database");
			return new Message();
		}
	}

	@Override
	public ReturnType<Message> create(Message item)
	{
		return update(item);
	}

	@Override
	public List<ReturnType<Message>> updateMessages(Set<Message> messages)
	{
		List<ReturnType<Message>> retVal = new ArrayList<>();

		for ( Message message : messages )
		{
			retVal.add(update(message));
		}

		return retVal;
	}

	public VelocityConfig getVelocityConfig()
	{
		return velocityConfig;
	}

	@Autowired
	public void setVelocityConfig(VelocityConfig velocityConfig)
	{
		this.velocityConfig = velocityConfig;
	}

	@Override
	public MessageRecordDao getDao()
	{
		return (MessageRecordDao) super.getDao();
	}

	@Override
	public Message emptyInstance()
	{
		return new Message();
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
