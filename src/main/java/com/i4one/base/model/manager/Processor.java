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
package com.i4one.base.model.manager;

import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;

/**
 * Any object that performs operations on the given type
 * 
 * @author Hamid Badiozamani
 * @param <U>
 * @param <T>
 */
public interface Processor<U extends RecordType, T extends RecordTypeDelegator<U>>
{
	/**
	 * Performs some sort of the process on the given item
	 * 
	 * @param item The item to perform processing on
	 * @param rowNum The position in a list (or 0 if it's the only item in the list)
	 * @param params Any optional parameters that need to be passed to the processor
	 * 
	 * @return Whether the caller should continue with the iteration (if applicable) or to stop
	 */
	public boolean process(T item, int rowNum, Object... params);
}
