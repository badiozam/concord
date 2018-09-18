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
package com.i4one.base.model.usermessage;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.SimpleSortableType;
import com.i4one.base.model.SortableType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.terminable.BaseTerminableSiteGroupType;
import com.i4one.base.model.manager.terminable.TerminableSiteGroupType;
import com.i4one.base.model.manager.terminable.UserTerminableClientType;
import com.i4one.base.model.user.User;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public class UserMessage extends BaseTerminableSiteGroupType<UserMessageRecord> implements TerminableSiteGroupType<UserMessageRecord>,UserTerminableClientType<UserMessageRecord>,SortableType<UserMessageRecord>
{
	static final long serialVersionUID = 42L;

	private final transient SortableType sortable;

	private transient User user;

	public UserMessage()
	{
		super(new UserMessageRecord());

		sortable = new SimpleSortableType(this);
	}

	protected UserMessage(UserMessageRecord delegate)
	{
		super(delegate);
		sortable = new SimpleSortableType(this);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( user == null )
		{
			user = new User();
		}
		user.resetDelegateBySer(getDelegate().getUserid());
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getMessage().isBlank() )
		{
			retVal.addError(new ErrorMessage("message", "msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".message.empty", "Message cannot be empty or entirely comprised of white space", new Object[]{"item", this}));
		}

		if ( !Utils.isEmpty(getUser().getUsername()) && !getUser().exists() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".user.dne", "User '$item.user.username' not found", new Object[]{"item", this}));
		}

		return retVal;
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setUser(getUser());
	}

	@Override
	public User getUser()
	{
		return getUser(true);
	}

	public User getUser(boolean doLoad)
	{
		if ( doLoad )
		{
			user.loadedVersion();
		}

		return user;
	}

	@Override
	public void setUser(User user)
	{
		this.user = user;
		getDelegate().setUserid(user.getSer());
	}

	public IString getMessage()
	{
		return getDelegate().getMessage();
	}

	public void setMessage(IString name)
	{
		getDelegate().setMessage(name);
	}

	@Override
	public int getOrderWeight()
	{
		return sortable.getOrderWeight();
	}

	@Override
	public void setOrderWeight(int orderWeight)
	{
		sortable.setOrderWeight(orderWeight);
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		if ( super.fromCSVInternal(csv) )
		{
			sortable.fromCSVList(csv);

			String email = csv.get(0);
			if ( !Utils.isEmpty(email))
			{
				getUser().setEmail(email);
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected StringBuilder toCSVInternal(boolean header)
	{
		StringBuilder retVal = super.toCSVInternal(header);
		retVal.append(sortable.toCSV(header));

		if ( header )
		{
			// XXX: Needs to be i18n
			retVal.append(Utils.csvEscape("User")).append(",");

			return new StringBuilder(retVal.toString().replaceAll("Title", "Message"));
		}
		else
		{
			if ( getUser().exists() )
			{
				retVal.append("\"").append(Utils.csvEscape(getUser().getEmail())).append("\",");
			}
			else
			{
				retVal.append("\"").append("").append("\",");
			}
		}

		return retVal;
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getUserid() + "-" + getDelegate().getMessage().hashCode() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
	}
}
