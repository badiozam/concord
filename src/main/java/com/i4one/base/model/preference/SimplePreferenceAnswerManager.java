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

import static com.i4one.base.core.Utils.currentDateTime;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.BaseSimpleManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimplePreferenceAnswerManager extends BaseSimpleManager<PreferenceAnswerRecord, PreferenceAnswer> implements PreferenceAnswerManager
{
	@Override
	public PreferenceAnswer emptyInstance()
	{
		return new PreferenceAnswer();
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<PreferenceAnswer> clone(PreferenceAnswer item)
	{
		try
		{
			if ( item.exists() )
			{
				String currTimeStamp = "" + currentDateTime();
				IString workingTitle = new IString(item.getAnswer()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				PreferenceAnswer answer = new PreferenceAnswer();
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
	public Set<PreferenceAnswer> getAnswers(Preference preference)
	{
		return convertDelegates(getDao().getAnswers(preference.getSer()));
	}

	@Override
	public PreferenceAnswerRecordDao getDao()
	{
		return (PreferenceAnswerRecordDao) super.getDao();
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<PreferenceAnswer>> processPreferenceAnswers(Set<PreferenceAnswer> preferenceAnswers)
	{
		List<ReturnType<PreferenceAnswer>> retVal = new ArrayList<>();

		for (PreferenceAnswer preferenceAnswer : preferenceAnswers )
		{
			getLogger().debug("Processing preference answer\n" + preferenceAnswer + "@" + Integer.toHexString(System.identityHashCode(preferenceAnswer)));
			if ( !preferenceAnswer.getAnswer().isBlank() )
			{
				if ( preferenceAnswer.exists() )
				{
					ReturnType<PreferenceAnswer> answerUpdate = update(preferenceAnswer);
					retVal.add(answerUpdate);
				}
				else
				{
					ReturnType<PreferenceAnswer> answerCreate = create(preferenceAnswer);
					retVal.add(answerCreate);
				}
			}
			else if ( preferenceAnswer.exists() )
			{
				// The user didn't want this answer
				//
				PreferenceAnswer removedAnswer = remove(preferenceAnswer);

				ReturnType<PreferenceAnswer> answerRemove = new ReturnType<>();
				answerRemove.setPre(removedAnswer);
				answerRemove.setPost(new PreferenceAnswer());

				retVal.add(answerRemove);
			}
		}

		return retVal;
	}

}
