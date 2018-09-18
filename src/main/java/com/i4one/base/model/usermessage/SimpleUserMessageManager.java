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
package com.i4one.base.model.usermessage;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.terminable.BaseSimpleTerminableSiteGroupTypeManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Hamid Badiozamani
 */
public class SimpleUserMessageManager extends BaseSimpleTerminableSiteGroupTypeManager<UserMessageRecord, UserMessage> implements UserMessageManager
{
	@Override
	public boolean exists(UserMessage userMessage)
	{
		return getDao().exists(userMessage.getDelegate());
	}

	@Override
	public List<ReturnType<UserMessage>> importCSV(InputStream stream, Supplier<UserMessage> instantiator, Function<UserMessage,Boolean> preprocessor, Consumer<ReturnType<UserMessage>> postprocessor)
	{
		return super.importCSVInternal(stream, instantiator, preprocessor, postprocessor);
	}

	@Override
	public OutputStream exportCSV(Set<UserMessage> items, OutputStream out)
	{
		return exportCSVInternal(items, out);
	}

	@Override
	public UserMessage emptyInstance()
	{
		return new UserMessage();
	}

	@Override
	public UserMessageRecordDao getDao()
	{
		return (UserMessageRecordDao) super.getDao();
	}
}