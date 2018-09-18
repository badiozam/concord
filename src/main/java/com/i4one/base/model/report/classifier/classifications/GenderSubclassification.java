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
package com.i4one.base.model.report.classifier.classifications;

import com.i4one.base.model.UserType;
import com.i4one.base.model.report.classifier.BaseSubclassification;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.Subclassification;

/**
 * @author Hamid Badiozamani
 */
public class GenderSubclassification<T extends UserType> extends BaseSubclassification<T> implements Subclassification<T>
{
	private final String gender;

	public GenderSubclassification(Classification<T, GenderSubclassification<T>> parent, String gender)
	{
		super(parent, gender);

		this.gender = gender;
	}

	@Override
	public boolean belongs(UserType item)
	{
		return item.getUser().getGender().equalsIgnoreCase(getGender());
	}

	public String getGender()
	{
		return gender;
	}
}
