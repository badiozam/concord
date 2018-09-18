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
public class ZipCodeTarget extends BaseTarget
{
	private String zipCode;

	private static final String KEY_PREFIX = "base.users-zipcode";

	public ZipCodeTarget(String key)
	{
		super(key);

		parse(key);
	}

	public ZipCodeTarget(String zipCode, boolean none)
	{
		super(KEY_PREFIX + ":" + zipCode);

		this.zipCode = zipCode;
	}

	@Override
	public boolean isDefault()
	{
		return Utils.isEmpty(getZipCode());
	}

	@Override
	public String getKey()
	{
		return KEY_PREFIX + ":" + getZipCode();
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
			throw new IllegalArgumentException("Expecting format " + getName() + ":<zipcode>, but got '" + target + "'");
		}

		zipCode = targetSplit[1];
	}

	public String getZipCode()
	{
		return zipCode;
	}

	public void setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
	}

}
