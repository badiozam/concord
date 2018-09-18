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
package com.i4one.base.model.report.classifier;

import com.i4one.base.model.i18n.IString;

/**
 * A sub-classification whose only purpose is to test whether a given record
 * belongs to it or not.
 *
 * @author Hamid Badiozamani
 */
public interface Subclassification<T extends Object> extends Comparable<Subclassification<T>>
{
	/**
	 * Gets the classification this subclassification belongs to.
	 * 
	 * @return The parent classification
	 */
	public Classification<T, ? extends Subclassification<T>> getParent();

	/**
	 * Get the key for this subclassification. The key should uniquely
	 * identify the key within the parent classification.
	 * Examples would be "m", "1", "18-24" or "90210".
	 * 
	 * @return The key for the subclassification.
	 */
	public String getKey();

	/**
	 * Get the i18n title. This field can be empty in which case the key
	 * should be used to transform it using the MessageManager.
	 * 
	 * @return The title if it exists, or an empty IString otherwise
	 */
	public IString getTitle();

	/**
	 * Get a string that uniquely identifies this subclassification globally.
	 * 
	 * @return The unique ID for this subclass.
	 */
	public String getUniqueID();

	/**
	 * Whether this subclassifications members are always exclusive to another's.
	 * 
	 * @param sub The sub to compare
	 * 
	 * @return True if this subclassifications members are always exclusive to
	 * 	another's.
	 */
	public boolean isExclusiveTo(Subclassification<T> sub);

	/**
	 * Whether the given item belongs in this subclassification or not.
	 * 
	 * @param item The item to test
	 * 
	 * @return True if the item belongs, false otherwise.
	 */
	public boolean belongs(T item);
}
