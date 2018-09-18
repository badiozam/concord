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
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface MessageManager extends Manager<MessageRecord,Message>
{
	/**
	 * Retrieves a message for a given client and the client's first default language
	 *
	 * @param client The client for which the message is to be retrieved
	 * @param key The key of the message to retrieve
	 *
	 * @return The message or an empty Message object
	 */
	public Message getMessage(SingleClient client, String key);

	/**
	 * Retrieves a message for a given client and the client's language
	 *
	 * @param client The client for which the message is to be retrieved
	 * @param language The language in which to retrieve the message key
	 * @param key The key of the message to retrieve
	 *
	 * @return The message or an empty Message object
	 */
	public Message getMessage(SingleClient client, String key, String language);

	/**
	 * Retrieves all messages starting with the given key for the given client
	 *
	 * @param client The client for which the messages are to be retrieved
	 * @param key The beginning key fragment to search for
	 * @param language The language in use
	 * @param pagination The pagination/ordering information
	 *
	 * @return A list of all messages for the given client (including inherited values)
	 */
	public List<Message> getAllMessages(SingleClient client, String key, String language, PaginationFilter pagination);

	/**
	 * Get all message values for the given key in all available languages.
	 * 
	 * @param client The client for which the messages are to be retrieved
	 * @param key The exact key
	 * 
	 * @return A list of messages for each available language for the given key
	 */
	public List<Message> getAllMessages(SingleClient client, String key);

	/**
	 * Convenience method to find and build a message. The arguments supplied must of
	 * alternating String and Object types such that the String element represents
	 * the key and the Object type the value as expected by the message.
	 *
	 * @param client The client for which the message is to be retrieved
	 * @param key The key of the message to retrieve
	 * @param language The language in which to retrieve the message key
	 * @param args The arguments to merge into the message
	 *
	 * @return The wholly built message
	 */
	public String buildMessage(SingleClient client, String key, String language, Object... args);

	/**
	 * Build a message with the given arguments. The arguments supplied must of
	 * alternating String and Object types such that the String element represents
	 * the key and the Object type the value as expected by the message.
	 *
	 * @param message The message object to build
	 * @param args The arguments to merge into the message
	 *
	 * @return The wholly built message
	 */
	public String buildMessage(Message message, Object... args);

	/**
	 * Build a message with the given arguments. 
	 *
	 * @param message The message object to build
	 * @param args The arguments to merge into the message
	 *
	 * @return The wholly built message
	 */
	public String buildMessage(Message message, Map<String, Object> args);

	/**
	 * Build an i18n message with the given arguments. 
	 *
	 * @param client The client for which the message is to be retrieved
	 * @param key The message key
	 * @param args The arguments to merge into the message
	 *
	 * @return The wholly built message
	 */
	public IString buildIStringMessage(SingleClient client, String key, Object... args);

	/**
	 * Update a group of messages together.
	 * 
	 * @param messages The set of messages to update.
	 * 
	 * @return The status for each message's individual update
	 * 	operation.
	 */
	public List<ReturnType<Message>> updateMessages(Set<Message> messages);
}
