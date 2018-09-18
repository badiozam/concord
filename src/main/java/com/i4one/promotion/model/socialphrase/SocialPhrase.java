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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.manager.instantwinnable.SimpleTerminableInstantWinnable;
import com.i4one.base.model.manager.instantwinnable.TerminableInstantWinnableClientType;
import com.i4one.base.model.manager.terminable.BaseTerminableSiteGroupType;
import com.i4one.base.model.manager.triggerable.SimpleTerminableTriggerable;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class SocialPhrase extends BaseTerminableSiteGroupType<SocialPhraseRecord> implements TerminableTriggerableClientType<SocialPhraseRecord, SocialPhrase>, TerminableInstantWinnableClientType<SocialPhraseRecord, SocialPhrase>
{
	private final transient SimpleTerminableTriggerable<SocialPhraseRecord, SocialPhrase> triggerable;
	private final transient SimpleTerminableInstantWinnable<SocialPhraseRecord, SocialPhrase> instantWinnable;

	public SocialPhrase()
	{
		super(new SocialPhraseRecord());

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}

	protected SocialPhrase(SocialPhraseRecord delegate)
	{
		super(delegate);

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}
	
	@Override
	protected void init()
	{
		super.init();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( Utils.isEmpty(getDelegate().getPhrases()) )
		{
			retVal.addError(new ErrorMessage("phrases", "msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".phrases.empty", "Phrases cannot be empty or entirely comprised of white space", new Object[]{"item", this}));
		}

		retVal.merge(triggerable.validate());
		retVal.merge(instantWinnable.validate());

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		triggerable.setOverrides();
		instantWinnable.setOverrides();
	}

	public String getPhrases()
	{
		return getDelegate().getPhrases();
	}

	public void setPhrases(String phrases)
	{
		getDelegate().setPhrases(Utils.trimString(Utils.forceEmptyStr(phrases)).replaceAll("\\s+", " "));
	}

	public List<String> getPhrasesList()
	{
		return Arrays.asList(getPhrases().split(","));
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return Objects.hashCode(getPhrases()) + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
	}

	@Override
	protected boolean equalsInternal(RecordTypeDelegator<SocialPhraseRecord> right)
	{
		if ( right instanceof SocialPhrase )
		{
			SocialPhrase rightPhrase = (SocialPhrase)right;
			return getStartTimeSeconds() == rightPhrase.getStartTimeSeconds() &&
				getEndTimeSeconds() == rightPhrase.getEndTimeSeconds() &&
				getPhrases().equals(rightPhrase.getPhrases());
		}
		else
		{
			return false;
		}
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		if  ( super.fromCSVInternal(csv) )
		{
			String phrases = csv.get(0); csv.remove(0);
			setPhrases(phrases);

			boolean retVal = true;

			retVal &= triggerable.fromCSVList(csv);
			retVal &= instantWinnable.fromCSVList(csv);

			return retVal;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected StringBuilder toCSVInternal(boolean header)
	{
		StringBuilder retVal = super.toCSVInternal(header);

		if ( header )
		{
			// XXX: Needs to be i18n
			retVal.append(Utils.csvEscape("Phrases")).append(",");
		}
		else
		{
			retVal.append("\"").append(Utils.csvEscape(getPhrases())).append("\",");
		}

		retVal.append(triggerable.toCSV(header));
		retVal.append(instantWinnable.toCSV(header));

		return retVal;
	}

	@Override
	public Set<ExclusiveBalanceTrigger<SocialPhrase>> getExclusiveBalanceTriggers()
	{
		return triggerable.getExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getNonExclusiveBalanceTriggers()
	{
		return triggerable.getNonExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getBalanceTriggers()
	{
		return triggerable.getBalanceTriggers();
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> balanceTriggers)
	{
		triggerable.setBalanceTriggers(balanceTriggers);
	}

	@Override
	public Set<ExclusiveInstantWin<SocialPhrase>> getExclusiveInstantWins()
	{
		return instantWinnable.getExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getNonExclusiveInstantWins()
	{
		return instantWinnable.getNonExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getInstantWins()
	{
		return instantWinnable.getInstantWins();
	}

	@Override
	public void setInstantWins(Collection<InstantWin> instantWins)
	{
		instantWinnable.setInstantWins(instantWins);
	}

	/**
	 * Determines whether a match is found in a given list of strings.
	 * A match is found if all phrases of this object can be found in
	 * the input list of phrases.
	 * 
	 * @param phrases The phrase list to match.
	 * 
	 * @return True if there is a match.
	 */
	public boolean matches(List<String> phrases)
	{
		return phrases.containsAll(getPhrasesList());
	}

	/**
	 * Converts a given phrase string to an array of strings that could
	 * potentially be matched as belonging to a given Social Phrase. The
	 * string has all commas removed, divided split on whitespace. The
	 * whole string is also included in the return value as it may be the
	 * only item that matches.
	 * 
	 * @param phraseStr The string to convert
	 * 
	 * @return An array of phrases to match
	 */
	public static List<String> toPhraseArray(String phraseStr)
	{
		String[] phraseComponents = Utils.forceEmptyStr(phraseStr).replaceAll(",", "").split("\\s+");
		String[] phraseComponentsAndPhrase = new String[phraseComponents.length + 1];

		System.arraycopy(phraseComponents, 0, phraseComponentsAndPhrase, 0, phraseComponents.length);
		phraseComponentsAndPhrase[phraseComponents.length] = phraseStr;

 		List<String> retVal = Arrays.asList(phraseComponentsAndPhrase);
		retVal.sort(null);

		return retVal;
	}
}
