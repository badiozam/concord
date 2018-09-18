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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.BaseSimpleManager;
import java.lang.reflect.InvocationTargetException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleQuestionBankManager extends BaseSimpleManager<QuestionBankRecord, QuestionBank> implements QuestionBankManager
{
	@Transactional(readOnly = false)
	@Override
	public ReturnType<QuestionBank> clone(QuestionBank item)
	{
		try
		{
			if ( item.exists() )
			{
				// XXX: This should already be taken care of outside but we need
				// to load all of the internal properties of the object (i.e.
				// the questionBank answers and the correct answer)
				//
				initModelObject(item);

				String currTimeStamp = String.valueOf(Utils.currentDateTime());
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				QuestionBank questionBank = new QuestionBank();
				questionBank.copyFrom(item);
	
				questionBank.setSer(0);
				questionBank.setTitle(workingTitle);
	
				ReturnType<QuestionBank> createdTrivia = create(questionBank);

				return createdTrivia;
			}
			else
			{
				throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.dne", "You are attempting to clone a non-existent item: $item", new Object[] { "item", item }));
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
		{
			Errors errors = new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));

			throw errors;
		}
	}

	@Override
	public QuestionBankRecordDao getDao()
	{
		return (QuestionBankRecordDao) super.getDao();
	}

	@Override
	public QuestionBank emptyInstance()
	{
		return new QuestionBank();
	}
}
