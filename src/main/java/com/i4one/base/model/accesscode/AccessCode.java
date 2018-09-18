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
package com.i4one.base.model.accesscode;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.terminable.BaseTerminableSingleClientType;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.manager.instantwinnable.SimpleTerminableInstantWinnable;
import com.i4one.base.model.manager.instantwinnable.TerminableInstantWinnableClientType;
import com.i4one.base.model.manager.triggerable.SimpleTerminableTriggerable;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public class AccessCode extends BaseTerminableSingleClientType<AccessCodeRecord> implements TerminableTriggerableClientType<AccessCodeRecord, AccessCode>,TerminableInstantWinnableClientType<AccessCodeRecord, AccessCode>
{
	static final long serialVersionUID = 42L;

	private final transient SimpleTerminableTriggerable<AccessCodeRecord, AccessCode> triggerable;
	private final transient SimpleTerminableInstantWinnable<AccessCodeRecord, AccessCode> instantWinnable;

	public AccessCode()
	{
		super(new AccessCodeRecord());

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}

	public AccessCode(AccessCodeRecord delegate)
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
	public void setOverrides()
	{
		super.setOverrides();

		triggerable.setOverrides();
		instantWinnable.setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		triggerable.actualizeRelations();
		instantWinnable.actualizeRelations();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal.merge(triggerable.validate());
		retVal.merge(instantWinnable.validate());

		return retVal;
	}

	public String getCode()
	{
		return getDelegate().getCode();
	}

	public void setCode(String code)
	{
		getDelegate().setCode(code);
	}

	public IString getTitle()
	{
		return getDelegate().getTitle();
	}

	public void setTitle(IString title)
	{
		getDelegate().setTitle(title);
	}

	public IString getDescr()
	{
		return getDelegate().getDescr();
	}

	public void setDescr(IString descr)
	{
		getDelegate().setDescr(descr);
	}

	public IString getAccessDenied()
	{
		return getDelegate().getAccessdenied();
	}

	public void setAccessDenied(IString msg)
	{
		getDelegate().setAccessdenied(msg);
	}

	public Set<String> getPages()
	{
		String[] pageArray = getDelegate().getPages().split(":");
		return Arrays.asList(pageArray).stream().collect(Collectors.toSet());
	}

	public void setPages(String pages)
	{
		getDelegate().setPages(pages);
	}

	public void setPages(Collection<String> pages)
	{
		StringBuilder pageStr = new StringBuilder();
		for ( String page : pages)
		{
			pageStr.append(page);
			pageStr.append(":");
		}

		// We remove the last separator before setting the encoded string
		//
		setPages(pageStr.toString().substring(0, pageStr.length() - 1));
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		if  ( super.fromCSVInternal(csv) )
		{
			String title = csv.get(0); csv.remove(0);
			setTitle(new IString(title));

			String code = csv.get(0); csv.remove(0);
			setCode(code);

			String descr = csv.get(0); csv.remove(0);
			setDescr(new IString(descr));

			String accessDenied = csv.get(0); csv.remove(0);
			setAccessDenied(new IString(accessDenied));

			String pages = csv.get(0); csv.remove(0);
			setPages(pages);

			boolean retVal = true;

			//retVal &= triggerable.fromCSVList(csv);
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
			retVal.append(Utils.csvEscape("Title")).append(",");
			retVal.append(Utils.csvEscape("Code")).append(",");
			retVal.append(Utils.csvEscape("Prompt")).append(",");
			retVal.append(Utils.csvEscape("Access Denied")).append(",");
			retVal.append(Utils.csvEscape("Pages")).append(",");
		}
		else
		{
			retVal.append("\"").append(Utils.csvEscape(getTitle().toString())).append("\",");
			retVal.append("\"").append(Utils.csvEscape(getCode())).append("\",");
			retVal.append("\"").append(Utils.csvEscape(getDescr().toString())).append("\",");
			retVal.append("\"").append(Utils.csvEscape(getAccessDenied().toString())).append("\",");
			retVal.append("\"").append(Utils.csvEscape(getDelegate().getPages())).append("\",");
		}

		//retVal.append(triggerable.toCSV(header));
		retVal.append(instantWinnable.toCSV(header));

		return retVal;
	}

	@Override
	public Set<ExclusiveBalanceTrigger<AccessCode>> getExclusiveBalanceTriggers()
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
	protected String uniqueKeyInternal()
	{
		return getCode() + "-" + getStartTimeSeconds();
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

	@Override
	public Set<ExclusiveInstantWin<AccessCode>> getExclusiveInstantWins()
	{
		return instantWinnable.getExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getNonExclusiveInstantWins()
	{
		return instantWinnable.getNonExclusiveInstantWins();
	}
}