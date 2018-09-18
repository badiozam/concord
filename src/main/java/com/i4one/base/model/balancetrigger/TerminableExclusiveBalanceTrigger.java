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
package com.i4one.base.model.balancetrigger;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.terminable.TerminableClientType;

/**
 * A terminable balance trigger to be used for a specific terminable feature.
 * The trigger will always have the same start/end times as its terminable owner
 * 
 * @author Hamid Badiozamani
 * 
 * @param <T> The type of the feature that will be associated to this trigger
 */
public class TerminableExclusiveBalanceTrigger<T extends TerminableClientType<?>> extends ExclusiveBalanceTrigger<T>
{
	public TerminableExclusiveBalanceTrigger(T owner)
	{
		super(owner);

		setStartTimeSeconds(owner.getStartTimeSeconds());
		setEndTimeSeconds(owner.getEndTimeSeconds());
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( !isValidDuring(getOwner()) )
		{
			retVal.addError("startTimeSeconds", new ErrorMessage("msg.base.TerminableDefaultBalanceTrigger.timeMismatch", "This item is not available at the same times as its owner", new Object[]{ "item", this, "owner", getOwner()}));
		}

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		setStartTimeSeconds(getOwner().getStartTimeSeconds());
		setEndTimeSeconds(getOwner().getEndTimeSeconds());
	}
}
