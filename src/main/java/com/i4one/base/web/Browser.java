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
package com.i4one.base.web;

import com.i4one.base.core.Utils;
import static com.i4one.base.core.Utils.defaultIfNaN;
import static com.i4one.base.core.Utils.forceEmptyStr;
import static com.i4one.base.core.Utils.isEmpty;
import javax.servlet.http.HttpServletRequest;

/**
 * This class represents a user's browser
 *
 * @author Hamid Badiozamani
 */
public class Browser
{
	// The lowest browser version that supports XML
	//
	private static final int LOWEST_XML_MAJOR_VERSION = 4;

	// The user-agent string passed in the constructor
	//
	private String userAgent;

	// The type of browser (IE, Netscape, Mozilla, FireFox)
	//
	private String browserType;

	// The major version number of the browser
	//
	private int majorVersion;

	// The minor version number
	//
	private int minorVersion;

	public Browser(HttpServletRequest req)
	{
		init(req.getHeader("User-Agent"));
	}

	/**
	 * The constructor
	 *
	 * @param userAgent The user agent string passed by the browser
	 *                  that identifies it.
	 */
	public Browser(String userAgent)
	{
		init(userAgent);
	}

	private void init(String userAgent)
	{
		this.userAgent = forceEmptyStr(userAgent);

		if ( !isEmpty(userAgent) )
		{
			// Now try to parse it... we first split the string up
			// by looking for the very first space
			//
			int sepIndex = userAgent.indexOf(' ');
			String browserTag = ( sepIndex > 0 ) ? userAgent.substring(0, sepIndex) : userAgent;

			// Now that we have the browser tag, separate it from the version number
			//
			sepIndex = browserTag.indexOf('/');
			this.browserType = ( sepIndex > 0 ) ? browserTag.substring(0, sepIndex) : "";
			String versionStr = ( sepIndex > 0 ) ? browserTag.substring(sepIndex + 1, browserTag.length()) : "";

			// Now get the minor and major versions
			//
			sepIndex = versionStr.indexOf('.');
			String majorStr = ( sepIndex > 0 ) ? versionStr.substring(0, sepIndex) : "0";
			String minorStr = ( sepIndex > 0 ) ? versionStr.substring(sepIndex + 1, versionStr.length()) : "0";

			// Convert the version numbers
			//
			majorVersion = defaultIfNaN(majorStr, 0);
			minorVersion = defaultIfNaN(minorStr, 0);
		}
		else
		{
			browserType = "unknown";
			majorVersion = 0;
			minorVersion = 0;
		}
	}

	/**
	 * Get the user agent
	 *
	 * @return The user agent
	 */
	public String getUserAgent()
	{
		return userAgent;
	}

	/**
	 * Returns a stringified representation of this object
	 *
	 * @return A string representing this object
	 */
	@Override
	public String toString()
	{
		return browserType + "/" + majorVersion + "." + minorVersion;
	}

	/**
	 * Whether or not this browser is XML compliant or not
	 *
	 * @return true if the browser is XML compliant, false otherwise
	 */
	public boolean isXMLCompliant()
	{
		return browserType.equals("Mozilla") && majorVersion >= LOWEST_XML_MAJOR_VERSION;
	}

	public String getBrowserType()
	{
		return browserType;
	}

	public void setBrowserType(String browserType)
	{
		this.browserType = browserType;
	}

	public int getMajorVersion()
	{
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion)
	{
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion()
	{
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion)
	{
		this.minorVersion = minorVersion;
	}

	public boolean getShowBrowserWarning()
	{
		return isIE(6) || isIE(7);
	}

	/**
	 * Whether the browser is IE or not
	 */
	public boolean isIE()
	{
		return userAgent.indexOf("MSIE") != -1;
	}

	public boolean isIE(int version)
	{
		return userAgent.indexOf("MSIE " + version) != -1;
	}
}
