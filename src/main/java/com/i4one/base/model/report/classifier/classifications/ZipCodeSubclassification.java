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
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.report.classifier.BaseSubclassification;
import com.i4one.base.model.report.classifier.Classification;
import com.i4one.base.model.report.classifier.Subclassification;
import com.i4one.base.model.user.User;

/**
 * @author Hamid Badiozamani
 */
public class ZipCodeSubclassification<T extends UserType> extends BaseSubclassification<T> implements Subclassification<T>
{
	private String zipCode;

	public ZipCodeSubclassification(Classification<T, ZipCodeSubclassification<T>> parent, String zipCode)
	{
		super(parent, zipCode);

		this.zipCode = zipCode;
	}

	@Override
	public boolean belongs(UserType item)
	{
		User user = item.getUser();
		return user.getZipcode().startsWith(zipCode);
	}

	public String getZipCode()
	{
		return zipCode;
	}

	public void setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
	}

	@Override
	public IString getTitle()
	{
		return new IString(zipCode);
	}
}