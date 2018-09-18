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
package com.i4one.base.spring;

import com.i4one.base.core.Utils;
import com.i4one.base.web.controller.Model;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.velocity.VelocityView;

/**
 * VelocityView subclass that exposes the Velocity model to the i4one Model
 * object for use in creating custom error messages for a given client. The
 * main purpose is to expose the RequestAttribute object that is used
 * frequently in forms but that is not set as part of our i4one Model object.
 *
 * @author Hamid Badiozamani
 */
public class VelocityThemedView extends VelocityView
{
	@Override
	protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
	{
		Object i4oneModelObj = model.get(Model.MODEL);
		if ( i4oneModelObj != null && i4oneModelObj instanceof Model )
		{
			// We add the view's model to the i4one model to allow access
			// to form backing objects, errors and other elements that are
			// not otherwise accessible in our model.
			//
			Model i4oneModel = (Model)i4oneModelObj;
			i4oneModel.put(Model.VIEW_MODEL, model);

			getLogger().trace("Adding view model to i4one Model: " + i4oneModel.keySet());
		}
		else
		{
			getLogger().trace("i4one Model not found in: " + model);
		}

		VelocityContext context = new VelocityContext(model, VelocityConfigurer.getVelocityToolContext());

		return context;
	}

	@Override
	protected RequestContext createRequestContext(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
	{
		RequestContext context = super.createRequestContext(request, response, model);
		return context;
	}

	@Override
	protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception
	{
		getLogger().trace("Expose helpers called with model = " + Utils.toCSV(model.keySet()));
		model.put(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, new ThemedRequestContext(request, null, getServletContext(), model));
	}

	public final Log getLogger()
	{
		return logger;
	}

}
