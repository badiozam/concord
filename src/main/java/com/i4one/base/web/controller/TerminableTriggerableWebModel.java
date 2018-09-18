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

import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.balancetrigger.TerminableExclusiveBalanceTrigger;
import org.springframework.util.AutoPopulatingList;
import org.springframework.util.AutoPopulatingList.ElementFactory;

/**
 * Provides functionality for Terminable triggerables.
 * 
 * @author Hamid Badiozamani
 * 
 * @param <U> The form backing object's database record type
 * @param <T> The form backing object's model type
 */
public class TerminableTriggerableWebModel<U extends TerminableClientRecordType, T extends TerminableTriggerableClientType<U,T>> extends TriggerableWebModel<U,T> implements ElementFactory<ExclusiveBalanceTrigger<T>>
{
	public TerminableTriggerableWebModel(T model)
	{
		super(model);
	}

	@Override
	public ExclusiveBalanceTrigger<T> createElement(int i) throws AutoPopulatingList.ElementInstantiationException
	{
		return new TerminableExclusiveBalanceTrigger<>(getModel());
	}
}
