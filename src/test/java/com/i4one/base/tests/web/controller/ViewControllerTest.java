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
package com.i4one.base.tests.web.controller;

import com.i4one.base.model.message.MessageManager;
import com.i4one.base.tests.core.BaseUserManagerTest;
import com.i4one.base.web.ModelManager;
import com.i4one.base.web.controller.BaseViewController;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Hamid Badiozamani
 */
@Ignore
public abstract class ViewControllerTest extends BaseUserManagerTest
{
	private BaseViewController viewController;

	// These managers are used by all BaseViewController objects
	//
	private MessageManager messageManager;
	private ModelManager modelManager;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}

	public boolean initRequest(MockHttpServletRequest request) throws Exception
	{
		return initRequest(request, new MockHttpServletResponse());
	}

	public boolean initRequest(MockHttpServletRequest request, MockHttpServletResponse response) throws Exception
	{
		boolean retVal = preHandle(request, response);

		super.setMockRequest(request);
		super.setMockResponse(response);

		return retVal;
	}

	/**
	 * Pre-handle a given request using the interceptor chain
	 *
	 * @param request The incoming request to preHandle
	 * @param response The outgoing response that was set
	 *
	 * @return The ModelAndView object returned by the testHandler
	 * @throws java.lang.Exception
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Map<String, HandlerMapping> handlerMappings = applicationContext.getBeansOfType(HandlerMapping.class);
		for ( HandlerMapping handlerMapping : handlerMappings.values() )
		{
			HandlerExecutionChain chain = handlerMapping.getHandler(request);

			if ( chain != null )
			{
				for (HandlerInterceptor interceptor : chain.getInterceptors() )
				{
					getLogger().debug("preHandle calling on interceptor: " + interceptor);
					boolean doNext = interceptor.preHandle(request, response, getViewController());
					if ( !doNext )
					{
						// Handler interceptor put a stop to the rest of the chain
						//
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Pre-handle a given request using the interceptor chain
	 *
	 * @param request The incoming request to post handle
	 * @param response The outgoing response that was set
	 * @param modelAndView The model and view returned by the handler
	 * @throws java.lang.Exception
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) throws Exception
	{
		/*
		HandlerMapping handlerMapping = (HandlerMapping) super.applicationContext.getBean("handlerMapping");
		HandlerExecutionChain chain = handlerMapping.getHandler(request);

		for (HandlerInterceptor interceptor : chain.getInterceptors() )
		{
			interceptor.postHandle(request, response, chain.getHandler(), modelAndView);
		}
		*/
		Map<String, HandlerMapping> handlerMappings = applicationContext.getBeansOfType(HandlerMapping.class);
		for ( HandlerMapping handlerMapping : handlerMappings.values() )
		{
			HandlerExecutionChain chain = handlerMapping.getHandler(request);

			if ( chain != null )
			{
				for (HandlerInterceptor interceptor : chain.getInterceptors() )
				{
					interceptor.postHandle(request, response, chain.getHandler(), modelAndView);
				}
			}
		}
	}

	public BaseViewController getViewController()
	{
		return viewController;
	}

	public void setViewController(BaseViewController viewController)
	{
		this.viewController = viewController;
		this.viewController.setMessageManager(getMessageManager());
		this.viewController.setModelManager(getModelManager());
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
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
}
