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
package com.i4one.base.model.report.classifier;

import com.i4one.base.core.BaseLoggable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseClassification<T extends Object, SUB extends Subclassification<T>> extends BaseLoggable implements Classification<T, SUB>
{
	private final Set<SUB> subclasses;

	public BaseClassification()
	{
		subclasses = new LinkedHashSet<>();
	}

	@Override
	public int compareTo(Classification o)
	{
		if ( o == null )
		{
			// We're always greater than a null reference
			//
			return 1;
		}
		else
		{
			return getKey().compareTo(o.getKey());
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if ( o == null )
		{
			return false;
		}
		else if ( o == this )
		{
			return true;
		}
		else if ( o instanceof Classification )
		{
			Classification right = (Classification)o;

			return compareTo(right) == 0;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return getKey().hashCode();
	}

	@Override
	public Set<SUB> getSubclassifications()
	{
		return subclasses;
	}

	@Override
	public void setSubclassifications(Collection<SUB> subclasses)
	{
		subclasses.clear();
		subclasses.addAll(subclasses);
	}

	@Override
	public boolean add(SUB subclass)
	{
		return subclasses.add(subclass);
	}

	@Override
	public boolean remove(SUB subclass)
	{
		return subclasses.remove(subclass);
	}

	protected Set<SUB> getSubclasses()
	{
		return Collections.unmodifiableSet(subclasses);
	}

	@Override
	public boolean hasSubclassification(Subclassification<T> subclass)
	{
		if ( subclasses.isEmpty() )
		{
			return false;
		}
		else
		{
			SUB subItem = subclasses.iterator().next();
			if ( subItem.getClass().isInstance(subclass))
			{
				subItem = (SUB)subclass;
				return subclasses.contains(subItem);
			}
			else
			{
				return false;
			}
		}
	}

	@Override
	public boolean isSameClassification(Classification<T, SUB> classification)
	{
		return classification != null && classification.getKey().equals(getKey());
	}

	@Override
	public String toString()
	{
		return getKey();
	}
}
