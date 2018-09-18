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

import com.i4one.base.dao.Dao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface MessageRecordDao extends Dao<MessageRecord>
{
	/**
	 * Look up a message by client/key and language
	 *
	 * @param clientid The client to look under
	 * @param language The language of the message
	 * @param key The key of the message
	 *
	 * @return The message matching the client/language/key values or null if not found.
	 */
	public MessageRecord getMessage(int clientid, String language, String key);

	/**
	 * Return all message records for a given client and a given key. Only the language
	 * parameter will be different for the return value's elements.
	 *
	 * @param clientid The client to look up
	 * @param key The key value to match
	 *
	 * @return A list of message records matching the key/clientid combination
	 */
	public List<MessageRecord> getMessagesByKey(int clientid, String key);

	/**
	 * Search for all message records for a given client and a given key fragment.
	 * Only the language parameter will be different for the return value's elements.
	 *
	 * @param clientid The client to look up
	 * @param key The key value to match
	 * @param pagination The qualifier/sorting info
	 *
	 * @return A list of message records matching the key/clientid combination
	 */
	public List<MessageRecord> searchMessagesByKey(int clientid, String key, PaginationFilter pagination);

	/**
	 * Return all message records for a given client and a given language. Only the key
	 * parameter will be different for the return value's elements.
	 *
	 * @param clientid The client to look up
	 * @param language The language value to match
	 *
	 * @return A list of message records matching the language/clientid combination
	 */
	public List<MessageRecord> getMessagesByLanguage(int clientid, String language);
}
