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
package com.i4one.base.model.emailblast.fragment;

import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.ForeignKey;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.emailblast.EmailBlastRecord;

/**
 * @author Hamid Badiozamani
 */
public class EmailBlastFragment extends BaseRecordTypeDelegator<EmailBlastFragmentRecord>
{
	static final long serialVersionUID = 42L;

	private transient ForeignKey<EmailBlastRecord, EmailBlast> emailBlastFk;

	public EmailBlastFragment()
	{
		super(new EmailBlastFragmentRecord());
	}

	protected EmailBlastFragment(EmailBlastFragmentRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		emailBlastFk = new ForeignKey<>(this,
			getDelegate()::getEmailblastid,
			getDelegate()::setEmailblastid,
			() -> { return new EmailBlast(); });
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		emailBlastFk.actualize();
	}

	public int getStatus()
	{
		return getDelegate().getStatus();
	}

	public void setStatus(int status)
	{
		getDelegate().setStatus(status);
	}
	
	public int getFragmentOffset()
	{
		return getDelegate().getFragoffset();
	}

	public void setFragmentOffset(int fragoffset)
	{
		getDelegate().setFragoffset(fragoffset);
	}

	public int getFragmentLimit()
	{
		return getDelegate().getFraglimit();
	}

	public void setFragmentLimit(int fraglimit)
	{
		getDelegate().setFraglimit(fraglimit);
	}

	public int getFragmentCount()
	{
		return getDelegate().getFragcount();
	}

	public void setFragmentCount(int fragcount)
	{
		getDelegate().setFragcount(fragcount);
	}

	public int getFragmentSent()
	{
		return getDelegate().getFragsent();
	}

	public void setFragmentSent(int fragsent)
	{
		getDelegate().setFragsent(fragsent);
	}

	public String getOwner()
	{
		return getDelegate().getOwner();
	}

	public void setOwner(String owner)
	{
		getDelegate().setOwner(owner);
	}

	public int getLastUpdateTimeSeconds()
	{
		return getDelegate().getLastupdatetm();
	}
	
	public void setLastUpdateTimeSeconds(int lastupdatetime)
	{
		getDelegate().setLastupdatetm(lastupdatetime);
	}

	public EmailBlast getEmailBlast()
	{
		return getEmailBlast(true);
	}

	public EmailBlast getEmailBlast(boolean doLoad)
	{
		return emailBlastFk.get(doLoad);
	}

	public void setEmailblast(EmailBlast emailBlast)
	{
		emailBlastFk.set(emailBlast);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getEmailblastid() + "-" + getDelegate().getFragoffset();
	}
}