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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * This class builds bean names using a [schema].[class] naming convention where
 * schema is the package name immediately following "com.i4one". Referenced as the
 * name-generator=".." parameter of the context:component-scan element in XML
 * configurations.
 * 
 * @author Hamid Badiozamani
 */
public class I4oneBeanNameGenerator extends AnnotationBeanNameGenerator implements BeanNameGenerator
{
	private final transient Logger logger;

	public I4oneBeanNameGenerator()
	{
		super();

		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
	protected String buildDefaultBeanName(BeanDefinition bd)
	{
		String canonicalName = bd.getBeanClassName();
		String[] nameComponents = canonicalName.split("\\.");

		String schema = "";
		if ( nameComponents.length > 2 )
		{
			// com.i4one.<schema>.model...<className>
			//
			schema = nameComponents[2];
		}

		String className = nameComponents[nameComponents.length - 1];
		getLogger().trace("canonicalName = " + canonicalName + " => " + schema + "." + className);

		return schema + "." + className;
	}

	protected Logger getLogger()
	{
		return logger;
	}

}
