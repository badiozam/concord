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


import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.Errors;
import javax.annotation.PostConstruct;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import static org.quartz.SimpleScheduleBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class EmailBlastJobScheduler extends BaseLoggable
{
	private Scheduler scheduler;

	public EmailBlastJobScheduler()
	{
		getLogger().debug("Constructing " + this);
	}

	// Consider having this bean be manually instantiated to avoid having it
	// schedule things in the web server
	//
	@PostConstruct
	public void init()
	{
		getLogger().debug("Initializing " + this);

		// Data to be passed to each new job
		//
		JobDataMap jdm = new JobDataMap();

		JobDetail emailBlastJob = JobBuilder.newJob(EmailBlastScheduler.class).withIdentity("emailBlastJob").usingJobData(jdm).build();
		Trigger emailBlastJobTrigger = TriggerBuilder.newTrigger().withIdentity("emailBlastJob").startNow().withSchedule(simpleSchedule().withIntervalInSeconds(10).repeatForever()).build();

		try
		{
			if ( !getScheduler().checkExists(emailBlastJob.getKey()) )
			{
				getScheduler().scheduleJob(emailBlastJob, emailBlastJobTrigger);
			}
		}
		catch (SchedulerException ex)
		{
			throw new Errors(ex);
		}
	}

	public Scheduler getScheduler()
	{
		return scheduler;
	}

	@Autowired
	public void setScheduler(Scheduler scheduler)
	{
		this.scheduler = scheduler;
	}
}
