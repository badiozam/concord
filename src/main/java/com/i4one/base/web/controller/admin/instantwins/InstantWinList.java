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
package com.i4one.base.web.controller.admin.instantwins;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.manager.GenericFeature;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class InstantWinList extends BaseLoggable
{
	private Integer featureId;
	private String featureName;
	private final Set<WebModelInstantWin> instantWins;

	public InstantWinList()
	{
		instantWins = new LinkedHashSet<>();

		featureName = "userManager.create";
		featureId = 0;
	}

	public Set<WebModelInstantWin> getInstantWins()
	{
		return instantWins;
	}

	public void setInstantWins(Collection<WebModelInstantWin> instantWins)
	{
		getLogger().debug("setInstantWins called with " + instantWins);

		this.instantWins.clear();
		this.instantWins.addAll(instantWins);
	}

	public void setAllInstantWins(Collection<InstantWin> newInstantWins)
	{
		instantWins.clear();

		// We convert to the wrapped version for the remove option to become
		// available. This has the effect of making the items in the list above
		// our own since the delegates are not duplicated
		//
		newInstantWins.stream().forEach( (currInstantWin) ->
		{
			getLogger().debug("Add instantWin " + currInstantWin);

			// Load the user as well since it might be needed for display
			//
			WebModelInstantWin instantWin = new WebModelInstantWin(currInstantWin);
			instantWin.getUser().loadedVersion();

			instantWins.add(instantWin);
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