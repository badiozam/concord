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
package com.i4one.promotion.web.controller.admin.clickthrus;

import com.i4one.base.model.Errors;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.web.controller.TerminableInstantWinnableWebModel;
import com.i4one.base.web.controller.TerminableTriggerableWebModel;
import com.i4one.promotion.model.clickthru.ClickThru;
import com.i4one.promotion.model.clickthru.ClickThruRecord;
import java.util.Collection;

/**
 * @author Hamid Badiozamani
 */
public class WebModelClickThru extends ClickThru
{
	private final transient TerminableTriggerableWebModel<ClickThruRecord,ClickThru> triggerable;
	private final transient TerminableInstantWinnableWebModel<ClickThruRecord,ClickThru> instantWinning;

	public WebModelClickThru()
	{
		super();

		triggerable = new TerminableTriggerableWebModel<>(this);
		instantWinning = new TerminableInstantWinnableWebModel<>(this);
	}

	protected WebModelClickThru(ClickThruRecord delegate)
	{
		super(delegate);

		triggerable = new TerminableTriggerableWebModel<>(this);
		instantWinning = new TerminableInstantWinnableWebModel<>(this);
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal = triggerable.validateModel(retVal);
		retVal = instantWinning.validateModel(retVal);

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		triggerable.setModelOverrides();
		instantWinning.setModelOverrides();

		// We have to set the overrides here after the balance triggers have been
		// set to our filtered results
		//
		super.setOverrides();
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> newTriggers)
	{
		// The super class implementation is needed for both exclusive and
		// non-exclusive triggers to be set so that upon form submission
		// the most accurate information is displayed
		//
		super.setBalanceTriggers(newTriggers);

		triggerable.setBalanceTriggers(newTriggers);
	}

	@Override
	public void setInstantWins(Collection<InstantWin> newInstantWins)
	{
		super.setInstantWins(newInstantWins);

		instantWinning.setInstantWins(newInstantWins);
	}

	public TerminableTriggerableWebModel<ClickThruRecord, ClickThru> getTriggerable()
	{
		return triggerable;
	}

	public TerminableInstantWinnableWebModel<ClickThruRecord, ClickThru> getInstantWinning()
	{
		return instantWinning;
	}

}
