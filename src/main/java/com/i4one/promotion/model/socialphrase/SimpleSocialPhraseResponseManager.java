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
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleSocialPhraseResponseManager extends BaseActivityManager<SocialPhraseResponseRecord, SocialPhraseResponse, SocialPhrase> implements SocialPhraseResponseManager
{
	private SocialPhraseManager socialPhraseManager;

	@Override
	public SocialPhraseResponse emptyInstance()
	{
		return new SocialPhraseResponse();
	}

	@Override
	protected ReturnType<SocialPhraseResponse> createInternal(SocialPhraseResponse socialPhraseResponse)
	{
		// We ensure that the accurate time stamp is reflected
		//
		socialPhraseResponse.setQuantity(1);

		// Handled by the superclass
		//socialPhraseResponse.setTimeStampSeconds(Utils.currentTimeSeconds());

		SocialPhraseResponse prevResponse = getActivity(socialPhraseResponse.getSocialPhrase(), socialPhraseResponse.getUser());
		SocialPhraseResponseRecord lockedRecord = null;

		if ( prevResponse.exists() )
		{
			// We want to make sure we have a lock on that record and go off of that locked item
			// for any checks. If the item didn't exist, there's no reason to lock.
			//
			lockedRecord = lock(prevResponse);
			prevResponse = new SocialPhraseResponse(lockedRecord);
		}

		// We do this to ensure that all qualifiers (such as client ownership) are satisfied.
		// This prevents situations where a user can attempt a click thru directly by serial
		// number on a different client
		//
		if ( !socialPhraseResponse.getSocialPhrase().isValidAt(socialPhraseResponse.getTimeStampSeconds()) )
		{
			getLogger().debug("Social phrase is no longer valid");

			// The socialPhrase has expired by the time we reached this point
			//
			ReturnType<SocialPhraseResponse> expired = new ReturnType<>();

			// We set both to the socialPhraseResponse which should not exist
			// at this point, the reason we do this is to preserve the user
			// and item id that was attempted for other managers
			//
			expired.setPre(socialPhraseResponse);
			expired.setPost(socialPhraseResponse);

			return expired;
		}
		else if ( prevResponse.exists() )
		{
			getLogger().debug("Social phrase is valid incrementing the previous response " + prevResponse);

			// Updated the previous response
			//
			socialPhraseResponse.setSer(prevResponse.getSer());
			socialPhraseResponse.setQuantity(prevResponse.getQuantity() + 1);

			// Note that at this point, since we haven't called socialPhraseResponse.loadedVersion()
			// all of the previous data (i.e. timestamp) is still preserved

			return super.updateInternal(lockedRecord, socialPhraseResponse);
		}
		else
		{
			getLogger().debug("Social phrase is valid creating response " + socialPhraseResponse);

			return super.createInternal(socialPhraseResponse);
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<SocialPhraseResponse> update(SocialPhraseResponse socialPhraseResponse)
	{
		throw new UnsupportedOperationException("Can't update a social phrase response.");
	}

	@Override
	public List<ReturnType<SocialPhraseResponse>> processSocialPhrases(String phraseStr, User user, TerminablePagination pagination)
	{
		getLogger().debug("Processing phrase " + phraseStr + " for user " + user);
		List<ReturnType<SocialPhraseResponse>> retVal = new ArrayList<>();
		
		// No commas allowed and split the string on white space
		//
		List<String> phrases = SocialPhrase.toPhraseArray(phraseStr.replaceAll("\\s+", " "));

		// Check all of the phrases to see if they match any live ones
		// at our input time
		//
		Set<SocialPhrase> potentialPhrases = new HashSet<>();
		for ( String phrase : phrases )
		{
			Set<SocialPhrase> validPhrases = getSocialPhraseManager().getLiveSocialPhrases(phrase, pagination);
			potentialPhrases.addAll(validPhrases);
		}

		// We're only processing the first code that's live and available
		//
		for ( SocialPhrase phrase : potentialPhrases)
		{
			SocialPhraseResponse phraseResponse = new SocialPhraseResponse();
			phraseResponse.setSocialPhrase(phrase);
			phraseResponse.setUser(user);

			if ( phrase.matches(phrases) )
			{
				retVal.add(create(phraseResponse));
			}
		}

		return retVal;
	}

	@Override
	public SocialPhraseResponseRecordDao getDao()
	{
		return (SocialPhraseResponseRecordDao) super.getDao();
	}

	public SocialPhraseManager getSocialPhraseManager()
	{
		return socialPhraseManager;
	}

	@Autowired
	public void setSocialPhraseManager(SocialPhraseManager socialPhraseManager)
	{
		this.socialPhraseManager = socialPhraseManager;
	}
}
