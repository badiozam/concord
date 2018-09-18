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
package com.i4one.base.model.user;

import com.i4one.base.model.BaseSettings;
import com.i4one.base.model.Settings;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.i18n.IString;
import java.util.Collections;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class UserSettings extends BaseSettings implements Settings
{
	private SingleClient client;
	private EmailTemplate birthdayEmailTemplate;
	private Set<BalanceTrigger> birthdayBalanceTriggers;
	private boolean birthdayEnabled;

	public UserSettings()
	{
		birthdayEmailTemplate = new EmailTemplate();
		birthdayBalanceTriggers = Collections.emptySet();
	}

	public EmailTemplate getBirthdayEmailTemplate()
	{
		return birthdayEmailTemplate;
	}

	public void setBirthdayEmailTemplate(EmailTemplate birthdayEmailTemplate)
	{
		this.birthdayEmailTemplate = birthdayEmailTemplate;
	}

	public Set<BalanceTrigger> getBirthdayBalanceTriggers()
	{
		return birthdayBalanceTriggers;
	}

	public void setBirthdayBalanceTriggers(Set<BalanceTrigger> birthdayBalanceTriggers)
	{
		this.birthdayBalanceTriggers = birthdayBalanceTriggers;
	}

	public boolean isBirthdayEnabled()
	{
		return birthdayEnabled;
	}

	public void setBirthdayEnabled(boolean birthdayEnabled)
	{
		this.birthdayEnabled = birthdayEnabled;
	}

	public SingleClient getClient()
	{
		return client;
	}

	public void setClient(SingleClient client)
	{
		this.client = client;
	}

	@Override
	public IString getNameSingle()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public IString getNamePlural()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
