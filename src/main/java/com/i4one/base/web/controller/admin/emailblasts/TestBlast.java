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
package com.i4one.base.web.controller.admin.emailblasts;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;

/**
 * @author Hamid Badiozamani
 */
public class TestBlast implements WebModel
{
	private Model model;
	private EmailBlast emailBlast;
	private String usernames;
	private boolean hasSent;

	public TestBlast()
	{
		emailBlast = new EmailBlast();
		usernames = "";
		hasSent = false;
	}

	public Errors validate()
	{
		Errors errors = new Errors();

		if ( !getEmailBlast().exists() )
		{
			errors.addError(new ErrorMessage("msg.base." + getClass().getSimpleName() + ".emailblast.dne", "No e-mail blast specified for testing", new Object[]{"item", this}));

			setHasSent(true);
		}

		if ( Utils.isEmpty(getUsernames()))
		{
			errors.addError(new ErrorMessage("usernames", "msg.base." + getClass().getSimpleName() + ".usernames.empty", "No users specified for testing", new Object[]{"item", this}));
			setHasSent(false);
		}

		return errors;
	}

	@Override
	public Model getModel()
	{
		return model;
	}

	@Override
	public void setModel(Model model)
	{
		this.model = model;
	}

	public EmailBlast getEmailBlast()
	{
		return emailBlast;
	}

	public void setEmailBlast(EmailBlast emailBlast)
	{
		this.emailBlast = emailBlast;
	}

	public String getUsernames()
	{
		return usernames;
	}

	public void setUsernames(String usernames)
	{
		this.usernames = usernames;
	}

	public boolean isHasSent()
	{
		return hasSent;
	}

	public void setHasSent(boolean hasSent)
	{
		this.hasSent = hasSent;
	}

}
