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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.client.SiteGroup;
import com.i4one.base.model.client.SiteGroupManager;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.pagination.BasePaginableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimplePreferenceManager extends BasePaginableManager<PreferenceRecord, Preference> implements PreferenceManager
{
	private SiteGroupManager siteGroupManager;
	private UserPreferenceManager userPreferenceManager;
	private PreferenceAnswerManager preferenceAnswerManager;
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "base.preferenceManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "base.preferenceManager.namePlural";

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Preference> clone(Preference item)
	{
		try
		{
			if ( item.exists() )
			{
				// XXX: This should already be taken care of outside but we need
				// to load all of the internal properties of the object (i.e.
				// the preference answers)
				//
				initModelObject(item);

				String currTimeStamp = String.valueOf(Utils.currentDateTime());
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				Preference preference = new Preference();
				preference.copyFrom(item);
	
				preference.setSer(0);
				preference.setTitle(workingTitle);
	
				ReturnType<Preference> createdPreference = create(preference);

				return createdPreference;
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

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Preference> createInternal(Preference preference)
	{
		// Can't have null values for the correct answer, so we set it to
		// some arbitrary non-zero value as a place holder to be reset to
		// the correct serial number once we have one.
		//
		ReturnType<Preference> retVal = super.createInternal(preference);

		for (PreferenceAnswer preferenceAnswer : preference.getPreferenceAnswers() )
		{
			// Force the preference answer to have the newly created preference
			// (although by this point it already should have it)
			//
			preferenceAnswer.setPreference(retVal.getPost());
		}

		List<ReturnType<PreferenceAnswer>> processedAnswers = getPreferenceAnswerManager().processPreferenceAnswers(preference.getPreferenceAnswers());
		retVal.addChain(getPreferenceAnswerManager(), "processPreferenceAnswers", new ReturnType<>(processedAnswers));

		// Should have been set by actualizeRelations but just in case something goes wrong
		// we should maintain consistency here
		//
		getDao().updateAnswerCount(preference.getSer());

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Preference> updateInternal(PreferenceRecord preferenceRecord, Preference preference)
	{
		// We override this method instead of update in order to ensure that initModelObject is called
		// after the answers have been processed
		//
		ReturnType<Preference> retVal = super.updateInternal(preferenceRecord, preference);

		List<ReturnType<PreferenceAnswer>> processedAnswers = getPreferenceAnswerManager().processPreferenceAnswers(preference.getPreferenceAnswers());
		retVal.addChain(getPreferenceAnswerManager(), "processPreferenceAnswers", new ReturnType<>(processedAnswers));

		// Should have been set by actualizeRelations but just in case something goes wrong
		// we should maintain consistency here
		//
		getDao().updateAnswerCount(preference.getSer());

		return retVal;
	}

	@Override
	public Set<Preference> getAllPreferences(SingleClient requestClient, PaginationFilter pagination)
	{
		Set<SiteGroup> siteGroups = getSiteGroupManager().getSiteGroups(requestClient);
		return convertDelegates(getDao().getAll(new SiteGroupPagination(siteGroups, pagination)));
	}

	@Override
	public PreferenceSettings getSettings(SingleClient client)
	{
		PreferenceSettings retVal = new PreferenceSettings();
		retVal.setClient(client);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		return retVal;
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<PreferenceSettings> updateSettings(PreferenceSettings settings)
	{
		ReturnType<PreferenceSettings> retVal = new ReturnType<>();

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		// XXX: Consolidate this into a single list and create a new method in MessageManager to update
		// a batch of messages so that the historical records are chained together.

		List<Message> singularNames = settings.getNameSingleMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = settings.getNamePluralMessages(client, NAMEPLURAL_KEY);

		singularNames.forEach( (message) -> { getMessageManager().update(message); } );
		pluralNames.forEach( (message) -> { getMessageManager().update(message); } );

		retVal.setPost(settings);

		return retVal;
	}

	@Override
	public PreferenceRecordDao getDao()
	{
		return (PreferenceRecordDao) super.getDao();
	}

	public PreferenceAnswerManager getPreferenceAnswerManager()
	{
		return preferenceAnswerManager;
	}

	@Autowired
	public void setPreferenceAnswerManager(PreferenceAnswerManager answerManager)
	{
		this.preferenceAnswerManager = answerManager;
	}

	public UserPreferenceManager getUesrPreferenceManager()
	{
		return userPreferenceManager;
	}

	@Autowired
	public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager)
	{
		this.userPreferenceManager = userPreferenceManager;
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

	public SiteGroupManager getSiteGroupManager()
	{
		return siteGroupManager;
	}

	@Autowired
	public void setSiteGroupManager(SiteGroupManager siteGroupManager)
	{
		this.siteGroupManager = siteGroupManager;
	}

	@Override
	public Preference emptyInstance()
	{
		return new Preference();
	}

	@Override
	protected Preference initModelObject(Preference item)
	{
		item.setPreferenceAnswers(getPreferenceAnswerManager().getAnswers(item));

		return item;
	}
}
