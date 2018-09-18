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

import com.i4one.base.core.LRUMap;
import com.i4one.base.core.Utils;
import java.lang.annotation.Annotation;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * This candidate resolver matches the {@link I4oneBeanNameGenerator I4oneBeanNameGenerator}
 * naming convention in order to resolve autowired components. The name of the parameter of
 * a setter method is used to generate the class name and the schema is derived from the
 * package of the type of the component.
 * 
 * @author Hamid Badiozamani
 * @see I4oneBeanNameGenerator
 */
public class I4oneAutowireCandidateResolver extends QualifierAnnotationAutowireCandidateResolver implements AutowireCandidateResolver
{
	private static final int MAX_BEANNAME_CACHE_SIZE = 100;

	private final transient Logger logger;
	private final DefaultListableBeanFactory beanFactory;
	private final Map<DependencyDescriptor, String> beanNameCache;

	public I4oneAutowireCandidateResolver(DefaultListableBeanFactory beanFactory)
	{
		super();

		logger = LoggerFactory.getLogger(getClass());
		this.beanFactory = beanFactory;
		beanNameCache = new LRUMap<>(MAX_BEANNAME_CACHE_SIZE);
	}

	protected String getSchema(String canonicalName)
	{
		String[] nameComponents = canonicalName.split("\\.");

		String schema = "";
		if ( nameComponents.length > 2 )
		{
			// com.i4one.<schema>.model...<className>
			//
			schema = nameComponents[2];
		}

		return schema;
	}

	@Override
	public Object getSuggestedValue(DependencyDescriptor descriptor)
	{
		Object qualifierValue = super.getSuggestedValue(descriptor);

		if ( qualifierValue != null )
		{
			// There was an @Qualifier which takes precedence
			//
			return qualifierValue;
		}
		else
		{
			String beanName = getBeanName(descriptor);

			getLogger().trace("Bean name to use is {}", beanName);
			if ( beanFactory.containsBean(beanName) )
			{
				return beanFactory.getBean(beanName);
			}
			else
			{
				return null;
			}
		}
	}

	protected String getBeanName(DependencyDescriptor descriptor)
	{
		String beanName = beanNameCache.get(descriptor);

		if ( beanName == null )
		{
			getLogger().trace("Determining suggested autowire candidate value from {}, {}", descriptor.getDependencyName(), descriptor.getDependencyType());
	
			Annotation[] annotations = descriptor.getAnnotations();

			getLogger().trace("Found {} annotations: {}", annotations.length, Utils.toCSV(annotations) );
	
			if ( annotations.length == 0 )
			{
				MethodParameter methodParam = descriptor.getMethodParameter();
				annotations = methodParam.getMethodAnnotations();
	
				getLogger().trace("Found {} method annotations: {}", annotations.length, Utils.toCSV(annotations) );
			}
	
			for ( Annotation annotation : annotations )
			{
				if ( isQualifier(annotation.annotationType()) )
				{
					beanName = (String)AnnotationUtils.getValue(annotation);
					getLogger().trace("Annotation {} is a qualifer with value {}", annotation, beanName);
					break;
				}
			}
	
			// If we couldn't find an @Qualifier annotation fallback to the naming convention
			//
			if ( beanName == null )
			{
				Class clazz = descriptor.getDependencyType();
				String schema = getSchema(clazz.getCanonicalName());
				String simpleName = Character.toUpperCase(descriptor.getDependencyName().charAt(0)) + descriptor.getDependencyName().substring(1);
		
				beanName = schema + "." + simpleName;
			}

			beanNameCache.put(descriptor, beanName);
		}

		return beanName;
	}

	public Logger getLogger()
	{
		return logger;
	}

}
