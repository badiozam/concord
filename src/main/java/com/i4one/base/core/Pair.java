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
package com.i4one.base.core;

import java.util.Map;


/**
 * This class encapsulates an associative pair
 *
 * @author Hamid Badiozamani
 * 
 * @param <T> The key type
 * @param <U> The value type
 */
public class Pair<T, U> implements Map.Entry<T, U>
{
	private T key;
	private U value;

	public Pair(T k, U v)
	{
		this.key = k;
		this.value = v;
	}

	@Override
	public T getKey()
	{
		return key;
	}

	public void setKey(T val)
	{
		this.key = val;
	}

	@Override
	public U getValue()
	{
		return value;
	}

	@Override
	public U setValue(U value)
	{
		U oldValue = this.value;
		this.value = value;

		return oldValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o)
	{
		if ( o == null )
		{
			return false;
		}
		else
		{
			if ( getClass().equals(o.getClass()))
			{
				Pair<T, U> right = ( Pair<T, U> )o;

				return key.equals(right.getKey()) && value.equals(right.getValue());
			}
			else
			{
				return false;
			}
		}
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 47 * hash + ( this.key != null ? this.key.hashCode() : 0 );
		hash = 47 * hash + ( this.value != null ? this.value.hashCode() : 0 );
		return hash;
	}
}

