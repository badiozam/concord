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
package com.i4one.base.model.manager.instantwinnable;

import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.AttachableClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import java.util.Collection;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface InstantWinnableClientType<U extends ClientRecordType, T extends SingleClientType<U>> extends AttachableClientType<U,T>
{
	/**
	 * Get the instant wins that can be associated solely to this
	 * item. This is typically a subset of the instant wins associated
	 * with the item.
	 * 
	 * @return The instant wins that can only be associated to this item.
	 */
	public Set<ExclusiveInstantWin<T>> getExclusiveInstantWins();

	/**
	 * Get the instant wins that may be associated with other features
	 * but are also associated with this item.
	 * 
	 * @return The instant wins that may be associated to other items as
	 * well as this item.
	 */
	public Set<InstantWin> getNonExclusiveInstantWins();

	/**
	 * Get the set of all triggers associated with this item.
	 * 
	 * Normally, this would be the stronger Set class. However, 
	 * due to the nature of Spring's form backing object referencing
	 * scheme we need to use a dynamic expandable array.
	 *
	 * @return The set of triggers
	 */
	public Set<InstantWin> getInstantWins();

	/**
	 * Set the triggers associated with this item.
	 *
	 * @param instantWins The new set of instant wins, duplicates are automatically
	 * 	removed
	 */
	public void setInstantWins(Collection<InstantWin> instantWins);
}
