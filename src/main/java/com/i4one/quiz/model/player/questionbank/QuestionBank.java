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
package com.i4one.quiz.model.player.questionbank;

import com.i4one.base.model.BaseSiteGroupType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.SiteGroupType;
import com.i4one.base.model.i18n.IString;

/**
 * A trivia game.
 * 
 * @author Hamid Badiozamani
 */
public class QuestionBank extends BaseSiteGroupType<QuestionBankRecord> implements SiteGroupType<QuestionBankRecord>
{
	static final long serialVersionUID = 42L;

	public QuestionBank()
	{
		super(new QuestionBankRecord());
	}

	protected QuestionBank(QuestionBankRecord delegate)
	{
		super(delegate);
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getTitle().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".title.empty", "Title cannot be empty", new Object[]{"item", this}));
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

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle().toString();
	}
}
