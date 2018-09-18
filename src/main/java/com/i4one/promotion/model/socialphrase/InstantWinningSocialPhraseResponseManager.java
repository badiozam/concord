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
package com.i4one.promotion.model.socialphrase;

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.instantwinnable.BaseInstantWinningManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class InstantWinningSocialPhraseResponseManager extends BaseInstantWinningManager<SocialPhraseResponseRecord, SocialPhraseResponse, SocialPhrase> implements SocialPhraseResponseManager
{
	private SocialPhraseResponseManager socialPhraseResponseManager;

	@Override
	public User getUser(SocialPhraseResponse item)
	{
		return item.getUser();
	}

	@Override
	public RecordTypeDelegator<?> getAttachee(SocialPhraseResponse socialPhraseResponse)
	{
		return socialPhraseResponse.getActionItem();
	}

	@Override
	public SocialPhraseResponseManager getImplementationManager()
	{
		return getSocialPhraseResponseManager();
	}

	@Override
	protected void processAttached(ReturnType<SocialPhraseResponse> retSocialPhraseResponse, String methodName)
	{
		// We only process the instant wins if the socialPhrase was successfully processed for the first time
		//
		SocialPhraseResponse pre = retSocialPhraseResponse.getPre();
		SocialPhraseResponse post = retSocialPhraseResponse.getPost();

		if ( post.exists() && pre.equals(post) ||
			!post.exists() )
		{
			getLogger().debug("Instant wins not processed for " + retSocialPhraseResponse.getPost().getSocialPhrase() + " and user " + retSocialPhraseResponse.getPost().getUser(false) + " since the phrase was previously played or expired");
		}
		else
		{
			super.processAttached(retSocialPhraseResponse, methodName);
		}

	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<SocialPhraseResponse>> processSocialPhrases(String socialPhraseStr, User user, TerminablePagination pagination)
	{
		List<ReturnType<SocialPhraseResponse>> retVal = getSocialPhraseResponseManager().processSocialPhrases(socialPhraseStr, user, pagination);

		retVal.forEach( (ReturnType<SocialPhraseResponse> retSocialPhraseResponse) -> 
		{
			// We have to simulate the create method for each item
			//
			processAttached(retSocialPhraseResponse, "create");
		});

		return retVal;
	}

	public SocialPhraseResponseManager getSocialPhraseResponseManager()
	{
		return socialPhraseResponseManager;
	}

	@Autowired
	@Qualifier("promotion.TriggeredSocialPhraseResponseManager")
	public void setSocialPhraseResponseManager(SocialPhraseResponseManager socialPhraseResponseManager)
	{
		this.socialPhraseResponseManager = socialPhraseResponseManager;
	}

}
