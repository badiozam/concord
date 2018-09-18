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
package com.i4one.base.web.persistence;

import com.i4one.base.core.BaseLoggable;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstraction layer for persisting objects given a request/response object.
 * We can switch between setting cookies or storing as session variables here
 * in one place instead of all over the code
 *
 * @author Hamid Badiozamani
 */
public abstract class Persistence extends BaseLoggable
{
	private HttpServletRequest request;
	private HttpServletResponse response;

	/**
	 * Persists a value using a given key
	 * 
	 * @param key The key
	 * @param value The value
	 */
	public abstract void put(String key, Serializable value);

	/**
	 * Persists a value using a given key
	 * 
	 * @param key The key
	 * @param value The value
	 * @param expiresAfterSeconds How many seconds the value is to persist
	 */
	public abstract void put(String key, Serializable value, int expiresAfterSeconds);

	/**
	 * Persists a given value using the value's class name as its key
	 * 
	 * @param value The value to persist
	 */
	public void put(Serializable value)
	{
		put(value.getClass().getCanonicalName(), value);
	}

	/**
	 * Persists a given value using the value's class name as its key
	 * 
	 * @param value The value to persist
	 * @param expiresAfterSeconds How long the persistence is to last
	 */
	public void put(Serializable value, int expiresAfterSeconds)
	{
		put(value.getClass().getCanonicalName(), value, expiresAfterSeconds);
	}

	/**
	 * Get a persisted value using its key
	 * 
	 * @param key The key to use to retrieve the value
	 * 
	 * @return The persisted value or null if none were found by that name
	 */
	public abstract Serializable get(String key);

	/**
	 * Get a persisted value by its class type
	 * 
	 * @param type The exact type of the item to retrieve
	 * 
	 * @return The persisted value or null if not found
	 */
	public Serializable get(Class type)
	{
		return get(type.getCanonicalName());
	}

	/**
	 * Remove a given value using its key
	 * 
	 * @param key The key that the value was stored under
	 * 
	 * @return The old value or null if it didn't exist
	 */
	public Serializable remove(String key)
	{
		Serializable oldValue = get(key); 
		if ( oldValue != null )
		{
			put(key, null);
		}

		return oldValue;
	}

	/**
	 * Remove a given value using its type
	 * 
	 * @param type The type of the item that was stored
	 * 
	 * @return The old value or null if it didn't exist
	 */
	public Serializable remove(Class type)
	{
		return remove(type.getCanonicalName());
	}

	public static Persistence getPersistence(HttpServletRequest request, HttpServletResponse response)
	{
		// Using CookiePersistence for now, can switch it to Session if needed
		//
		Persistence retVal = new CookiePersistence();
		//Persistence retVal = new SessionPersistence();
		retVal.setRequest(request);
		retVal.setResponse(response);

		return retVal;
	}

	public static void putObject(HttpServletRequest request, HttpServletResponse response, String key, Serializable value, int expiresAfterSeconds)
	{
		Persistence persistence = getPersistence(request, response);

		persistence.put(key, value, expiresAfterSeconds);
	}
	
	public static void putObject(HttpServletRequest request, HttpServletResponse response, String key, Serializable value)
	{
		Persistence persistence = getPersistence(request, response);

		persistence.put(key, value);
	}

	public static Serializable getObject(HttpServletRequest request, String key)
	{
		Persistence persistence = getPersistence(request, null);

		return persistence.get(key);
	}

	public static Serializable getObject(HttpServletRequest request, Class type)
	{
		Persistence persistence = getPersistence(request, null);

		return persistence.get(type);
	}

	public HttpServletRequest getRequest()
	{
		return request;
	}

	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	public HttpServletResponse getResponse()
	{
		return response;
	}

	public void setResponse(HttpServletResponse response)
	{
		this.response = response;
	}

}
