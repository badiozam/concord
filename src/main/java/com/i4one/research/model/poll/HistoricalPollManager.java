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
package com.i4one.research.model.poll;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.categorizable.BaseCategorizableHistoricalManager;
import com.i4one.base.model.manager.categorizable.CategorizableHistoricalManager;
import com.i4one.base.model.manager.categorizable.CategorizablePrivilegedManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("research.PollManager")
public class HistoricalPollManager extends BaseCategorizableHistoricalManager<PollRecord, Poll> implements CategorizableHistoricalManager<PollRecord, Poll>, PollManager
{
	private CategorizablePrivilegedManager<PollRecord, Poll> privilegedPollManager;

	@Override
	public PollSettings getSettings(SingleClient client)
	{
		return getPollManager().getSettings(client);
	}

	@Override
	public ReturnType<PollSettings> updateSettings(PollSettings settings)
	{
		return getPollManager().updateSettings(settings);
	}

	public PollManager getPollManager()
	{
		return (PollManager) getPrivilegedPollManager();
	}

	public CategorizablePrivilegedManager<PollRecord, Poll> getPrivilegedPollManager()
	{
		return privilegedPollManager;
	}

	@Autowired
	public <P extends PollManager & CategorizablePrivilegedManager<PollRecord, Poll>>
	 void setPrivilegedPollManager(P privilegedPollManager)
	{
		this.privilegedPollManager = privilegedPollManager;
	}

	@Override
	public CategorizablePrivilegedManager<PollRecord, Poll> getImplementationManager()
	{
		return getPrivilegedPollManager();
	}
}
