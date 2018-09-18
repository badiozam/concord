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

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.instantwinnable.BaseInstantWinningManager;
import com.i4one.base.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class InstantWinningTriviaResponseManager extends BaseInstantWinningManager<TriviaResponseRecord, TriviaResponse, Trivia> implements TriviaResponseManager
{
	private TriviaResponseManager triviaResponseManager;

	@Override
	public User getUser(TriviaResponse item)
	{
		return item.getUser();
	}

	@Override
	public RecordTypeDelegator<?> getAttachee(TriviaResponse triviaResponse)
	{
		return triviaResponse.getTrivia();
	}

	@Override
	public TriviaResponseManager getImplementationManager()
	{
		return getTriviaResponseManager();
	}

	@Override
	protected void processAttached(ReturnType<TriviaResponse> retTriviaResponse, String methodName)
	{
		// We only process the instant wins if the trivia was successfully processed for the first time
		//
		TriviaResponse pre = retTriviaResponse.getPre();
		TriviaResponse post = retTriviaResponse.getPost();

		if (
			(post.exists() && pre.equals(post)) 	||	// Previously played
			(!post.exists())			||	// Failed/Expired
			(!post.isCorrect())				// Incorrect answer
		)
		{
			getLogger().debug("Instant wins not processed for " + retTriviaResponse.getPost().getTrivia() + " and user " + retTriviaResponse.getPost().getUser(false) + " since the trivia was previously played or expired");
		}
		else
		{
			super.processAttached(retTriviaResponse, methodName);
		}

	}

	public TriviaResponseManager getTriviaResponseManager()
	{
		return triviaResponseManager;
	}

	@Autowired
	@Qualifier("promotion.TriggeredTriviaResponseManager")
	public void setTriviaResponseManager(TriviaResponseManager triviaResponseManager)
	{
		this.triviaResponseManager = triviaResponseManager;
	}

}
