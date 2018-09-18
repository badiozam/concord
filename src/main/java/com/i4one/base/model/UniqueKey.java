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

import com.i4one.base.core.BaseLoggable;
import java.util.Objects;

/**
 * This class generates a unique key for a RecordTypeDelegator for comparison
 * purposes.
 * 
 * @author Hamid Badiozamani
 */
public class UniqueKey extends BaseLoggable implements Comparable
{
	private final RecordTypeDelegator delegator;

	public UniqueKey(RecordTypeDelegator item)
	{
		this.delegator = item;
	}

	@Override
	public int compareTo(Object o)
	{
		if ( o instanceof UniqueKey )
		{
			UniqueKey rightKey = (UniqueKey)o;
			return compareTo(rightKey);
		}
		else
		{
			throw new ClassCastException("Can't compare " + o.getClass() + " with " + getClass());
		}
	}

	public int compareTo(UniqueKey rightKey)
	{
		if ( delegator.getClass().isAssignableFrom(rightKey.getDelegator().getClass()) )
		{
			RecordTypeDelegator rightItem = rightKey.getDelegator();

			// This is a double-check on table names to ensure that the two items are equals
			// should not be necessary
			//
			//if ( delegator.getFeatureName().equals(rightKey.getDelegator().getFeatureName()))
			if ( delegator.exists() && rightItem.exists() )
			{
				int comparison = delegator.getSer().compareTo(rightItem.getSer());
				if ( getLogger().isTraceEnabled() )
				{
					getLogger().trace("{} compared to {} returning via serial numbers {}", delegator, rightItem, comparison);
				}

				return comparison;
			}
			else
			{
				int comparison = delegator.uniqueKey().compareTo(rightItem.uniqueKey());
				if ( getLogger().isTraceEnabled())
				{
					getLogger().trace("{} compared to {} returning via unique keys ", delegator.uniqueKey(), rightItem.uniqueKey(), comparison);
				}

				return comparison;
			}
		}
		else
		{
			throw new ClassCastException("Can't compare " + rightKey.getDelegator().getClass() + " with " + delegator.getClass());
		}
	}

	@Override
	public String toString()
	{
		if ( delegator.exists() )
		{
			return delegator.getFeatureName() + ":" + delegator.getSer();
		}
		else
		{
			return delegator.getFeatureName() + "-" + delegator.uniqueKey();
		}
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 97 * hash + Objects.hashCode(this.delegator);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final UniqueKey other = (UniqueKey) obj;
		return equals(other);
	}

	public boolean equals(UniqueKey rightKey)
	{
		return compareTo(rightKey) == 0;
	}

	public RecordTypeDelegator<?> getDelegator()
	{
		return delegator;
	}
}
