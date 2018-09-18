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
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.web.interceptor.ClientInterceptor;
import java.awt.ComponentOrientation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class emulates an HttpServletRequest object but with a few more convenience
 * methods to facilitate usage in Velocity. It is used primarily by the model object
 *
 * @author Hamid Badiozamani
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper
{
	/** The session map name to allow us to use $request.session.somename in VTL */
	public static final String SESSION = "session";

	/** The session map name to allow us to use $request.params.somename in VTL */
	public static final String PARAMS = "params";

	/** The session map name to allow us to use $request.attrs.somename in VTL */
	public static final String ATTRS = "attrs";

	/** The current client object */
	public static final String CLIENT = "client";

	/** The client groups this client belongs to */
	public static final String SITEGROUPS = "siteGroups";

	/** The selected lanuage */
	public static final String LANGUAGE = "language";

	/** The selected lanuage */
	public static final String LANGUAGEDIR = "languageDir";

	/** The selected timezone */
	public static final String TIMEZONE = "timeZone";

	/** The selected timezone */
	public static final String CALENDAR = "calendar";

	/** The base URL for this request (used for building relative URLs)  */
	public static final String BASEURL = "baseurl";

	/** The base URL for this request (used for building relative URLs)  */
	public static final String FULLBASEURL = "fullbaseurl";

	/** The full base URL for this request (used for building absolute URLs)  */
	public static final String FULLREQUESTURI = "fullrequesturi";

	/** The message key prefix for the given view */
	public static final String MESSAGEPREFIX = "msgPrefix";

	/** The model for this request */
	public static final String MODEL = "model";

	private final ArrayList<String> attrNames;
	private final ArrayList<String> paramNames;
	private final ArrayList<String> sessionNames;

	private final HashMap<String,Object> properties;
	private final URLTokenizer urlTokenizer;

	private final Calendar calendar;
	private final long timeInMillis;
	private final int timeInSeconds;


	private Log logger;

	@SuppressWarnings("rawtypes")
	public HttpServletRequestWrapper(HttpServletRequest request)
	{
		super(request);

		logger = LogFactory.getLog(getClass());

		urlTokenizer = new URLTokenizer(request);

		HashMap<String,Object> paramsMap = new HashMap<>();
		paramNames = new ArrayList<>();

		HashMap<String,Object> sessionMap = new HashMap<>();
		sessionNames = new ArrayList<>();

		HashMap<String,Object> attrMap = new HashMap<>();
		attrNames = new ArrayList<>();

		// Store the parameter and session names as arrays to avoid the warning
		// Velocity gives about Enumerations being one-time use only
		//
		Enumeration params = request.getParameterNames();
		while ( params.hasMoreElements() )
		{
			String currName = (String)params.nextElement();

			paramNames.add(currName);
			paramsMap.put(currName, getParamValue(currName));
		}

		Enumeration attrs = request.getAttributeNames();
		while ( attrs.hasMoreElements() )
		{
			String currName = (String)attrs.nextElement();

			attrNames.add(currName);
			attrMap.put(currName, getAttribute(currName));
		}


		HttpSession session = request.getSession();
		Enumeration sessions = session.getAttributeNames();
		while ( sessions.hasMoreElements() )
		{
			String currName = (String)sessions.nextElement();

			sessionNames.add(currName);
			sessionMap.put(currName, session.getAttribute(currName));
		}

		properties = new HashMap<>();
		properties.put(ATTRS, attrMap);
		properties.put(PARAMS, paramsMap);
		properties.put(SESSION, sessionMap);

		SingleClient client = (SingleClient)attrMap.get(ClientInterceptor.ATTR_CLIENT);
		Set<SiteGroup> siteGroups = (Set<SiteGroup>)attrMap.get(ClientInterceptor.ATTR_SITEGROUPS);
		String language = Utils.defaultIfNull((String)attrMap.get(ClientInterceptor.ATTR_LANGUAGE), "en");
		TimeZone timeZone = (TimeZone)attrMap.get(ClientInterceptor.ATTR_TIMEZONE);

		properties.put(CLIENT, client);
		properties.put(SITEGROUPS, siteGroups);
		properties.put(LANGUAGE, language);
		properties.put(LANGUAGEDIR, ComponentOrientation.getOrientation(new Locale(language)).isLeftToRight() ? "ltr" : "rtl");
		properties.put(TIMEZONE, timeZone);

		properties.put(BASEURL, urlTokenizer.getBaseURL());
		properties.put(FULLBASEURL, urlTokenizer.getFullBaseURL());
		properties.put(FULLREQUESTURI, urlTokenizer.getFullRequestURI());
		properties.put(MESSAGEPREFIX, urlTokenizer.getMessagePrefix());

		if ( timeZone == null || getLocale() == null )
		{
			calendar = Calendar.getInstance();
		}
		else
		{
			calendar = Calendar.getInstance(timeZone, getLocale());
		}

		timeInMillis = calendar.getTimeInMillis();
		timeInSeconds = (int)(timeInMillis / 1000l);
	}

	public Calendar getCalendar()
	{
		return calendar;
	}

	public long getTimeInMillis()
	{
		return timeInMillis;
	}

	public int getTimeInSeconds()
	{
		return timeInSeconds;
	}

	public String getBaseURL()
	{
		return urlTokenizer.getBaseURL();
	}

	public String getFullBaseURL()
	{
		return urlTokenizer.getFullBaseURL();
	}

	public String getFullRequestURI()
	{
		return urlTokenizer.getFullRequestURI();
	}

	public String[] getParamNames()
	{
		return paramNames.toArray(new String[paramNames.size()]);
	}

	public String[] getAttrNames()
	{
		return attrNames.toArray(new String[attrNames.size()]);
	}

	public final Object getParamValue(String name)
	{
		String[] paramValues = getParameterValues(name);
		if ( paramValues != null && paramValues.length == 1)
		{
			return paramValues[0];
		}
		else // paramValues == null || paramValues.length > 1
		{
			return paramValues;
		}
	}

	public String[] getSessionNames()
	{
		return sessionNames.toArray(new String[sessionNames.size()]);
	}

	public Object getSessionValue(String name)
	{
		Object retVal = getSession().getAttribute(name);
		getLogger().trace("Getting session attribute " + name + " = " + retVal);

		return retVal;
	}

	public Object get(String name)
	{
		Object retVal = properties.get(name);
		getLogger().trace("Getting property with name " + name + " = " + retVal);

		return retVal;
	}

	public Log getLogger()
	{
		return logger;
	}

	public void setLogger(Log logger)
	{
		this.logger = logger;
	}
}
