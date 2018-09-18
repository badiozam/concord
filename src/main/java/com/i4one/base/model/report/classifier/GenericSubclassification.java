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

/**
 * A generic subclassification for use in comparisons to other concrete subclassifications.
 * This class is intended to be used in specifying a lineage when reading data from
 * the report.
 * 
 * @author Hamid Badiozamani
 */
public class GenericSubclassification<T extends Object> extends BaseSubclassification<T>
{
	public GenericSubclassification(Classification<T, ? extends Subclassification<T>> parent, String title)
	{
		super(parent, title);
	}

	@Override
	public int compareTo(Subclassification right)
	{
		int retVal = super.compareTo(right);

		return retVal;
	}

	@Override
	public boolean belongs(T item)
	{
		throw new UnsupportedOperationException("Generic subclassification can only be used for comparison.");
	}
}
