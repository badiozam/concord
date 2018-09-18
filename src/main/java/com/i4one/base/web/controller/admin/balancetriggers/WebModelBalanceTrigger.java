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

import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class WebModelBalanceTrigger extends BalanceTrigger
{
	static final long serialVersionUID = 42L;

	private final transient List<RecordTypeDelegator<?>> featureList;

	public WebModelBalanceTrigger()
	{
		super();

		featureList = new ArrayList<>();

		// The policy is to create non-exclusive triggers here, and exclusive
		// triggers within the feature to which the trigger will be attached
		//
		getDelegate().setExclusive(false);
	}

	public WebModelBalanceTrigger(BalanceTrigger trigger)
	{
		super(trigger.getDelegate());

		featureList = new ArrayList<>();
	}

	public List<RecordTypeDelegator<?>> getFeatureList()
	{
		return Collections.unmodifiableList(featureList);
	}

	public void setFeatureList(Collection<RecordTypeDelegator<?>> featureList)
	{
		this.featureList.clear();
		this.featureList.addAll(featureList);
	}

	@Override
	public final void setExclusive(boolean exclusive)
	{
		// Can't alter the exclusive flag because this trigger will always be
		// non-exclusive
		//
	}
}
