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
package com.i4one.base.model.manager.attachable;

import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.manager.StatefulManager;
import com.i4one.base.model.user.User;

/**
 * @author Hamid Badiozamani
 */
public interface AttachedManager<U extends RecordType, T extends RecordTypeDelegator<U>> extends StatefulManager<U,T>
{
	/**
	 * Get the user for whom to process attached items associated with the given item.
	 * A user that does not exist will cause the triggers not to fire.
	 * 
	 * @param item The item for which triggers are being processed
	 * 
	 * @return  The user for whom to process the triggers for
	 */
	public User getUser(T item);

	/**
	 * The item that has balance expenses attached to it. This is the specific
	 * item that the user has chosen to engage.
	 * 
	 * @param item The activity record being processed
	 * 
	 * @return The item that has balance expenses attached to it.
	 */
	public RecordTypeDelegator<?> getAttachee(T item);
}
