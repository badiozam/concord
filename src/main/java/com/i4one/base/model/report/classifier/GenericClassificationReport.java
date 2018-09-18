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

import com.i4one.base.dao.RecordType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class GenericClassificationReport<U extends RecordType, T extends Object> extends ClassificationReport<U, T>
{
	public GenericClassificationReport(String title)
	{
		super(title);
	}

	@Override
	protected T convert(U record)
	{
		throw new UnsupportedOperationException("Generic Classification Reports are only used for holding.");
	}

	public void setValues(Map<String, Integer> newValues)
	{
		values.clear();

		for ( Map.Entry<String, Integer> entry : newValues.entrySet() )
		{
			Set<Subclassification<T>> key = new HashSet<>();

			String lineage = entry.getKey().replaceFirst("^\\[", "").replaceFirst("\\]$", "").replaceAll(" ", "");
			String[] keyClassifications = lineage.split(",");

			for ( String classification : keyClassifications )
			{
				String[] classComponents = classification.split("#");
				if ( classComponents.length == 2 )
				{
					Classification<T,? extends Subclassification<T>> generic = new GenericClassification<>(classComponents[0]);
					Subclassification<T> genericSub = new GenericSubclassification<>(generic, classComponents[1]);

					key.add(genericSub);
				}
			}

			if ( add(key) )
			{
				values.put(key, entry.getValue());
			}
		}
	}

	public void setClassifications(Set<Classification<T, ? extends Subclassification<T>>> newClassifications)
	{
		classifications.clear();
		for ( Classification<T, ? extends Subclassification<T>> newClass : newClassifications )
		{
			classifications.add(newClass);
		}
	}
}
