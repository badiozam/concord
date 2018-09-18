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
package com.i4one.base.model.jobs;

import com.i4one.base.core.SimpleLogger;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.web.RequestState;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseQuartzJob extends QuartzJobBean
{
	private RequestState requestState;
	private final SimpleLogger logger;

	public BaseQuartzJob()
	{
		logger = new SimpleLogger(this);
		logger.getLog().debug("Instantiating " + this);
	}

	@Override
	protected void executeInternal(JobExecutionContext jec) throws JobExecutionException
	{
		getLogger().debug("Executing {}", this);
		getRequestState().reset();

		// XXX: Need to set up a "background" admin with limited permissions
		// who will be running this process
		//
		Admin admin = new Admin();
		admin.setSer(2);
		admin.loadedVersion();

		getRequestState().setAdmin(admin);

		doExecuteInternal(jec);

		// Log the administrator out
		//
		getRequestState().setAdmin(new Admin());
	}
	
	protected abstract void doExecuteInternal(JobExecutionContext jec) throws JobExecutionException;

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}

	public final Logger getLogger()
	{
		return logger.getLog();
	}
}
