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
package com.i4one.base.model.accesscode;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.ClientPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.BaseSimpleTerminableManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleAccessCodeManager extends BaseSimpleTerminableManager<AccessCodeRecord, AccessCode> implements AccessCodeManager
{
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "base.accessCodeManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "base.accessCodeManager.namePlural";
	private static final String DEFAULTDESCR_KEY = "base.accessCodeManager.defaultDescr";
	private static final String DEFAULTACCESSDENIED_KEY = "base.accessCodeManager.defaultAccessDenied";

	@Override
	public AccessCode emptyInstance()
	{
		return new AccessCode();
	}

	@Override
	public Set<AccessCode> getLiveCodes(String code, TerminablePagination pagination)
	{
		return convertDelegates(getDao().getLiveCodes(code, pagination));
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<AccessCode> clone(AccessCode item)
	{
		try
		{
			if ( item.exists() )
			{
				AccessCode retVal = new AccessCode();
				retVal.copyFrom(item);
	
				retVal.setSer(0);
				retVal.setTitle(item.getTitle());
				retVal.setDescr(item.getDescr());
				retVal.setAccessDenied(item.getAccessDenied());

				SingleClient client = item.getClient();
				int startOfTomorrow = Utils.getStartOfDay(client.getCalendar());
				int endOfTomorrow = Utils.getEndOfDay(client.getCalendar());

				// Try to find the next access code date that's not scheduled but
				// go no farther than a year
				//
				for (int i = 1; i < 366; i++)
				{
					retVal.setStartTimeSeconds(startOfTomorrow + 86400 * i);
					retVal.setEndTimeSeconds(endOfTomorrow + (86400 * i));
					retVal.setCode(item.getCode() + i);
					Set<AccessCode> liveCodes = getLive(
						new TerminablePagination(retVal.getStartTimeSeconds(),
							new ClientPagination(client,
							SimplePaginationFilter.SINGLE)));

					if (liveCodes.isEmpty())
					{
						// Found a day without an access code
						//
						break;
					}
				}
		
				return create(retVal);
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
	public List<ReturnType<AccessCode>> importCSV(InputStream stream, Supplier<AccessCode> instantiator, Function<AccessCode,Boolean> preprocessor, Consumer<ReturnType<AccessCode>> postprocessor)
	{
		return super.importCSVInternal(stream, instantiator, preprocessor, postprocessor);
	}

	@Override
	public OutputStream exportCSV(Set<AccessCode> items, OutputStream out)
	{
		return exportCSVInternal(items, out);
	}

	@Override
	public AccessCodeSettings getSettings(SingleClient client)
	{
		AccessCodeSettings retVal = new AccessCodeSettings();
		retVal.setClient(client);

		boolean enabled = Utils.defaultIfNaB(getClientOptionManager().getOption(client, getEnabledOptionKey()).getValue(), true);
		retVal.setEnabled(enabled);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		List<Message> instructions = getMessageManager().getAllMessages(client, DEFAULTDESCR_KEY);
		List<Message> outros = getMessageManager().getAllMessages(client, DEFAULTACCESSDENIED_KEY);

		retVal.setDescr(instructions);
		retVal.setOutro(outros);

		return retVal;
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<AccessCodeSettings> updateSettings(AccessCodeSettings settings)
	{
		ReturnType<AccessCodeSettings> retVal = new ReturnType<>(settings);

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		// XXX: Consolidate this into a single list and create a new method in MessageManager to update
		// a batch of messages so that the historical records are chained together.

		List<Message> singularNames = settings.getNameSingleMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = settings.getNamePluralMessages(client, NAMEPLURAL_KEY);

		List<Message> instructions = settings.getDescr(client, DEFAULTDESCR_KEY);
		List<Message> outros = settings.getDefaultAccessDeniedMessages(client, DEFAULTACCESSDENIED_KEY);

		singularNames.forEach( (message) -> { getMessageManager().update(message); } );
		pluralNames.forEach( (message) -> { getMessageManager().update(message); } );

		instructions.forEach( (message) -> { getMessageManager().update(message); } );
		outros.forEach( (message) -> { getMessageManager().update(message); } );

		updateOption(client, getEnabledOptionKey(), String.valueOf(settings.isEnabled()), retVal);

		return retVal;
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
	public AccessCodeRecordDao getDao()
	{
		return (AccessCodeRecordDao) super.getDao();
	}
}
