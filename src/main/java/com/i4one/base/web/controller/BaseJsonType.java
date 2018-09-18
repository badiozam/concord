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
import com.i4one.base.web.controller.SubmitStatus.ModelStatus;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseJsonType
{
	private final Model model;

	public BaseJsonType(Model model)
	{
		this.model = model;
	}

	@JsonIgnore
	protected SubmitStatus getSubmitStatus()
	{
		return (SubmitStatus) model.get(BaseViewController.MODELSTATUS);
	}

	public String getMessage()
	{
		SubmitStatus submitStatus = getSubmitStatus();
		if ( submitStatus != null )
		{
			submitStatus.setTitleMessage( model.getMessageManager().getMessage(model.getSingleClient(), submitStatus.getTitleMessage().getKey()));
			return submitStatus.getTitle();
		}
		else
		{
			return "Error: could not process request, please try again later.";
		}
	}

	public ModelStatus getStatus()
	{
		SubmitStatus submitStatus = getSubmitStatus();
		if ( submitStatus != null )
		{
			return submitStatus.getModelStatus();
		}
		else
		{
			return null;
		}
	}

	@JsonIgnore
	public Model getModel()
	{
		return model;
	}
}
