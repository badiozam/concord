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

/**
 * This class keeps track of an object's age expiring it once the
 * object has expired
 *
 * @author Hamid Badiozamani
 * 
 * @param <T> The type of object to store
 */
public class AgingObject<T>
{
	private T object;
	private long maxAge;
	private long birthTime;

	/**
	 * @param object The object to store
	 * @param maxAge The maximum age the object can be, or 0 for indefinite
	 */
	public AgingObject(T object, long maxAge)
	{
		this.object = object;
		this.maxAge = maxAge;
		this.birthTime = System.currentTimeMillis();
	}

	/**
	 * Gets the object depending on whether it has expired or not
	 *
	 * @return The object if it's still fresh, null otherwise
	 */
	public T getObject()
	{
		return getObject(System.currentTimeMillis());
	}

	/**
	 * Gets the object depending on whether it has expired or not
	 *
	 * @param asOf The time to use when determining expiration
	 *
	 * @return The object if it's still fresh, null otherwise
	 */
	public T getObject(long asOf)
	{
		// We only check the age if we've been given an age
		//
		if ( maxAge > 0 && getBirthTime() + maxAge < asOf )
		{
			return null;
		}

		return object;
	}

	public void setObject(T object)
	{
		this.object = object;
		this.birthTime = System.currentTimeMillis();
	}

	public long getMaxAge()
	{
		return maxAge;
	}

	public void setMaxAge(long maxAge)
	{
		this.maxAge = maxAge;
	}

	public long getBirthTime()
	{
		return birthTime;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj)
	{
		if ( obj == null )
		{
			return false;
		}

		if ( getClass() != obj.getClass())
		{
			return false;
		}

		final AgingObject<T> other = ( AgingObject<T> )obj;
		if ( this.object != other.object && ( this.object == null || !this.object.equals(other.object)))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 31 * hash + ( this.object != null ? this.object.hashCode() : 0 );
		return hash;
	}
}
