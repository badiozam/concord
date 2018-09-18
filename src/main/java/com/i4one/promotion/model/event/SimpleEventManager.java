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
package com.i4one.promotion.model.event;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.categorizable.BaseSimpleCategorizableManager;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.promotion.model.event.category.EventCategory;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleEventManager extends BaseSimpleCategorizableManager<EventRecord, Event, EventCategory> implements EventManager
{
	private EventResponseManager eventResponseManager;
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "promotion.eventManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "promotion.eventManager.namePlural";
	private static final String DEFAULTINTRO_KEY = "promotion.eventManager.defaultIntro";

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Event> clone(Event item)
	{
		try
		{
			if ( item.exists() )
			{
				// XXX: This should already be taken care of outside but we need
				// to load all of the internal properties of the object (i.e.
				// the event answers and the correct answer)
				//
				initModelObject(item);

				String currTimeStamp = String.valueOf(Utils.currentDateTime());
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				Event event = new Event();
				event.copyFrom(item);
	
				event.setSer(0);
				event.setTitle(workingTitle);
				event.setStartTimeSeconds(item.getStartTimeSeconds() + (86400 * 365));
				event.setEndTimeSeconds(item.getEndTimeSeconds() + (86400 * 366));
	
				ReturnType<Event> createdEvent = create(event);

				return createdEvent;
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
	public EventSettings getSettings(SingleClient client)
	{
		EventSettings retVal = new EventSettings();
		retVal.setClient(client);

		boolean enabled = Utils.defaultIfNaB(getClientOptionManager().getOption(client, getEnabledOptionKey()).getValue(), true);
		retVal.setEnabled(enabled);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		List<Message> intros = getMessageManager().getAllMessages(client, DEFAULTINTRO_KEY);
		retVal.setIntros(intros);

		return retVal;
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<EventSettings> updateSettings(EventSettings settings)
	{
		ReturnType<EventSettings> retVal = new ReturnType<>(settings);

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		// XXX: Consolidate this into a single list and create a new method in MessageManager to update
		// a batch of messages so that the historical records are chained together.

		List<Message> singularNames = settings.getNameSingleMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = settings.getNamePluralMessages(client, NAMEPLURAL_KEY);

		List<Message> intros = settings.getDefaultIntroMessages(client, DEFAULTINTRO_KEY);

		singularNames.forEach( (message) -> { getMessageManager().update(message); } );
		pluralNames.forEach( (message) -> { getMessageManager().update(message); } );

		intros.forEach( (message) -> { getMessageManager().update(message); } );

		updateOption(client, getEnabledOptionKey(), String.valueOf(settings.isEnabled()), retVal);

		return retVal;
	}

	@Override
	public EventRecordDao getDao()
	{
		return (EventRecordDao) super.getDao();
	}

	public EventResponseManager getEventResponseManager()
	{
		return eventResponseManager;
	}

	@Autowired
	public void setEventResponseManager(EventResponseManager eventResponseManager)
	{
		this.eventResponseManager = eventResponseManager;
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	@Override
	public Event emptyInstance()
	{
		return new Event();
	}
}
