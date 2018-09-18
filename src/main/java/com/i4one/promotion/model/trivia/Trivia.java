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
import com.i4one.base.model.ForeignKey;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.SimpleSortableType;
import com.i4one.base.model.SortableType;
import com.i4one.base.model.manager.instantwinnable.SimpleTerminableInstantWinnable;
import com.i4one.base.model.manager.triggerable.SimpleTerminableTriggerable;
import com.i4one.base.model.manager.instantwinnable.TerminableInstantWinnableClientType;
import com.i4one.base.model.manager.triggerable.TerminableTriggerableClientType;
import com.i4one.base.model.manager.categorizable.BaseCategorizableTerminableSiteGroupType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.balancetrigger.ExclusiveBalanceTrigger;
import com.i4one.base.model.instantwin.ExclusiveInstantWin;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.promotion.model.trivia.category.TriviaCategory;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A trivia game.
 * 
 * @author Hamid Badiozamani
 */
public class Trivia extends BaseCategorizableTerminableSiteGroupType<TriviaRecord, TriviaCategory> implements TerminableTriggerableClientType<TriviaRecord, Trivia>, TerminableInstantWinnableClientType<TriviaRecord, Trivia>, SortableType<TriviaRecord>
{
	static final long serialVersionUID = 42L;

	public static final int TYPE_SINGLEANSER_RADIO = 0;
	public static final int TYPE_SINGLEANSWER_SELECT = 1;

	private transient Set<TriviaAnswer> triviaAnswers;
	private transient ForeignKey<TriviaAnswerRecord, TriviaAnswer> correctAnswerFk;

	private final transient SimpleTerminableTriggerable<TriviaRecord, Trivia> triggerable;
	private final transient SimpleTerminableInstantWinnable<TriviaRecord, Trivia> instantWinnable;
	private final transient SortableType sortable;

	public Trivia()
	{
		super(new TriviaRecord());

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);

