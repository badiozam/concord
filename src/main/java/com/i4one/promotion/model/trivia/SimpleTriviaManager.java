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
package com.i4one.promotion.model.trivia;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.categorizable.BaseSimpleCategorizableManager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.promotion.model.trivia.category.TriviaCategory;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleTriviaManager extends BaseSimpleCategorizableManager<TriviaRecord, Trivia, TriviaCategory> implements TriviaManager
{
	private TriviaResponseManager triviaResponseManager;
	private TriviaAnswerManager triviaAnswerManager;
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "promotion.triviaManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "promotion.triviaManager.namePlural";
	private static final String DEFAULTINTRO_KEY = "promotion.triviaManager.defaultIntro";
	private static final String DEFAULTOUTRO_KEY = "promotion.triviaManager.defaultOutro";

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Trivia> clone(Trivia item)
	{
		try
		{
			if ( item.exists() )
			{
				// XXX: This should already be taken care of outside but we need
				// to load all of the internal properties of the object (i.e.
				// the trivia answers and the correct answer)
				//
				initModelObject(item);

				String currTimeStamp = String.valueOf(Utils.currentDateTime());
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				Trivia trivia = new Trivia();
				trivia.copyFrom(item);
	
				trivia.setSer(0);
				trivia.setTitle(workingTitle);
				trivia.setStartTimeSeconds(item.getStartTimeSeconds() + (86400 * 365));
				trivia.setEndTimeSeconds(item.getEndTimeSeconds() + (86400 * 366));
	
				ReturnType<Trivia> createdTrivia = create(trivia);

				return createdTrivia;
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
	public ReturnType<Trivia> createInternal(Trivia trivia)
	{
		// Can't have null values for the correct answer, so we set it to
		// some arbitrary non-zero value as a place holder to be reset to
		// the correct serial number once we have one.
		//
		trivia.getDelegate().setCorrectanswerid(42);
		ReturnType<Trivia> retVal = super.createInternal(trivia);

		for (TriviaAnswer triviaAnswer : trivia.getTriviaAnswers() )
		{
			// Force the trivia answer to have the newly created trivia
			// (although by this point it already should have it)
			//
			triviaAnswer.setTrivia(retVal.getPost());
		}

		List<ReturnType<TriviaAnswer>> processedAnswers = getTriviaAnswerManager().processTriviaAnswers(trivia.getTriviaAnswers());
		retVal.addChain(getTriviaAnswerManager(), "processTriviaAnswers", new ReturnType<>(processedAnswers));

		// Update the correct answer's serial number
		//
		setCorrectAnswer(trivia);

		// Should have been set by actualizeRelations but just in case something goes wrong
		// we should maintain consistency here
		//
		getDao().updateAnswerCount(trivia.getSer());

		return retVal;
	}

	protected void setCorrectAnswer(Trivia trivia)
	{
		for (TriviaAnswer triviaAnswer : trivia.getTriviaAnswers() )
		{
			// Here we know the correct answer has to have a serial number
			//
			if ( triviaAnswer.isCorrect() )
			{
				getDao().setCorrectAnswer(trivia.getSer(), triviaAnswer.getSer());
				trivia.setCorrectAnswer(triviaAnswer);

				break;
			}
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Trivia> updateInternal(TriviaRecord triviaRecord, Trivia trivia)
	{
		// We override this method instead of update in order to ensure that initModelObject is called
		// after the answers have been processed
		//
		ReturnType<Trivia> retVal = super.updateInternal(triviaRecord, trivia);

		List<ReturnType<TriviaAnswer>> processedAnswers = getTriviaAnswerManager().processTriviaAnswers(trivia.getTriviaAnswers());
		retVal.addChain(getTriviaAnswerManager(), "processTriviaAnswers", new ReturnType<>(processedAnswers));

		if ( !Objects.equals(triviaRecord.getCorrectanswerid(), trivia.getCorrectAnswer(false).getSer()) )
		{
			if ( getTriviaResponseManager().hasActivity(trivia) )
			{
				// Can't switch the correct answer on people who already answered since they would
				// then show up as having answered incorrectly on reports.
				//
				Errors errors = new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".hasresponses.", "This trivia game's correct answer can't be changed because there are users who have already played it.", new Object[] { "item", trivia }));

				throw errors;
			}
			else
			{
				// Update the correct answer's serial number
				//
				setCorrectAnswer(trivia);
			}
		}

		// Should have been set by actualizeRelations but just in case something goes wrong
		// we should maintain consistency here
		//
		getDao().updateAnswerCount(trivia.getSer());

		return retVal;
	}

	@Override
	public TriviaSettings getSettings(SingleClient client)
	{
		TriviaSettings retVal = new TriviaSettings();
		retVal.setClient(client);

		boolean enabled = Utils.defaultIfNaB(getClientOptionManager().getOption(client, getEnabledOptionKey()).getValue(), true);
		retVal.setEnabled(enabled);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		List<Message> intros = getMessageManager().getAllMessages(client, DEFAULTINTRO_KEY);
		List<Message> outros = getMessageManager().getAllMessages(client, DEFAULTOUTRO_KEY);
		retVal.setIntroOutro(intros, outros);

		return retVal;
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<TriviaSettings> updateSettings(TriviaSettings settings)
	{
		ReturnType<TriviaSettings> retVal = new ReturnType<>(settings);

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
	public TriviaRecordDao getDao()
	{
		return (TriviaRecordDao) super.getDao();
	}

	public TriviaAnswerManager getTriviaAnswerManager()
	{
		return triviaAnswerManager;
	}

	@Autowired
	public void setTriviaAnswerManager(TriviaAnswerManager answerManager)
	{
		this.triviaAnswerManager = answerManager;
	}

	public TriviaResponseManager getTriviaResponseManager()
	{
		return triviaResponseManager;
	}

	@Autowired
	public void setTriviaResponseManager(TriviaResponseManager triviaResponseManager)
	{
		this.triviaResponseManager = triviaResponseManager;
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
	public Trivia emptyInstance()
	{
		return new Trivia();
	}

	@Override
	protected Trivia initModelObject(Trivia item)
	{
		Trivia retVal = super.initModelObject(item);

		retVal.setTriviaAnswers(getTriviaAnswerManager().getAnswers(item, SimplePaginationFilter.NONE));

		return retVal;
	}
}
