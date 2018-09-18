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
import com.i4one.base.dao.SortableRecordType;
import com.i4one.base.model.i18n.IString;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class SimpleSortableType<U extends SortableRecordType, T extends SortableType<U>> extends BaseRecordTypeDelegatorForwarder<U, T> implements SortableType<U>
{
	public SimpleSortableType(T forward)
	{
		super(forward);
	}

	@Override
	public IString getTitle()
	{
		return getForward().getTitle();
	}

	@Override
	public void setTitle(IString title)
	{
		getForward().setTitle(title);
	}

	@Override
	public int getOrderWeight()
	{
		return getDelegate().getOrderweight();
	}

	@Override
	public void setOrderWeight(int orderWeight)
	{
		getDelegate().setOrderweight(orderWeight);
	}

	@Override
	public boolean fromCSVList(List<String> csv)
	{
		String orderWeight = csv.get(0);
		setOrderWeight(Utils.defaultIfNaN(orderWeight, 0));

		return true;
	}

	@Override
	public String toCSV(boolean header)
	{
		StringBuilder retVal = new StringBuilder();

		if ( header )
		{
			// XXX: Needs i18n
			retVal.append(Utils.csvEscape("Order Weight")).append(",");
		}
		else
		{
			retVal.append(getOrderWeight()).append(",");
		}

		return retVal.toString();
	}
}
