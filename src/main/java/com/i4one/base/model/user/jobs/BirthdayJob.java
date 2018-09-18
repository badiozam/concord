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

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.jobs.BaseQuartzJob;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.model.user.UserSettings;
import java.util.Calendar;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
@DisallowConcurrentExecution 
public class BirthdayJob extends BaseQuartzJob
{
	private SingleClientManager singleClientManager;
	private UserManager userManager;

	public BirthdayJob()
	{
		super();

		getLogger().debug("Instantiating " + this);
	}

	@Override
	protected void doExecuteInternal(JobExecutionContext jec) throws JobExecutionException
	{
		// We need to get all users in the database, and for each one whose
		// birthday it is, go through each client they're a member of and
		// credit/e-mail them accordingly.
		//
		JobDataMap jdm = jec.getJobDetail().getJobDataMap();
		Object clientObj = jdm.get("client");

		if ( clientObj instanceof SingleClient)
		{
			SingleClient client = (SingleClient)clientObj;
			getRequestState().setSingleClient(client);

			UserSettings userSettings = getUserManager().getSettings(client);
			if ( userSettings.isBirthdayEnabled() )
			{
				Calendar cal = client.getCalendar();
				int birthmm = cal.get(Calendar.MONTH) + 1;
				int birthdd = cal.get(Calendar.DAY_OF_MONTH);
	
				// We use the time zone of the client through which the user
				// initially signed up to find his/her birthday. Ths avoids
				// the problem of a user changing his timezone on his birthday
				// thus potentially skipping his birthday.
				//
				SimplePaginationFilter birthdayPagination = new SimplePaginationFilter();
				birthdayPagination.setQualifier("birthmm", birthmm);
				birthdayPagination.setQualifier("birthdd", birthdd);
	
				getLogger().debug("Birthdays enabled for {} on {}/{}, processing", client, birthmm, birthdd);
				int thisYear = cal.get(Calendar.YEAR);
				for ( User user : getUserManager().getAllMembers(client, birthdayPagination) )
				{
					// Process the birthday
					//
					getUserManager().processBirthday(user, thisYear);
				}
				getLogger().debug("Done processing birthdays for {} on {}/{}", client, birthmm, birthdd);
			}
			else
			{
				getLogger().debug("Skipping birthday for {}: disabled", client);
			}
		}
		else
		{
			getLogger().debug("Client not set properly: {} from jdm keys {}", clientObj, jdm.getKeys());
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

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}
}
