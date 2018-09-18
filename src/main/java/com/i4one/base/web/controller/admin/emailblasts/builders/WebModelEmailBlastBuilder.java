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
package com.i4one.base.web.controller.admin.emailblasts.builders;

import com.i4one.base.core.AutoPopulatingMap;
import com.i4one.base.model.Errors;
import com.i4one.base.model.emailblast.builder.EmailBlastBuilder;
import com.i4one.base.model.emailblast.builder.EmailBlastBuilderRecord;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public class WebModelEmailBlastBuilder extends EmailBlastBuilder implements WebModel
{
	private transient Model model;
	private transient AutoPopulatingMap<String, IString> state;

	private IString fromAddress;
	private IString subject;
	private IString htmlBody;
	private IString textBody;

	public WebModelEmailBlastBuilder()
	{
		super();
	}

	protected WebModelEmailBlastBuilder(EmailBlastBuilderRecord delegate)
	{
		super(delegate);
	}

	@Override
	public void init()
	{
		super.init();

		setFromAddress(getEmailTemplate().getFromAddress());
		setSubject(getEmailTemplate().getSubject());
		setHtmlBody(getEmailTemplate().getHtmlBody());
		setTextBody(getEmailTemplate().getTextBody());

		state = new AutoPopulatingMap((item) -> { return new IString(); }, super.getSavedState());
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		return errors;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		getEmailTemplate().setFromAddress(getFromAddress());
		getEmailTemplate().setSubject(getSubject());
		getEmailTemplate().setHtmlBody(getHtmlBody());
		getEmailTemplate().setTextBody(getTextBody());
	}

	@Override
	public Map<String, IString> getSavedState()
	{
		// We ensure that the map has all of the keys we need
		//
		return state;
	}

	public Set<BuilderVariable> getHtmlBuilderVariables()
	{
		return toBuilderVariableSet(getHTMLVariables());
	}

	public Set<BuilderVariable> getTextBuilderVariables()
	{
		return toBuilderVariableSet(getTextVariables());
	}

	protected Set<BuilderVariable> toBuilderVariableSet(Set<String> items)
	{
		return items.stream()
			.map( (item) -> { return new BuilderVariable(item); } )
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public IString getFromAddress()
	{
		return fromAddress;
	}

	public void setFromAddress(IString fromAddress)
	{
		this.fromAddress = fromAddress;
	}

	public IString getSubject()
	{
		return subject;
	}

	public void setSubject(IString subject)
	{
		this.subject = subject;
	}

	public IString getHtmlBody()
	{
		return htmlBody;
	}

	public void setHtmlBody(IString htmlBody)
	{
		this.htmlBody = htmlBody;
	}

	public IString getTextBody()
	{
		return textBody;
	}

	public void setTextBody(IString textBody)
	{
		this.textBody = textBody;
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
}
