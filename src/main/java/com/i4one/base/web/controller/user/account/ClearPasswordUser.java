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

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.user.User;
import java.security.NoSuchAlgorithmException;

/**
 * @author Hamid Badiozamani
 */
public abstract class ClearPasswordUser extends User
{
	private String clearPassword;
	private String confirmPassword;

	public ClearPasswordUser()
	{
		super();

		clearPassword = "";
		confirmPassword = "";
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		validatePassword(retVal);

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		try
		{
			setPassword(getClearPassword());
		}
		catch (NoSuchAlgorithmException ex)
		{
			getLogger().error("Couldn't set password field", ex);
		}
	}

	protected void validatePassword(Errors errors)
	{
		if ( !getClearPassword().matches(getClient().getOptionValue("validate.User.password")))
		{
			errors.addError(new ErrorMessage("clearPassword", "msg.base.User.invalidPassword", "Invalid Password", new Object[]{"user", this}));
		}
		else if ( !getClearPassword().equals(confirmPassword))
		{
			errors.addError(new ErrorMessage("confirmPassword", "msg.base.User.invalidConfirmPassword", "Your passwords don't match", new Object[]{"user", this}));
		}
	}

	public void setClearPassword(String password)
	{
		this.clearPassword = password;
	}

	public String getClearPassword()
	{
		return clearPassword;
	}

	public String getConfirmPassword()
	{
		return "";
	}

	public void setConfirmPassword(String confirmPassword)
	{
		this.confirmPassword = confirmPassword;
	}

	/*
	 * We want to bypass the password if the user signs up using Facebook
	 *
	@AssertTrue(message = "msg.base.User.invalidConfirmPassword")
	public boolean getPasswordMatches()
	{
		return forceEmptyStr(confirmPassword).equals(forceEmptyStr(clearPassword));
	}

	public void setPasswordMatches(boolean ignored)
	{
	}
	*/
}
