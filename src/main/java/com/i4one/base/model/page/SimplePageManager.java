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
package com.i4one.base.model.page;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.terminable.BaseSimpleTerminableSiteGroupTypeManager;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimplePageManager extends BaseSimpleTerminableSiteGroupTypeManager<PageRecord, Page> implements PageManager
{
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "base.pageManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "base.pageManager.namePlural";
	private static final String DEFAULTTITLE_KEY = "base.pageManager.defaultTitle";
	private static final String DEFAULTBODY_KEY = "base.pageManager.defaultBody";

	@Override
	protected Page cloneInternal(Page item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return super.cloneInternal(item);
	}

	@Override
	public PageSettings getSettings(SingleClient client)
	{
		PageSettings retVal = new PageSettings();
		retVal.setClient(client);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		List<Message> titles = getMessageManager().getAllMessages(client, DEFAULTTITLE_KEY);
		List<Message> bodies = getMessageManager().getAllMessages(client, DEFAULTBODY_KEY);
		retVal.setTitleBody(titles, bodies);

		return retVal;
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<PageSettings> updateSettings(PageSettings settings)
	{
		ReturnType<PageSettings> retVal = new ReturnType<>();

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		Set<Message> settingMessages = new HashSet<>();

		settingMessages.addAll(settings.getNameSingleMessages(client, NAMESINGLE_KEY));
		settingMessages.addAll(settings.getNamePluralMessages(client, NAMEPLURAL_KEY));

		settingMessages.addAll(settings.getDefaultTitleMessages(client, DEFAULTTITLE_KEY));
		settingMessages.addAll(settings.getDefaultBodyMessages(client, DEFAULTBODY_KEY));

		getMessageManager().updateMessages(settingMessages);

		retVal.setPost(settings);

		return retVal;
	}

	@Override
	public PageRecordDao getDao()
	{
		return (PageRecordDao) super.getDao();
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
	public Page emptyInstance()
	{
		return new Page();
	}
}