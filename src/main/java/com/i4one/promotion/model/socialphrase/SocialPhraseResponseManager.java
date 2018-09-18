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
import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface SocialPhraseResponseManager extends ActivityManager<SocialPhraseResponseRecord, SocialPhraseResponse, SocialPhrase>
{
	/**
	 * Attempts to process a given phrase for a particular user. If the phrase was
	 * previously played by the user, the corresponding SocialPhraseResponse record is
	 * set as both pre and post elements and no action is taken. Otherwise,
	 * a new SocialPhraseResponse object is created.
	 * 
	 * @param phraseResponse The phrase response to create
	 * 
	 * @return The result of the processing
	 */
	@Override
	public ReturnType<SocialPhraseResponse> create(SocialPhraseResponse phraseResponse);

	/**
	 * Attempts to record a response for any number of phrases that match the
	 * given phrase string identifier.
	 * 
	 * @param phraseStr The phrase string identifier
	 * @param user The user responding to the phrases
	 * @param pagination The pagination to use when retrieving the phrases,
	 * 	a terminable pagination with the current time chains this object.
	 * 
	 * @return A set of responses that were recorded for the given phrases
	 */
	public List<ReturnType<SocialPhraseResponse>> processSocialPhrases(String phraseStr, User user, TerminablePagination pagination);
}