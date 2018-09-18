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
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseSubclassification<T extends Object> extends BaseLoggable implements Subclassification<T>
{
	private final Classification<T, ? extends Subclassification<T>> parent;
	private final String key;
	private final String uniqueId;

	public BaseSubclassification(Classification<T, ? extends Subclassification<T>> parent, String title)
	{
		this.key = title;
		this.parent = parent;

		this.uniqueId = parent.getKey() + "#" + getKey();
	}

	@Override
	public boolean isExclusiveTo(Subclassification<T> sub)
	{
		if ( sub == null )
		{
			return false;
		}
		else
		{
			// Typically, if we're part of the same classification
			// we're exclusive. In some cases, such as when two
			// subclassifications are combined as in AgeGender, this
			// method needs to be overridden to reflect the
			// inclusiveness of the subclassifications
			//
			return getParent().hasSubclassification(sub);
		}
	}

	@Override
	public int compareTo(Subclassification sub)
	{
		if ( sub == null )
		{
			// We're always greater than a null reference
			//
			return 1;
		}
		else
		{
			// We need to avoid name clashes, which is why
			// we use the toString() method that includes
			// the parent's title as well.
			//
			return getUniqueID().compareTo(sub.getUniqueID());
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
		else if ( o instanceof Subclassification )
		{
			Subclassification right = (Subclassification)o;

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
	public IString getTitle()
	{
		return IString.BLANK;
	}

	@Override
	public final String getKey()
	{
		return key;
	}

	@Override
	public Classification<T, ? extends Subclassification<T>> getParent()
	{
		return parent;
	}

	@Override
	public String toString()
	{
		return getUniqueID();
	}

	@Override
	public String getUniqueID()
	{
		return uniqueId;
	}
}
