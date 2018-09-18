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

import com.i4one.base.model.manager.Manager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class represents a return type that can carry multiple values
 *
 * @author Hamid Badiozamani
 * 
 * @param <T> The central item that is being processed
 */
public class ReturnType<T> extends HashMap<String, Object> implements Map<String, Object>
{
	static final long serialVersionUID = 42L;

	private final HashMap<String,List<ReturnType<?>>> chain;

	private T pre;
	private T post;

	public ReturnType()
	{
		super();

		chain = new HashMap<>();
	}

	public ReturnType(T returnValue)
	{
		super();

		post = returnValue;
		chain = new HashMap<>();
	}

	public T getPost()
	{
		return post;
	}

	public void setPost(T returnValue)
	{
		this.post = returnValue;
	}

	public T getPre()
	{
		return pre;
	}

	public void setPre(T pre)
	{
		this.pre = pre;
	}

	public <R extends RecordTypeDelegator<?>> void addChain(Manager manager, String methodName, List<ReturnType<R>> returnValues)
	{
		addChain(manager.getInterfaceName() + "." + methodName, returnValues);
	}

	public void addChain(Manager manager, String methodName, ReturnType<?> returnValue)
	{
		addChain(manager.getInterfaceName() + "." + methodName, returnValue);
	}

	public <R extends RecordTypeDelegator<?>> void addChain(String methodName, List<ReturnType<R>> returnValues)
	{
		List<ReturnType<?>> chainList = getChainList(methodName);
		chainList.addAll(returnValues);

		chain.put(methodName, chainList);
	}

	public void addChain(String methodName, ReturnType<?> returnValue)
	{
		List<ReturnType<?>> chainList = getChainList(methodName);
		chainList.add(returnValue);

		chain.put(methodName, chainList);
	}

	public List<ReturnType<?>> getChainList(Manager manager, String methodName)
	{
		return getChainList(manager.getInterfaceName() + "." + methodName);
	}

	public List<ReturnType<?>> getChainList(String methodName)
	{
		List<ReturnType<?>> retVal = chain.get(methodName);
		if ( retVal == null )
		{
			retVal = new ArrayList<>();
		}

		return retVal;
	}

	public ReturnType<?> getChain(Manager manager, String methodName)
	{
		List<ReturnType<?>> chainList = getChainList(manager, methodName);
		if ( chainList.isEmpty() )
		{
			return null;
		}
		else
		{
			return chainList.get(0);
		}
	}

	public ReturnType<?> getChain(String methodName)
	{
		List<ReturnType<?>> chainList = getChainList(methodName);
		if ( chainList.isEmpty() )
		{
			return null;
		}
		else
		{
			return chainList.get(0);
		}
	}

	public Collection<ReturnType<?>> getAllChains()
	{
		ArrayList<ReturnType<?>> flattenedChains = new ArrayList<>();

		for ( List<ReturnType<?>> items : chain.values() )
		{
			flattenedChains.addAll(items);
		}

		return flattenedChains;
	}
}
