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
package com.i4one.base.model.report.classifier.display;

import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.report.classifier.Subclassification;
import java.util.Set;

/**
 * A single data point in the form of key =&gt; value. The key is a set of
 * subclassifications that uniquely identifies what the value represents. For
 * example, a key could be "gender#m" or "age#18-24|gender#m".
 * 
 * @author Hamid Badiozamani
 * @param <SUB> The type of subclassifications that the key is made of.
 */
public class DataPoint<T extends Object, SUB extends Subclassification<T>> implements Comparable
{
	private final Set<? extends Subclassification<T>> key;
	private final SUB endPoint;
	private final IString title;
	private final int value;

	public DataPoint(SUB endPoint, Set<? extends Subclassification<T>> key, Integer value)
	{
		this.key = key;
		this.value = value == null ? 0 : value;
		this.endPoint = endPoint;
		this.title = IString.BLANK;
	}

	public DataPoint(IString title, SUB endPoint, Set<? extends Subclassification<T>> key, Integer value)
	{
		this.key = key;
		this.value = value == null ? 0 : value;
		this.endPoint = endPoint;
		this.title = title;
	}

	public Set<? extends Subclassification<T>> getKey()
	{
		return key;
	}

	public int getValue()
	{
		return value;
	}

	public IString getTitle()
	{
		return title;
	}

	public String getTitleKey()
	{
		return endPoint.getUniqueID();
	}

	public SUB getEndPoint()
	{
		return endPoint;
	}

	@Override
	public String toString()
	{
		return getKey() + "=" + getValue();
	}

	@Override
	public int compareTo(Object o)
	{
		DataPoint right = (DataPoint)o;
		if ( getValue() == right.getValue() )
		{
			return getTitleKey().compareTo(right.getTitleKey());
		}
		else
		{
			// Descending order for sorting
			//
			return (getValue() < right.getValue() ? 1 : -1);
		}
	}
}
