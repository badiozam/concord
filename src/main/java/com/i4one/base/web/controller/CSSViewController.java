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

import com.i4one.base.model.message.Message;
import com.i4one.base.spring.ClasspathResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class CSSViewController extends BaseViewController implements ViewController
{
	private ClasspathResourceLoader resourceLoader;

	@RequestMapping(value = "**/cssc/{name}.cssc", produces="text/css")
	public void fetchCSS(@PathVariable("name") String name, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);
		Message message = getMessageManager().getMessage(model.getSingleClient(), name +".cssc", model.getLanguage());

		if ( message.exists() )
		{
			getLogger().debug("Rendering CSS w/ name " + name);
			initResponse(model, response, null);
			byte[] content = getMessageManager().buildMessage(message, model).getBytes();

			response.setContentType("text/css");
			response.setContentLength(content.length);
			response.getOutputStream().write(content);
		}
		else
		{
			// We bypass velocity for CSS views
			//
			String location = getResourceLoader().getPrefix() + "base/cssc/" + name + ".cssc";
			outputResource(location, response);
		}
	}

	@RequestMapping(value = "**/jsc/{name}.jsc", produces="text/javascript")
	public void fetchJS(@PathVariable("name") String name, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getLogger().debug("Received request for JS w/ name " + name);

		Model model = initRequest(request, null);
		Message message = getMessageManager().getMessage(model.getSingleClient(), name +".jsc",  model.getLanguage());

		if ( message.exists() )
		{
			getLogger().debug("Rendering JS w/ name " + name);

			response.setContentType("text/javascript");

			initResponse(model, response, null);
			response.getWriter().print(getMessageManager().buildMessage(message, model));
			/*
			byte[] content = message.buildMessage(model).getBytes();

			response.setContentType("text/javascript");
			response.setContentLength(content.length);
			response.getOutputStream().write(content);
			*/
		}
		else
		{
			// We bypass velocity for JS views
			//
			String location = getResourceLoader().getPrefix() + "base/js/" + name + ".jsc";
			outputResource(location, response);
		}
	}

	protected void outputResource(String location, HttpServletResponse response) throws IOException
	{
		getLogger().trace("Loading from class path w/ location = " + location + " using class loader " + getClass().getClassLoader());
		OutputStream os = null;
		try (final InputStream is = getClass().getClassLoader().getResourceAsStream(location))
		{
			os = response.getOutputStream();
			getLogger().trace("Received input stream " + is);

			int c;
			while (true)
			{
				c = is.read();
				os.write(c);
				if ( c < 0 )
				{
					break;
				}
			}
		}
		catch (IOException ex)
		{
			throw ex;
		}
		finally
		{
			try
			{
				os.close();
			}
			catch (IOException ex)
			{
				getLogger().debug("Could not close output stream", ex);
			}
		}
	}

	@Override
	public boolean isAuthRequired()
	{
		return false;
	}

	public ClasspathResourceLoader getResourceLoader()
	{
		return resourceLoader;
	}

	@Autowired
	public void setResourceLoader(ClasspathResourceLoader resourceLoader)
	{
		this.resourceLoader = resourceLoader;
	}

	@Override
	protected String getRedirKey()
	{
		throw new UnsupportedOperationException("No redirection available.");
	}
}
