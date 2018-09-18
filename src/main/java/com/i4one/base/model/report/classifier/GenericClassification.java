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

/**
 * A generic classification for use in comparisons to other concrete classifications.
 * This class is intended to be used in specifying a lineage when reading data from
 * the report.
 * 
 * @author Hamid Badiozamani
 */
public class GenericClassification<T extends Object> extends BaseClassification<T, GenericSubclassification<T>>
{
	private final String title;

	public GenericClassification(String title)
	{
		this.title = title;
	}

	@Override
	public String getKey()
	{
		return title;
	}

	@Override
	public int compareTo(Classification right)
	{
		int retVal = super.compareTo(right);

		return retVal;
	}

	@Override
	public boolean hasSubclassification(Subclassification<T> subclass)
	{
		return getSubclasses().contains(subclass);
	}

	@Override
	public ClassificationDisplay<T, GenericSubclassification<T>> getDefaultDisplay(ClassificationReport<?,T> report)
	{
		throw new UnsupportedOperationException("Generic classifications don't have displays.");
	}
}
