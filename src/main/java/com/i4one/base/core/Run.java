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
package com.i4one.base.core;

import com.i4one.base.spring.I4oneApplicationContextInitializer;
import java.io.File;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * The command line runtime operation. The main(..) method of this class is
 * to be executed from the command line and specified as such in the appropriate
 * JAR build. The main(..) method then performs the following tasks:
 * 
 * <ol>
 * 	<li>Sets up a "logs" directory relative to the current directory for
 * all log files.</li>
 * 	<li>Insantiates an application context using the jarApplicationContext.xml
 * 	<li>Starts the Quartz scheduler which starts running any jobs that may have
 * been scheduled during the start up phase of the application context.</li>
 * </ol>
 * 
 * The workflow for background processing is to first create a bean that is
 * instantiated by Spring. This bean, during its post-construction phase will
 * schedule jobs with the default scheduler. These jobs are then started by this
 * class calling the start() method on the default scheduler.
 * 
 * @author Hamid Badiozamani
 */
public class Run
{
	public static void main(String[] args) throws Exception
	{
		// Needed for log4j logging
		//
		System.setProperty("catalina.base", ".");
		File logDir = new File("logs");
		if ( !logDir.exists() )
		{
			logDir.mkdir();
		}

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "classpath:jar/jarApplicationContext.xml" }, false);

		// Registers autowire name resolution
		//
		I4oneApplicationContextInitializer i4oneInit = new I4oneApplicationContextInitializer();
		i4oneInit.initialize(context);

		context.registerShutdownHook();
		context.refresh();

		SchedulerFactoryBean quartzScheduler = context.getBean(SchedulerFactoryBean.class);
		quartzScheduler.start();
	}
}
