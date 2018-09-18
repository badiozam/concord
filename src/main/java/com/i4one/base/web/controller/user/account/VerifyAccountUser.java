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
package com.i4one.base.web.controller.user.account;

import com.i4one.base.model.Errors;
import com.i4one.base.model.user.User;

/**
 * @author Hamid Badiozamani
 */
public class VerifyAccountUser extends User
{
	static final long serialVersionUID = 42L;

	@Override
	public Errors validate()
	{
		Errors errors = new Errors();

		// We just need to have a valid mobile phone number
		//
		validateCellPhoneNumber(errors);
		return errors;
	}

	@Override
	public boolean equals(Object right)
	{
		if ( right == null )
		{
			return false;
		}
		else
		{
			if ( right instanceof VerifyAccountUser )
			{
				return super.equals(right);
			}
			else
			{
				return false;
			}
		}
	}
}
