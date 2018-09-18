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
package com.i4one.base.model.manager.triggerable;

import com.i4one.base.dao.ClientRecordType;
import com.i4one.base.model.AttachableClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import java.util.Collection;
import java.util.Set;

/**
 * Interface representing model objects that can have BalanceTrigger attachments.
 * 
 * @author Hamid Badiozamani
 * 
 * @param <U> The underlying delegate type
 * @param <O> The balance owner's type (i.e. typically the concrete type of the subclass)
 */
public interface TriggerableClientType<U extends ClientRecordType, O extends SingleClientType> extends AttachableClientType<U,O>
{
	/**
	 * Get the balance triggers that can be associated solely to this
	 * item. This is typically a subset of the balance triggers associated
	 * with the item.
	 * 
	 * @return The balance triggers that can only be associated to this item.
	 */
	public Set<ExclusiveBalanceTrigger<O>> getExclusiveBalanceTriggers();

	/**
	 * Get the balance triggers that may be associated with other features
	 * but are also associated with this item.
	 * 
	 * @return The balance triggers that may be associated to other items as
	 * well as this item.
	 */
	public Set<BalanceTrigger> getNonExclusiveBalanceTriggers();

	/**
	 * Get the set of all triggers associated with this item.
	 * 
	 * Normally, this would be the stronger Set class. However, 
	 * due to the nature of Spring's form backing object referencing
	 * scheme we need to use a dynamic expandable array.
	 *
	 * @return The set of triggers
	 */
	public Set<BalanceTrigger> getBalanceTriggers();

	/**
	 * Set the triggers associated with this item.
	 *
	 * @param balanceTriggers The new set of triggers, duplicates are automatically
	 * 	removed
	 */
	public void setBalanceTriggers(Collection<BalanceTrigger> balanceTriggers);
}
