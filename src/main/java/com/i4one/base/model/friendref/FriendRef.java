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
package com.i4one.base.model.friendref;

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.ClientType;
import com.i4one.base.model.UserType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import java.util.Date;

/**
 * @author Hamid Badiozamani
 */
public class FriendRef extends BaseSingleClientType<FriendRefRecord> implements ClientType,UserType
{
	private transient User user;
	private transient User friend;

	public FriendRef()
	{
		super(new FriendRefRecord());
	}

	public FriendRef(FriendRefRecord delegate)
	{
		super(delegate);
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

		if ( friend == null )
		{
			friend = new User();
		}

		if ( getDelegate().getFriendid() != null )
		{
			friend.resetDelegateBySer(getDelegate().getFriendid());
		}
	}

	public User makeOrGetFriend()
	{
		if ( getDelegate().getFriendid() != null )
		{
			return getFriend();
		}
		else
		{
			User newFriend = new User();
			newFriend.setFirstName(getFirstName());
			newFriend.setLastName(getLastName());
			newFriend.setEmail(getEmail());
			newFriend.setClient(getClient(false));

			return newFriend;
		}
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setUser(getUser());
		setFriend(getFriend());
	}
	
	public String getEmail()
	{
		return getDelegate().getEmail();
	}

	public void setEmail(String email)
	{
		getDelegate().setEmail(email);
	}

	public String getFirstName()
	{
		return getDelegate().getFirstname();
	}

	public void setFirstName(String firstname)
	{
		getDelegate().setFirstname(firstname);
	}

	public String getLastName()
	{
		return getDelegate().getLastname();
	}

	public void setLastName(String lastname)
	{
		getDelegate().setLastname(lastname);
	}

	public String getMessage()
	{
		return getDelegate().getMessage();
	}
	
	public void setMessage(String message)
	{
		getDelegate().setMessage(message);
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

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getUserid() + "-" + getEmail() + "-" + getDelegate().getFriendid();
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

	@Override
	public void setClient(SingleClient client)
	{
		// Through which client is the user being referred?
		//
		setClientInternal(client);
	}

	public User getFriend()
	{
		return getFriend(true);
	}

	public User getFriend(boolean doLoad)
	{
		if ( doLoad )
		{
			friend.loadedVersion();
		}

		return friend;
	}

	public void setFriend(User friend)
	{
		this.friend = friend;
		if ( friend.exists() )
		{
			getDelegate().setFriendid(friend.getSer());
		}
		else
		{
			getDelegate().setFriendid(null);
		}
	}

	/**
	 * Whether the referral has been completed and the friend who has been
	 * referred has signed up.
	 * 
	 * @return True if the referral is complete, false otherwise
	 */
	public boolean hasRegistered()
	{
		return getDelegate().getFriendid() != null;
	}

	public boolean isTrickleEligible(FriendRefSettings settings, int currentTimeSeconds)
	{
		return ( hasRegistered() &&
			(settings.getTrickleDuration() == 0 ||
				trickleExpiresTimeSeconds(settings) > currentTimeSeconds ));
	}

	public int trickleExpiresTimeSeconds(FriendRefSettings settings)
	{
		return getFriend().getCreateTimeSeconds() + settings.getTrickleDuration();
	}

	public Date trickleExpiresTime(FriendRefSettings settings)
	{
		return Utils.toDate(trickleExpiresTimeSeconds(settings));
	}
}
