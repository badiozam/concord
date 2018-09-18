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
package com.i4one.base.model.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.core.Base;
import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.SimpleParsingTerminable;
import com.i4one.base.model.message.Message;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import javax.mail.Session;
import static javax.mail.Session.getDefaultInstance;

/**
 * @author Hamid Badiozamani
 */
public class SingleClient extends BaseRecordTypeDelegator<SingleClientRecord> implements Client
{
	static final long serialVersionUID = 42L;

	private static final String VALID_EMAIL = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[a-z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\\b$";

	private transient SingleClient parent;
	private transient Locale locale;
	private transient IString baseURL;

	// We can load the root client from the database later, but essentially
	// this is what the object should look like
	//
	private static final SingleClient rootClient;
	static
	{
		rootClient = new SingleClient();
		rootClient.setName("ROOT");
		rootClient.getDelegate().setSer(1);
	}

	public SingleClient()
	{
		super(new SingleClientRecord());
	}

	protected SingleClient(SingleClientRecord delegate)
	{
		super(delegate);

	}

	@Override
	protected void init()
	{
		// We have to do this type of initialization here and set the parent to null due to circular
		// dependency issues which would cause infinite recursion. By deferring the parent reference's
		// initialization we avoid this problem.
		//
		SingleClientRecord parentRecord = new SingleClientRecord();
		if ( getDelegate().getParentid() != null && getDelegate().getParentid() > 0 )
		{
			parentRecord.setSer(getDelegate().getParentid());
			parent = new SingleClient(parentRecord);
		}
		else
		{
			parent = null;
		}

		baseURL = new IString();
		for (String lang : getLanguageList() )
		{
			baseURL.put(lang, "//" + getDelegate().getDomain() + "/" + getDelegate().getName() + "/" + lang + "/");
		}
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		if ( !getEmail().matches(VALID_EMAIL) )
		{
			errors.addError("email", new ErrorMessage("msg.base.client.invalidEmail", "Invalid e-mail address: $item.email", new Object[]{"item", this}));
		}

		if ( getDescr().isEmpty() )
		{
			errors.addError("descr", new ErrorMessage("msg.base.client.invalidDescr", "Site name cannot be empty", new Object[]{"item", this}));
		}

		return errors;
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setParent(getParent());
	}

	public String getOptionValue(String key)
	{
		return Base.getInstance().getClientOptionManager().getOptionValue(this, key);
	}

	public Message getMessage(String key)
	{
		return Base.getInstance().getMessageManager().getMessage(this, key);
	}

	public Message getMessage(String key, String language)
	{
		return Base.getInstance().getMessageManager().getMessage(this, key, language);
	}

	@Override
	public String getName()
	{
		return getDelegate().getName();
	}

	public void setName(String name)
	{
		getDelegate().setName(name);
	}

	@JsonIgnore
	public String getDescr()
	{
		return getDelegate().getDescr();
	}

	public void setDescr(String descr)
	{
		getDelegate().setDescr(descr);
	}

	@JsonIgnore
	public String getEmail()
	{
		return getDelegate().getEmail();
	}

	public void setEmail(String email)
	{
		getDelegate().setEmail(email);
	}

	@JsonIgnore
	public String getTimeZoneID()
	{
		return getDelegate().getTz();
	}

	public void setTimeZoneID(String timeZone)
	{
		getDelegate().setTz(timeZone);
	}

	@JsonIgnore
	public String getCountry()
	{
		return getDelegate().getCountry();
	}

	public void setCountry(String country)
	{
		getDelegate().setCountry(country);
	}

	@JsonIgnore
	public String getLanguages()
	{
		return getDelegate().getLanguages();
	}

	public void setLanguages(String languages)
	{
		getDelegate().setLanguages(languages);
	}

	@JsonIgnore
	public String getDomain()
	{
		return getDelegate().getDomain();
	}

	public void setDomain(String domain)
	{
		getDelegate().setDomain(domain);
	}

	public String getOperator()
	{
		return getDelegate().getOperator();
	}

	public void setOperator(String operator)
	{
		getDelegate().setOperator(operator);
	}

	public String getAddress()
	{
		return getDelegate().getAddress();
	}

	public void setAddress(String address)
	{
		getDelegate().setAddress(address);
	}

	public String getURL()
	{
		return getDelegate().getUrl();
	}

	public void setURL(String url)
	{
		getDelegate().setUrl(url);
	}

	public String getLogoURL()
	{
		return getDelegate().getLogourl();
	}

	public void setLogoURL(String logourl)
	{
		getDelegate().setLogourl(logourl);
	}

	public String getBaseURL(String lang)
	{
		return baseURL.get(lang);
	}

	public IString getBaseURL()
	{
		return baseURL;
	}

	/**
	 * Tests whether this object is the root client or not
	 *
	 * @return True if the client is the root, false otherwise
	 */
	@JsonIgnore
	public boolean isRoot()
	{
		return equals(getRoot());
	}

	@JsonIgnore
	public SingleClient getParent()
	{
		return getParent(true);
	}

	public SingleClient getParent(boolean doLoad)
	{
		if ( parent == null )
		{
			parent = new SingleClient(new SingleClientRecord());
		}
		else if ( doLoad )
		{
			Base base = Base.getInstance();
			if ( base == null )
			{
				throw new RuntimeException("Base is not initialized");
			}
			SingleClientManager clientManager = base.getSingleClientManager();
			parent = clientManager.getClient(parent.getSer());
		}

		return parent;
	}

	public void setParent(SingleClient parent)
	{
		this.parent = parent;
		getDelegate().setParentid(parent.getSer());
	}

	/**
	 * Gets the root node client
	 *
	 * @return The root node client
	 */
	public static SingleClient getRoot()
	{
		return rootClient;
	}

	/**
	 * Tests whether this client belongs to the given client or not
	 *
	 * @param client The client to test
	 *
	 * @return True if this object is a descendant of the given client
	 */
	public boolean belongsTo(SingleClient client)
	{
		if ( !exists() || !client.exists() )
		{
			// Sanity check here, if we or the incoming client don't exist, there's no check
			//
			getLogger().trace("belongsTo for " + this + " called with non-existent client");
			return false;
		}
		else
		{
			getLogger().trace("Testing whether " + this + " belongs to " + client);

			if ( equals(client) )
			{
				getLogger().trace(this + " is equal to " + client);
				return true;
			}
			else
			{
				// This ensures we don't infinitely recurse
				//
				if ( isRoot() )
				{
					getLogger().trace("Hit root node and didn't find " + client + " in any of our parents");
					return false;
				}
				else
				{
					// If our parent belongs to the incoming client
					// then we belong to it as well
					//
					return getParent().belongsTo(client);
				}
			}
		}
	}

	@Override
	public boolean isMemberOf(Client client)
	{
		getLogger().trace("Testing whether " + this + " is a member of " + client);

		if ( client instanceof SingleClient )
		{
			// We are a member of the client if we belong to it
			//
			SingleClient singleClient = (SingleClient)client;
			return belongsTo(singleClient);
		}
		else if ( client instanceof SiteGroup )
		{
			SiteGroup siteGroup = (SiteGroup)client;
			return siteGroup.contains(this);
		}
		else
		{
			// Consider throwing an exception here since there should be
			// no other kinds?
			//
			return false;
		}
	}

	public Locale getLocale()
	{
		if ( locale == null )
		{
			String country = getCountry();
			String language = getLanguageList().get(0);

			locale = new Locale(language, country);
		}

		return locale;
	}

	/**
	 * Get a list of the supported languages for this client
	 * 
	 * @return The supported languages for this client
	 */
	@JsonIgnore
	public List<String> getLanguageList()
	{
		return Arrays.asList(Utils.forceEmptyStr(getLanguages()).split(","));
	}

	public void setLanguageList(List<String> languages)
	{
		setLanguages(Utils.toCSV(languages.toArray()));
	}

	public TimeZone getTimeZone()
	{
		return TimeZone.getTimeZone(getTimeZoneID());
	}

	@JsonIgnore
	public DateFormat getDateFormat()
	{
		return getDateFormat(getLocale());
	}

	public DateFormat getDateFormat(Locale locale)
	{
		//DateFormat retVal = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
		DateFormat retVal = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		retVal.setTimeZone(getTimeZone());

		return retVal;
	}

	@JsonIgnore
	public Calendar getCalendar()
	{
		return Calendar.getInstance(getTimeZone(), getLocale());
	}

	@JsonIgnore
	public Session getMailSession()
	{
		List<ClientOption> mailOptions = Base.getInstance().getClientOptionManager().getOptions(this, "mail.", SimplePaginationFilter.NONE);

		Properties props = new Properties();
		mailOptions.stream().forEach((option) ->
		{
			props.put(option.getKey(), option.getValue());
		});

		// Standard properties
		//
		props.put("mail.store.protocol", "smtp");
		props.put("mail.transport.protocol", "smtp");

		return getDefaultInstance(props);
	}
	
	@Deprecated
	public int parseToSeconds(String startTime) throws ParseException
	{
		return parseToSeconds(startTime, getLocale());
	}

	@Deprecated
	public String toDateString(int seconds, Locale locale)
	{
		return SimpleParsingTerminable.toDateString(seconds, locale, getTimeZone());
	}

	@Deprecated
	public int parseToSeconds(String timeStr, Locale locale) throws ParseException
	{
		return SimpleParsingTerminable.parseToSeconds(timeStr, locale, getTimeZone());
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getName();
	}

	@Override
	protected boolean equalsInternal(RecordTypeDelegator<SingleClientRecord> right)
	{
		if ( right instanceof SingleClient )
		{
			SingleClient rightClient = (SingleClient)right;
			return getName().equals(rightClient.getName());
		}
		else
		{
			return false;
		}
	}
}
