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
package com.i4one.research.model.survey.answer;

import static com.i4one.base.core.Utils.currentDateTime;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.research.model.survey.question.Question;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleAnswerManager extends BasePaginableManager<AnswerRecord, Answer> implements AnswerManager
{
	@Override
	public Answer emptyInstance()
	{
		return new Answer();
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Answer> clone(Answer item)
	{
		try
		{
			if ( item.exists() )
			{
				String currTimeStamp = "" + currentDateTime();
				IString workingTitle = new IString(item.getAnswer()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				Answer answer = new Answer();
				answer.copyFrom(item);
	
				answer.setSer(0);
				answer.setAnswer(workingTitle);
	
				return create(answer);
			}
			else
			{
				throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.dne", "You are attempting to clone a non-existent item: $item", new Object[] { "item", item }));
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
		{
			Errors errors = new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".clone.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));

			throw errors;
		}
	}

	@Override
	public Set<Answer> getAnswers(Question question, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAnswers(question.getSer(), pagination));
	}

	@Override
	public AnswerRecordDao getDao()
	{
		return (AnswerRecordDao) super.getDao();
	}
}
