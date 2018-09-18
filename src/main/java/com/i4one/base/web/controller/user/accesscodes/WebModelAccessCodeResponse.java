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
package com.i4one.base.web.controller.user.accesscodes;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.accesscode.AccessCodeResponse;

/**
 * @author Hamid Badiozamani
 */
public class WebModelAccessCodeResponse extends AccessCodeResponse
{
	private transient String code;

	public WebModelAccessCodeResponse()
	{
		super();

		code = "";
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		getLogger().debug("Validating code response with code " + getCode());

		// Can't have an empty code if no serial number was specified
		//
		if ( Utils.isEmpty(getCode()) )
		{
			errors.addError(new ErrorMessage("code.code", "msg.base.AccessCode.emptyCode", "Please enter a code", new Object[]{"item", this}));
		}

		// The user has to be set
		//
		if ( !getUser().exists() )
		{
			errors.addError(new ErrorMessage("msg.base.accessCodeResponseManager.processCode.userdne", "You must be logged in", new Object[] { "user", getUser()}, null));
		}

		return errors;
	}

	public boolean getCorrect()
	{
		return isCorrect();
	}

	public boolean isCorrect()
	{
		return getAccessCode().exists() && getCode().equalsIgnoreCase(getAccessCode().getCode());
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = Utils.trimString(code);
	}
}
