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
package com.i4one.base.model.user.targets;

import com.i4one.base.dao.qualifier.SimpleComparisonQualifier;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;

/**
 * @author Hamid Badiozamani
 */
public class AgeGenderPagination extends SimplePaginationFilter implements PaginationFilter
{
	private String gender;
	private int minBirthyyyy;
	private int maxBirthyyyy;

	public AgeGenderPagination(String gender, int minBirthyyyy, int maxBirthyyyy)
	{
		this.gender = gender;
		this.minBirthyyyy = minBirthyyyy;
		this.maxBirthyyyy = maxBirthyyyy;
	}

	@Override
	protected void initQualifiers()
	{
		super.initQualifiers();

		if ( !getGender().equalsIgnoreCase("a"))
		{
			getColumnQualifier().setQualifier("gender", getGender());
		}

		getColumnQualifier().setQualifier("minbirthyyyy", new SimpleComparisonQualifier("birthyyyy", ">=", getMinBirthyyyy()));
		getColumnQualifier().setQualifier("maxbirthyyyy", new SimpleComparisonQualifier("birthyyyy", "<=", getMaxBirthyyyy()));
	}
	
	@Override
	protected String toStringInternal()
	{
		return "gender:" + getGender() + "min:" + getMinBirthyyyy() + "max:" + getMaxBirthyyyy() + "," + super.toStringInternal();
	}

	public int getMinBirthyyyy()
	{
		return minBirthyyyy;
	}

	public void setMinBirthyyyy(int minBirthyyyy)
	{
		this.minBirthyyyy = minBirthyyyy;
	}

	public int getMaxBirthyyyy()
	{
		return maxBirthyyyy;
	}

	public void setMaxBirthyyyy(int maxBirthyyyy)
	{
		this.maxBirthyyyy = maxBirthyyyy;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}
}