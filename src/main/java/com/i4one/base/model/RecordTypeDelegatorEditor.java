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

import com.i4one.base.core.Base64;
import static com.i4one.base.core.Base64.decodeToObject;
import static com.i4one.base.core.Base64.encodeObject;
import static com.i4one.base.core.Utils.defaultIfNaN;
import com.i4one.base.dao.BaseRecordType;
import java.beans.PropertyEditorSupport;
import static java.lang.String.valueOf;
import org.apache.commons.logging.Log;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Base class for serializing and deserializing records, classes that require editors
 * should simply declare a ClassEditor<..> that extends this class in the same package
 *
 * @author Hamid Badiozamani
 * `
 * @param <U> The underlying record type
 * @param <T> The model type
 */
public abstract class RecordTypeDelegatorEditor<U extends BaseRecordType, T extends BaseRecordTypeDelegator<U>> extends PropertyEditorSupport
{
	private final Log logger;

	private final Class<U> recordType;
	private final Class<T> recordTypeDelegator;

	public RecordTypeDelegatorEditor(Class<U> recordType, Class<T> recordTypeDelegator)
	{
		this.recordType = recordType;
		this.recordTypeDelegator = recordTypeDelegator;

		logger = getLog(getClass());
	}

	@Override
	public void setAsText(String text)
	{
		try
		{
			//U delegate = getFromTextBase64(text);
			U delegate = getFromTextSer(text);

			T value = recordTypeDelegator.newInstance();
			value.setOwnedDelegate(delegate);

			setValue(value);
		}

		catch (InstantiationException | IllegalAccessException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public U getFromTextSer(String text) throws InstantiationException, IllegalAccessException
	{
		U delegate = recordType.newInstance();
		delegate.setSer(defaultIfNaN(text, 0));

		return delegate;
	}

	public U getFromTextBase64(String text) throws InstantiationException, IllegalAccessException
	{
		// Attempt to deserialize the object
		//
		@SuppressWarnings("unchecked")
		U delegate = (U) decodeToObject(text);

		return delegate;
	}

	@Override
	public String getAsText()
	{
		//String retVal = getAsTextBase64();
		String retVal = getAsTextSer();

		getLogger().trace("Returning " + retVal);

		return retVal;
	}

	@SuppressWarnings("unchecked")
	public String getAsTextSer()
	{
		return valueOf(((T)getValue()).getSer());
	}

	@SuppressWarnings("unchecked")
	public String getAsTextBase64()
	{
		return encodeObject(((T)getValue()).getDelegate(), Base64.DONT_BREAK_LINES);
	}

	public Log getLogger()
	{
		return logger;
	}
}
