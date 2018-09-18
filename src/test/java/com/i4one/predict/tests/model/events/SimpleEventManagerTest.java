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
package com.i4one.predict.tests.model.events;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.tests.core.BaseManagerTest;
import com.i4one.predict.model.category.EventCategory;
import com.i4one.predict.model.category.EventCategoryManager;
import com.i4one.predict.model.category.EventCategoryRecord;
import com.i4one.predict.model.category.EventCategoryRecordDao;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.event.EventManager;
import java.text.ParseException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SimpleEventManagerTest extends BaseManagerTest
{
	private EventCategory firstCategory;

	private EventManager simpleEventManager;
	private EventCategoryManager simpleEventCategoryManager;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		EventCategoryRecord eventCategoryRecord = new EventCategoryRecord();
		eventCategoryRecord.setClientid(getFirstClient().getSer());
		eventCategoryRecord.setSitegroupid(getFirstSiteGroup().getSer());
		eventCategoryRecord.setDescr(new IString("en", "First category created in manager"));
		eventCategoryRecord.setDetailpicurl("");
		eventCategoryRecord.setThumbnailurl("");
		eventCategoryRecord.setTitle(new IString("en", "First Category"));

		getEventCategoryRecordDao().insert(eventCategoryRecord);
		assertTrue(eventCategoryRecord.exists());

		firstCategory = new EventCategory();
		firstCategory.setOwnedDelegate(eventCategoryRecord);

		getEventManager().init();
		getEventCategoryManager().init();
	}

	@Test
	public void testCreateEvent() throws ParseException, Exception
	{
		Event event = new Event();
		event.setTitle(new IString("First Event"));
		event.setSiteGroup(getFirstSiteGroup());
		event.setCategory(getFirstCategory());
		event.setClient(getFirstClient());
		event.setMinBid(0);
		event.setMaxBid(5);
		event.setPromo(new IString("Test promo"));
		event.setDescr(new IString("Test descr"));
		event.setBrief(new IString("Test brief"));
		event.setReference(new IString("Test reference"));
		event.setStartTimeSeconds( Utils.currentTimeSeconds() - 10 );
		event.setClosesBySeconds( Utils.currentTimeSeconds() - 5 );
		event.setEndTimeSeconds( Utils.currentTimeSeconds());
		event.setPostsBySeconds( Utils.currentTimeSeconds() + 5 );

		int endTime = event.getEndTimeSeconds();
		int startTime = event.getStartTimeSeconds();
		assertNotEquals(endTime, 0);
		assertNotEquals(startTime, 0);
		assertNotEquals(startTime, endTime);

		int postsBy = event.getPostsBySeconds();
		int closesBy = event.getClosesBySeconds();
		assertNotEquals(closesBy, 0);
		assertNotEquals(postsBy, 0);
		assertNotEquals(closesBy, postsBy);

		logAdminIn(getFirstAdmin());

		ReturnType<Event> retVal = getEventManager().create(event);
		assertTrue(retVal.getPost().exists());
		assertEquals(retVal.getPost().getStartTimeSeconds(), startTime);
		assertEquals(retVal.getPost().getEndTimeSeconds(), endTime);
	}

	public EventCategory getFirstCategory()
	{
		return firstCategory;
	}

	public void setFirstCategory(EventCategory firstCategory)
	{
		this.firstCategory = firstCategory;
	}

	public EventManager getEventManager()
	{
		return getSimpleEventManager();
	}

	public EventManager getSimpleEventManager()
	{
		return simpleEventManager;
	}

	@Autowired
	public void setSimpleEventManager(EventManager simpleEventManager)
	{
		this.simpleEventManager = simpleEventManager;
	}

	public EventCategoryManager getEventCategoryManager()
	{
		return getSimpleEventCategoryManager();
	}

	public EventCategoryManager getSimpleEventCategoryManager()
	{
		return simpleEventCategoryManager;
	}

	@Autowired
	public void setSimpleEventCategoryManager(EventCategoryManager simpleEventCategoryManager)
	{
		this.simpleEventCategoryManager = simpleEventCategoryManager;
	}

	protected EventCategoryRecordDao getEventCategoryRecordDao()
	{
		return (EventCategoryRecordDao) getDaoManager().getNewDao("predict.JdbcEventCategoryRecordDao");
	}

}
