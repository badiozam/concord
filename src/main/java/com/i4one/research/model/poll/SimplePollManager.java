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
package com.i4one.research.model.poll;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.categorizable.BaseSimpleCategorizableManager;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.research.model.poll.category.PollCategory;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimplePollManager extends BaseSimpleCategorizableManager<PollRecord, Poll, PollCategory> implements PollManager
{
	private PollResponseManager pollResponseManager;
	private PollAnswerManager pollAnswerManager;
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "research.pollManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "research.pollManager.namePlural";
	private static final String DEFAULTINTRO_KEY = "research.pollManager.defaultIntro";
	private static final String DEFAULTOUTRO_KEY = "research.pollManager.defaultOutro";

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Poll> clone(Poll item)
	{
		try
		{
			if ( item.exists() )
			{
				// XXX: This should already be taken care of outside but we need
				// to load all of the internal properties of the object (i.e.
				// the poll answers)
				//
				initModelObject(item);

				String currTimeStamp = String.valueOf(Utils.currentDateTime());
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				Poll poll = new Poll();
				poll.copyFrom(item);
	
				poll.setSer(0);
				poll.setTitle(workingTitle);
				poll.setStartTimeSeconds(item.getStartTimeSeconds() + (86400 * 365));
				poll.setEndTimeSeconds(item.getEndTimeSeconds() + (86400 * 366));
				poll.setPollingStartTimeSeconds(item.getPollingStartTimeSeconds() + (86400 * 365));
				poll.setPollingEndTimeSeconds(item.getPollingEndTimeSeconds() + (86400 * 365));
	
				ReturnType<Poll> createdPoll = create(poll);

				return createdPoll;
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
	public ReturnType<Poll> createInternal(Poll poll)
	{
		// Can't have null values for the correct answer, so we set it to
		// some arbitrary non-zero value as a place holder to be reset to
		// the correct serial number once we have one.
		//
		ReturnType<Poll> retVal = super.createInternal(poll);

		for (PollAnswer pollAnswer : poll.getPollAnswers() )
		{
			// Force the poll answer to have the newly created poll
			// (although by this point it already should have it)
			//
			pollAnswer.setPoll(retVal.getPost());
		}

		List<ReturnType<PollAnswer>> processedAnswers = getPollAnswerManager().processPollAnswers(poll.getPollAnswers());
		retVal.addChain(getPollAnswerManager(), "processPollAnswers", new ReturnType<>(processedAnswers));

		// Should have been set by actualizeRelations but just in case something goes wrong
		// we should maintain consistency here
		//
		getDao().updateAnswerCount(poll.getSer());

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Poll> updateInternal(PollRecord pollRecord, Poll poll)
	{
		// We override this method instead of update in order to ensure that initModelObject is called
		// after the answers have been processed
		//
		ReturnType<Poll> retVal = super.updateInternal(pollRecord, poll);

		List<ReturnType<PollAnswer>> processedAnswers = getPollAnswerManager().processPollAnswers(poll.getPollAnswers());
		retVal.addChain(getPollAnswerManager(), "processPollAnswers", new ReturnType<>(processedAnswers));

		// Should have been set by actualizeRelations but just in case something goes wrong
		// we should maintain consistency here
		//
		getDao().updateAnswerCount(poll.getSer());

		return retVal;
	}

	@Override
	public PollSettings getSettings(SingleClient client)
	{
		PollSettings retVal = new PollSettings();
		retVal.setClient(client);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		List<Message> intros = getMessageManager().getAllMessages(client, DEFAULTINTRO_KEY);
		List<Message> outros = getMessageManager().getAllMessages(client, DEFAULTOUTRO_KEY);
		retVal.setIntroOutro(intros, outros);

		boolean enabled = Utils.defaultIfNaB(getClientOptionManager().getOption(client, getEnabledOptionKey()).getValue(), true);
		retVal.setEnabled(enabled);

		return retVal;
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<PollSettings> updateSettings(PollSettings settings)
	{
		ReturnType<PollSettings> retVal = new ReturnType<>(settings);

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		// XXX: Consolidate this into a single list and create a new method in MessageManager to update
		// a batch of messages so that the historical records are chained together.

		List<Message> singularNames = settings.getNameSingleMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = settings.getNamePluralMessages(client, NAMEPLURAL_KEY);

		List<Message> intros = settings.getDefaultIntroMessages(client, DEFAULTINTRO_KEY);
		List<Message> outros = settings.getDefaultOutroMessages(client, DEFAULTOUTRO_KEY);

		singularNames.forEach( (message) -> { getMessageManager().update(message); } );
		pluralNames.forEach( (message) -> { getMessageManager().update(message); } );

		intros.forEach( (message) -> { getMessageManager().update(message); } );
		outros.forEach( (message) -> { getMessageManager().update(message); } );

		updateOption(client, getEnabledOptionKey(), String.valueOf(settings.isEnabled()), retVal);

		return retVal;
	}

	@Override
	public PollRecordDao getDao()
	{
		return (PollRecordDao) super.getDao();
	}

	public PollAnswerManager getPollAnswerManager()
	{
		return pollAnswerManager;
	}

	@Autowired
	public void setPollAnswerManager(PollAnswerManager answerManager)
	{
		this.pollAnswerManager = answerManager;
	}

	public PollResponseManager getPollResponseManager()
	{
		return pollResponseManager;
	}

	@Autowired
	public void setPollResponseManager(PollResponseManager pollResponseManager)
	{
		this.pollResponseManager = pollResponseManager;
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
	public Poll emptyInstance()
	{
		return new Poll();
	}

	@Override
	protected Poll initModelObject(Poll item)
	{
		item.setPollAnswers(getPollAnswerManager().getAnswers(item));

		return item;
	}
}
