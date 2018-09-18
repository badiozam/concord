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
package com.i4one.rewards.web.controller.admin.prizes;

import com.i4one.base.model.manager.GenericFeature;
import com.i4one.rewards.model.prize.PrizeFulfillment;
import com.i4one.rewards.model.prize.PrizeWinning;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class WebModelWinningsPrize extends WebModelPrize
{
	private final Set<PrizeWinning> winnings;
	private final PrizeFulfillment fulfillmentTemplate;

	// The feature that directed the user to the winnings pages. This can be
	// a non-existent entry which would make the back button return the user
	// to the prize listing page. Otherwise, the user is to be redirected to
	// this feature's update page
	//
	private GenericFeature feature;

	public WebModelWinningsPrize()
	{
		super();

		winnings = new LinkedHashSet<>();
		feature = new GenericFeature(0, "rewards.prizes");

		fulfillmentTemplate = new PrizeFulfillment();
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();
	}

	public GenericFeature getFeature()
	{
		return feature;
	}

	public void setFeature(GenericFeature feature)
	{
		this.feature = feature;
	}

	public Set<PrizeWinning> getWinnings()
	{
		return Collections.unmodifiableSet(winnings);
	}

	public void setWinnings(Collection<PrizeWinning> winnings)
	{
		this.winnings.clear();
		this.winnings.addAll(winnings);
	}

	public PrizeFulfillment getFulfillmentTemplate()
	{
		return fulfillmentTemplate;
	}
}
