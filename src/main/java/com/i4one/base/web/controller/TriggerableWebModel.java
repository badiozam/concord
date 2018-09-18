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
package com.i4one.base.web.controller;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.triggerable.TriggerableClientType;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.AutoPopulatingList;
import org.springframework.util.AutoPopulatingList.ElementFactory;

/**
 * Provides implementation for a form backing object's triggers. The getExclusiveBalanceTriggers()
 * method should be called whenever the form backing object that contains this object's
 * getExclusiveBalanceTriggers() method is called. Provides an auto-populating list of exclusive
 * balance triggers for the form backing object so they can be edited easily.
 *  
 * Furthermore, the validateModel(..) method should be called within the form backing
 * object's validate(..) method. And the setModelOverrides() method should be called
 * within the form backing object's setOverrides() method.
 * 
 * @author Hamid Badiozamani
 * 
 * @param <U> The form backing object's database record type
 * @param <T> The form backing object's model type
 */
public class TriggerableWebModel<U extends ClientRecordType, T extends TriggerableClientType<U,T>> extends BaseLoggable implements ElementFactory<ExclusiveBalanceTrigger<T>>
{
	private transient List<ExclusiveBalanceTrigger<T>> triggers;
	private final transient T model;

	public TriggerableWebModel(T model)
	{
		this.model = model;

		triggers = new AutoPopulatingList<>(this);
	}

	public Errors validateModel(Errors errors)
	{
		Errors retVal = errors;

		// Note: The name of this field in the container class has to be "triggerable"
		// in order for error messages to work properly
		//
		retVal.replaceFieldNames("exclusiveBalanceTriggers", "triggerable.exclusiveBalanceTriggers");
		return retVal;
	}

	public void setModelOverrides()
	{
		// Filter out any non-existent and 0 amount triggers
		//
		List<BalanceTrigger> filteredTriggers = getFilteredTriggers();

		// Set the overrides for each of the triggers
		//
		filteredTriggers.forEach( (trigger) -> { trigger.setOverrides(); });

		// After the overrides have been set, we reinitialize the model with
		// these since the references are unknown to the non-web model instance
		//
		model.setBalanceTriggers(filteredTriggers);
	}

	private List<BalanceTrigger> getFilteredTriggers()
	{
		return getExclusiveBalanceTriggers().stream()
			.filter( (trigger) -> { return trigger != null; } )
			.filter( (trigger) -> { return trigger.exists() || trigger.getAmount() != 0; } )
			.collect(Collectors.toList());
	}

	public List<ExclusiveBalanceTrigger<T>> getTriggers()
	{
		return getExclusiveBalanceTriggers();
	}

	public List<ExclusiveBalanceTrigger<T>> getExclusiveBalanceTriggers()
	{
		if ( triggers == null || triggers.isEmpty() )
		{
			getLogger().debug("getTriggers() initializing triggers from " + Utils.toCSV(getModel().getExclusiveBalanceTriggers()));

			Collection<ExclusiveBalanceTrigger<T>> exclusiveTriggers = getModel().getExclusiveBalanceTriggers();
			List<ExclusiveBalanceTrigger<T>> backingList = new ArrayList<>(exclusiveTriggers);

			triggers = new AutoPopulatingList<>(backingList, this);
		}

		return triggers;
	}

	public void setBalanceTriggers(Collection<BalanceTrigger> balanceTriggers)
	{
		// We can't trust the incoming balanceTriggers because it may have been set by us
		// in the setModelOverrides() method. So by clearing the triggers here, we force
		// a reload from the model when the getExclusiveBalanceTriggers() method is called next.
		//
		triggers.clear();
	}

	@Override
	public ExclusiveBalanceTrigger<T> createElement(int i) throws AutoPopulatingList.ElementInstantiationException
	{
		return new ExclusiveBalanceTrigger<>(getModel());
	}

	public T getModel()
	{
		return model;
	}
}
