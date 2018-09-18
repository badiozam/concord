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
import com.i4one.base.dao.terminable.TerminableClientRecordType;
import com.i4one.base.model.manager.terminable.TerminableClientType;
import com.i4one.base.web.controller.Model;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseTerminableCrudController<U extends TerminableClientRecordType, T extends TerminableClientType<U>> extends BaseClientTypeCrudController<U,T>
{
	@Override
	public Model initRequest(HttpServletRequest request, T modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute != null )
		{
			// Set the date/time parsing locale to that of the incoming request
			//
			modelAttribute.setParseLocale(model.getRequest().getLocale());

			boolean isRequestGet = request.getMethod().equals("GET");
			if ( !modelAttribute.exists() && isRequestGet )
			{
				Calendar cal = model.getRequest().getCalendar();

				modelAttribute.setStartTimeSeconds(Utils.getStartOfDay(cal));
				modelAttribute.setEndTimeSeconds(Utils.getEndOfDay(cal));
			}
		}

		return model;
	}

	@Override
	protected String getRemoveRedirURL(Model model, T item)
	{
		return "index.html?display=" + (item.isPast(model.getTimeInSeconds()) ? "past" : item.isLive(model.getTimeInSeconds()) ? "live" : "future");
	}

}
