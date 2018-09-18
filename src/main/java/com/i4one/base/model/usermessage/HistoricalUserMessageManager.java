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

import com.i4one.base.model.manager.HistoricalManager;
import com.i4one.base.model.manager.terminable.BaseTerminableHistoricalManager;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePrivilegedManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("base.UserMessageManager")
public class HistoricalUserMessageManager extends BaseTerminableHistoricalManager<UserMessageRecord, UserMessage> implements TerminableManager<UserMessageRecord, UserMessage>, HistoricalManager<UserMessageRecord, UserMessage>, UserMessageManager
{
	private TerminablePrivilegedManager<UserMessageRecord, UserMessage> privilegedUserMessageManager;

	@Override
	public boolean exists(UserMessage userMessage)
	{
		return getUserMessageManager().exists(userMessage);
	}

	@Override
	public TerminablePrivilegedManager<UserMessageRecord, UserMessage> getImplementationManager()
	{
		return getPrivilegedUserMessageManager();
	}

	public UserMessageManager getUserMessageManager()
	{
		return (UserMessageManager)getPrivilegedUserMessageManager();
	}

	public TerminablePrivilegedManager<UserMessageRecord, UserMessage> getPrivilegedUserMessageManager()
	{
		return privilegedUserMessageManager;
	}

	@Autowired
	public <P extends UserMessageManager & TerminablePrivilegedManager<UserMessageRecord, UserMessage>>
	 void setPrivilegedUserMessageManager(P privilegedUserMessageManager)
	{
		this.privilegedUserMessageManager = privilegedUserMessageManager;
	}
}
