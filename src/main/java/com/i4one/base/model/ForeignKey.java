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
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a foreign key relationship between two classes. The owner maintains
 * a foreign key reference. In order for the class to properly maintain the reference,
 * the init() and actualize() methods must be called in the owner's respective init()
 * and actualize() methods.
 * 
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public class ForeignKey<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseLoggable
{
	private T instance;

	private final RecordTypeDelegator<?> owner;

	private final Supplier<T> newInstance;
	private final Supplier<Integer> getId;
	private final Consumer<Integer> setId;

	public ForeignKey(RecordTypeDelegator<?> owner, Supplier<Integer> getId, Consumer<Integer> setId, Supplier<T> newInstance)
	{
		this.owner = owner;

		this.getId = getId;
		this.setId = setId;

		this.newInstance = newInstance;
		this.instance = null;
	}

	public void reset(Integer ser)
	{
		if ( ser != null && instance != null )
		{
			instance.resetDelegateBySer(ser);
		}
	}

	public void actualize()
	{
		// The instance's serial number may have been set outside callers,
		// which means that our delegate's serial number is now different
		// than that of the instance's serial number. Before we do any
		// database work, these two need to be reconciled.
		//
		if ( instance != null )
		{
			setId.accept(instance.getSer());
		}
	}

	public T get(boolean doLoad)
	{
		if ( instance == null )
		{
			instance = newInstance.get();
			instance.resetDelegateBySer(getId.get());
		}

		if ( doLoad )
		{
			instance.loadedVersion();
		}

		return instance;
	}

	public void set(T item)
	{
		this.instance = item;
		setId.accept(item.getSer());
	}

	public RecordTypeDelegator<?> getOwner()
	{
		return owner;
	}
}
