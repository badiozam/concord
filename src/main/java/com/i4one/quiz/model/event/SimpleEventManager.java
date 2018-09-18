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
package com.i4one.quiz.model.event;

import com.i4one.base.core.Utils;
import static com.i4one.base.core.Utils.currentDateTime;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.categorizable.BaseSimpleCategorizableManager;
import com.i4one.quiz.model.category.QuizCategory;
import java.lang.reflect.InvocationTargetException;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class retrieves prediction events for a given term
 *
 * @author Hamid Badiozamani
 */
public class SimpleEventManager extends BaseSimpleCategorizableManager<EventRecord, Event, QuizCategory> implements EventManager
{
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Event> clone(Event item)
	{
		try
		{
			if ( item.exists() )
			{
				String currTimeStamp = "" + currentDateTime();
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
				/*
				boolean existsByTitle = true;
	
				// Keep trying new titles, limiting our search to 50 maximum
				//
				for (int i = 0; i < 50 && existsByTitle; i++ )
				{
					workingTitle = item.getTitle() + " [CLONE #" + (i + 1) + "]";
	
					Event existing = getEvent(item.getClient(), workingTitle);
					existsByTitle = existing.exists();
				}
	
				// If after 50 tries, we're still unsuccessful, fall back to the current time stamp instead
				//
				if ( existsByTitle )
				{
					String currTimeStamp = "" + currentDateTime();
					workingTitle = item.getTitle() + " [CLONED @ " + currTimeStamp + "]";
				}
				*/
	
				Event event = new Event();
				event.copyFrom(item);
	
				event.setSer(0);
				event.setTitle(workingTitle);
				event.setStartTimeSeconds(Utils.currentTimeSeconds() + (86400 * 365));
				event.setEndTimeSeconds(Utils.currentTimeSeconds() + (86400 * 366));
	
				ReturnType retVal = create(event);

				return retVal;
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
	public EventRecordDao getDao()
	{
		return (EventRecordDao) super.getDao();
	}

	@Override
	public Event emptyInstance()
	{
		return new Event();
	}
}
