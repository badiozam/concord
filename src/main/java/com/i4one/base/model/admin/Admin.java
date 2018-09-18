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
package com.i4one.base.model.admin;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Authenticable;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import org.springframework.security.crypto.codec.Hex;

/**
 * This class represents an administrator with a given set of privileges.
 *
 * @author Hamid Badiozamani
 */
public class Admin extends BaseRecordTypeDelegator<AdminRecord> implements Authenticable
{
	static final long serialVersionUID = 42L;

	public Admin()
	{
		super(new AdminRecord());
	}

	protected Admin(AdminRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( !getEmail().matches("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[a-z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\\b$") )
		{
			retVal.addError(new ErrorMessage("email", "msg.base.Admin.invalidEmail", "Invalid e-mail address: $admin.email", new Object[]{"admin", this}));
		}

		return retVal;
	}

	@Override
	protected String uniqueKeyInternal()
	{
		String username = getUsername();
		String email = getEmail();

		// We provide a unique key that includes the password so that authenticate(..)
		// calls in the AdminManager can be cached with different passwords as inputs
		//
		if ( !Utils.isEmpty(username))
		{
			return username + "-" + Objects.hashCode(getPassword());
		}
		else if (!Utils.isEmpty(email))
		{
			return email + "-" + Objects.hashCode(getPassword());
		}
		else
		{
			return "NONE";
		}
	}

	@Override
	public String getUsername()
	{
		return getDelegate().getUsername();
	}

	public void setUsername(String username)
	{
		getDelegate().setUsername(username);
	}

	@Override
	public String getPassword()
	{
		return getDelegate().getPassword();
	}

	public void setPassword(String password) throws NoSuchAlgorithmException
	{
		getDelegate().setPassword(new String(Hex.encode(Utils.getHash(Utils.forceEmptyStr(password), "SHA-256"))));
	}

	public String getMD5Password() throws NoSuchAlgorithmException
	{
		return Utils.getMD5Hex(getDelegate().getPassword());
	}

	public String getName()
	{
		return getDelegate().getName();
	}

	public void setName(String name)
	{
		getDelegate().setName(name);
	}

	@Override
	public String getEmail()
	{
		return getDelegate().getEmail();
	}

	public void setEmail(String email)
	{
		getDelegate().setEmail(Utils.forceEmptyStr(email).toLowerCase());
	}

	public boolean isForceUpdate()
	{
		return getDelegate().isForceUpdate();
	}

	public boolean getForceUpdate()
	{
		return getDelegate().getForceUpdate();
	}

	public void setForceUpdate(boolean forceUpdate)
	{
		getDelegate().setForceUpdate(forceUpdate);
	}

	/*
	@Override
	public boolean equals(Object right)
	{
		// This equality operator checks the three given strings but NOT
		// the serial numbers if the fields exist
		//
		if ( right instanceof Admin )
		{
			Admin rightAdmin = (Admin) right;
			if ( getUsername() != null && rightAdmin.getUsername() != null )
			{
				// If username match fails, perhaps serial number will work
				//
				if ( getUsername().equalsIgnoreCase(rightAdmin.getUsername()) )
				{
					return true;
				}
				else
				{
					getLogger().trace("No match by username, attempting the superclass equals method");
					return super.equals(right);
				}
			}
			else
			{
				getLogger().trace("No username's available for comparison, attempting the super class equals(..) method");
				return super.equals(right);
			}
		}
		else
		{
			getLogger().debug(right + " is not an instance of " + Admin.class.getSimpleName());
			return false;
		}
	}
	*/
}
