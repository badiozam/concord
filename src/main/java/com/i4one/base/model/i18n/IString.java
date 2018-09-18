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
package com.i4one.base.model.i18n;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.i4one.base.core.Base;
import com.i4one.base.core.SimpleLogger;
import com.i4one.base.core.Utils;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.slf4j.Logger;

/**
 * @author Hamid Badiozamani
 */
public class IString extends HashMap<String, String> implements Map<String, String>,Cloneable,Serializable
{
	static final long serialVersionUID = 42L;

	/** An empty IString */
	public static final IString BLANK = new IString();

	private transient SimpleLogger logger;
	private transient Function<String, String> postProcessor;

	public IString()
	{
		super();

		init();
	}

	/**
	 * Convenience constructor which accepts arguments in the form of
	 * (language1, value, language2, value, ...) and initializes the
	 * object to the mappings.
	 * 
	 * @param keyValuePairs The key-value pairs to construct the map from
	 */
	public IString(String... keyValuePairs)
	{
		super();
		init();

		putAll(Utils.makeMap((Object[]) keyValuePairs));
	}

	/**
	 * Constructs a new IString from the given JSON string representation.
	 * 
	 * @param jsonStr The JSON string to de-serialize
	 */
	public IString(String jsonStr)
	{
		super();
		init();

		if ( jsonStr != null && jsonStr.startsWith("{"))
		{
			putAll(Base.getInstance().getGson().fromJson(jsonStr, HashMap.class));
		}
		else
		{
			put("en", jsonStr);
		}
	}

	public IString(IString right)
	{
		super();
		init();

		this.putAll(right);
	}

	private void init()
	{
		logger = new SimpleLogger(this);

		// By default there's no post processing
		//
		postProcessor = ((item) -> { return item; } );
	}

	@Override
	public Object clone()
	{
		return new IString(this);
	}

	/**
	 * Determines whether there was at least one entry that's not empty.
	 * Note that if a single entry is not empty, this method will return
	 * false. This method is to be used to ensure that there is always a
	 * fallback string to display.
	 * 
	 * @return True if all of the strings were empty, false if any were not.
	 */
	public boolean isBlank()
	{
		for ( Map.Entry<String, String> entry : entrySet() )
		{
			if ( !Utils.isEmpty(entry.getValue()) )
			{
				//getLogger().debug("isBlank: value '" + entry.getValue() + "' for language '" + entry.getKey() + "' is not empty, returning false");
				return false;
			}
		}

		// All of them were empty
		//
		return true;
	}

	/**
	 * Determines whether all entries are not blank or not. If a single entry
	 * is blank this method returns false. This method is used to ensure that
	 * no entry is left blank.
	 * 
	 * @return True if no entries are blank, false if there are no entries or
	 * 	if one of the entries is blank.
	 */
	public boolean isAllNotBlank()
	{
		if ( isEmpty() )
		{
			return false;
		}

		for ( Map.Entry<String, String> entry : entrySet() )
		{
			if ( Utils.isEmpty(entry.getValue()) )
			{
				// This one's empty which means not all were non-empty
				//
				return false;
			}
		}

		// None of them were empty
		//
		return true;
	}

	public IString appendAll(String str)
	{
		entrySet().forEach( (item) -> { item.setValue(item.getValue() + str);});
		return this;
	}

	public IString append(IString str)
	{
		entrySet().forEach( (item) -> { item.setValue(item.getValue() + str.get(item.getKey()));});
		return this;
	}

	@Override
	public String get(Object key)
	{
		return postProcessor.apply(getInternal(key));
	}

	protected String getInternal(Object key)
	{
		if ( containsKey(key) )
		{
			return Utils.forceEmptyStr(super.get(key));
		}
		else if ( !isEmpty() )
		{
			return Utils.forceEmptyStr(entrySet().iterator().next().getValue());
		}
		else
		{
			return null;
		}
	}

	/**
	 * Set the value for a given language
	 * 
	 * @param lang The language
	 * @param value The value for the language
	 */
	public void set(String lang, String value)
	{
		put(lang, Utils.forceEmptyStr(value));
	}

	public void setPostProcessor(Function<String, String> postProcessor)
	{
		this.postProcessor = postProcessor;
	}

	public Set<String> getLanguages()
	{
		return Collections.unmodifiableSet(keySet());
	}

	@Override
	public String toString()
	{
		try
		{
			Collection<String> values = values();
			if ( values.size() == 1 )
			{
				// In cases where there is only one entry, there's no
				// need to convert the whole object to JSON
				//
				return values.iterator().next();
			}
			else
			{
				//getLogger().debug("Converting toString from ", new Throwable());
				//return Base.getInstance().getGson().toJson(this);
				return Base.getInstance().getJacksonObjectMapper().writeValueAsString(this);
			}
		}
		catch (JsonProcessingException ex)
		{
			getLogger().info("Could not convert " + super.toString() + " to JSON format: ", ex);
			return ex.toString();
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		init();
	}

	protected Logger getLogger()
	{
		return logger.getLog();
	}
}