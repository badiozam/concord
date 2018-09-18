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
package com.i4one.base.web.interceptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.web.ModelManager;
import com.i4one.base.web.URLTokenizer;
import com.i4one.base.web.controller.admin.AdminViewController;
import com.i4one.base.web.controller.admin.auth.AdminAuthController;
import com.i4one.base.web.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author Hamid Badiozamani
 */
public class AdminAuthInterceptor extends HandlerInterceptorAdapter
{
	private final Log logger;

	private String httpsDomain;
	private String unauthView;
	private ModelManager modelManager;
	private ObjectMapper objectMapper;

	public AdminAuthInterceptor()
	{
		logger = LogFactory.getLog(getClass());
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		if ( getLogger().isDebugEnabled() )
		{
			getLogger().trace("preHandle for " + request.getContextPath() + ", requestURI = " + request.getRequestURI() + " with handler " + handler.getClass() + " and request " + request.getClass());
		}

		AdminViewController controller = null;
		if ( handler instanceof AdminViewController )
		{
			controller = (AdminViewController) handler;
		}
		else if ( handler instanceof HandlerMethod )
		{
			if ( ((HandlerMethod)handler).getBean() instanceof AdminViewController )
			{
				controller = (AdminViewController)((HandlerMethod)handler).getBean();
			}
		}

		if ( controller != null )
		{
			getLogger().trace("Found: " + controller + " with auth required = " + controller.isAuthRequired());

			if ( !request.isSecure() )
			{
				String hostName = "www";
				if ( request.getServerName().contains(getHttpsDomain()))
				{
					// Extract the first hostname from the URL to use when redirecting
					//
					String[] urlParts = request.getServerName().split("\\.", 3);
					getLogger().debug("Found " + urlParts.length + " from " + request.getServerName());
					if ( urlParts.length >= 3 )
					{
						hostName = urlParts[0];
					}
				}

				String redirURL = "https://" + hostName + "." + getHttpsDomain() + request.getRequestURI() + "?" + Utils.forceEmptyStr(request.getQueryString());
				getLogger().debug("Redirecting to secure " + redirURL);

				response.sendRedirect(redirURL);

				return false;
			}
			else
			{
				if ( controller.isAuthRequired())
				{
					String requestURI = request.getRequestURI();
					getLogger().trace("Auth required for " + requestURI + " at context path " + request.getContextPath());
	
					Admin currAdmin = UserAdminModelInterceptor.getAdmin(request);
					if ( !currAdmin.exists() )
					{
						// Admin is not logged in, need to determine whether this is a RESTful request or HTML
						// 
						if ( requestURI.endsWith(".json"))
						{
							// JSON request, write out the error
							//
							Errors errors = new Errors(new ErrorMessage("msg.base.admin.auth.unauth", "Please log in first", new Object[]{}));
							errors.setLanguage(ClientInterceptor.getLanguage(request));
							errors.setSingleClient(ClientInterceptor.getSingleClient(request));
	
							getObjectMapper().writeValue(response.getOutputStream(), errors);
						}
						else
						{
							URLTokenizer urlTokenizer = new URLTokenizer(request);
		
							String redirURL = urlTokenizer.getBaseURL() + "/" + getUnauthView();
							getLogger().debug("Admin " + currAdmin + " is not logged in, redirecting to '" + redirURL + "'");
		
							// This is where we send the admin back to after they've successfully authenticated
							//
							Persistence.putObject(request, response, AdminAuthController.ADMIN_REDIRTO_KEY, requestURI);
							response.sendRedirect(redirURL);
						}
	
						return false;
					}
					else
					{
						// XXX: Needs more security here. See UserAuthInterceptor
						//
					}
				}
				else if ( !(controller instanceof AdminAuthController) )
				{
					Persistence.putObject(request, response, AdminAuthController.ADMIN_REDIRTO_KEY, null);
				}
			}
		}

		return super.preHandle(request, response, handler);
	}

	public String getHttpsDomain()
	{
		return httpsDomain;
	}

	public void setHttpsDomain(String httpsDomain)
	{
		this.httpsDomain = httpsDomain;
	}

	public String getUnauthView()
	{
		return unauthView;
	}

	public void setUnauthView(String unauthView)
	{
		this.unauthView = unauthView;
	}

	public Log getLogger()
	{
		return logger;
	}

	public ModelManager getModelManager()
	{
		return modelManager;
	}

	@Autowired
	public void setModelManager(ModelManager modelManager)
	{
		this.modelManager = modelManager;
	}

	public ObjectMapper getObjectMapper()
	{
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}

}
