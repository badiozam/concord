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
package com.i4one.base.web.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.core.Utils;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.user.User;
import com.i4one.base.web.HttpServletRequestWrapper;
import com.i4one.base.web.interceptor.ClientInterceptor;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Hamid Badiozamani
 */
public class Model extends HashMap<String, Object> implements Serializable
{
	/** The HTTP Request object. */
	public static final String REQUEST = "request";

	/**
	 * These declarations server to allow the model to contain some key
	 * request parameters. The reason these aren't their own methods
	 * here is because we want to be able to access the variables from
	 * VTL without having to call "$model.[key]"
	 */
	public static final String LANGUAGE = HttpServletRequestWrapper.LANGUAGE;
	public static final String LANGUAGEDIR = HttpServletRequestWrapper.LANGUAGEDIR;
	public static final String BASEURL = HttpServletRequestWrapper.BASEURL;
	public static final String MESSAGEPREFIX = HttpServletRequestWrapper.MESSAGEPREFIX;

	/**
	 * A reference to the view's model map, this is useful particularly for cases
	 * when a form backing object needs to be referenced by a message.
	 */
	public static final String VIEW_MODEL = "viewModel";

	/** A reference to self. */
	public static final String MODEL = "model";

	/** The title of the page. */
	public static final String TITLE = "title";

	/** Whether to use AJAX to load items on the page or not. */
	public static final String USING_AJAX = "usingAjax";

	/** Whether the incoming request is an AJAX request or not. */
	public static final String IS_AJAX_REQUEST = "isAjaxRequest";

	/** The source IP address. */
	public static final String SOURCE_IP = "sourceIP"; // "sourceIP";

	/** The current server time in milliseconds and seconds. */
	public static final String CURRENT_TIME = "currentTime";

	private static final long serialVersionUID = 42L;
	private transient Log logger;
	private transient MessageManager messageManager;

	private final transient HttpServletRequestWrapper requestWrapper;

	/**
	 * Constructs a model off of the given incoming request
	 * 
	 * @param request The incoming HTTP request
	 */
	public Model(HttpServletRequest request)
	{
		logger = LogFactory.getLog(getClass());

		if ( request instanceof HttpServletRequestWrapper )
		{
			requestWrapper = (HttpServletRequestWrapper)request;
		}
		else
		{
			requestWrapper = new HttpServletRequestWrapper(request);
		}
		put(REQUEST, requestWrapper);

		put(MODEL, this);
		put(TITLE, "Welcome");
		put(USING_AJAX, true);
		put(IS_AJAX_REQUEST, Utils.forceEmptyStr(request.getHeader("X-Requested-With")).equalsIgnoreCase("XMLHttpRequest"));
		put(SOURCE_IP, request.getRemoteAddr());

		put(CURRENT_TIME, new Date(requestWrapper.getTimeInMillis()));
		put(LANGUAGE, requestWrapper.get(HttpServletRequestWrapper.LANGUAGE));
		put(LANGUAGEDIR, requestWrapper.get(HttpServletRequestWrapper.LANGUAGEDIR));
		put(BASEURL, requestWrapper.get(HttpServletRequestWrapper.BASEURL));
		put(MESSAGEPREFIX, requestWrapper.get(HttpServletRequestWrapper.MESSAGEPREFIX));

		// These can't be null
		//
		put(UserAdminModelInterceptor.USER, new User());
		put(UserAdminModelInterceptor.ADMIN, new Admin());
	}

	public long getTimeInMillis()
	{
		return requestWrapper.getTimeInMillis();
	}

	public int getTimeInSeconds()
	{
		return requestWrapper.getTimeInSeconds();
	}

	@JsonIgnore
	public HttpServletRequestWrapper getRequest()
	{
		return requestWrapper;
	}

	public String getLanguage()
	{
		return Utils.forceEmptyStr((String) get(LANGUAGE));
	}

	public String getBaseURL()
	{
		return Utils.forceEmptyStr((String) get(BASEURL));
	}

	public Admin getAdmin()
	{
		return (Admin)get(UserAdminModelInterceptor.ADMIN);
	}

	public User getUser()
	{
		return (User)get(UserAdminModelInterceptor.USER);
	}

	public void setUser(User user)
	{
		put(UserAdminModelInterceptor.USER, user);
	}

	/**
	 * Builds a message given the current key and the message arguments. This
	 * model object is added to the argument map if no item named "model" exists
	 * already. If the message key does not exist for the current request's
	 * language, the first available language for the given client is used to
	 * lookup the message.
	 * 
	 * @param key The key of the message
	 * @param args The arguments to pass to the message builder
	 * 
	 * @return The compiled message
	 */
	public String buildMessage(String key, Object... args)
	{
		getLogger().trace("Getting message " + key + " with language " + getLanguage());
		Message currMessage = getMessageManager().getMessage(getSingleClient(), key, getLanguage());

		// Default to the first available language of the client if this one does not exist
		//
		if ( !currMessage.exists() )
		{
			currMessage = getMessageManager().getMessage(getSingleClient(), key, getSingleClient().getLanguageList().get(0));
		}

		Map<String, Object> argMap = Utils.makeMap(args);
		if ( !argMap.containsKey("model")) { argMap.put("model", this); }
		if ( !argMap.containsKey("request")) { argMap.put("request", getRequest()); }

		return getMessageManager().buildMessage(currMessage, argMap);
	}

	/**
	 * Builds a multi-language message given the current key and the message arguments.
	 * All of the languages available to the client are considered and added to the
	 * string. This model object is added to the argument map if none exists.
	 * 
	 * @param key The key of the message
	 * @param args The arguments to pass to the message builder
	 * 
	 * @return The compiled message
	 */
	public IString buildIMessage(String key, Object... args)
	{
		Map<String, Object> argMap = Utils.makeMap(args);
		if ( !argMap.containsKey("model"))
		{
			argMap.put("model", this);
		}

		IString retVal = new IString();
		SingleClient client = getSingleClient();
		for ( String lang : getSingleClient().getLanguageList() )
		{
			retVal.set(lang, getMessageManager().buildMessage(client, key, lang, argMap));
		}

		return retVal;
	}

	public SingleClient getSingleClient()
	{
		SingleClient retVal = ClientInterceptor.getSingleClient(getRequest());

		return retVal;
	}

	public SingleClient getSite()
	{
		return getSingleClient();
	}

	public Set<SiteGroup> getSiteGroups()
	{
		Set<SiteGroup> retVal = ClientInterceptor.getSiteGroups(getRequest());
		getLogger().trace("Returning site groups" + Utils.toCSV(retVal));

		return retVal;
	}

	public List<String> getSupportedLanguages()
	{
		return getSingleClient().getLanguageList();
	}

	@Override
	public Object get(Object keyObj)
	{
		if ( keyObj instanceof String )
		{
			String key = (String)keyObj;
			Object retVal = super.get(key);

			//getLogger().trace("Returning " + key + " = " + retVal);
			return retVal;
		}
		else
		{
			return null;
		}
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public final Log getLogger()
	{
		return logger;
	}

	public void setLogger(Log logger)
	{
		this.logger = logger;
	}
}
