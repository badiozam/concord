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
package com.i4one.base.model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.SingleClientType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.user.User;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class Transaction extends BaseSingleClientType<TransactionRecord> implements SingleClientType<TransactionRecord>
{
	static final long serialVersionUID = 42L;

	public static final String SUCCESSFUL = "Successful";
	public static final String FAILED = "Failed";

	private transient User user;
	private transient Balance balance;
	private transient Transaction parent;

	private transient Set<Transaction> children;

	public Transaction()
	{
		super(new TransactionRecord());
	}

	protected Transaction(TransactionRecord delegate)
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

		if ( children == null )
		{
			children = new LinkedHashSet<>();
		}

		// The transactions may not always have a balance value
		//
		balance = new Balance();
		if ( getDelegate().getBalid() != null )
		{
			balance.resetDelegateBySer(getDelegate().getBalid());
		}

		if ( getDelegate().getParentid() != null )
		{
			parent = new Transaction();
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

		setUser(getUser());
		setBalance(getBalance());
		setParent(getParent());
	}

	@JsonIgnore
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

	public void setUser(User user)
	{
		this.user = user;

		getDelegate().setUserid(user.getSer());
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@JsonIgnore
	public Balance getBalance()
	{
		return getBalance(true);
	}

	public Balance getBalance(boolean doLoad)
	{
		if ( doLoad )
		{
			balance.loadedVersion();
		}

		return balance;
	}

	public void setBalance(Balance balance)
	{
		this.balance = balance;
		getDelegate().setBalid(balance.getSer());
	}

	public IString getDescr()
	{
		return getDelegate().getDescr();
	}

	public void setDescr(IString descr)
	{
		getDelegate().setDescr(descr);
	}

	public Integer getPrevBalance()
	{
		return getDelegate().getPrevbal();
	}

	public void setPrevBalance(int prevBal)
	{
		getDelegate().setPrevbal(prevBal);
	}

	public Integer getAmountTransacted()
	{
		return getDelegate().getBalxacted();
	}

	public void setAmountTransacted(int xacted)
	{
		getDelegate().setBalxacted(xacted);
	}
	
	public Integer getNewBalance()
	{
		return getDelegate().getNewbal();
	}

	public void setNewBalance(int newBal)
	{
		getDelegate().setNewbal(newBal);
	}

	public String getStatus()
	{
		return getDelegate().getStatus();
	}

	public void setStatus(String status)
	{
		getDelegate().setStatus(status);
	}
	
	public boolean isSuccessful()
	{
		return getStatus().equals(SUCCESSFUL);
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

	@JsonIgnore
	public Transaction getParent()
	{
		return getParent(true);
	}

	public Transaction getParent(boolean doLoad)
	{
		// We defer construction in order to avoid infinite recursion
		// of the parent object creating its own parent object in its
		// constructor/init() methods
		//
		if ( parent == null )
		{
			parent = new Transaction();
		}

		if ( doLoad )
		{
			parent.loadedVersion();
		}

		return parent;
	}

	public void setParent(Transaction parent)
	{
		this.parent = parent;
		getDelegate().setParentid(parent.getSer());
	}

	public Set<Transaction> getChildren()
	{
		return children;
	}

	public void setChildren(Collection<Transaction> children)
	{
		this.children.clear();
		this.children.addAll(children);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		// No unique keys
		//
		return toString();
	}
}
