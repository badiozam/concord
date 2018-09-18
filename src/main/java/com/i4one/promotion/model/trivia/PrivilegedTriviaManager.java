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
package com.i4one.promotion.model.trivia;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.categorizable.BaseCategorizablePrivilegedManager;
import com.i4one.base.model.manager.categorizable.CategorizablePrivilegedManager;
import com.i4one.base.model.manager.categorizable.CategorizableTerminableManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedTriviaManager extends BaseCategorizablePrivilegedManager<TriviaRecord, Trivia> implements CategorizablePrivilegedManager<TriviaRecord, Trivia>, TriviaManager
{
	private TriviaManager triviaManager;

	@Override
	public TriviaSettings getSettings(SingleClient client)
	{
		// Anyone can read the current settings because we need to know
		// whether the feature is enabled or not.
		//
		//checkRead(client, "updateSettings");
		return getTriviaManager().getSettings(client);
	}

	@Override
	public ReturnType<TriviaSettings> updateSettings(TriviaSettings settings)
	{
		checkWrite(settings.getClient(), "updateSettings");
		return getTriviaManager().updateSettings(settings);
	}

	public TriviaManager getTriviaManager()
	{
		return triviaManager;
	}

	@Autowired
	@Qualifier("promotion.InstantWinnableTriviaManager")
	public void setTriviaManager(TriviaManager instantWinnableTriviaManager)
	{
		this.triviaManager = instantWinnableTriviaManager;
	}

	@Override
	public CategorizableTerminableManager<TriviaRecord, Trivia> getImplementationManager()
	{
		return getTriviaManager();
	}

	@Override
	public SingleClient getClient(Trivia item)
	{
		return item.getClient(false);
	}
}
