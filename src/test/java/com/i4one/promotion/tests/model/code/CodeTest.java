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
package com.i4one.promotion.tests.model.code;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.TerminableExclusiveBalanceTrigger;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.instantwin.TerminableExclusiveInstantWin;
import com.i4one.base.tests.model.BaseRecordTypeDelegatorTest;
import com.i4one.promotion.model.code.Code;
import com.i4one.promotion.model.code.CodeManager;
import com.i4one.promotion.model.code.CodeRecord;
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
public class CodeTest extends BaseRecordTypeDelegatorTest<CodeRecord, Code>
{
	private BalanceManager balanceManager;
	private CodeManager codeManager;

	private Code code;

	@Override
	public Code newItem()
	{
		Code retVal = new Code();

		code.setStartTimeSeconds(5);
		code.setEndTimeSeconds(10);

		return retVal;
	}

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		code = new Code();
		code.setTitle(new IString("Code Title"));
		code.setCode("testing");
		code.setOutro(new IString("Outro"));

		code.setStartTimeSeconds(Utils.currentTimeSeconds() - 5);
		code.setEndTimeSeconds(Utils.currentTimeSeconds() + 5);

		code.setClient(getFirstClient());
		code.setSiteGroup(getFirstSiteGroup());

		logAdminIn(getFirstAdmin());

		ReturnType<Code> codeCreate = getCodeManager().create(code);
		assertTrue(codeCreate.getPost().exists());
	}


	@Test
	public void testUpdate()
	{
		int prevEndTime = code.getEndTimeSeconds();

		int codeSer = code.getSer();
		code.setSer(0);
		code.setSer(codeSer);

		code.setEndTimeSeconds(prevEndTime + 10);

		ReturnType<Code> codeUpdate = getCodeManager().update(code);
		assertEquals(prevEndTime + 10, code.getEndTimeSeconds());
	}

	@Test
	public void testCSV()
	{
		{
			BalanceTrigger trigger = new TerminableExclusiveBalanceTrigger(code);
			trigger.setAmount(1000);
			trigger.setBalance(getBalanceManager().getDefaultBalance(getFirstClient()));

			ArrayList<BalanceTrigger> triggers = new ArrayList<>();
			triggers.add(trigger);

			code.setBalanceTriggers(triggers);
		}

		{
			InstantWin instantWin = new TerminableExclusiveInstantWin(code);
			instantWin.setPercentWin(0.0f);
			instantWin.setLoserMessage(new IString("Sorry you lost."));
			instantWin.setWinnerMessage(new IString("Looks like you won."));

			ArrayList<InstantWin> instantWins = new ArrayList<>();
			instantWins.add(instantWin);

			code.setInstantWins(instantWins);
		}

		ReturnType<Code> codeUpdate = getCodeManager().update(code);

		String csv = code.toCSV(false);

		getLogger().debug("CSV export = " + csv);
		assertNotNull(csv);

		Code importedCode = new Code();
		importedCode.setClient(getFirstClient());
		importedCode.setSiteGroup(getFirstSiteGroup());

		importedCode.fromCSV(csv);

		String exportedCSV = importedCode.toCSV(false);

		assertNotSame(csv, exportedCSV);
		assertEquals(csv, exportedCSV);
	}

	@Test
	public void testCSVImportExport() throws IOException, Exception
	{
		ArrayList<Code> codes = new ArrayList<>();

		int numCodes = 50;
		for ( int i = 0; i < numCodes; i++ )
		{
			Code currCode = new Code();

			currCode.setTitle(new IString("Code Title" + i));
			currCode.setCode("testing" + i);
			currCode.setOutro(new IString("Outro" + i));

			currCode.setStartTimeSeconds(Utils.currentTimeSeconds() - 5 + i);
			currCode.setEndTimeSeconds(Utils.currentTimeSeconds() + 5 + i);

			currCode.setClient(getFirstClient());
			currCode.setSiteGroup(getFirstSiteGroup());

			{
				// Balance trigger for each code
				BalanceTrigger trigger = new TerminableExclusiveBalanceTrigger(currCode);
				trigger.setAmount(1000 + i);
				trigger.setBalance(getBalanceManager().getDefaultBalance(getFirstClient()));

				ArrayList<BalanceTrigger> triggers = new ArrayList<>();
				triggers.add(trigger);

				currCode.setBalanceTriggers(triggers);
			}

			{
				InstantWin instantWin = new TerminableExclusiveInstantWin(currCode);
				instantWin.setPercentWin(0.1f);
				instantWin.setLoserMessage(new IString("Sorry you lost."));
				instantWin.setWinnerMessage(new IString("Looks like you won."));

				ArrayList<InstantWin> instantWins = new ArrayList<>();
				instantWins.add(instantWin);

				currCode.setInstantWins(instantWins);
			}

			// Create should instantiate both balance triggers as well as the codes
			//
			ReturnType<Code> createVal = getCodeManager().create(currCode);
			assertTrue(createVal.getPost().exists());
			assertSame(createVal.getPost(), currCode);

			codes.add(createVal.getPost());
		}

		for ( Code currCode : codes )
		{
			assertTrue(currCode.exists());
			assertTrue(currCode.getCode().startsWith("testing"));
			assertFalse(currCode.getBalanceTriggers().isEmpty());

			BalanceTrigger currTrigger = currCode.getBalanceTriggers().iterator().next();
			assertTrue(currTrigger.isExclusive());
			assertTrue(currTrigger.getAmount() >= 1000);

			InstantWin currInstantWin = currCode.getInstantWins().iterator().next();
			assertTrue(currInstantWin.isExclusive());
			assertEquals(0.1f, currInstantWin.getPercentWin(), 0.0f);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		getLogger().debug(getCodeManager().exportCSV(codes.stream().collect(Collectors.toSet()), out));

		out.close();

		logAdminOut();
		logAdminIn(getSecondAdminRW());

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		List<ReturnType<Code>> importedResults = getCodeManager().importCSV(in,
			() ->
			{
				Code newCode = new Code();

				newCode.setClient(getSecondClient());
				newCode.setSiteGroup(getSecondSiteGroup());

				return newCode;
			}, (item) -> { return true; }, (item) -> {});

		Set<Code> imported = importedResults.stream().map( (result) -> { return result.getPost(); }).collect(Collectors.toSet());

		assertNotNull(imported);
		assertEquals(codes.size(), imported.size());

		for ( Code currCode : imported )
		{
			assertTrue(currCode.exists());
			assertTrue(currCode.getCode().startsWith("testing"));
			assertFalse(currCode.getBalanceTriggers().isEmpty());

			BalanceTrigger currTrigger = currCode.getBalanceTriggers().iterator().next();
			assertTrue(currTrigger.getAmount() >= 1000);

			InstantWin currInstantWin = currCode.getInstantWins().iterator().next();
			assertTrue(currInstantWin.isExclusive());
			assertEquals(0.1f, currInstantWin.getPercentWin(), 0.0f);
		}

	}

	public CodeManager getCodeManager()
	{
		return codeManager;
	}

	@Autowired
	public void setCodeManager(CodeManager codeManager)
	{
		this.codeManager = codeManager;
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
