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
package com.i4one.base.model.preference;

import com.i4one.base.model.BaseActivityType;
import com.i4one.base.model.SiteGroupActivityType;
import com.i4one.base.model.UsageType;

/**
 * @author Hamid Badiozamani
 */
public class UserPreference extends BaseActivityType<UserPreferenceRecord, PreferenceRecord, Preference> implements UsageType<UserPreferenceRecord>,SiteGroupActivityType<UserPreferenceRecord, Preference>
{
	static final long serialVersionUID = 42L;

	protected transient PreferenceAnswer preferenceAnswer;

	public UserPreference()
	{
		super(new UserPreferenceRecord());
	}

	protected UserPreference(UserPreferenceRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		if ( preferenceAnswer == null )
		{
			preferenceAnswer = new PreferenceAnswer();
		}
		if ( getDelegate().getAnswerid() != null )
		{
			preferenceAnswer.resetDelegateBySer(getDelegate().getAnswerid());
		}
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setPreferenceAnswer(getPreferenceAnswer());
	}

	@Override
	protected String uniqueKeyInternal()
	{
		if ( getPreference().isOpenAnswer() )
		{
			return getDelegate().getItemid() + "-" + getDelegate().getUserid() + "-open";
		}
		else
		{
			return getDelegate().getItemid() + "-" + getDelegate().getUserid() + "-" + getDelegate().getAnswerid();
		}
	}

	public Preference getPreference()
	{
		return getActionItem();
	}

	public Preference getPreference(boolean doLoad)
	{
		return getActionItem(doLoad);
	}

	public void setPreference(Preference preference)
	{
		setActionItem(preference);
	}

	public PreferenceAnswer getPreferenceAnswer()
	{
		return getPreferenceAnswer(true);
	}

	public PreferenceAnswer getPreferenceAnswer(boolean doLoad)
	{
		if ( doLoad )
		{
			preferenceAnswer.loadedVersion();
		}

		return preferenceAnswer;
	}

	public void setPreferenceAnswer(PreferenceAnswer preferenceAnswer)
	{
		this.preferenceAnswer = preferenceAnswer;
		getDelegate().setAnswerid(preferenceAnswer.getSer());
	}

	public String getOpenAnswer()
	{
		return getDelegate().getValue();
	}

	public void setOpenAnswer(String answer)
	{
		getDelegate().setValue(answer);
	}

	@Override
	protected Preference newActionItem()
	{
		return new Preference();
	}
}