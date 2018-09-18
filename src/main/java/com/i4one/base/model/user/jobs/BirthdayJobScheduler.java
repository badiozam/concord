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
package com.i4one.base.model.user.jobs;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.client.ClientOptionManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.jobs.BaseQuartzJob;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.Calendar;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
@DisallowConcurrentExecution 
public class BirthdayJobScheduler extends BaseQuartzJob
{
	private SingleClientManager singleClientManager;
	private ClientOptionManager clientOptionManager;

	private static final String BIRTHDAYJOB_ENABLED = "userManager.birthdayJob.enabled";

	public BirthdayJobScheduler()
	{
		super();
	}

	@Override
	protected void doExecuteInternal(JobExecutionContext jec) throws JobExecutionException
	{
		Scheduler scheduler = jec.getScheduler();

		// We need to get all users in the database, and for each one whose
		// birthday it is, go through each client they're a member of and
		// credit/e-mail them accordingly.
		//
		for ( SingleClient client : getSingleClientManager().getAllClients(SingleClient.getRoot(), SimplePaginationFilter.NONE))
		{
			boolean birthdayJobEnabled = Utils.defaultIfNaB( getClientOptionManager().getOptionValue(client, BIRTHDAYJOB_ENABLED), false);

			if ( birthdayJobEnabled )
			{
				// Data to be passed to each new job
				//
				JobDataMap jdm = new JobDataMap();
				jdm.put("client", client);

				Calendar cal = client.getCalendar();
				int birthmm = cal.get(Calendar.MONTH) + 1;
				int birthdd = cal.get(Calendar.DAY_OF_MONTH);

				String jobName = "birthdayJob-" + client.getName() + ":" + birthmm + "/" + birthdd;
				JobDetail birthDayJob = JobBuilder.newJob(BirthdayJob.class)
					.withIdentity(jobName).usingJobData(jdm).build();
		
				Trigger birthdayJobTrigger = TriggerBuilder.newTrigger()
					.startNow()
					.build();
		
				try
				{
					if ( !scheduler.checkExists(birthDayJob.getKey()) )
					{
						getLogger().debug("Scheduling job {}", birthDayJob);
						scheduler.scheduleJob(birthDayJob, birthdayJobTrigger);
					}
					else
					{
						getLogger().debug("Job already exists in the scheduler, skipping");
					}
				}
				catch (SchedulerException ex)
				{
					throw new Errors(ex);
				}
			}
		}
	}

	public SingleClientManager getSingleClientManager()
	{
		return singleClientManager;
	}

	@Autowired
	public void setSingleClientManager(SingleClientManager singleClientManager)
	{
		this.singleClientManager = singleClientManager;
	}

	public ClientOptionManager getClientOptionManager()
	{
		return clientOptionManager;
	}

	@Autowired
	public void setClientOptionManager(ClientOptionManager clientOptionManager)
	{
		this.clientOptionManager = clientOptionManager;
	}

}
