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
import com.i4one.base.model.emailblast.EmailBlastRecord;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.targeting.GenericTarget;
import com.i4one.base.model.targeting.Target;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.WebModel;
import com.i4one.base.web.controller.admin.targeting.WebModelTarget;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class WebModelEmailBlast extends EmailBlast implements WebModel
{
	private transient Model model;
	private final transient Set<WebModelTarget> webModelTargets;

	private IString fromAddress;
	private IString replyTo;
	private IString subject;
	private IString htmlBody;
	private IString textBody;

	public WebModelEmailBlast()
	{
		super();

		webModelTargets = new HashSet<>();
	}

	protected WebModelEmailBlast(EmailBlastRecord delegate)
	{
		super(delegate);

		webModelTargets = new HashSet<>();
	}

	@Override
	public void init()
	{
		super.init();

		setFromAddress(getEmailTemplate().getFromAddress());
		setReplyTo(getEmailTemplate().getReplyTo());
		setSubject(getEmailTemplate().getSubject());
		setHtmlBody(getEmailTemplate().getHtmlBody());
		setTextBody(getEmailTemplate().getTextBody());
	}

	@Override
	public Errors validate()
	{
		Errors errors = super.validate();

		// We allow 12h grace period for testing
		//
		int matureTimeSeconds = getMatureTimeSeconds() + 43200;
		if ( !exists() && matureTimeSeconds <= getModel().getTimeInSeconds() )
		{
			// We don't allow creating new items that go out immediately
			//
			errors.addError(new ErrorMessage("matureTimeString", "msg.base.EmailBlast.noimmediatecreate", "Cannot schedule e-mail blasts to go out immediately since e-mails that have gone out can not be recalled. Please create with a mature time at least 12 hours in advance, then double-check, then push out.", new Object[]{"item", this}));
		}

		return errors;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		getEmailTemplate().setFromAddress(getFromAddress());
		getEmailTemplate().setReplyTo(getReplyTo());
		getEmailTemplate().setSubject(getSubject());
		getEmailTemplate().setHtmlBody(getHtmlBody());
		getEmailTemplate().setTextBody(getTextBody());

		setSchedule(formatScheduleStr(getSchedule()));
	}

	private String formatScheduleStr(String scheduleStr)
	{
		// Convert all whitespace to single spaces
		//
		return Utils.trimString(scheduleStr.replaceAll("\\s+", " "));
	}

	public IString getFromAddress()
	{
		return fromAddress;
	}

	public void setFromAddress(IString fromAddress)
	{
		this.fromAddress = fromAddress;
	}

	public IString getReplyTo()
	{
		return replyTo;
	}

	public void setReplyTo(IString replyTo)
	{
		this.replyTo = replyTo;
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

	public String getTargetKeys()
	{
		return webModelTargets.toString();
	}

	@Override
	public void setTargets(Collection<Target> targets)
	{
		super.setTargets(targets);

		webModelTargets.clear();
		for ( Target target : targets )
		{
			webModelTargets.add(new WebModelTarget(target));
		}
	}

	public void setTargetKeys(String targetKeys) throws IOException
	{
		/*
		ObjectMapper mapper = Base.getInstance().getJacksonObjectMapper();
		WebModelTarget[] targets = mapper.readValue(targetKeys, WebModelTarget[].class);

		setTargets(Arrays.asList(targets));
		*/
		Set<Target> targets = new HashSet<>();
		String[] targetKeyList = targetKeys.split(",");

		for (String targetKey : targetKeyList )
		{
			targets.add(new GenericTarget(targetKey));
		}

		setTargets(targets);
	}

	public String getValidSchedule()
	{
		return getSchedule();
	}

	public void setValidSchedule(String validSchedule)
	{
		if ( !validSchedule.equals("--"))
		{
			setSchedule(validSchedule);
		}
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