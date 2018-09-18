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
import java.util.Iterator;
import java.util.Set;

/**
 * An arbitrary set comparator for use by SortedSets. The comparison follows the
 * following rules:
 * 
 * <ol>
 * 	<li>If the size of one set is greater than that of the other, the set
 * with the larger size is said to be the greater of the two.</li>
 * 	<li>If the sets are of equal size, then the sets are traversed and the
 * first element that is not equal to its counterpart determines the order of
 * the whole set regardless of subsequent members.</li>
 * 	<li>If all elements are equal and the sets are of the same size, then
 * both sets are equal.</li>
 * </ol>
 * 
 * @author Hamid Badiozamani
 */
public class SetComparator<T extends Set<? extends Comparable>> implements Comparator<T>
{

	@Override
	public int compare(T left, T right)
	{
		if ( left == right )
		{
			return 0;
		}
		else if ( left.size() == right.size() )
		{
			Iterator<? extends Comparable> leftIt = left.iterator();
			Iterator<? extends Comparable> rightIt = right.iterator();

			for ( int i = 0; i < left.size(); i ++ )
			{
				Comparable leftItem = leftIt.next();
				Comparable rightItem = rightIt.next();

				// The first item in either set that's different
				// determines the order regardless of subsequent
				// members.
				//
				int leftVRight = leftItem.compareTo(rightItem);
				if ( leftVRight != 0 )
				{
					return leftVRight;
				}
			}

			// All elements are equal
			//
			return 0;
		}
		else
		{
			return left.size() > right.size() ? 1 : -1;
		}
	}
}
