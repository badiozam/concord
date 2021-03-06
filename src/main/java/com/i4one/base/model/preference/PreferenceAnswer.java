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

import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ForeignKey;
import com.i4one.base.model.i18n.IString;

/**
 * @author Hamid Badiozamani
 */
public class PreferenceAnswer extends BaseRecordTypeDelegator<PreferenceAnswerRecord>
{
	static final long serialVersionUID = 42L;

	private transient ForeignKey<PreferenceRecord, Preference> preferenceFk;


	public PreferenceAnswer()
	{
		super(new PreferenceAnswerRecord());
	}

	protected PreferenceAnswer(PreferenceAnswerRecord delegate)
	{
		super(delegate);
	}

	@Override
	protected void init()
	{
		preferenceFk = new ForeignKey<>(this,
			getDelegate()::getPreferenceid,
			getDelegate()::setPreferenceid,
			() -> { return new Preference(); });
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getAnswer().isBlank() )
		{
			retVal.addError("answer", new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".answer.empty", "Answer cannot be empty", new Object[]{"item", this}));
		}

		return retVal;
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		preferenceFk.actualize();
	}

	public Preference getPreference()
	{
		return preferenceFk.get(true);
	}

	public Preference getPreference(boolean doLoad)
	{
		return preferenceFk.get(doLoad);
	}

	public void setPreference(Preference preference)
	{
		preferenceFk.set(preference);
	}

	public int getOrderWeight()
	{
		return getDelegate().getOrderweight();
	}

	public void setOrderWeight(int orderweight)
	{
		getDelegate().setOrderweight(orderweight);
	}

	public IString getAnswer()
	{
		return getDelegate().getAnswer();
	}

	public void setAnswer(IString answer)
	{
		getDelegate().setAnswer(answer);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getPreference(false).getSer() + " - " + getAnswer();
	}
}
