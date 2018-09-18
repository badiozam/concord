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
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import com.i4one.base.web.controller.BaseJsonType;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus.ModelStatus;

/**
 * @author Hamid Badiozamani
 */
public class JsonUserBalanceTrigger extends BaseJsonType
{
	private final UserBalanceTrigger userBalanceTrigger;

	public JsonUserBalanceTrigger(Model model, UserBalanceTrigger userBalanceTrigger)
	{
		super(model);

		this.userBalanceTrigger = userBalanceTrigger;
	}

	@JsonIgnore
	@Override
	public String getMessage()
	{
		return super.getMessage();
	}

	@JsonIgnore
	@Override
	public ModelStatus getStatus()
	{
		return super.getStatus();
	}

	@JsonIgnore
	public Balance getBalance()
	{
		return userBalanceTrigger.getBalanceTrigger().getBalance();
	}

	public int getAmount()
	{
		return userBalanceTrigger.getBalanceTrigger().getAmount();
	}

	public int getTimeStampSeconds()
	{
		return userBalanceTrigger.getTimeStampSeconds();
	}

	public String getValue()
	{
		return getAmount() + " " + getBalance().formatName(getAmount()).get(getModel().getLanguage());
	}
}
