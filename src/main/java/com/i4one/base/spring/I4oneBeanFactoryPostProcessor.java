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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * This post-processor adds an {@link I4oneAutowireCandidateResolver autowire candidate resolver}
 * to the bean factory which advises the bean resolver to try the [schema].[classname] naming convention.
 * 
 * @author Hamid Badiozamani
 * @see I4oneAutowireCandidateResolver
 */
public class I4oneBeanFactoryPostProcessor extends BaseLoggable implements BeanFactoryPostProcessor
{
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory clbf) throws BeansException
	{
		if ( clbf instanceof DefaultListableBeanFactory )
		{
			DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)clbf;
			beanFactory.setAutowireCandidateResolver(new I4oneAutowireCandidateResolver(beanFactory));
		}
	}
}
