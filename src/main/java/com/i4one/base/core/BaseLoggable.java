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

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class that provides access to a logger
 * 
 * @author Hamid Badiozamani
 */
public abstract class BaseLoggable
{
	private final transient Logger logger;

	public BaseLoggable()
	{
		logger = LoggerFactory.getLogger(getClass());
	}

	protected BaseLoggable(Class clazz)
	{
		logger = LoggerFactory.getLogger(clazz);
	}

	protected final Logger getLogger()
	{
		return logger;
	}

	/**
	 * Log a debug statement
	 * 
	 * @param statement A function that returns the debug string to log
	 */
	public void debug(Supplier<String> statement)
	{
		if ( logger.isDebugEnabled() )
		{
			logger.debug(statement.get());
		}
	}

	/**
	 * Log a debug statement
	 * 
	 * @param statement A function that returns the debug string to log
	 */
	public void trace(Supplier<String> statement)
	{
		if ( logger.isTraceEnabled() )
		{
			logger.trace(statement.get());
		}
	}

	/**
	 * Create a key using the input parameters. The given identifiers
	 * together uniquely a given object of its type.
	 * 
	 * @param ids The set of identifiers to use to create a key.
	 * 
	 * @return A string that appends all identifiers together
	 */
	public String makeKey(Object... ids)
	{
		StringBuilder retVal = new StringBuilder(toString());

		for (Object currId : ids)
		{
			retVal.append("-");
			appendKey(currId, retVal);
		}

		return retVal.toString();
	}

	/**
	 * Appends an object to the given key string being built. This method
	 * is called when a key is being built from a series of identifier
	 * objects via the makeKey(..) method. The default implementation simply
	 * converts the identifier object to a string and appends it to the
	 * input string builer.
	 * 
	 * @param id The identifier object.
	 * @param toAppend The string builder object that contains is to be
	 * 	appended to.
	 */
	protected void appendKey(Object id, StringBuilder toAppend)
	{
		toAppend.append(id);
	}
}
