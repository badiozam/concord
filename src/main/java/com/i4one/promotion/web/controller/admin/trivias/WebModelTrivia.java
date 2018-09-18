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
package com.i4one.promotion.web.controller.admin.trivias;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.balancetrigger.BalanceTrigger;
import com.i4one.base.model.instantwin.InstantWin;
import com.i4one.base.web.controller.TerminableInstantWinnableWebModel;
import com.i4one.base.web.controller.TerminableTriggerableWebModel;
import com.i4one.promotion.model.trivia.Trivia;
import com.i4one.promotion.model.trivia.TriviaAnswer;
import com.i4one.promotion.model.trivia.TriviaRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.AutoPopulatingList;
import org.springframework.util.AutoPopulatingList.ElementFactory;

/**
 * @author Hamid Badiozamani
 */
public class WebModelTrivia extends Trivia implements ElementFactory<TriviaAnswer>
{
	public static final int DEFAULT_TRIVIA_ANSWER_SIZE = 6;

	private final transient TerminableTriggerableWebModel<TriviaRecord,Trivia> triggerable;
	private final transient TerminableInstantWinnableWebModel<TriviaRecord,Trivia> instantWinning;

	private transient int correctIndex;
	private transient List<TriviaAnswer> answers;

	public WebModelTrivia()
	{
		super();

		triggerable = new TerminableTriggerableWebModel<>(this);
		instantWinning = new TerminableInstantWinnableWebModel<>(this);

		answers = new AutoPopulatingList<>(this);
		correctIndex = -1;
	}

	protected WebModelTrivia(TriviaRecord delegate)
	{
		super(delegate);

		triggerable = new TerminableTriggerableWebModel<>(this);
		instantWinning = new TerminableInstantWinnableWebModel<>(this);

		answers = new AutoPopulatingList<>(this);
		correctIndex = -1;
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		retVal = triggerable.validateModel(retVal);
		retVal = instantWinning.validateModel(retVal);

		// If the correct answer were to be loaded from the database,
		// that would overwrite our changes. Consider having a
		// separately owned correctAnswer object to test in order
		// to avoid database overwrites
		//
		if ( getCorrectAnswer(false).getAnswer().isBlank() )
		{
			retVal.addError(new ErrorMessage("msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".correctIndex.invalid", "Please select a correct answer", new Object[]{"item", this}));
		}
		else
		{
			getLogger().debug("Correct answer " + getCorrectAnswer(false) + " is not blank");
		}

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		triggerable.setModelOverrides();
		instantWinning.setModelOverrides();

		// Since we're managing the answers as well we need to ensure that
		// their overrides are also set
		//
		List<TriviaAnswer> filteredAnswers = getFilteredAnswers();
		filteredAnswers.forEach( (triviaAnswer) ->
		{
			triviaAnswer.setTrivia(this);

			// No need to set the overrides of the answers here since
			// that's taken care of by the manager when it creates/updates
			// each answer
			//
			// triviaAnswer.setOverrides();
		});

		setTriviaAnswers(filteredAnswers);

		// We only set the correct answer if it's within bounds and let
		// validate() throw an error if it's not
		//
		if ( correctIndex < filteredAnswers.size() )
		{
			getLogger().debug("Setting correct answer to " + filteredAnswers.get(correctIndex));
			setCorrectAnswer(filteredAnswers.get(correctIndex));
		}
		else
		{
			// The important thing to note here is that the correct
			// answer is empty, which shuold trigger a validate() error
			//
			setCorrectAnswer(new TriviaAnswer());
		}

		// We have to set the overrides here after the balance triggers have been
		// set to our filtered results
		//
		super.setOverrides();
	}

	protected List<TriviaAnswer> getFilteredAnswers()
	{
		return getAnswers().stream()
			.filter( (answer) -> { return answer != null; } )

			// Blank answers are to be handled by the manager (i.e. either removed or ignored)
			//
			//.filter( (answer) -> { return answer.exists() || !answer.getAnswer().isBlank(); } )
			.collect(Collectors.toList());
	}

	@Override
	public void setBalanceTriggers(Collection<BalanceTrigger> newTriggers)
	{
		super.setBalanceTriggers(newTriggers);

		triggerable.setBalanceTriggers(newTriggers);
	}

	@Override
	public void setInstantWins(Collection<InstantWin> newInstantWins)
	{
		super.setInstantWins(newInstantWins);

		instantWinning.setInstantWins(newInstantWins);
	}

	@Override
	public void setTriviaAnswers(Collection<TriviaAnswer> newAnswers)
	{
		getLogger().debug("Setting trivia answers to " + Utils.toCSV(newAnswers) + " called from ", new Throwable());
		super.setTriviaAnswers(newAnswers);

		// We have to force a reload of the answers since they may have
		// changed in the database
		//
		answers.clear();
	}

	@Override
	public void setCorrectAnswer(TriviaAnswer correctAnswer)
	{
		super.setCorrectAnswer(correctAnswer);

		// Force a reload of the correct index
		//
		correctIndex = -1;
	}

	public TerminableTriggerableWebModel<TriviaRecord, Trivia> getTriggerable()
	{
		return triggerable;
	}

	public TerminableInstantWinnableWebModel<TriviaRecord, Trivia> getInstantWinning()
	{
		return instantWinning;
	}

	public List<TriviaAnswer> getAnswers()
	{
		if ( answers == null || answers.isEmpty() )
		{
			getLogger().debug("getAnswers() initializing answers from " + Utils.toCSV(getTriviaAnswers()));

			Collection<TriviaAnswer> triviaAnswers = getTriviaAnswers();
			List<TriviaAnswer> backingList = new ArrayList<>(triviaAnswers);

			answers = new AutoPopulatingList<>(backingList, this);
		}

		// This creates the elements up to this point if they didn't already exist
		//
		answers.get(DEFAULT_TRIVIA_ANSWER_SIZE - 1);

		return answers;
	}

	public void setAnswers(List<TriviaAnswer> answers)
	{
		this.answers = answers;
	}

	@Override
	public TriviaAnswer createElement(int index) throws AutoPopulatingList.ElementInstantiationException
	{
		return new WebModelTriviaAnswer();
	}

	public int getCorrectIndex()
	{
		if ( correctIndex < 0 )
		{
			// We use the super-class method to get the correct answer
			// because we want to ensure it has been properly loaded
			//
			TriviaAnswer correctAnswer = getCorrectAnswer();

			correctIndex = 0;
			for ( TriviaAnswer currAnswer : getTriviaAnswers() )
			{
				if ( currAnswer.equals(correctAnswer) )
				{
					break;
				}

				correctIndex++;
			}
		}

		return correctIndex;
	}

	public void setCorrectIndex(int correctIndex)
	{
		this.correctIndex = correctIndex;
	}

}