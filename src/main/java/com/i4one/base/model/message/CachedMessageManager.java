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
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caches messages loaded by the database for a set period of time for faster lookup.
 * Since the Message system is hierarchical, any time a leaf node is removed the
 * parent value is used in its stead.
 *
 * @author Hamid Badiozamani
 */
@Service
public class CachedMessageManager extends SimpleMessageManager implements MessageManager
{
	@Cacheable(value = "messageManager", key="target.makeKey(#client, #key)")
	@Override
	public Message getMessage(SingleClient client, String key)
	{
		return super.getMessage(client, key);
	}

	@Cacheable(value = "messageManager", key="target.makeKey(#client, #key, #language)")
	@Override
	public Message getMessage(SingleClient client, String key, String language)
	{
		return super.getMessage(client, key, language);
	}

	@Cacheable(value = "messageManager", key="target.makeKey(#client, #key, #language, #pagination)")
	@Override
	public List<Message> getAllMessages(SingleClient client, String key, String language, PaginationFilter pagination)
	{
		return Collections.unmodifiableList(super.getAllMessages(client, key, language, pagination));
	}

	@Cacheable(value = "messageManager", key="target.makeKey(#client, #key)")
	@Override
	public List<Message> getAllMessages(SingleClient client, String key)
	{
		return super.getAllMessages(client, key);
	}

	@CacheEvict(value = "messageManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Message> create(Message message)
	{
		return super.create(message);
	}

	@CacheEvict(value = "messageManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Message> update(Message message)
	{
		return super.update(message);
	}

	@CacheEvict(value = "messageManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public Message remove(Message message)
	{
		return super.remove(message);
	}
	
	@CacheEvict(value = "messageManager", allEntries = true)
	@Override
	public List<ReturnType<Message>> updateMessages(Set<Message> messages)
	{
		return super.updateMessages(messages);
	}
}
