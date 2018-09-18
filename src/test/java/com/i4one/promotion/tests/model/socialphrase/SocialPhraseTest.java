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
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.TerminableExclusiveBalanceTrigger;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.instantwin.TerminableExclusiveInstantWin;
import com.i4one.base.tests.model.BaseRecordTypeDelegatorTest;
import com.i4one.promotion.model.socialphrase.SocialPhrase;
import com.i4one.promotion.model.socialphrase.SocialPhraseManager;
import com.i4one.promotion.model.socialphrase.SocialPhraseRecord;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Hamid Badiozamani
 */
public class SocialPhraseTest extends BaseRecordTypeDelegatorTest<SocialPhraseRecord, SocialPhrase>
{
	private BalanceManager balanceManager;
	private SocialPhraseManager socialPhraseManager;

	private SocialPhrase socialphrase;

	@Override
	public SocialPhrase newItem()
	{
		SocialPhrase retVal = new SocialPhrase();

		socialphrase.setStartTimeSeconds(5);
		socialphrase.setEndTimeSeconds(10);

		return retVal;
	}

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		socialphrase = new SocialPhrase();
		socialphrase.setTitle(new IString("SocialPhrase Title"));
		socialphrase.setPhrases("testing");

		socialphrase.setStartTimeSeconds(Utils.currentTimeSeconds() - 5);
		socialphrase.setEndTimeSeconds(Utils.currentTimeSeconds() + 5);

		socialphrase.setClient(getFirstClient());
		socialphrase.setSiteGroup(getFirstSiteGroup());

		logAdminIn(getFirstAdmin());

