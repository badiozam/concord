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
package com.i4one.base.web.controller.admin.auth;

import com.i4one.base.model.admin.Admin;
import com.i4one.base.web.persistence.Persistence;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hamid Badiozamani
 */
public class AuthAdmin extends Admin
{
	static final long serialVersionUID = 42L;

	private boolean rememberMe;
	private String MD5Password;
	private String clearPassword;

	public AuthAdmin()
	{
		super();

		rememberMe = false;
		MD5Password = "";
		clearPassword = "";
	}

	@NotBlank(message="msg.base.Admin.invalidPassword")
	public String getClearPassword()
	{
		return clearPassword;
	}

	public void setClearPassword(String clearPassword)
	{
		this.clearPassword = clearPassword;
	}

	@Override
	public String getMD5Password()
	{
		return MD5Password;
	}

	public void setMD5Password(String MD5Password)
	{
		this.MD5Password = MD5Password;
	}

	public boolean isRememberMe()
	{
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe)
	{
		this.rememberMe = rememberMe;
	}

	public void rememberMe(Persistence persistence, int seconds)
	{
		//try
		{
			/*
			 * We no longer remember the password, only usernames, let the browsers handle
			 * storing passwords if the user is on a secure terminal
			 * 
			 */
			/*
			// Sanitize the input credentials before persisting them (the serial number is
			// always returned as zero for AuthLogin so no need to do it here)
			//
			if ( Utils.isEmpty(getMD5Password()) )
			{
				// Store the MD5 encoded password if we didn't already have one set
				//
				setMD5Password(Utils.getMD5Hex(getPassword()));
			}

			// Redact the real password since it may be stored in the clear
			//
			setClearPassword(repeatStr("X", getClearPassword().length()));
			*/
			setClearPassword("");
			persistence.put(this, seconds);
		}
		/*
		catch (NoSuchAlgorithmException ex)
		{
			getLogger().error("Could not persist " + this, ex);
		}
		*/
	}

	public void forgetMe(Persistence persistence)
	{
		rememberMe(persistence, 0);
	}

	public static AuthAdmin fromPersistence(Persistence persistence)
	{
		return (AuthAdmin) persistence.get(AuthAdmin.class);
	}
}
