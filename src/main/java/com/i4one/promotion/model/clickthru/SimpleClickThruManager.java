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
package com.i4one.promotion.model.clickthru;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.terminable.BaseSimpleTerminableSiteGroupTypeManager;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleClickThruManager extends BaseSimpleTerminableSiteGroupTypeManager<ClickThruRecord, ClickThru> implements ClickThruManager
{
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "promotion.clickThruManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "promotion.clickThruManager.namePlural";
	private static final String DEFAULTOUTRO_KEY = "promotion.clickThruManager.defaultOutro";

	@Override
	public ClickThru cloneInternal(ClickThru item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		ClickThru retVal = super.cloneInternal(item);

		String currTimeStamp = String.valueOf(Utils.currentDateTime());
		retVal.setURL(" [CLONED @ " + currTimeStamp + "] " + item.getURL());

		return retVal;
	}

	@Override
	public ClickThruSettings getSettings(SingleClient client)
	{
		ClickThruSettings retVal = new ClickThruSettings();
		retVal.setClient(client);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		List<Message> outros = getMessageManager().getAllMessages(client, DEFAULTOUTRO_KEY);
		retVal.setOutro(outros);

		return retVal;
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClickThruSettings> updateSettings(ClickThruSettings settings)
	{
		ReturnType<ClickThruSettings> retVal = new ReturnType<>();

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		// XXX: Consolidate this into a single list and create a new method in MessageManager to update
		// a batch of messages so that the historical records are chained together.

		List<Message> singularNames = settings.getNameSingleMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = settings.getNamePluralMessages(client, NAMEPLURAL_KEY);

		List<Message> outros = settings.getDefaultOutroMessages(client, DEFAULTOUTRO_KEY);

		singularNames.forEach( (message) -> { getMessageManager().update(message); } );
		pluralNames.forEach( (message) -> { getMessageManager().update(message); } );

		outros.forEach( (message) -> { getMessageManager().update(message); } );

		retVal.setPost(settings);

		return retVal;
	}

	@Override
	public ClickThruRecordDao getDao()
	{
		return (ClickThruRecordDao) super.getDao();
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
	public ClickThru emptyInstance()
	{
		return new ClickThru();
	}
}