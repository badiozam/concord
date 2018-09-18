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

import com.i4one.base.model.MessageKeySettings;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.message.Message;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class FriendRefSettings extends MessageKeySettings
{
	private SingleClient client;

	private int maxFriends;
	private float tricklePercentage;
	private int trickleDuration;
	private IString defaultMessage;

	private Set<BalanceTrigger> balanceTriggers;

	private final IString name;

	public FriendRefSettings()
	{
		enabled = true;

		// XXX: Needs to be set properly
		name = new IString("Friend Referral");
	}

	@Override
	public IString getNameSingle()
	{
		return name;
	}

	@Override
	public IString getNamePlural()
	{
		return name;
	}

	public SingleClient getClient()
	{
		return client;
	}

	public void setClient(SingleClient client)
	{
		this.client = client;
	}

	public int getMaxFriends()
	{
		return maxFriends;
	}

	public void setMaxFriends(int maxFriends)
	{
		this.maxFriends = maxFriends;
	}

	public float getTricklePercentage()
	{
		return tricklePercentage;
	}

	public void setTricklePercentage(float tricklePercentage)
	{
		this.tricklePercentage = tricklePercentage;
	}

	public int getTrickleDuration()
	{
		return trickleDuration;
	}

	public void setTrickleDuration(int trickleDuration)
	{
		this.trickleDuration = trickleDuration;
	}

	public Set<BalanceTrigger> getBalanceTriggers()
	{
		return balanceTriggers;
	}

	public void setBalanceTriggers(Set<BalanceTrigger> balanceTriggers)
	{
		this.balanceTriggers = balanceTriggers;
	}

	public List<Message> getDefaultMessages(SingleClient client, String key)
	{
		return getMessages(client, key, defaultMessage);
	}

	public void setDefaultMessages(List<Message> messages)
	{
		setDefaultMessage(messagesToIString(messages));
	}

	public IString getDefaultMessage()
	{
		return defaultMessage;
	}

	public void setDefaultMessage(IString defaultMessage)
	{
		this.defaultMessage = defaultMessage;
	}
}
