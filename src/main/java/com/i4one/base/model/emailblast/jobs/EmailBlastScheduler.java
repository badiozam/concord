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
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.emailblast.EmailBlastManager;
import com.i4one.base.model.emailblast.EmailBlastStatus;
import com.i4one.base.model.jobs.BaseQuartzJob;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import java.util.Date;
import java.util.Set;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class traverses all child clients and schedules any live e-mail blasts
 * to be sent out.
 * 
 * @author Hamid Badiozamani
 */
@DisallowConcurrentExecution 
public class EmailBlastScheduler extends BaseQuartzJob
{
	/** Number of seconds that can pass before skipping the sending of a periodic e-mail */
	private static final int SKIP_DELAY = 60 * 60 * 2;

	private SingleClientManager singleClientManager;
	private EmailBlastManager emailBlastManager;

	public EmailBlastScheduler()
	{
		super();
	}

	@Override
	protected void doExecuteInternal(JobExecutionContext jec) throws JobExecutionException
	{
		Scheduler scheduler = jec.getScheduler();

		getLogger().debug("Looking for live e-mail blasts as of {}", Utils.currentDateTime());

		int currTime = Utils.currentTimeSeconds();
		for ( SingleClient client : getSingleClientManager().getAllClients(SingleClient.getRoot(), SimplePaginationFilter.NONE) )
		{
			getRequestState().setSingleClient(client);

			Set<EmailBlast> liveBlasts = getEmailBlastManager().getLive(client, currTime, SimplePaginationFilter.NONE);
			for ( EmailBlast currEmailBlast : liveBlasts )
			{
				JobDataMap jdm = new JobDataMap();
				jdm.put("emailblastid", currEmailBlast.getSer());
				jdm.put("client", client);

				// See if the job is eligible for scheduling
				//
				if ( currEmailBlast.getStatus() != EmailBlastStatus.SENDING &&
					currEmailBlast.getStatus() != EmailBlastStatus.DO_PAUSE &&
					currEmailBlast.getStatus() != EmailBlastStatus.PAUSED &&
					currEmailBlast.getStatus() != EmailBlastStatus.ERROR)
				{
					if ( currEmailBlast.isRecurrent() )
					{
						// This e-mail blast gets sent out periodically, check
						// to see if it's eligible to be sent out now
						//
						try
						{
							// We get the next available time this e-mail can be sent out AFTER the last time it
							// started sending out. If this e-mail is still being sent we won't get this far since
							// the conditional above only runs this for e-mail blasts that aren't sending.
							//
							//Date nextRun = generator.next(Utils.toDate(currEmailBlast.getSendStartTimeSeconds()));
							Date nextRun = currEmailBlast.getNextRecurrence();
							if ( nextRun.before(Utils.toDate(currTime)))
							{
								// We could run into a situation where we're always trying to catch up if the e-mail blast
								// takes longer to send out than the interval given. We have this check to skip sending out
								// if it's been longer than a set number of seconds since this e-mail blast was supposed to
								// go out.
								//
								if ( nextRun.after(Utils.toDate(currTime + SKIP_DELAY)))
								{
									getLogger().info("Missed window of opportunity for {}. Current time is {}, skip delay is {}, eligible run is {}", currEmailBlast, currTime, SKIP_DELAY, (int)(nextRun.getTime() / 1000L));
	
									currEmailBlast.setSendStartTimeSeconds(currTime);
									getEmailBlastManager().updateAttributes(currEmailBlast);
								}
								else
								{
									getLogger().debug("Scheduling recurring e-mail blast {}.", currEmailBlast);

									// Here within the window of opportunity
									//
									scheduleEmailBlast(scheduler, currEmailBlast, jdm);
								}
							}
						}
						catch (IllegalArgumentException iae)
						{
							getLogger().error("Invalid schedule '{}' given.", currEmailBlast.getSchedule(), iae);

							currEmailBlast.setStatus(EmailBlastStatus.ERROR);
							getEmailBlastManager().updateAttributes(currEmailBlast);
						}
					}
					else
					{
						getLogger().debug("Scheduling one-time e-mail blast {}.", currEmailBlast);

						// One-time e-mail blast
						//
						scheduleEmailBlast(scheduler, currEmailBlast, jdm);
					}
				}
				else
				{
					getLogger().debug("Skipping scheduling of {} since it appears to be already taken care of", currEmailBlast);
				}
			}
		}
	}

	private void scheduleEmailBlast(Scheduler scheduler, EmailBlast emailBlast, JobDataMap jdm)
	{
		JobDetail emailBlastJob = JobBuilder
			.newJob(EmailBlastJob.class)
			.withIdentity(emailBlast.uniqueKey())
			.usingJobData(jdm).build();

		Trigger emailBlastJobTrigger = newTrigger()
			.startNow()
			.build();

		try
		{
			scheduler.scheduleJob(emailBlastJob, emailBlastJobTrigger);
		}
		catch (SchedulerException se)
		{
			getLogger().error("Could not start job {} for e-mail blast {}", emailBlastJob, emailBlast, se);
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

	public EmailBlastManager getEmailBlastManager()
	{
		return emailBlastManager;
	}

	@Autowired
	public void setEmailBlastManager(EmailBlastManager emailBlastManager)
	{
		this.emailBlastManager = emailBlastManager;
	}
}
