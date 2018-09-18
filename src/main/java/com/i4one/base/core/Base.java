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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.i4one.base.dao.DaoManager;
import com.i4one.base.model.client.ClientOptionManager;
import com.i4one.base.model.client.SingleClientManager;
import com.i4one.base.model.message.MessageManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.validation.Validator;
import org.apache.velocity.app.VelocityEngine;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

/**
 * Initializes an instance of the application.
 *
 * @deprecated Use spring managed beans instead
 * @author Hamid Badiozamani
 */
@Service
public class Base extends BaseLoggable
{
	// Managers that are needed by model objects. Typically the model objects
	// are not instantiated through spring which means they're not autowirable
	//
	private SingleClientManager clientManager;
	private ClientOptionManager clientOptionManager;
	private MessageManager messageManager;

	// Needed by the RecordTypes to load themselves by serial number
	//
	private DaoManager daoManager;

	// The JSON converter for use in toJSONString()
	//
	private Gson gson;
	private ObjectMapper jacksonObjectMapper;

	// The velocity factory which we will use to get the velocity engine
	//
	private VelocityConfigurer velocityConfig;

	// The validator we'll use to validate object
	//
	private Validator validator;

	/**
	 * By having a static instance set here by Spring we avoid having to
	 * autowire basic dependencies every time by declaring fields in
	 * other classes
	 */
	private static Base instance;

	public static Base getInstance()
	{
		return instance;
	}

	public static void setInstance(Base instance)
	{
		Base.instance = instance;
	}

	public Base()
	{
		// The constructor here is public so that we can have Spring handle instantiation
		//
		super();

		// We have to make this method public so that Spring can instantiate the class
		//
		if ( instance != null )
		{
			getLogger().debug("Instance set in: ", new Throwable());
			throw new RuntimeException("Singleton Base already exists, please use the static getInstance() method instead.");
		}
		else
		{
			getLogger().debug("First instance set in: ", new Throwable());
			setInstance(this);
		}
	}

	@PostConstruct
	public void init()
	{
		getLogger().debug("Initializing instance");

		// Initialize here for use in toJSONString()
		//
		gson = new Gson();

		/*
		jacksonObjectMapper = new ObjectMapper();
		jacksonObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		jacksonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		*/

		// Reroute all java.util.logging calls over to SLF4J. This causes a major performance hit!
		// Needed for RestFB logging
		//
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		Logger.getLogger("").setLevel(Level.ALL);
	}

	public void destroy() throws SchedulerException
	{
		StdSchedulerFactory.getDefaultScheduler().shutdown();
	}

	public SingleClientManager getSingleClientManager()
	{
		return clientManager;
	}

	@Autowired
	public void setSingleClientManager(SingleClientManager readOnlySingleClientManager)
	{
		this.clientManager = readOnlySingleClientManager;
	}

	public ClientOptionManager getClientOptionManager()
	{
		return clientOptionManager;
	}

	@Autowired
	public void setClientOptionManager(ClientOptionManager readOnlyClientOptionManager)
	{
		this.clientOptionManager = readOnlyClientOptionManager;
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager readOnlyMessageManager)
	{
		this.messageManager = readOnlyMessageManager;
	}

	public DaoManager getDaoManager()
	{
		return daoManager;
	}

	@Autowired
	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	public Gson getGson()
	{
		return gson;
	}

	public ObjectMapper getJacksonObjectMapper()
	{
		return jacksonObjectMapper;
	}

	@Autowired
	public void setJacksonObjectMapper(ObjectMapper jacksonObjectMapper)
	{
		this.jacksonObjectMapper = jacksonObjectMapper;
	}

	public VelocityEngine getVelocityEngine()
	{
		return velocityConfig.getVelocityEngine();
	}

	@Autowired
	public void setVelocityConfig(VelocityConfigurer velocityConfig)
	{
		this.velocityConfig = velocityConfig;
	}
}
