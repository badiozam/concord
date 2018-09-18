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
package com.i4one.base.model.message;

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.terminable.SimpleParsingTerminable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hamid Badiozamani
 */
public class Message extends BaseSingleClientType<MessageRecord>
{
	static final long serialVersionUID = 42L;

	public Message()
	{
		super(new MessageRecord());
	}

	protected Message(MessageRecord delegate)
	{
		super(delegate);
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@NotBlank
	public String getKey()
	{
		return getDelegate().getKey();
	}

	public void setKey(String key)
	{
		// Make sure to trim any spaces for consistency
		//
		getDelegate().setKey(key.trim());
	}

	@NotBlank
	public String getLanguage()
	{
		return getDelegate().getLanguage();
	}

	public void setLanguage(String language)
	{
		getDelegate().setLanguage(language);
	}

	/**
	 * Gets the raw value of the message without any parsing
	 * 
	 * @return The unparsed value of the message
	 */
	public String getValue()
	{
		return getDelegate().getValue();
	}

	public void setValue(String value)
	{
		getDelegate().setValue(value);
	}

	public void setValue(IString value)
	{
		if ( value.containsKey(getLanguage()))
		{
			getDelegate().setValue(value.get(getLanguage()));
		}
		else
		{
			getDelegate().setValue(value.values().iterator().next());
		}
	}

	public Integer getUpdateTimeSeconds()
	{
		return getDelegate().getUpdateTime();
	}

	public void setUpdateTimeSeconds(Integer updateTime)
	{
		getDelegate().setUpdateTime(updateTime);
	}

	public Date getUpdateTime()
	{
		return Utils.toDate(getUpdateTimeSeconds());
	}

	public void getUpdateTime(String startTimeStr) throws ParseException
	{
		setUpdateTimeSeconds(SimpleParsingTerminable.parseToSeconds(startTimeStr, getClient().getLocale(), getClient().getTimeZone()));
	}

	public static Map<String, Message> messagesFromIString(SingleClient client, String key, IString istr)
	{
		return messagesFromIString(client, key, istr, "en");
	}

	public static Map<String, Message> messagesFromIString(SingleClient client, String key, IString istr, String defLang)
	{
		Map<String, Message> retVal = new HashMap<>();
		for (String lang : istr.getLanguages())
		{
			Message message = new Message();
			message.setClient(client);
			message.setKey(key);
			message.setLanguage(lang);
			message.setValue(istr.get(lang));

			retVal.put(lang, message);
		}

		if ( !retVal.containsKey(defLang) )
		{
			// We need to have the default language entry
			//
			Message message = new Message();
			message.setClient(client);
			message.setKey(key);
			message.setLanguage(defLang);
			message.setValue("");

			retVal.put(defLang, message);
		}

		return retVal;
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getKey() + "-" + getLanguage();
	}
}
