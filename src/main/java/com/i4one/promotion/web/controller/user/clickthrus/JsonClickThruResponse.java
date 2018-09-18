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
package com.i4one.promotion.web.controller.user.clickthrus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.BalanceTriggerManager;
import com.i4one.base.model.balancetrigger.UserBalanceTriggerManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.BaseActivityJsonType;
import com.i4one.base.web.controller.Model;
import com.i4one.promotion.model.clickthru.ClickThruResponse;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class JsonClickThruResponse extends BaseActivityJsonType<ClickThruResponse>
{
	private BalanceTriggerManager balanceTriggerManager;
	private UserBalanceTriggerManager userBalanceTriggerManager;

	private int eligibleIn;

	public JsonClickThruResponse(Model model, ReturnType<ClickThruResponse> clickThruResponse)
	{
		super(model, clickThruResponse);

		eligibleIn = 0;
	}

	public void init()
	{
		if ( getBalanceTriggerManager() != null && getUserBalanceTriggerManager() != null )
		{
			User user = getItem().getPost().getUser(false);
			if ( user.exists() )
			{
				Set<BalanceTrigger> balanceTriggers = getBalanceTriggerManager().getAllTriggersByFeature(getItem().getPost().getClickThru(false), SimplePaginationFilter.NONE);

				eligibleIn = Integer.MAX_VALUE;
				for ( BalanceTrigger trigger : balanceTriggers )
				{
					// We want the earliest available time
					//
					int eligibleSeconds = getUserBalanceTriggerManager().eligibleInSeconds(trigger, user, getModel().getTimeInSeconds());
					eligibleIn = (eligibleIn < eligibleSeconds) ? eligibleIn : eligibleSeconds;
				}
			}
		}
	}

	public int getEligibleIn()
	{
		return eligibleIn;
	}

	public void setEligibleIn(int eligibleIn)
	{
		this.eligibleIn = eligibleIn;
	}

	public String getPrompt()
	{

		return getModel().buildMessage("msg.promotion.user.clickthrus.index.nexteligible", "eligible", getEligibleIn());
	}

	@JsonIgnore
	public UserBalanceTriggerManager getUserBalanceTriggerManager()
	{
		return userBalanceTriggerManager;
	}

	@JsonIgnore
	public void setUserBalanceTriggerManager(UserBalanceTriggerManager userBalanceTriggerManager)
	{
		this.userBalanceTriggerManager = userBalanceTriggerManager;
	}

	@JsonIgnore
	public BalanceTriggerManager getBalanceTriggerManager()
	{
		return balanceTriggerManager;
	}

	@JsonIgnore
	public void setBalanceTriggerManager(BalanceTriggerManager balanceTriggerManager)
	{
		this.balanceTriggerManager = balanceTriggerManager;
	}
}
