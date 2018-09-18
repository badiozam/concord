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
package com.i4one.promotion.tests.model.socialphrase;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.tests.core.BaseUserManagerTest;
import com.i4one.promotion.model.socialphrase.SocialPhrase;
import com.i4one.promotion.model.socialphrase.SocialPhraseManager;
import com.i4one.promotion.model.socialphrase.SocialPhraseResponse;
import com.i4one.promotion.model.socialphrase.SocialPhraseResponseManager;
import java.util.List;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SocialPhraseResponseTest extends BaseUserManagerTest
{
	private SocialPhraseResponseManager socialPhraseResponseManager;
	private SocialPhraseManager socialPhraseManager;
	private SocialPhrase socialPhrase;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		socialPhrase = new SocialPhrase();
		socialPhrase.setTitle(new IString("SocialPhrase Title"));
		socialPhrase.setPhrases("#wtf,#omg");

		socialPhrase.setStartTimeSeconds(Utils.currentTimeSeconds() - 5);
		socialPhrase.setEndTimeSeconds(Utils.currentTimeSeconds() + 5);

		socialPhrase.setClient(getFirstClient());
		socialPhrase.setSiteGroup(getFirstSiteGroup());

		logAdminIn(getFirstAdmin());

		ReturnType<SocialPhrase> socialphraseCreate = getSocialPhraseManager().create(socialPhrase);
		assertTrue(socialphraseCreate.getPost().exists());
		assertEquals(2, socialPhrase.getPhrasesList().size());
	}


	@Test
	public void testPhraseFound()
	{
		logAdminOut();
		logUserIn(getFirstUser());

		List<ReturnType<SocialPhraseResponse>> processed = getSocialPhraseResponseManager()
			.processSocialPhrases("Wow what just happened?! #omg #wtf",
				getFirstUser(),
				new TerminablePagination(Utils.currentTimeSeconds(),
					new SiteGroupPagination(getFirstSiteGroup(),
						new ClientPagination(getFirstClient(),
							SimplePaginationFilter.NONE))));

		assertNotNull(processed);
		assertEquals(1, processed.size());

		ReturnType<SocialPhraseResponse> response = processed.iterator().next();
		assertNotNull(response);
		assertTrue(response.getPost().exists());
		assertEquals(1, response.getPost().getQuantity());
		assertEquals(socialPhrase, response.getPost().getSocialPhrase());
	}

	@Test
	public void testPhraseNotFound()
	{
		logAdminOut();
		logUserIn(getFirstUser());

		List<ReturnType<SocialPhraseResponse>> processed = getSocialPhraseResponseManager()
			.processSocialPhrases("#wtf is up with that?",
				getFirstUser(),
				new TerminablePagination(Utils.currentTimeSeconds(),
					new SiteGroupPagination(getFirstSiteGroup(),
						new ClientPagination(getFirstClient(),
							SimplePaginationFilter.NONE))));

		assertNotNull(processed);
		assertEquals(0, processed.size());
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

	public SocialPhraseResponseManager getSocialPhraseResponseManager()
	{
		return socialPhraseResponseManager;
	}

	@Autowired
	public void setSocialPhraseResponseManager(SocialPhraseResponseManager socialPhraseResponseManager)
	{
		this.socialPhraseResponseManager = socialPhraseResponseManager;
	}

}