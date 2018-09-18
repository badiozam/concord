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

import com.i4one.base.core.Utils;
import com.i4one.base.model.targeting.BaseTarget;

/**
 * @author Hamid Badiozamani
 */
public class AgeGenderTarget extends BaseTarget
{
	private String gender;
	private int minAge;
	private int maxAge;

	private static final String KEY_PREFIX = "base.users-agegender";

	public AgeGenderTarget(String key)
	{
		super(key);

		parse(key);
	}

	public AgeGenderTarget(String gender, int minAge, int maxAge)
	{
		super(KEY_PREFIX + ":" + gender + "-" + minAge + "-" + maxAge);

		this.gender = gender;
		this.minAge = minAge;
		this.maxAge = maxAge;
	}

	@Override
	public boolean isDefault()
	{
		return getGender().equals("a") && minAge == 0 && maxAge == 100;
	}

	@Override
	public String getKey()
	{
		return KEY_PREFIX + ":" + getGender() + "-" + getMinAge() + "-" + getMaxAge();
	}

	@Override
	public String getName()
	{
		return KEY_PREFIX;
	}

	private void parse(String target)
	{
		String[] targetSplit = target.split(":");
		if ( targetSplit.length != 2 )
		{
			throw new IllegalArgumentException("Expecting format " + getName() + ":<gender>-<min>-<max>, but got '" + target + "'");
		}

		String[] ageRange = targetSplit[1].split("-");
		if ( ageRange.length != 3 )
		{
			throw new IllegalArgumentException("Expecting format " + getName() + ":<gender>-<min>-<max>, but got '" + target + "'");
		}
		else
		{
			// First item is the prefix "base.users:age"
			// Second item is the minimum age
			// Optional third item is the maximum age
			//
			gender = ageRange[0];
			minAge = Utils.defaultIfNaN(ageRange[1], 0);
			maxAge = Utils.defaultIfNaN(ageRange[2], 999);
		}
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public int getMinAge()
	{
		return minAge;
	}

	public void setMinAge(int minAge)
	{
		this.minAge = minAge;
	}

	public int getMaxAge()
	{
		return maxAge;
	}

	public void setMaxAge(int maxAge)
	{
		this.maxAge = maxAge;
	}

}
