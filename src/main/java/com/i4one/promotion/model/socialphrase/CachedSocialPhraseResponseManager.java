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
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedSocialPhraseResponseManager extends SimpleSocialPhraseResponseManager implements SocialPhraseResponseManager
{
	@Cacheable(value = "socialPhraseResponseManager", key = "target.makeKey(#socialPhrase, #user)")
	@Override
	public SocialPhraseResponse getActivity(SocialPhrase socialPhrase, User user)
	{
		return super.getActivity(socialPhrase, user);
	}

	@CacheEvict(value = "socialPhraseResponseManager", allEntries = true )
	@Override
	public ReturnType<SocialPhraseResponse> create(SocialPhraseResponse socialPhraseResponse)
	{
		return super.create(socialPhraseResponse);
	}

	@CacheEvict(value = "socialPhraseResponseManager", allEntries = true )
	@Override
	public List<ReturnType<SocialPhraseResponse>> processSocialPhrases(String socialPhraseStr, User user, TerminablePagination pagination)
	{
		return super.processSocialPhrases(socialPhraseStr, user, pagination);
	}

	@CacheEvict(value = "socialPhraseResponseManager", allEntries = true )
	@Override
	public ReturnType<SocialPhraseResponse> update(SocialPhraseResponse respondent)
	{
		return super.update(respondent);
	}

	@CacheEvict(value = "socialPhraseResponseManager", allEntries = true)
	@Override
	public SocialPhraseResponse remove(SocialPhraseResponse respondent)
	{
		return super.remove(respondent);
	}
}
