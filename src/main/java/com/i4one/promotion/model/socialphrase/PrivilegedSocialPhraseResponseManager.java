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
import com.i4one.base.model.manager.activity.BaseActivityPrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.report.ActivityReport;
import com.i4one.base.model.report.TopLevelReport;
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
public class PrivilegedSocialPhraseResponseManager extends BaseActivityPrivilegedManager<SocialPhraseResponseRecord, SocialPhraseResponse, SocialPhrase> implements SocialPhraseResponseManager
{
	private SocialPhraseResponseManager socialPhraseResponseManager;

	@Override
	public User getUser(SocialPhraseResponse item)
	{
		return item.getUser();
	}

	@Override
	public SocialPhraseResponseManager getImplementationManager()
	{
		return getSocialPhraseResponseManager();
	}

	@Override
	public SingleClient getClient(SocialPhraseResponse item)
	{
		return item.getSocialPhrase().getClient();
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<SocialPhraseResponse>> processSocialPhrases(String socialPhraseStr, User user, TerminablePagination pagination)
	{
		List<ReturnType<SocialPhraseResponse>> retVal = getSocialPhraseResponseManager().processSocialPhrases(socialPhraseStr, user, pagination);

		// The user has to have had access to each socialPhrase retrieved before
		// we allow the transaction to go through
		//
		retVal.forEach( (ReturnType<SocialPhraseResponse> retSocialPhraseResponse) ->
		{
			checkWrite(retSocialPhraseResponse.getPost(), getUser(retSocialPhraseResponse.getPost()), "create");
		});

		return retVal;
	}

	public SocialPhraseResponseManager getSocialPhraseResponseManager()
	{
		return socialPhraseResponseManager;
	}

	@Autowired
	@Qualifier("promotion.InstantWinningSocialPhraseResponseManager")
	public void setSocialPhraseResponseManager(SocialPhraseResponseManager socialPhraseResponseManager)
	{
		this.socialPhraseResponseManager = socialPhraseResponseManager;
	}

	@Override
	public ActivityReport getReport(SocialPhraseResponse item, TopLevelReport report, PaginationFilter pagination)
	{
		return getSocialPhraseResponseManager().getReport(item, report, pagination);
	}
}
