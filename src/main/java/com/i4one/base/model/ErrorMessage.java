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

import com.i4one.base.core.Base;
import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.message.Message;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a single error contained in the Errors list
 *
 * @author Hamid Badiozamani
 */
public class ErrorMessage
{
	private Exception exception;
	private String fieldName;
	private String messageKey;
	private String defaultMessage;
	private ArrayList<Object> params;

	public ErrorMessage(String messageKey, String defaultMessage, Object[] params)
	{
		init("", messageKey, defaultMessage, params, null);
	}

	public ErrorMessage(String messageKey, String defaultMessage, Object[] params, Exception exception)
	{
		init("", messageKey, defaultMessage, params, exception);
	}

	public ErrorMessage(String fieldName, String messageKey, String defaultMessage, Object[] params)
	{
		init(fieldName, messageKey, defaultMessage, params, null);
	}

	public ErrorMessage(String fieldName, String messageKey, String defaultMessage, Object[] params, Exception exception)
	{
		init(fieldName, messageKey, defaultMessage, params, exception);
	}

	private void init(String fieldName, String messageKey, String defaultMessage, Object[] params, Exception exception)
	{
		this.fieldName = fieldName;
		this.messageKey = Utils.forceEmptyStr(messageKey);
		this.defaultMessage = Utils.forceEmptyStr(defaultMessage);
		this.params = new ArrayList<>(params.length);
		this.params.addAll(Arrays.asList(params));
		this.exception = exception;
	}

	public String getDefaultMessage()
	{
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage)
	{
		this.defaultMessage = defaultMessage;
	}

	public String getMessageKey()
	{
		return messageKey;
	}

	public void setMessageKey(String messageKey)
	{
		this.messageKey = messageKey;
	}

	public Object[] getParams()
	{
		return params.toArray();
	}

	public void setMessageArgument(String key, Object value)
	{
		this.params.add(key);
		this.params.add(value);
	}

	public void addParam(Object param)
	{
		this.params.add(param);
	}

	public void setParams(List<Object> params)
	{
		this.params.clear();
		this.params.addAll(params);
	}

	public void setParams(Object[] params)
	{
		this.params.clear();
		this.params.addAll(Arrays.asList(params));
	}

	public Exception getException()
	{
		return exception;
	}

	public void setException(Exception exception)
	{
		this.exception = exception;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}

	@Override
	public String toString()
	{
		Message message = new Message();
		message.setValue(getDefaultMessage());

		StringBuilder retVal = new StringBuilder(Base.getInstance().getMessageManager().buildMessage(message, params.toArray()));

		// Include the exception's stack trace as we're likely being called internally
		//
		Throwable currThrowable = getException();

		for (int level = 0; currThrowable != null; level++ )
		{
			retVal.append("\n");
			retVal.append(Utils.repeatStr("\t", level));
			retVal.append(currThrowable.toString());

			for ( StackTraceElement stackElement : currThrowable.getStackTrace() )
			{
				retVal.append(Utils.repeatStr("\t", level + 1));
				retVal.append(stackElement);
				retVal.append("\n");
			}

			currThrowable = currThrowable.getCause();
		}

		return retVal.toString();
	}

	public String toString(SingleClient client, String language)
	{
		return Base.getInstance().getMessageManager().buildMessage(client, getMessageKey(), language, params.toArray());
	}

}
