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
package com.i4one.base.model.emailblast.jobs;

import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.emailblast.EmailBlastManager;
import com.i4one.base.model.emailblast.EmailBlastSender;
import com.i4one.base.model.jobs.BaseQuartzJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class EmailBlastJob extends BaseQuartzJob
{
	private EmailBlastManager emailBlastManager;
	private EmailBlastSender emailBlastSender;

	public EmailBlastJob()
	{
		super();
	}

	@Override
	protected void doExecuteInternal(JobExecutionContext jec) throws JobExecutionException
	{
		JobDataMap jdm = jec.getMergedJobDataMap();

		getRequestState().setSingleClient((SingleClient) jdm.get("client"));
		int currTime = getRequestState().getRequest().getTimeInSeconds();

		getLogger().debug("Looking up e-mail blast {} for client {} in thread {}", jdm.getIntValue("emailblastid"), jdm.get("client"), Thread.currentThread());
		EmailBlast emailBlast = getEmailBlastManager().getById(jdm.getIntValue("emailblastid"));

		if ( emailBlast.exists() && emailBlast.isLive(currTime))
		{
			getLogger().debug("Sending e-mail blast started at {}", Utils.currentDateTime());

			getEmailBlastSender().sendEmailBlast(emailBlast);

			getLogger().debug("Sending e-mail blast ended at {}", Utils.currentDateTime());
		}
		else
		{
			getLogger().warn("Email blast job {} is not available for execution at {}, terminating.", emailBlast, currTime);
		}
	}

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}

	public EmailBlastSender getEmailBlastSender()
	{
		return emailBlastSender;
	}

	@Autowired
	public void setEmailBlastSender(EmailBlastSender emailBlastSender)
	{
		this.emailBlastSender = emailBlastSender;
	}
}
