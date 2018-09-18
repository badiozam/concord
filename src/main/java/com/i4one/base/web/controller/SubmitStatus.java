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

import com.i4one.base.core.Base;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.message.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the status of a form submission
 * 
 * @author Hamid Badiozamani
 */
public class SubmitStatus
{
	public enum ModelStatus { SUCCESSFUL, WRONG, PREVPLAYED, EXPIRED, PARTIAL, FAILED };

	private ModelStatus modelStatus;
	private transient Message titleMessage;
	private final Model model;
	private final List<ReturnType<?>> managerResults;

	public SubmitStatus(Model model, ModelStatus modelStatus)
	{
		this.titleMessage = new Message();

		this.model = model;
		this.modelStatus = modelStatus;

		managerResults = new ArrayList<>();
	}

	public SubmitStatus(Message titleMessage, Model model, ModelStatus modelStatus)
	{
		this.titleMessage = titleMessage;
		this.model = model;
		this.modelStatus = modelStatus;

		managerResults = new ArrayList<>();
	}

	public String getTitle()
	{
		return Base.getInstance().getMessageManager().buildMessage(titleMessage, model);
	}

	public Message getTitleMessage()
	{
		return titleMessage;
	}

	public void setTitleMessage(Message titleMessage)
	{
		this.titleMessage = titleMessage;
	}

	public ModelStatus getModelStatus()
	{
		return modelStatus;
	}

	public void setModelStatus(ModelStatus modelStatus)
	{
		this.modelStatus = modelStatus;
	}

	public boolean isSuccessful()
	{
		return modelStatus == ModelStatus.SUCCESSFUL;
	}

	public boolean isPrevPlayed()
	{
		return modelStatus == ModelStatus.PREVPLAYED;
	}

	public boolean isExpired()
	{
		return modelStatus == ModelStatus.EXPIRED;
	}

	public boolean isPartial()
	{
		return modelStatus == ModelStatus.PARTIAL;
	}

	public boolean isWrong()
	{
		return modelStatus == ModelStatus.WRONG;
	}

	public boolean isFailed()
	{
		return modelStatus == ModelStatus.FAILED;
	}

	public ReturnType<?> getManagerResult()
	{
		if ( managerResults.isEmpty() )
		{
			return null;
		}
		else
		{
			return managerResults.get(0);
		}
	}

	public void setManagerResult(ReturnType<?> managerResult)
	{
		managerResults.set(0, managerResult);
	}

	public List<ReturnType<?>> getManagerResults()
	{
		return Collections.unmodifiableList(managerResults);
	}

	public void setManagerResults(List<? extends ReturnType<?>> managerResults)
	{
		this.managerResults.clear();
		this.managerResults.addAll(managerResults);
	}
}
