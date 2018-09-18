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
package com.i4one.base.web.controller.admin.balancetriggers;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.manager.GenericFeature;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class TriggerList extends BaseLoggable
{
	private Integer featureId;
	private String featureName;
	private final Set<WebModelBalanceTrigger> triggers;

	public TriggerList()
	{
		triggers = new LinkedHashSet<>();

		featureName = "userManager.create";
		featureId = 0;
	}

	public Set<WebModelBalanceTrigger> getTriggers()
	{
		return triggers;
	}

	public void setTriggers(Collection<WebModelBalanceTrigger> triggers)
	{
		getLogger().debug("setTriggers called with " + triggers);

		this.triggers.clear();
		this.triggers.addAll(triggers);
	}

	public void setAllTriggers(Collection<BalanceTrigger> newTriggers)
	{
		triggers.clear();

		// We convert to the wrapped version for the remove option to become
		// available. This has the effect of making the items in the list above
		// our own since the delegates are not duplicated
		//
		newTriggers.stream().forEach( (currBalanceTrigger) ->
		{
			getLogger().debug("Add trigger " + currBalanceTrigger);

			// Load the balance as well since it might be needed for display
			//
			WebModelBalanceTrigger trigger = new WebModelBalanceTrigger(currBalanceTrigger);
			trigger.getBalance().loadedVersion();

			triggers.add(trigger);
		});
	}

	public String getFeatureName()
	{
		return featureName;
	}

	public void setFeatureName(String featureName)
	{
		this.featureName = featureName;
	}

	public Integer getFeatureId()
	{
		return featureId;
	}

	public void setFeatureId(Integer featureId)
	{
		this.featureId = featureId;
	}

	public GenericFeature getFeature()
	{
		return new GenericFeature(getFeatureId(), getFeatureName());
	}
}