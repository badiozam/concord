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
package com.i4one.predict.web.controller.admin.events;

import com.i4one.base.model.Errors;
import com.i4one.base.model.balanceexpense.BalanceExpense;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.web.controller.ExpendableWebModel;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventRecord;
import java.util.Collection;
import java.util.Map;

/**
 * @author Hamid Badiozamani
 */
public class WebModelEvent extends Event
{
	private final transient ExpendableWebModel<EventRecord,Event> expendable;

	public WebModelEvent()
	{
		super();

		expendable = new ExpendableWebModel<>(this);
	}

	protected WebModelEvent(EventRecord delegate)
	{
		super(delegate);

		expendable = new ExpendableWebModel<>(this);
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal = expendable.validateModel(retVal);

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		expendable.setModelOverrides();

		// We have to set the overrides here after the balance expenses have been
		// set to our filtered results
		//
		super.setOverrides();

		IString newReference = getReference();
		for (Map.Entry<String, String> entry : newReference.entrySet() )
		{
			if (entry.getValue().startsWith("http"))
			{
				newReference.replace(entry.getKey(), entry.getValue().replaceFirst("http[s]*:\\/\\/", ""));
			}
			else
			{
				newReference.replace(entry.getKey(), entry.getValue());
			}
		}

		setReference(newReference);
	}

	@Override
	public void setBalanceExpenses(Collection<BalanceExpense> balanceExpenses)
	{
		super.setBalanceExpenses(balanceExpenses);

		expendable.setExpenses(balanceExpenses);
	}

	public ExpendableWebModel<EventRecord, Event> getExpendable()
	{
		return expendable;
	}
}
