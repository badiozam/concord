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

import com.i4one.base.model.report.classifier.display.ClassificationDisplay;
import java.util.Collection;
import java.util.Set;

/**
 * A classification describes a group of data with a common characteristic. It is
 * only a description and doesn't hold any data itself. Rather, the purpose of the
 * interface is to be used as a key in a mapping of Classification =&gt; Number
 * values which could then be viewed as a whole report.
 * 
 * @author Hamid Badiozamani
 * @param <T> The type of object being classified.
 * @param <SUB> The type of subclassifications that this classification contains.
 */
public interface Classification<T extends Object, SUB extends Subclassification<T>> extends Comparable<Classification<T, SUB>>
{
	/**
	 * The key for this classification. The key uniquely identifies
	 * this classification and does not consider its lineage.
	 * 
	 * @return The title for this classification
	 */
	public String getKey();

	/**
	 * Get the set of subclassifications that belong to this classification.
	 * 
	 * @return The set of subclassifications for this classification
	 */
	public Set<SUB> getSubclassifications();

	/**
	 * Set the subclassifications for this classification.
	 * 
	 * @param subclasses The subclassifications for this classifications
	 */
	public void setSubclassifications(Collection<SUB> subclasses);

	/**
	 * Adds the given subclassification to the current list of subclassifications.
	 * 
	 * @param subclass The classification to add
	 * 
	 * @return True if the subclassification was added, false if the
	 * 	subclassification was not added (likely because it's a duplicate).
	 */
	public boolean add(SUB subclass);

	/**
	 * Remove a given subclassification
	 * 
	 * @param subclass The subclassification to remove
	 * 
	 * @return True if the subclass was successfully removed, false if the subclass
	 * 	was not found
	 */
	public boolean remove(SUB subclass);

	/**
	 * Tests to see whether this subclassification exists anywhere in the
	 * descendent tree.
	 * 
	 * @param subclass The subclass to check
	 * 
	 * @return True if this or any sub-classifications contain the incoming
	 * 	value, false otherwise.
	 */
	public boolean hasSubclassification(Subclassification<T> subclass);

	/**
	 * Tests to see if this classification is the same as another. The
	 * classifications are compared only to each other. This is different
	 * behavior than the equals(..) method which considers parental lineage as well.
	 * 
	 * @param classification The classification to compare to
	 * 
	 * @return True if the reports are the same, false otherwise
	 */
	public boolean isSameClassification(Classification<T, SUB> classification);

	/**
	 * Get the default display class for this classification.
	 * 
	 * @param report The report containing the data for the display to render.
	 * 
	 * @return The default display class for this classification.
	 */
	public ClassificationDisplay<T, SUB> getDefaultDisplay(ClassificationReport<?,T> report);
}