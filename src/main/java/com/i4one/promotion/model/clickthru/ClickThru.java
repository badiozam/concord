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
package com.i4one.promotion.model.clickthru;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.manager.instantwinnable.SimpleTerminableInstantWinnable;
import com.i4one.base.model.manager.instantwinnable.TerminableInstantWinnableClientType;
import com.i4one.base.model.manager.terminable.BaseTerminableSiteGroupType;
import com.i4one.base.model.manager.terminable.TerminableSiteGroupType;
import com.i4one.base.model.manager.triggerable.SimpleTerminableTriggerable;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import java.util.Collection;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class ClickThru extends BaseTerminableSiteGroupType<ClickThruRecord> implements TerminableSiteGroupType<ClickThruRecord>, TerminableTriggerableClientType<ClickThruRecord, ClickThru>, TerminableInstantWinnableClientType<ClickThruRecord, ClickThru>
{

	private final transient SimpleTerminableTriggerable<ClickThruRecord, ClickThru> triggerable;
	private final transient SimpleTerminableInstantWinnable<ClickThruRecord, ClickThru> instantWinnable;

	public ClickThru()
	{
		super(new ClickThruRecord());

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}

	public ClickThru(ClickThruRecord delegate)
	{
		super(delegate);

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getOutro().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".outro.empty", "Outro cannot be empty", new Object[]{"item", this}));
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

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		triggerable.actualizeRelations();
		instantWinnable.actualizeRelations();
	}

	public IString getOutro()
	{
		return getDelegate().getOutro();
	}

	public void setOutro(IString outro)
	{
		getDelegate().setOutro(outro);
	}

	public String getURL()
	{
		return getDelegate().getUrl();
	}

	public void setURL(String url)
	{
		getDelegate().setUrl(Utils.trimString(url));
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getURL() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
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
	public Set<ExclusiveBalanceTrigger<ClickThru>> getExclusiveBalanceTriggers()
	{
		return triggerable.getExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getNonExclusiveBalanceTriggers()
	{
		return triggerable.getNonExclusiveBalanceTriggers();
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
	public Set<ExclusiveInstantWin<ClickThru>> getExclusiveInstantWins()
	{
		return instantWinnable.getExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getNonExclusiveInstantWins()
	{
		return instantWinnable.getNonExclusiveInstantWins();
	}
}
