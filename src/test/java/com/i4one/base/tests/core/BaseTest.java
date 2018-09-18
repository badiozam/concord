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
package com.i4one.base.tests.core;

import com.i4one.base.spring.I4oneApplicationContextInitializer;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.commons.logging.Log;
import org.springframework.test.context.ContextConfiguration;
import org.junit.Ignore;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
/**
 * This is the base class for all tests that loads the context configuration
 * for JUnit
 *
 * @author Hamid Badiozamani
 */
@ContextConfiguration(value = "classpath:test-context.xml", initializers = I4oneApplicationContextInitializer.class)
@WebAppConfiguration
@Ignore
public abstract class BaseTest extends AbstractTransactionalJUnit4SpringContextTests
{ 
	private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

	public Log getLogger()
	{
		return logger;
	}

	public Validator getValidator()
	{
		// We'll only be using a single validator implementation (Hibernate's) so we
		// don't need to declare this in the applicationContext file
		//
		return factory.getValidator();
	}
}
