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

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.terminable.BaseTerminablePrivilegedManager;
import com.i4one.base.model.manager.terminable.TerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.manager.terminable.TerminablePrivilegedManager;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedSocialPhraseManager extends BaseTerminablePrivilegedManager<SocialPhraseRecord, SocialPhrase> implements TerminablePrivilegedManager<SocialPhraseRecord, SocialPhrase>,SocialPhraseManager
{
	private SocialPhraseManager socialPhraseManager;

	@Override
	public TerminableManager<SocialPhraseRecord, SocialPhrase> getImplementationManager()
	{
		return getSocialPhraseManager();
	}

	@Override
	public SingleClient getClient(SocialPhrase item)
	{
		return item.getClient();
	}

	@Override
	public Set<SocialPhrase> getLiveSocialPhrases(String socialPhrase, TerminablePagination pagination)
	{
		return getSocialPhraseManager().getLiveSocialPhrases(socialPhrase, pagination);
	}

	@Override
	public SocialPhraseSettings getSettings(SingleClient client)
	{
		// Anyone can read the current settings because we need to know
		// whether the feature is enabled or not.
		//
		//checkRead(client, "updateSettings");
		return getSocialPhraseManager().getSettings(client);
	}

	@Override
	public ReturnType<SocialPhraseSettings> updateSettings(SocialPhraseSettings settings)
	{
		checkWrite(settings.getClient(), "updateSettings");
		return getSocialPhraseManager().updateSettings(settings);
	}

	public SocialPhraseManager getSocialPhraseManager()
	{
		return socialPhraseManager;
	}

	@Autowired
	@Qualifier("promotion.InstantWinnableSocialPhraseManager")
	public void setSocialPhraseManager(SocialPhraseManager socialPhraseManager)
	{
		this.socialPhraseManager = socialPhraseManager;
	}
}
