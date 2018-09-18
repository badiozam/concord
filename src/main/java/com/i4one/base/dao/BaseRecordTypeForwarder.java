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
package com.i4one.base.dao;

import com.i4one.base.core.BaseLoggable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseRecordTypeForwarder<T extends RecordType> extends BaseLoggable implements RecordType
{
	private final transient T forward;

	public BaseRecordTypeForwarder(T forward)
	{
		this.forward = forward;
	}

	@Override
	public RecordType clone() throws CloneNotSupportedException
	{
		return getForward().clone();
	}

	@Override
	public boolean exists()
	{
		return getForward().exists();
	}

	@Override
	public void fromJSONString(String json) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		getForward().fromJSONString(json);
	}

	@Override
	public boolean getExists()
	{
		return getForward().getExists();
	}

	@Override
	public String getFullTableName()
	{
		return getForward().getFullTableName();
	}

	@Override
	public void setTableName(String tableName)
	{
		getForward().setTableName(tableName);
	}

	@Override
	public String getSchemaName()
	{
		return getForward().getSchemaName();
	}

	@Override
	public void setSchemaName(String schemaName)
	{
		getForward().setSchemaName(schemaName);
	}

	@Override
	public int getSer()
	{
		return getForward().getSer();
	}

	@Override
	public String getTableName()
	{
		return getForward().getTableName();
	}

	@Override
	public boolean isLoaded()
	{
		return getForward().isLoaded();
	}

	@Override
	public void setLoaded(boolean isLoaded)
	{
		getForward().setLoaded(isLoaded);
	}

	@Override
	public String makeKey(int ser)
	{
		return getForward().makeKey(ser);
	}

	@Override
	public String makeKey()
	{
		return getForward().makeKey();
	}

	@Override
	public void setSer(int val)
	{
		getForward().setSer(val);
	}

	@Override
	public String toJSONString()
	{
		return getForward().toJSONString();
	}

	@Override
	public String toString()
	{
		return getForward().toString();
	}

	public final T getForward()
	{
		return forward;
	}
}