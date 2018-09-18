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
package com.i4one.base.model.manager.targetable;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ActivityType;
import com.i4one.base.model.targeting.BaseTarget;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseActivityTarget<U extends ActivityType> extends BaseTarget implements ActivityTarget<U>
{
	protected int itemid;
	protected U empty;

	public BaseActivityTarget(String key)
	{
		super(key);
		
		empty = emptyInstance();
		parse(key);
	}

	public BaseActivityTarget(int itemid)
	{
		super(String.valueOf(itemid));

		empty = emptyInstance();
		this.itemid = itemid;
	}

	@Override
	public boolean isDefault()
	{
		return itemid == 0;
	}

	protected abstract U emptyInstance();

	@Override
	public String getKey()
	{
		return getName() + ":" + itemid;
	}

	@Override
	public String getName()
	{
		return empty.getActionItem().getFeatureName();
	}

	private void parse(String target)
	{
		String[] targetSplit = target.split(":");
		if (targetSplit.length != 2)
		{
			throw new IllegalArgumentException("Expecting format " + getName() + ":<itemid>, but got '" + target + "'");
		}
		else
		{
			// First item is the prefix
			// Second item is the id
			//
			itemid = Utils.defaultIfNaN(targetSplit[1], 0);
		}
	}

	@Override
	public int getItemid()
	{
		return itemid;
	}

	public void setItemid(int itemid)
	{
		this.itemid = itemid;
	}
}
