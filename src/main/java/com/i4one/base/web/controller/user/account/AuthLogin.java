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

import com.i4one.base.core.Utils;
import com.i4one.base.model.user.User;
import com.i4one.base.web.persistence.Persistence;
import java.security.NoSuchAlgorithmException;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hamid Badiozamani
 */
public class AuthLogin extends User
{
	static final long serialVersionUID = 42L;

	private boolean rememberMe;
	private String MD5Password;
	private String clearPassword;

	@Override
	public Integer getSer()
	{
		// Since this is a web model object we don't
		// want to expose any database serial numbers
		//
		return 0;
	}

	@NotBlank(message="msg.base.AuthLogin.invalidUsername")
	@Override
	public String getUsername()
	{
		return super.getUsername();
	}

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
		return this.MD5Password;
	}

	public void setMD5Password(String md5Password)
	{
		this.MD5Password = md5Password;
	}

	public boolean isRememberMe()
	{
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe)
	{
		this.rememberMe = rememberMe;
	}

	public void persist(Persistence persistence, int seconds)
	{
		try
		{
			// Sanitize the input credentials before persisting them (the serial number is
			// always returned as zero for AuthLogin so no need to do it here)
			//
			if ( Utils.isEmpty(getMD5Password()) )
			{
				// Store the MD5 encoded password if we didn't already have one set
				//
				setMD5Password(super.getMD5Password());
			}

			// Redact the real password since it may be stored in the clear
			//
			setClearPassword(Utils.repeatStr("X", getClearPassword().length()));
			getLogger().debug("Persisting " + this + " with clear password = " + clearPassword);
			persistence.put(this, seconds);
		}
		catch (NoSuchAlgorithmException ex)
		{
			getLogger().error("Could not persist " + this, ex);
		}
	}

	public void unpersist(Persistence persistence)
	{
		persistence.remove(getClass());
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
			if ( right instanceof AuthLogin )
			{
				return super.equals(right);
			}
			else
			{
				return false;
			}
		}
	}

	public static AuthLogin fromPersistence(Persistence persistence)
	{
		return (AuthLogin) persistence.get(AuthLogin.class);
	}
}