		ReturnType<SocialPhrase> socialphraseCreate = getSocialPhraseManager().create(socialphrase);
		assertTrue(socialphraseCreate.getPost().exists());
	}


	@Test
	public void testWhitespaceNormalization()
	{
		SocialPhrase testPhrase = new SocialPhrase();

		testPhrase.setTitle(new IString("SocialPhrase Title"));
		testPhrase.setPhrases("	testing something   with 	 strange whitespace ");

		assertEquals("testing something with strange whitespace", testPhrase.getPhrases());
	}

	@Test
	public void testUpdate()
	{
		int prevEndTime = socialphrase.getEndTimeSeconds();

		int socialphraseSer = socialphrase.getSer();
		socialphrase.setSer(0);
		socialphrase.setSer(socialphraseSer);

		socialphrase.setEndTimeSeconds(prevEndTime + 10);

		ReturnType<SocialPhrase> socialphraseUpdate = getSocialPhraseManager().update(socialphrase);
		assertEquals(prevEndTime + 10, socialphrase.getEndTimeSeconds());
	}

	@Test
	public void testCSV()
	{
		{
			BalanceTrigger trigger = new TerminableExclusiveBalanceTrigger(socialphrase);
			trigger.setAmount(1000);
			trigger.setBalance(getBalanceManager().getDefaultBalance(getFirstClient()));

			ArrayList<BalanceTrigger> triggers = new ArrayList<>();
			triggers.add(trigger);

			socialphrase.setBalanceTriggers(triggers);
		}

		{
			InstantWin instantWin = new TerminableExclusiveInstantWin(socialphrase);
			instantWin.setPercentWin(0.0f);
			instantWin.setLoserMessage(new IString("Sorry you lost."));
			instantWin.setWinnerMessage(new IString("Looks like you won."));

			ArrayList<InstantWin> instantWins = new ArrayList<>();
			instantWins.add(instantWin);

			socialphrase.setInstantWins(instantWins);
		}

		ReturnType<SocialPhrase> socialphraseUpdate = getSocialPhraseManager().update(socialphrase);

		String csv = socialphrase.toCSV(false);

		getLogger().debug("CSV export = " + csv);
		assertNotNull(csv);

		SocialPhrase importedSocialPhrase = new SocialPhrase();
		importedSocialPhrase.setClient(getFirstClient());
		importedSocialPhrase.setSiteGroup(getFirstSiteGroup());

		importedSocialPhrase.fromCSV(csv);

		String exportedCSV = importedSocialPhrase.toCSV(false);

		assertNotSame(csv, exportedCSV);
		assertEquals(csv, exportedCSV);
	}

	@Test
	public void testCSVImportExport() throws IOException, Exception
	{
		ArrayList<SocialPhrase> socialphrases = new ArrayList<>();

		int numSocialPhrases = 50;
		for ( int i = 0; i < numSocialPhrases; i++ )
		{
			SocialPhrase currSocialPhrase = new SocialPhrase();

			currSocialPhrase.setTitle(new IString("SocialPhrase Title" + i));
			currSocialPhrase.setPhrases("testing" + i);

			currSocialPhrase.setStartTimeSeconds(Utils.currentTimeSeconds() - 5 + i);
			currSocialPhrase.setEndTimeSeconds(Utils.currentTimeSeconds() + 5 + i);

			currSocialPhrase.setClient(getFirstClient());
			currSocialPhrase.setSiteGroup(getFirstSiteGroup());

			{
				// Balance trigger for each socialphrase
				BalanceTrigger trigger = new TerminableExclusiveBalanceTrigger(currSocialPhrase);
				trigger.setAmount(1000 + i);
				trigger.setBalance(getBalanceManager().getDefaultBalance(getFirstClient()));

				ArrayList<BalanceTrigger> triggers = new ArrayList<>();
				triggers.add(trigger);

				currSocialPhrase.setBalanceTriggers(triggers);
			}

			{
				InstantWin instantWin = new TerminableExclusiveInstantWin(currSocialPhrase);
				instantWin.setPercentWin(0.1f);
				instantWin.setLoserMessage(new IString("Sorry you lost."));
				instantWin.setWinnerMessage(new IString("Looks like you won."));

				ArrayList<InstantWin> instantWins = new ArrayList<>();
				instantWins.add(instantWin);

				currSocialPhrase.setInstantWins(instantWins);
			}

			// Create should instantiate both balance triggers as well as the socialphrases
			//
			ReturnType<SocialPhrase> createVal = getSocialPhraseManager().create(currSocialPhrase);
			assertTrue(createVal.getPost().exists());
			assertSame(createVal.getPost(), currSocialPhrase);

			socialphrases.add(createVal.getPost());
		}

		for ( SocialPhrase currSocialPhrase : socialphrases )
		{
			assertTrue(currSocialPhrase.exists());
			assertTrue(currSocialPhrase.getPhrases().startsWith("testing"));
			assertFalse(currSocialPhrase.getBalanceTriggers().isEmpty());

			BalanceTrigger currTrigger = currSocialPhrase.getBalanceTriggers().iterator().next();
			assertTrue(currTrigger.isExclusive());
			assertTrue(currTrigger.getAmount() >= 1000);

			InstantWin currInstantWin = currSocialPhrase.getInstantWins().iterator().next();
			assertTrue(currInstantWin.isExclusive());
			assertEquals(0.1f, currInstantWin.getPercentWin(), 0.0f);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		getLogger().debug(getSocialPhraseManager().exportCSV(socialphrases.stream().collect(Collectors.toSet()), out));

		out.close();

		logAdminOut();
		logAdminIn(getSecondAdminRW());

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		List<ReturnType<SocialPhrase>> importedResults = getSocialPhraseManager().importCSV(in,
			() ->
			{
				SocialPhrase newSocialPhrase = new SocialPhrase();

				newSocialPhrase.setClient(getSecondClient());
				newSocialPhrase.setSiteGroup(getSecondSiteGroup());

				return newSocialPhrase;
			}, (item) -> { return true; }, (item) -> {});

		Set<SocialPhrase> imported = importedResults.stream().map( (result) -> { return result.getPost(); }).collect(Collectors.toSet());

		assertNotNull(imported);
		assertEquals(socialphrases.size(), imported.size());

		for ( SocialPhrase currSocialPhrase : imported )
		{
			assertTrue(currSocialPhrase.exists());
			assertTrue(currSocialPhrase.getPhrases().startsWith("testing"));
			assertFalse(currSocialPhrase.getBalanceTriggers().isEmpty());

			BalanceTrigger currTrigger = currSocialPhrase.getBalanceTriggers().iterator().next();
			assertTrue(currTrigger.getAmount() >= 1000);

			InstantWin currInstantWin = currSocialPhrase.getInstantWins().iterator().next();
			assertTrue(currInstantWin.isExclusive());
			assertEquals(0.1f, currInstantWin.getPercentWin(), 0.0f);
		}

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

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	@Qualifier("base.CachedBalanceManager")
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}
}
