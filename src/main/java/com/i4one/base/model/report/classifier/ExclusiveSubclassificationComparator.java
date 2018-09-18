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

import java.util.Comparator;

/**
 * Comparator that determines equality based on whether two subclassifications
 * are exclusive to each other or not.
 * 
 * @author Hamid Badiozamani
 */
public class ExclusiveSubclassificationComparator<T extends Subclassification> implements Comparator<T>
{
	@Override
	public int compare(T left, T right)
	{
		if ( left == right )
		{
			return 0;
		}
		else if ( right == null )
		{
			return -1;
		}
		else if ( left == null )
		{
			return 1;
		}
		else if ( left.isExclusiveTo(right) )
		{
			return 0;
		}
		else
		{
			return left.compareTo(right);
		}
	}
}