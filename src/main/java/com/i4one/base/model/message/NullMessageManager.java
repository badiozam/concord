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
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A message manager that does nothing. This is needed mainly for bootstrapping purposes
 * as there is a circular dependency with ReadOnlyMessageManager:
 * 
 * ReadOnlyMessageManager &gt; CachedMessageManager &gt;
 * VelocityConfig &gt; MessageResourceLoader &gt; ReadOnlyMessageManager
 * 
 * @author Hamid Badiozamani
 */
public class NullMessageManager implements MessageManager
{

	@Override
	public Message getMessage(SingleClient client, String key)
	{
		return emptyInstance();
	}

	@Override
	public Message getMessage(SingleClient client, String language, String key)
	{
		return emptyInstance();
	}

	@Override
	public List<Message> getAllMessages(SingleClient client, String key, String language, PaginationFilter pagination)
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Message> getAllMessages(SingleClient client, String key)
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public String buildMessage(SingleClient client, String key, String language, Object... args)
	{
		return null;
	}

	@Override
	public String buildMessage(Message message, Object... args)
	{
		return null;
	}

	@Override
	public String buildMessage(Message message, Map<String, Object> args)
	{
		return null;
	}

	@Override
	public IString buildIStringMessage(SingleClient client, String key, Object... args)
	{
		return null;
	}

	@Override
	public List<ReturnType<Message>> updateMessages(Set<Message> messages)
	{
		return null;
	}

	@Override
	public void init()
	{
	}

	@Override
	public void init(Map<String, Object> options)
	{
	}

	@Override
	public Message emptyInstance()
	{
		return new Message();
	}

	@Override
	public MessageRecord lock(Message item)
	{
		return null;
	}

	@Override
	public Message getById(int id)
	{
		return emptyInstance();
	}

	@Override
	public void loadById(Message item, int id)
	{
	}

	@Override
	public ReturnType<Message> create(Message item)
	{
		return new ReturnType<>();
	}

	@Override
	public ReturnType<Message> update(Message item)
	{
		return new ReturnType<>();
	}

	@Override
	public Message remove(Message item)
	{
		return emptyInstance();
	}

	@Override
	public ReturnType<Message> clone(Message item)
	{
		return new ReturnType<>();
	}

	@Override
	public TopLevelReport getReport(Message item, TopLevelReport report, PaginationFilter pagination)
	{
		throw new UnsupportedOperationException("Null message manager");
	}

	@Override
	public <R extends Object> ClassificationReport<MessageRecord, R> getReport(Message item, ClassificationReport<MessageRecord, R> report, PaginationFilter pagination)
	{
		throw new UnsupportedOperationException("Null message manager.");
	}

	@Override
	public String getInterfaceName()
	{
		return "";
	}

	@Override
	public Dao<MessageRecord> getDao()
	{
		return null;
	}

	@Override
	public List<ReturnType<Message>> importCSV(InputStream importStream, Supplier<Message> instantiator, Function<Message,Boolean> preprocessor, Consumer<ReturnType<Message>> postprocessor)
	{
		throw new UnsupportedOperationException("Null message manager.");
	}

	@Override
	public OutputStream exportCSV(Set<Message> items, OutputStream out)
	{
		throw new UnsupportedOperationException("Null message manager.");
	}
}
