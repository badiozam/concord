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
package com.i4one.base.web.controller.admin.admins;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.admin.Admin;
import java.security.NoSuchAlgorithmException;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hamid Badiozamani
 */
public class ResetPasswordAdmin extends Admin
{
	private String newPassword;
	private String confirmPassword;
	private String currentPassword; 

	@Override
	public Errors validate()
	{
		// Skip validation for everything since we're just
		// wanting to just reset the admin's password
		//
		return Errors.NO_ERRORS;
	}

	public String getCurrentPassword()
	{
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword)
	{
		this.currentPassword = currentPassword;
	}

	@NotBlank(message = "msg.base.Admin.emptyPassword")
	@Size(min = 6, message = "msg.base.Admin.invalidPassword")
	public String getNewPassword()
	{
		return newPassword;
	}

	public void setNewPassword(String password) throws NoSuchAlgorithmException
	{
		this.newPassword = password;
		super.setPassword(newPassword);
	}

	public String getConfirmPassword()
	{
		return "";
	}

	public void setConfirmPassword(String confirmPassword)
	{
		this.confirmPassword = confirmPassword;
	}

	@AssertTrue(message = "msg.base.Admin.invalidConfirmPassword")
	public boolean getPasswordMatches()
	{
		return Utils.forceEmptyStr(confirmPassword).equals(Utils.forceEmptyStr(newPassword));
	}

	public void setPasswordMatches(boolean ignored)
	{
	}

}
