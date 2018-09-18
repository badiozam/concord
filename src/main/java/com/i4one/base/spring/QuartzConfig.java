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
package com.i4one.base.spring;

import com.i4one.base.core.BaseLoggable;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Hamid Badiozamani
 */
@Configuration
public class QuartzConfig extends BaseLoggable
{
	private DataSource dataSource;
	private PlatformTransactionManager transactionManager;
	private ApplicationContext applicationContext;

	@Bean
	public SchedulerFactoryBean quartzScheduler()
	{
		SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();

		quartzScheduler.setDataSource(getDataSource());
		quartzScheduler.setTransactionManager(getTransactionManager());
		quartzScheduler.setOverwriteExistingJobs(true);
		quartzScheduler.setSchedulerName("i4one");

		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(getApplicationContext());
		quartzScheduler.setJobFactory(jobFactory);

		quartzScheduler.setQuartzProperties(buildQuartzProperties());
		quartzScheduler.setAutoStartup(false);

		return quartzScheduler;
	}

	@Bean
	public Properties buildQuartzProperties()
	{
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocation(new ClassPathResource("/quartz.properties"));

		Properties properties = null;
		try
		{
			factory.afterPropertiesSet();
			properties = factory.getObject();
		}
		catch (IOException e)
		{
			getLogger().error("Cannot load quartz.properties.", e);
		}

		return properties;
	}

	public DataSource getDataSource()
	{
		return dataSource;
	}

	@Autowired
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public PlatformTransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	public ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

}
