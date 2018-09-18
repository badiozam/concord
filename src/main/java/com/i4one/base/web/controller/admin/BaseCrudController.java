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
package com.i4one.base.web.controller.admin;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.web.controller.Model;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseCrudController<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseManagedTypeController<U, T>
{
	public static final String RESULT = "result";

	@Override
	public final Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		throw new UnsupportedOperationException("Please use the type-specific initRequest(..) method instead.");
	}
	
	@Override
	public final Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		throw new UnsupportedOperationException("Please use the type-specific initResponse(..) method instead.");
	}

	public Model initRequest(HttpServletRequest request, T modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		return model;
	}

	public Model initResponse(Model model, HttpServletResponse response, T modelAttribute)
	{
		Model retVal = super.initResponse(model, response, modelAttribute);

		return retVal;
	}

	protected Model createUpdateImpl(T item,
					Integer id,
					HttpServletRequest request, HttpServletResponse response)
	{
		getLogger().debug("createUpdate(..) called with id = " + id);
		if ( id != null )
		{
			getManager().loadById(item, id);
		}

		Model model = initRequest(request, item);

		return initResponse(model, response, item);
	}

	protected Model doUpdateImpl(T item, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, item);

		try
		{
			if ( item.exists() )
			{
				ReturnType<T> updateResult = getManager().update(item);
				item.copyFrom(updateResult.getPost());

				success(model, getMessageRoot() + ".update.success");
			}
			else
			{
				ReturnType<T> createResult = getManager().create(item);
				item.copyFrom(createResult.getPost());

				success(model, getMessageRoot() + ".create.success");
			}
		}
		catch (Errors errors)
		{
			fail(model, getMessageRoot() + ".update.failure", result, errors);
		}

		return initResponse(model, response, item);
	}

	protected ModelAndView cloneImpl(Integer id, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Model model = initRequest(request, null);

		T item = getManager().getById(Utils.defaultIfNull(id, 0));

		String redirURL = "index.html";
		if ( item.exists() )
		{
			ReturnType<T> cloneResult = getManager().clone(item);
			model.put(RESULT, cloneResult);

			// If we make it this far, we were successfull
			//
			success(model, getMessageRoot() + ".clone.success");
			redirURL = "update.html?id=" + cloneResult.getPost().getSer();
		}
		else
		{
			fail(model, getMessageRoot() + ".clone.failure", null, Errors.NO_ERRORS);
		}

		//response.sendRedirect(redirURL);
		return redirOnSuccess(model, null, null, null, redirURL, request, response);
	}

	@Deprecated
	protected ModelAndView removeImpl(Integer id, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		T item = getManager().getById(Utils.defaultIfNull(id, 0));

		Model model = initRequest(request, item);
		try
		{
			if ( item.exists() )
			{
				// The database should handle the cascading removal thru the use of foreign keys
				//
				item = getManager().remove(item);
	
				// If we make it this far, we were successfull
				//
				success(model, getMessageRoot() + ".remove.success");
			}
			else
			{
				fail(model, getMessageRoot() + ".remove.failure", null, Errors.NO_ERRORS);
			}
		}
		catch (Errors errors)
		{
			getLogger().debug("Caught errors {}", errors);
			fail(model, getMessageRoot() + ".remove.failure", null, errors);
		}

		return redirOnSuccess(model, null, null, null, getRemoveRedirURL(model, item), request, response);
	}

	protected String getRemoveRedirURL(Model model, T item)
	{
		return "index.html";
	}

	protected abstract String getMessageRoot();
}
