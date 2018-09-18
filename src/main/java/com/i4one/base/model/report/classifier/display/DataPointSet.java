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

import com.i4one.base.model.report.classifier.Subclassification;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class DataPointSet<T extends Object, SUB extends Subclassification<T>> implements Set<DataPoint<T, SUB>>
{
	private final Set<DataPoint<T, SUB>> setImpl;
	private int total;

	public DataPointSet(Set<DataPoint<T, SUB>> setImpl)
	{
		this.setImpl = setImpl;
		total = 0;
	}

	@Override
	public int size()
	{
		return setImpl.size();
	}

	@Override
	public boolean isEmpty()
	{
		return setImpl.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return setImpl.contains(o);
	}

	@Override
	public Iterator<DataPoint<T, SUB>> iterator()
	{
		return setImpl.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return setImpl.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return setImpl.toArray(a);
	}

	@Override
	public boolean add(DataPoint<T, SUB> e)
	{
		if ( setImpl.add(e) )
		{
			total += e.getValue();
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean remove(Object o)
	{
		if ( setImpl.remove(o) )
		{
			DataPoint dp = (DataPoint)o;
			total -= dp.getValue();

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return setImpl.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends DataPoint<T, SUB>> c)
	{
		if ( setImpl.addAll(c) )
		{
			recomputeTotal();
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		if ( setImpl.retainAll(c) )
		{
			recomputeTotal();
			return true;
		}
		else
		{
			return false;
		}
	}

	private void recomputeTotal()
	{
		// In certain cases we have no choice but to iterate over all elements
		// again to make sure the total is consistent.
		//
		total = 0;
		for ( DataPoint<T, SUB> d : setImpl )
		{
			total += d.getValue();
		}
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		total = 0;
		return setImpl.removeAll(c);
	}

	@Override
	public void clear()
	{
		total = 0;
		setImpl.clear();
	}

	public int getTotal()
	{
		return total;
	}
}
