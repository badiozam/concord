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
package com.i4one.promotion.model.code;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.model.manager.instantwinnable.SimpleTerminableInstantWinnable;
import com.i4one.base.model.manager.instantwinnable.TerminableInstantWinnableClientType;
import com.i4one.base.model.manager.terminable.BaseTerminableSiteGroupType;
import com.i4one.base.model.manager.triggerable.SimpleTerminableTriggerable;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public class Code extends BaseTerminableSiteGroupType<CodeRecord> implements TerminableTriggerableClientType<CodeRecord, Code>,TerminableInstantWinnableClientType<CodeRecord, Code>
{
	private final transient SimpleTerminableTriggerable<CodeRecord, Code> triggerable;
	private final transient SimpleTerminableInstantWinnable<CodeRecord, Code> instantWinnable;

	public Code()
	{
		super(new CodeRecord());

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}

	protected Code(CodeRecord delegate)
	{
		super(delegate);

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);
	}
	
	@Override
	protected void init()
	{
		super.init();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( Utils.isEmpty(getDelegate().getCode()) )
		{
			retVal.addError(new ErrorMessage("code", "msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".code.empty", "Codes cannot be empty or entirely comprised of white space", new Object[]{"item", this}));
		}

		retVal.merge(triggerable.validate());
		retVal.merge(instantWinnable.validate());

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		triggerable.setOverrides();
		instantWinnable.setOverrides();
	}

	public IString getOutro()
	{
		return getDelegate().getOutro();
	}

	public void setOutro(IString outro)
	{
		getDelegate().setOutro(outro);
	}

	public String getCode()
	{
		return getDelegate().getCode();
	}

	public void setCode(String code)
	{
		getDelegate().setCode(Utils.forceEmptyStr(code));
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getCode() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
	}

	@Override
	protected boolean equalsInternal(RecordTypeDelegator<CodeRecord> right)
	{
		if ( right instanceof Code )
		{
			Code rightCode = (Code)right;
			return getStartTimeSeconds() == rightCode.getStartTimeSeconds() &&
				getEndTimeSeconds() == rightCode.getEndTimeSeconds() &&
				getCode().equals(rightCode.getCode());
		}
		else
		{
			return false;
		}
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		if  ( super.fromCSVInternal(csv) )
		{
			String code = csv.get(0); csv.remove(0);
			setCode(code);

			String outro = csv.get(0); csv.remove(0);
			setOutro(new IString(outro));

			boolean retVal = true;

			retVal &= triggerable.fromCSVList(csv);
			retVal &= instantWinnable.fromCSVList(csv);

			return retVal;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected StringBuilder toCSVInternal(boolean header)
	{
		StringBuilder retVal = super.toCSVInternal(header);

		if ( header )
		{
			// XXX: Needs to be i18n
			retVal.append(Utils.csvEscape("Code")).append(",");
			retVal.append(Utils.csvEscape("Outro")).append(",");
		}
		else
		{
			retVal.append("\"").append(Utils.csvEscape(getCode())).append("\",");
			retVal.append("\"").append(Utils.csvEscape(getOutro().toString())).append("\",");
		}

		retVal.append(triggerable.toCSV(header));
		retVal.append(instantWinnable.toCSV(header));

		return retVal;
	}

	@Override
	public Set<ExclusiveBalanceTrigger<Code>> getExclusiveBalanceTriggers()
	{
		return triggerable.getExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getNonExclusiveBalanceTriggers()
	{
		return triggerable.getNonExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getBalanceTriggers()
	{
		return triggerable.getBalanceTriggers();
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> balanceTriggers)
	{
		triggerable.setBalanceTriggers(balanceTriggers);
	}

	@Override
	public Set<ExclusiveInstantWin<Code>> getExclusiveInstantWins()
	{
		return instantWinnable.getExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getNonExclusiveInstantWins()
	{
		return instantWinnable.getNonExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getInstantWins()
	{
		return instantWinnable.getInstantWins();
	}

	@Override
	public void setInstantWins(Collection<InstantWin> instantWins)
	{
		instantWinnable.setInstantWins(instantWins);
	}
}
