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
package com.i4one.base.web.persistence;

import com.i4one.base.core.Base64;
import static com.i4one.base.core.Base64.decodeToObject;
import static com.i4one.base.core.Base64.encodeObject;
import static com.i4one.base.core.Utils.forceEmptyStr;
import java.io.Serializable;
import javax.servlet.http.Cookie;

/**
 * @author Hamid Badiozamani
 */
public class CookiePersistence extends Persistence
{
	private static final String DELETED = "da602f0b162fccbf6b150cfcfc7a7379";

	@Override
	public void put(String key, Serializable value)
	{
		// Expires at the end of the session
		//
		put(key, value, -1);
	}

	@Override
	public void put(String key, Serializable value, int expiresAfterSeconds)
	{
		Cookie cookie = new Cookie(key, null);
		cookie.setPath("/");

		if ( value == null )
		{
			cookie.setValue(DELETED);
			cookie.setMaxAge(0);
			getLogger().trace("Removing cookie " + cookie.getName() + " = " + cookie.getValue());
		}
		else
		{
			cookie.setMaxAge(expiresAfterSeconds);
			cookie.setValue(encodeObject(value, Base64.DONT_BREAK_LINES));
			getLogger().trace("Adding cookie " + cookie.getName() + " = " + cookie.getValue());
		}

		getResponse().addCookie(cookie);
	}

	@Override
	public Serializable get(String key)
	{
		Serializable retVal = null;
		Cookie[] cookies = getRequest().getCookies();
		if ( cookies != null )
		{
			for ( Cookie cookie : cookies )
			{
				// Cookies can be kept by a browser instead of removed, by setting the value to
				// the constant here, we can always be sure of whether a cookie was deleted or not
				//
				if ( cookie.getName().equals(key) && !cookie.getValue().equals(DELETED))
				{
					try
					{
						retVal = (Serializable) decodeToObject(forceEmptyStr(cookie.getValue()));
						getLogger().trace("Found cookie with key = " + key + " = " + retVal);
					}
					catch (IllegalArgumentException iae)
					{
						getLogger().trace("Couldn't deserialize: " + iae);
					}
					break;
				}
			}
		}

		return retVal;
	}

}
