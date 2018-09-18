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
package com.i4one.base.model.report;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hamid Badiozamani
 */
public class JsonReportTypeIdResolver extends TypeIdResolverBase implements TypeIdResolver
{
	private transient Logger logger;

	@Override
	public void init(JavaType bt)
	{
		super.init(bt);

		logger = LoggerFactory.getLogger(getClass());
		getLogger().debug("Init called with " + bt);
	}

	@Override
	public String idFromValue(Object o)
	{
		return idFromValueAndType(o, o.getClass());
	}

	@Override
	public String idFromValueAndType(Object o, Class<?> type)
	{
		getLogger().trace("idFromValueAndType called with object " + o + " and type " + type);
		return type.getCanonicalName();
	}

	@Override
	public JavaType typeFromId(DatabindContext context, String id)
	{
		JavaType retVal = super.typeFromId(context, id);

		getLogger().debug("typeFromId called with context " + context + " and id " + id + " returning " + retVal);
		return retVal;
	}

	@Override
	public JsonTypeInfo.Id getMechanism()
	{
		return JsonTypeInfo.Id.NAME;
	}

	public Logger getLogger()
	{
		return logger;
	}

}
