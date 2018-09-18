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
package com.i4one.base.model.emailblast;

import com.i4one.base.core.Utils;
import com.i4one.base.model.BaseSingleClientType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ForeignKey;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.emailtemplate.EmailTemplateRecord;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.terminable.SimpleParsingTerminable;
import com.i4one.base.model.targeting.GenericTarget;
import com.i4one.base.model.targeting.Target;
import com.i4one.base.model.targeting.Targetable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.springframework.scheduling.support.CronSequenceGenerator;

/**
 * @author Hamid Badiozamani
 */
public class EmailBlast extends BaseSingleClientType<EmailBlastRecord> implements Targetable
{
	static final long serialVersionUID = 42L;

	private transient ForeignKey<EmailTemplateRecord, EmailTemplate> emailTemplateFk;
	private transient Set<Target> targets;

	public EmailBlast()
	{
		super(new EmailBlastRecord());
	}

	protected EmailBlast(EmailBlastRecord delegate)
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

		targets = new HashSet<>();
		String[] targetKeyList = Utils.forceEmptyStr(getTargetSQL()).split(",");
		for (String targetKey : targetKeyList )
		{
			targets.add(new GenericTarget(targetKey));
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
			errors.addError(new ErrorMessage("title", "msg.base.EmailBlast.emptytitle", "Title cannot be empty.", new Object[]{"item", this}));
		}

		if ( !Utils.isEmpty(getSchedule()))
		{
			try
			{
				new CronSequenceGenerator(getSchedule(), getClient().getTimeZone());
			}
			catch (IllegalArgumentException | IndexOutOfBoundsException e)
			{
				errors.addError(new ErrorMessage("schedule", "msg.base.EmailBlast.invalidSchedule", "Invalid schedule.", new Object[]{"item", this}));
			}
		}

		errors.merge(getEmailTemplate().validate());

		return errors;
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		emailTemplateFk.actualize();
	}

	public boolean isPast(int asOf)
	{
		return getMatureTimeSeconds() <= asOf &&
			(getStatus() == EmailBlastStatus.COMPLETED || getStatus() == EmailBlastStatus.ERROR );
	}

	public boolean isLive(int asOf)
	{
		return getMatureTimeSeconds() <= asOf &&
			!(getStatus() == EmailBlastStatus.COMPLETED || getStatus() == EmailBlastStatus.ERROR );
	}

	public boolean isFuture(int asOf)
	{
		return getMatureTimeSeconds() > asOf;
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
	
	public int getTotalCount()
	{
		return getDelegate().getTotalcount();
	}

	public void setTotalCount(int totalCount)
	{
		getDelegate().setTotalcount(totalCount);
	}

	public int getTotalSent()
	{
		return getDelegate().getTotalsent();
	}

	public void setTotalSent(int totalSent)
	{
		getDelegate().setTotalsent(totalSent);
	}

	public int getStatus()
	{
		return getDelegate().getStatus();
	}

	public void setStatus(int status)
	{
		getDelegate().setStatus(status);
	}

	public Date getSendStartTime()
	{
		return Utils.toDate(getSendStartTimeSeconds());
	}

	public int getSendStartTimeSeconds()
	{
		return getDelegate().getSendstarttm();
	}

	public void setSendStartTimeSeconds(int sentStarttm)
	{
		getDelegate().setSendstarttm(sentStarttm);
	}

	public Date getSendEndTime()
	{
		return Utils.toDate(getSendEndTimeSeconds());
	}

	public int getSendEndTimeSeconds()
	{
		return getDelegate().getSendendtm();
	}

	public void setSendEndTimeSeconds(int sentEndtm)
	{
		getDelegate().setSendendtm(sentEndtm);
	}

	public Date getMatureTime()
	{
		return Utils.toDate(getMatureTimeSeconds());
	}

	public int getMatureTimeSeconds()
	{
		return getDelegate().getMaturetm();
	}

	public void setMatureTimeSeconds(int maturetm)
	{
		getDelegate().setMaturetm(maturetm);
	}

	public String getMatureTimeString()
	{
		return SimpleParsingTerminable.toDateString(getMatureTimeSeconds(), getClient().getLocale(), getClient().getTimeZone());
	}

	public void setMatureTimeString(String matureTime) throws ParseException
	{
		setMatureTimeSeconds(SimpleParsingTerminable.parseToSeconds(matureTime, getClient().getLocale(), getClient().getTimeZone()));
	}

	public String getSchedule()
	{
		return getDelegate().getSchedule();
	}

	public void setSchedule(String schedule)
	{
		getDelegate().setSchedule(schedule);
	}

	public Date getNextRecurrence()
	{
		if ( !Utils.isEmpty(getSchedule()))
		{
			CronSequenceGenerator generator = new CronSequenceGenerator(getSchedule(), getClient().getTimeZone());
			Date nextRecurrence = generator.next(getSendStartTime());

			// Most likely this hasn't gone out even once
			//
			if (nextRecurrence.before(getMatureTime()))
			{
				return getMatureTime();
			}
			else
			{
				return nextRecurrence;
			}
		}
		else
		{
			return getMatureTime();
		}
	}

	public boolean getRecurrent()
	{
		return isRecurrent();
	}

	public boolean isRecurrent()
	{
		return !Utils.isEmpty(getSchedule());
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
	public Set<Target> getTargets()
	{
		return Collections.unmodifiableSet(targets);
	}

	@Override
	public void setTargets(Collection<Target> targets)
	{
		this.targets.clear();
		this.targets.addAll(targets);
	}

	@Override
	protected void copyFromInternal(RecordTypeDelegator<EmailBlastRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyFromInternal(right);
		if ( right instanceof EmailBlast )
		{
			EmailBlast rightEmail = (EmailBlast)right;

			// Targets are stateless and can therefore be simply referenced
			//
			setTargets(rightEmail.getTargets());
		}
	}

	@Override
	protected void copyOverInternal(RecordTypeDelegator<EmailBlastRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyOverInternal(right);
		if ( right instanceof EmailBlast )
		{
			EmailBlast rightQuestion = (EmailBlast)right;

			if ( !rightQuestion.getTargets().isEmpty() )
			{
				copyFromInternal(right);
			}
		}
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getDelegate().getTitle() + "-" + getDelegate().getMaturetm();
	}
}