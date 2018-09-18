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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.model.ActivityType;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import com.i4one.base.web.controller.user.account.JsonUser;
import com.i4one.promotion.web.controller.user.clickthrus.JsonUserBalanceTrigger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseActivityJsonType<T extends ActivityType> extends BaseJsonType
{
	private final ReturnType<T> item;

	public BaseActivityJsonType(Model model, ReturnType<T> item)
	{
		super(model);

		this.item = item;
	}

	public List<JsonUserBalanceTrigger> getProcessedTriggers()
	{
		List<ReturnType<UserBalanceTrigger>> processedTriggers = (List<ReturnType<UserBalanceTrigger>>) getItem().get("processedTriggers");
		if ( processedTriggers != null )
		{
			return processedTriggers
				.stream()
				.map( (processedTrigger) -> { return new JsonUserBalanceTrigger(getModel(), processedTrigger.getPost()); } )
				.collect(Collectors.toList());
		}
		else
		{
			return Collections.EMPTY_LIST;
		}
	}

	@JsonIgnore
	public ReturnType<T> getItem()
	{
		return item;
	}

	public JsonUser getUser()
	{
		return new JsonUser(getItem().getPost().getUser());
	}
}
