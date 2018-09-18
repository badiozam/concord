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
import com.i4one.base.dao.RecordType;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * This class forwards all interface methods to a RecordTypeDelegator of the
 * same type.
 * 
 * @author Hamid Badiozamani
 * @param <U> The database record type
 * @param <T> The model class
 */
public abstract class BaseRecordTypeDelegatorForwarder<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseLoggable implements RecordTypeDelegator<U>
{
	/** The type that has supplies the methods to forward to. */
	protected final T forward;

	public BaseRecordTypeDelegatorForwarder(T forward)
	{
		this.forward = forward;
	}

	public final void init()
	{
		initInternal();
	}

	protected void initInternal()
	{
	}

	@Override
	public final Errors validate()
	{
		// We can't forward the request because it might cause
		// a circular loop and any validate methods that use
		// this class might skip their super-class' validate()
		// method. So we force the subclass to provide the
		// implementation and rely on the container to call
		// validate on this object
		//
		return validateInternal();
	}

	@Override
	public final boolean isValid()
	{
		return forward.isValid();
	}

	/**
	 * Validate the object
	 * 
	 * @return The set of errors for validate
	 */
	protected Errors validateInternal()
	{
		return new Errors();
	}

	@Override
	public final void setOverrides()
	{
		// See notes on validate()
		setOverridesInternal();
	}

	protected void setOverridesInternal()
	{
	}

	@Override
	public final void actualizeRelations()
	{
		// See notes on validate()
		actualizeRelationsInternal();
	}

	protected void actualizeRelationsInternal()
	{
	}

	@Override
	public String uniqueKey()
	{
		// See notes on validate
		return uniqueKeyInternal();
	}

	protected String uniqueKeyInternal()
	{
		throw new UnsupportedOperationException("Uniqueness is not a factor here.");
	}

	@Override
	public int compareTo(RecordTypeDelegator<U> o)
	{
		return forward.compareTo(o);
	}

	@Override
	public boolean equals(RecordTypeDelegator<U> o)
	{
		return forward.equals(o);
	}

	@Override
	public void copyFrom(RecordTypeDelegator<U> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		forward.copyFrom(right);
	}

	@Override
	public void copyOver(RecordTypeDelegator<U> right) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
	{
		forward.copyOver(right);
	}

	@Override
	public boolean exists()
	{
		return forward.exists();
	}

	@Override
	public U getDelegate()
	{
		return forward.getDelegate();
	}

	@Override
	public String getFeatureName()
	{
		return forward.getFeatureName();
	}

	@Override
	public String getGlobalID()
	{
		return forward.getGlobalID();
	}

	protected T getForward()
	{
		return forward;
	}

	@Override
	public String getModelName()
	{
		return getForward().getModelName();
	}

	@Override
	public Integer getSer()
	{
		return forward.getSer();
	}

	@Override
	public void loadedVersion()
	{
		forward.loadedVersion();
	}

	@Override
	public void resetDelegateBySer(int ser)
	{
		forward.resetDelegateBySer(ser);
	}

	@Override
	public void setDelegate(U delegate)
	{
		forward.setDelegate(delegate);
	}

	@Override
	public void setOwnedDelegate(U delegate)
	{
		forward.setOwnedDelegate(delegate);
	}

	@Override
	public void reset()
	{
		forward.reset();
	}

	@Override
	public void setSer(Integer ser)
	{
		forward.setSer(ser);
	}

	@Override
	public String toJSONString()
	{
		return forward.toJSONString();
	}

	@Override
	public boolean fromCSV(String csv)
	{
		return forward.fromCSV(csv);
	}

	@Override
	public boolean fromCSVList(List<String> csv)
	{
		return forward.fromCSVList(csv);
	}

	@Override
	public String toCSV(boolean header)
	{
		return forward.toCSV(header);
	}
}