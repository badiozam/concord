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
package com.i4one.base.model.adminhistory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.client.SingleClient;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class AdminHistory extends BaseSingleClientType<AdminHistoryRecord>
{
	static final long serialVersionUID = 42L;

	private transient Admin admin;
	private transient JsonParser jsonParser;

	private transient AdminHistory parent;
	private transient Set<AdminHistory> children;
	//private transient ObjectMapper objectMapper;

	public AdminHistory()
	{
		super(new AdminHistoryRecord());
	}

	protected AdminHistory(AdminHistoryRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( admin == null )
		{
			admin = new Admin();
		}
		admin.resetDelegateBySer(getDelegate().getAdminid());

		jsonParser = new JsonParser();
		/*
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		*/
		if ( children == null )
		{
			children = new LinkedHashSet<>();
		}

		if ( getDelegate().getParentid() != null )
		{
			parent = new AdminHistory();
			if ( !getDelegate().getParentid().equals(getSer()) )
			{
				parent.resetDelegateBySer(getDelegate().getParentid());
			}
		}

	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setAdmin(getAdmin());
		setParent(getParent());
	}

	public String getBefore()
	{
		return getDelegate().getBefore();
	}

	public void setBefore(RecordTypeDelegator<?> before)
	{
		getDelegate().setBefore(before.toJSONString());
	}

	public String getBeforeParsed() throws IOException
	{
		return formatJson(jsonParser.parse(getBefore()));
		//return formatJson(getBefore());
	}

	public String getAfter()
	{
		return getDelegate().getAfter();
	}

	public void setAfter(RecordTypeDelegator<?> after)
	{
		getDelegate().setAfter(after.toJSONString());
	}

	public String getAfterParsed() throws IOException
	{
		return formatJson(jsonParser.parse(getAfter()));
		//return formatJson(getAfter());
	}

	/*
	public String formatJson(String jsonItem) throws IOException
	{
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = objectMapper.readValue(jsonItem, Map.class);

		if ( jsonMap.containsKey("value"))
		{
			return String.valueOf(jsonMap.get("value"));
		}
		else
		{
			return objectMapper.writeValueAsString(jsonMap);
		}
	}
	*/

	/**
	 * Given a JSON string, returns the 'value' element or the whole json string with
	 * pretty printing
	 * 
	 * @param jsonElement The item to parse
	 * 
	 * @return The 'value' element or a beautified version of the string
	 */
	public String formatJson(JsonElement jsonElement)
	{
		if ( jsonElement.isJsonObject() )
		{
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if ( jsonObject.has("value") && !jsonObject.get("value").isJsonNull() )
			{
				return jsonObject.get("value").getAsString();
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(jsonElement);
	}

	public String getDescr()
	{
		return getDelegate().getDescr();
	}

	public void setDescr(String descr)
	{
		getDelegate().setDescr(descr);
	}

	public String getFeature()
	{
		return getDelegate().getFeature();
	}

	public void setFeature(String feature)
	{
		getDelegate().setFeature(feature);
	}

	public int getFeatureID()
	{
		return getDelegate().getFeatureid();
	}

	public void setFeatureID(RecordTypeDelegator<?> before, RecordTypeDelegator<?> after)
	{
		if ( after.exists() )
		{
			setFeatureID(after.getSer());
		}
		else
		{
			setFeatureID(before.getSer());
		}
	}

	public void setFeatureID(int featureId)
	{
		getDelegate().setFeatureid(featureId);
	}

	public String getAction()
	{
		return getDelegate().getAction();
	}

	public void setAction(String action)
	{
		getDelegate().setAction(action);
	}

	public Date getTimeStamp()
	{
		return Utils.toDate(getTimeStampSeconds());
	}

	public int getTimeStampSeconds()
	{
		return getDelegate().getTimestamp();
	}

	public void setTimeStampSeconds(int timestamp)
	{
		getDelegate().setTimestamp(timestamp);
	}

	public String getSourceIP()
	{
		return getDelegate().getSourceip();
	}

	public void setSourceIP(String ip)
	{
		getDelegate().setSourceip(ip);
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	public Admin getAdmin()
	{
		return getAdmin(true);
	}

	public Admin getAdmin(boolean doLoad)
	{
		if ( doLoad )
		{
			admin.loadedVersion();
		}

		return admin;
	}

	public void setAdmin(Admin admin)
	{
		this.admin = admin;
		getDelegate().setAdminid(admin.getSer());
	}

	public AdminHistory getParent()
	{
		return getParent(true);
	}

	public AdminHistory getParent(boolean doLoad)
	{
		if ( parent == null )
		{
			parent = new AdminHistory();
		}

		if ( doLoad )
		{
			parent.loadedVersion();
		}

		return parent;
	}

	public void setParent(AdminHistory parent)
	{
		this.parent = parent;
		getDelegate().setParentid(parent.getSer());
	}

	public Set<AdminHistory> getChildren()
	{
		return children;
	}

	public void setChildren(Collection<AdminHistory> children)
	{
		this.children.clear();
		this.children.addAll(children);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getFeature() + "-" + getFeatureID() + "-" + getTimeStamp();
	}
}
