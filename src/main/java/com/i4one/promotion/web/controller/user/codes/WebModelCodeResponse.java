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
package com.i4one.promotion.web.controller.user.codes;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.promotion.model.code.CodeResponse;

/**
 * @author Hamid Badiozamani
 */
public class WebModelCodeResponse extends CodeResponse
{
	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		getLogger().debug("Validating code response with code " + getCode());
		if ( getCode().exists() )
		{
			// If we're given a serial number we can just look up the code,
			// and assume that it was created correctly by an administrator
			//
			getCode().loadedVersion();

			if ( !getCode().exists() )
			{
				errors.addError(new ErrorMessage("code.ser", "msg.promotion.Code.invalidCode", "Please enter a valid code to play", new Object[]{"item", this}));
			}
		}
		else
		{
			// Can't have an empty code if no serial number was specified
			//
			if ( Utils.isEmpty(getCode().getCode()) )
			{
				errors.addError(new ErrorMessage("code.code", "msg.promotion.Code.emptyCode", "Please enter a code to play", new Object[]{"item", this}));
			}
		}

		// The user has to be set
		//
		if ( !getUser().exists() )
		{
			errors.addError(new ErrorMessage("msg.promotion.codeResponseManager.processCode.userdne", "You must be logged in", new Object[] { "user", getUser()}, null));
		}

		return errors;
	}
}
