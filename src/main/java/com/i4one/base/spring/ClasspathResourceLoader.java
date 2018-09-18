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

import com.i4one.base.core.Utils;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * A custom classpath resource loader that allows us to place our .vm
 * files in their own sub-directory while preserving the database mapping.
 * Currently, the getResourceStream(..) method is called while resolving
 * internal VTL includes.
 *
 * @author Hamid Badiozamani
 */
public class ClasspathResourceLoader extends org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
{
	private Log logger;

	private String prefix;
	private String packageSuffix;
	private String suffix;

	public ClasspathResourceLoader()
	{
		super();

		logger = LogFactory.getLog(getClass());
	}

	/**
	 * Resolves a resource stream based on a key. The key can be in one of several
	 * formats:
	 * <ol>
	 * 	<li>:[package]/[path] =&gt; Resolves to a resource by package name only.
	 * 		The computed key to be resolved by the superclass will be in the
	 * 		form of [prefix][package][packagesuffix][path][suffx].</li>
	 * 	<li>[package]/[messagekey] =&gt; Resolves to a resource by message key.
	 * 		The computed key to be resolved by the superclass will be in the
	 * 		form of [prefix][package][packagesuffix][messagekey][suffix].
	 * 		Intended for use to load a stream from the database.</li>
	 * 	<li>[client]/[language]/[package]/[messagekey] =&gt; Resolves to a resource
	 * 		by message key. Same format as above but with client and language
	 * 		parameters ignored. Intended for incoming web requests which would
	 * 		contain client and language in their URL paths.</li>
	 * </ol>
	 * 
	 * @param key The key to use to resolve the resource stream
	 * 
	 * @return An InputStream to the resource
	 */
	@Override
	public InputStream getResourceStream(String key)
	{
		getLogger().debug("Looking up resource stream with name " + key);

		if ( Utils.isEmpty(key))
		{
			throw new ResourceNotFoundException(getClass().getSimpleName() + ": Template key is empty");
		}
		else
		{
			String messageKey = "";
			String packageName = "";

			if ( key.startsWith(":"))
			{
				// Direct loading of resources from the classpath. Keys are
				// expected to be in the format <schema>/<path>
				//
				String[] keyOpts = key.split("/", 2);

				// We remove the ":" prefix
				//
				packageName = keyOpts[0].substring(1);
				messageKey = keyOpts[1];

				getLogger().debug("Parsed key as: package = " + packageName + ", messageKey = " + messageKey);

			}
			else
			{
				// The input key is split into three parts: client/language/key
				//
				String[] keyOpts = key.split("/", 4);
				if ( keyOpts.length < 2)
				{
					getLogger().debug("Key has invalid length " + keyOpts.length);
					throw new ResourceNotFoundException(getClass().getSimpleName() + ": Template key " + key + " does not specify client, language and package");
				}
				else if ( keyOpts.length < 4 )
				{
					packageName = keyOpts[0];
					messageKey = keyOpts[1];
	
					getLogger().debug("Parsed key as: package = " + packageName + ", messageKey = " + messageKey);
				}
				else
				{
					String clientName = keyOpts[0];
					String language = keyOpts[1];
					packageName = keyOpts[2];
					messageKey = keyOpts[3];
	
					getLogger().debug("Parsed key as: client = " + clientName + ", language = " + language + ", package = " + packageName + ", messageKey = " + messageKey);
				}
			}

			String computedKey = getPrefix() + packageName + getPackageSuffix() + messageKey + getSuffix();
			return getSuperResourceStream(computedKey);
		}
	}

	public InputStream getSuperResourceStream(String key)
	{
		getLogger().debug("Returning the superclass resource stream with key = " + key);
		return super.getResourceStream(key);
	}

	public Log getLogger()
	{
		return logger;
	}

	public void setLogger(Log logger)
	{
		this.logger = logger;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public String getPackageSuffix()
	{
		return packageSuffix;
	}

	public void setPackageSuffix(String packageSuffix)
	{
		this.packageSuffix = packageSuffix;
	}

}