		sortable = new SimpleSortableType(this);
	}

	protected Trivia(TriviaRecord delegate)
	{
		super(delegate);

		triggerable = new SimpleTerminableTriggerable<>(this);
		instantWinnable = new SimpleTerminableInstantWinnable<>(this);

		sortable = new SimpleSortableType(this);
	}

	@Override
	public void init()
	{
		super.init();

		correctAnswerFk = new ForeignKey<>(this,
			getDelegate()::getCorrectanswerid,
			getDelegate()::setCorrectanswerid,
			() -> { return new TriviaAnswer(); });

		if ( triviaAnswers == null )
		{
			triviaAnswers = new LinkedHashSet<>();
		}
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		if ( getDelegate().getIntro().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".intro.empty", "Intro cannot be empty", new Object[]{"item", this}));
		}

		if ( getDelegate().getOutro().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".outro.empty", "Outro cannot be empty", new Object[]{"item", this}));
		}

		retVal.merge(triggerable.validate());
		retVal.merge(instantWinnable.validate());

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		triggerable.setOverrides();
		instantWinnable.setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		triggerable.actualizeRelations();
		instantWinnable.actualizeRelations();

		int answerCount = 0;
		for (TriviaAnswer answer : getTriviaAnswers() )
		{
			if ( !answer.getAnswer().isBlank() )
			{
				// The answer count is only incremented for those
				// answers that exist
				//
				answerCount++;
			}
		}

		setAnswerCount(answerCount);

		correctAnswerFk.actualize();
	}

	public IString getDescr()
	{
		return getDelegate().getDescr();
	}

	public void setDescr(IString descr)
	{
		getDelegate().setDescr(descr);
	}

	public IString getIntro()
	{
		return getDelegate().getIntro();
	}

	public void setIntro(IString intro)
	{
		getDelegate().setIntro(intro);
	}

	public IString getOutro()
	{
		return getDelegate().getOutro();
	}

	public void setOutro(IString intro)
	{
		getDelegate().setOutro(intro);
	}

	public boolean getRandomize()
	{
		return getDelegate().getRandomize();
	}

	public void setRandomize(boolean randomize)
	{
		getDelegate().setRandomize(randomize);
	}

	public int getAnswerCount()
	{
		return getDelegate().getAnswercount();
	}

	public void setAnswerCount(int answerCount)
	{
		getDelegate().setAnswercount(answerCount);
	}

	@Override
	public int getOrderWeight()
	{
		return sortable.getOrderWeight();
	}

	@Override
	public void setOrderWeight(int orderweight)
	{
		sortable.setOrderWeight(orderweight);
	}

	public int getQuestionType()
	{
		return getDelegate().getQuestiontype();
	}

	public void setQuestionType(int questionType)
	{
		getDelegate().setQuestiontype(questionType);
	}

	public TriviaAnswer getCorrectAnswer()
	{
		return getCorrectAnswer(true);
	}

	public TriviaAnswer getCorrectAnswer(boolean doLoad)
	{
		return correctAnswerFk.get(doLoad);
	}

	public void setCorrectAnswer(TriviaAnswer correctAnswer)
	{
		// It's important not to load this from the database and overwrite
		// whatever values may have been set in memory by the callers
		//
		correctAnswerFk.get(false).setCorrect(false);
		correctAnswerFk.set(correctAnswer);
		correctAnswerFk.get(false).setCorrect(true);
	}

	public Set<TriviaAnswer> getTriviaAnswers()
	{
		return Collections.unmodifiableSet(triviaAnswers);
	}

	public void setTriviaAnswers(Collection<TriviaAnswer> answers)
	{
		this.triviaAnswers.clear();
		this.triviaAnswers.addAll(answers);
	}

	@Override
	protected void copyFromInternal(RecordTypeDelegator<TriviaRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyFromInternal(right);
		if ( right instanceof Trivia )
		{
			Trivia rightTrivia = (Trivia)right;

			triviaAnswers.clear();
			for ( TriviaAnswer triviaAnswer : rightTrivia.getTriviaAnswers() )
			{
				TriviaAnswer newAnswer = new TriviaAnswer();
				newAnswer.copyFrom(triviaAnswer);
				newAnswer.setSer(0);

				triviaAnswers.add(newAnswer);

				// Should check for the correct answer as we go through
				//
				if ( rightTrivia.getCorrectAnswer().equals(triviaAnswer) )
				{
					setCorrectAnswer(newAnswer);
				}
			}
		}
	}

	@Override
	protected void copyOverInternal(RecordTypeDelegator<TriviaRecord> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		super.copyOverInternal(right);
		if ( right instanceof Trivia )
		{
			Trivia rightTrivia = (Trivia)right;

			// If the right trivia's answers aren't empty, then regardless
			// of our answers we'll want to copy the right's trivia's answers
			//
			if ( !rightTrivia.getTriviaAnswers().isEmpty() )
			{
				copyFromInternal(right);
			}
		}
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getTitle() + "-" + getStartTimeSeconds() + "-" + getEndTimeSeconds();
	}

	@Override
	protected boolean equalsInternal(RecordTypeDelegator<TriviaRecord> right)
	{
		if ( right instanceof Trivia )
		{
			Trivia rightTrivia = (Trivia)right;
			return getStartTimeSeconds() == rightTrivia.getStartTimeSeconds() &&
				getEndTimeSeconds() == rightTrivia.getEndTimeSeconds() &&
				getTitle().equals(rightTrivia.getTitle());
		}
		else
		{
			return false;
		}
	}

	@Override
	protected boolean fromCSVInternal(List<String> csv)
	{
		if  ( super.fromCSVInternal(csv) )
		{
			String outro = csv.get(0); csv.remove(0);
			setOutro(new IString(outro));

			boolean retVal = true;

			retVal &= triggerable.fromCSVList(csv);
			retVal &= instantWinnable.fromCSVList(csv);

			return retVal;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected StringBuilder toCSVInternal(boolean header)
	{
		StringBuilder retVal = super.toCSVInternal(header);

		if ( header )
		{
			// XXX: Needs to be i18n
			retVal.append(Utils.csvEscape("Outro")).append(",");
		}
		else
		{
			retVal.append("\"").append(Utils.csvEscape(getOutro().toString())).append("\",");
		}

		retVal.append(triggerable.toCSV(header));
		retVal.append(instantWinnable.toCSV(header));

		return retVal;
	}

	@Override
	protected TriviaCategory initCategory(int categoryid)
	{
		TriviaCategory retVal = new TriviaCategory();
		retVal.setSer(categoryid);
		retVal.loadedVersion();

		return retVal;
	}

	@Override
	public Set<BalanceTrigger> getBalanceTriggers()
	{
		return triggerable.getBalanceTriggers();
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> balanceTriggers)
	{
		triggerable.setBalanceTriggers(balanceTriggers);
	}

	@Override
	public Set<ExclusiveBalanceTrigger<Trivia>> getExclusiveBalanceTriggers()
	{
		return triggerable.getExclusiveBalanceTriggers();
	}

	@Override
	public Set<BalanceTrigger> getNonExclusiveBalanceTriggers()
	{
		return triggerable.getNonExclusiveBalanceTriggers();
	}

	@Override
	public Set<InstantWin> getInstantWins()
	{
		return instantWinnable.getInstantWins();
	}

	@Override
	public void setInstantWins(Collection<InstantWin> instantWins)
	{
		instantWinnable.setInstantWins(instantWins);
	}

	@Override
	public Set<ExclusiveInstantWin<Trivia>> getExclusiveInstantWins()
	{
		return instantWinnable.getExclusiveInstantWins();
	}

	@Override
	public Set<InstantWin> getNonExclusiveInstantWins()
	{
		return instantWinnable.getNonExclusiveInstantWins();
	}
}
