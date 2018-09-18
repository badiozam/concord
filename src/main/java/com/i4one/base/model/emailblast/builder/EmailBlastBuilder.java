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
package com.i4one.base.model.emailblast.builder;

import com.i4one.base.core.Base64;
import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ForeignKey;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.emailtemplate.EmailTemplateRecord;
import com.i4one.base.model.i18n.IString;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class EmailBlastBuilder extends BaseSingleClientType<EmailBlastBuilderRecord>
{
	static final long serialVersionUID = 42L;

	private transient ForeignKey<EmailTemplateRecord, EmailTemplate> emailTemplateFk;
	private transient HashMap<String, IString> savedState;

	public EmailBlastBuilder()
	{
		super(new EmailBlastBuilderRecord());
	}

	protected EmailBlastBuilder(EmailBlastBuilderRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		super.init();

		emailTemplateFk = new ForeignKey<>(this,
			getDelegate()::getEmailtemplateid,
			getDelegate()::setEmailtemplateid,
			() -> { return new EmailTemplate(); });

		if ( !Utils.isEmpty(getDelegate().getSavedstate()))
		{
			savedState = (HashMap<String, IString>)Base64.decodeToObject(getDelegate().getSavedstate());
		}
		else
		{
			savedState = new HashMap<>();
		}
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		getEmailTemplate().setClient(getClient());
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		if ( getTitle().isBlank() )
		{
			// We don't allow creating new items that go out immediately
			//
			errors.addError(new ErrorMessage("title", "msg.base.EmailBlastBuilder.emptytitle", "Title cannot be empty.", new Object[]{"item", this}));
		}

		errors.merge(getEmailTemplate().validate());

		return errors;
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		emailTemplateFk.actualize();

		getDelegate().setSavedstate(Base64.encodeObject(savedState));
	}

	/**
	 * Computes the set of variables from the HTML body.
	 * 
	 * @return The set of variable names to be set when building the e-mail blast.
	 */
	public Set<String> getHTMLVariables()
	{
		return getVariables(getEmailTemplate().getHtmlBody());
	}

	/**
	 * Computes the set of variables from the text body.
	 * 
	 * @return The set of variable names to be set when building the e-mail blast.
	 */
	public Set<String> getTextVariables()
	{
		return getVariables(getEmailTemplate().getTextBody());
	}

	protected Set<String> getVariables(IString content)
	{
		Set<String> retVal = new LinkedHashSet<>();

		for ( String currLang : content.getLanguages() )
		{
			retVal.addAll(Utils.getVars("[^", "/", "^]", content.get(currLang)));
		}

		return retVal;
	}

	public IString getTitle()
	{
		return getDelegate().getTitle();
	}

	public void setTitle(IString title)
	{
		getDelegate().setTitle(title);
	}

	public IString getTarget()
	{
		return getDelegate().getTarget();
	}
	
	public void setTarget(IString target)
	{
		getDelegate().setTarget(target);
	}

	public String getTargetSQL()
	{
		return getDelegate().getTargetsql();
	}

	public void setTargetSQL(String targetSQL)
	{
		getDelegate().setTargetsql(targetSQL);
	}

	public Map<String, IString> getSavedState()
	{
		return savedState;
	}

	public void setSavedState(Map<String, IString> savedState)
	{
		this.savedState.clear();
		this.savedState.putAll(savedState);
	}
	
	public EmailTemplate getEmailTemplate()
	{
		return getEmailTemplate(true);
	}

	public EmailTemplate getEmailTemplate(boolean doLoad)
	{
		return emailTemplateFk.get(doLoad);
	}

	public void setEmailTemplate(EmailTemplate emailTemplate)
	{
		emailTemplateFk.set(emailTemplate);
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getTitle().toString();
	}
}