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
package com.i4one.base.model;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.RecordType;
import com.i4one.base.dao.activity.ActivityRecordType;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 * @param <T> The usage record type
 * @param <AR> The action item (i.e. owner) record type
 * @param <A> The action item (i.e. owner) type
 */
public abstract class BaseActivityType<T extends ActivityRecordType, AR extends RecordType, A extends RecordTypeDelegator<AR>> extends BaseUserType<T> implements ActivityType<T, A>
{
	private transient ActivityTypeForeignKey<AR, A, ActivityType<T, A>> actionItem; 

	protected BaseActivityType(T delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		actionItem = new ActivityTypeForeignKey<>(this, this::newActionItem);
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		actionItem.actualize();
	}

	protected abstract A newActionItem();

	@Override
	public A getActionItem()
	{
		return actionItem.get(true);
	}

	public A getActionItem(boolean doLoad)
	{
		return actionItem.get(doLoad);
	}

	public void setActionItem(A actionItem)
	{
		this.actionItem.set(actionItem);
	}
	@Override
	public Date getTimeStamp()
	{
		return Utils.toDate(getTimeStampSeconds());
	}

	@Override
	public int getTimeStampSeconds()
	{
		return getDelegate().getTimestamp();
	}

	@Override
	public void setTimeStampSeconds(int timeStamp)
	{
		getDelegate().setTimestamp(timeStamp);
	}

	@Override
	public int getQuantity()
	{
		return 1;
	}

	@Override
	public void setQuantity(int quantity)
	{
		throw new UnsupportedOperationException("Can't set quantity on a non-quantifiable activity, please use QuantifiedActivityType instead");
	}
}
