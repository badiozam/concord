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
package com.i4one.research.model.survey;

import com.i4one.base.core.BlockingSingleItemCollection;
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
import com.i4one.research.model.survey.answer.Answer;
import com.i4one.research.model.survey.category.SurveyCategory;
import com.i4one.research.model.survey.question.Question;
import com.i4one.research.model.survey.question.QuestionManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleSurveyManager extends BaseSimpleCategorizableManager<SurveyRecord, Survey, SurveyCategory> implements SurveyManager
{
	private QuestionManager questionManager;
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "research.surveyManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "research.surveyManager.namePlural";
	private static final String DEFAULTINTRO_KEY = "research.surveyManager.defaultIntro";
	private static final String DEFAULTOUTRO_KEY = "research.surveyManager.defaultOutro";

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Survey> clone(Survey item)
	{
		try
		{
			if ( item.exists() )
			{
				String currTimeStamp = String.valueOf(Utils.currentDateTime());
				IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");
	
				Survey survey = new Survey();
				survey.copyFrom(item);
	
				survey.setSer(0);
				survey.setTitle(workingTitle);
				survey.setStartTimeSeconds(item.getStartTimeSeconds() + (86400 * 365));
				survey.setEndTimeSeconds(item.getEndTimeSeconds() + (86400 * 366));
	
				ReturnType<Survey> createdSurvey = create(survey);

				// Consider moving this into the create(..) method instead by setting the references within each
				// object and then having the create method traverse it and create recursively
				//
				List<ReturnType<Question>> createdQuestions = new ArrayList<>();
				for ( Question currQuestion : getQuestionManager().getQuestions(item, SimplePaginationFilter.NONE) )
				{
					Question questionCopy = new Question();
					questionCopy.copyFrom(currQuestion);
					questionCopy.setSer(0);
					questionCopy.setSurvey(createdSurvey.getPost());
					questionCopy.getAnswers().clear();

					for ( Answer currAnswer : currQuestion.getAnswers() )
					{
						Answer answerCopy = new Answer();
						answerCopy.copyFrom(currAnswer);
						answerCopy.setSer(0);

						questionCopy.getAnswers().add(answerCopy);
					}

					ReturnType<Question> questionCreate = getQuestionManager().create(questionCopy);
					createdSurvey.getPost().getQuestions().add(questionCreate.getPost());

					createdQuestions.add(questionCreate);
				}
	
				// Set all of the return types as part of the return value chain
				//
				ReturnType<List<ReturnType<Question>>> createdQuestionsRetVal = new ReturnType<>();
				createdQuestionsRetVal.setPost(createdQuestions);
				createdSurvey.addChain(getQuestionManager(), "create", createdQuestionsRetVal);

				return createdSurvey;
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
	public ReturnType<Survey> update(Survey survey)
	{
		// XXX: This should ideally be done at the DAO level
		// Make sure the question count is always consistent when updating a survey
		//
		survey.setQuestionCount(getDao().getQuestionCount(survey.getSer()));
		ReturnType<Survey> retVal = super.update(survey);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<Survey>> importCSV(InputStream stream, Supplier<Survey> instantiator, Function<Survey,Boolean> preprocessor, Consumer<ReturnType<Survey>> postprocessor)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			Survey newSurvey = instantiator.get();

			// We only import 1 survey at a time
			//
			List<ReturnType<Survey>> retVal = new ArrayList<>(1);
			Map<Integer, Question> questions = new HashMap<>();
			Map<Integer, List<Answer>> answers = new HashMap<>();

			String state = "survey";
			int lineNo = 0;

			String currLine;
			boolean stopped = false;
			do
			{
				lineNo++;
				currLine = reader.readLine();
	
				if ( !Utils.isEmpty(currLine) && !currLine.startsWith("###"))
				{
					if ( state.equals("survey"))
					{
						if ( currLine.toLowerCase().startsWith("survey") )
						{
							// The first heading line is optional we just ignore
							//
						}
						else
						{
							if ( newSurvey.fromCSV(currLine) )
							{
								state = "questionsHeader";

								// Only create if the preprocessor allows us to
								//
								if ( preprocessor.apply(newSurvey) )
								{
									try
									{
										ReturnType<Survey> createdSurvey = create(newSurvey);
										postprocessor.accept(createdSurvey);
									}
									catch (Errors errors )
									{
										ReturnType<Survey> notImported = new ReturnType<>();
										notImported.put("importErrors", new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.notcreated", "Could not import item, database error while creating", new Object[] {"lineNo", lineNo})));
										retVal.add(notImported);

										stopped = true;
									}
								}
							}
							else
							{
								ReturnType<Survey> notImported = new ReturnType<>();
								notImported.put("importErrors", new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.invalidCSV", "Could not import item due to invalid CSV format at line $lineNo", new Object[] {"lineNo", lineNo})));
	
								retVal.add(notImported);
								stopped = true;
							}
						}
					}
					else if  ( state.equals("questionsHeader"))
					{
						if ( currLine.toLowerCase().startsWith("questions") )
						{
							state = "questions";
						}
						else
						{
							ReturnType<Survey> notImported = new ReturnType<>();
							notImported.put("importErrors", new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.invalidCSV", "Could not import item due to invalid CSV format at line $lineNo, expecting 'QUESTIONS' but got $line", new Object[] {"lineNo", lineNo, "line", currLine})));

							retVal.add(notImported);
							stopped = true;
						}
					}
					else if ( state.equals("questions"))
					{
						if ( currLine.toLowerCase().startsWith("answers"))
						{
							state = "answers";
						}
						else
						{
							Question newQuestion = new Question();
							newQuestion.setSurvey(newSurvey);

							if ( newQuestion.fromCSV(currLine) )
							{
								questions.put(newQuestion.getSer(), newQuestion);
							}
							else
							{
								ReturnType<Survey> notImported = new ReturnType<>();
								notImported.put("importErrors", new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.invalidCSV", "Could not import item due to invalid CSV format at line $lineNo", new Object[] {"lineNo", lineNo})));

								retVal.add(notImported);
								stopped = true;
							}
						}
					}
					else // if ( state.equals("answers") )
					{
						if ( currLine.toLowerCase().startsWith("survey"))
						{
							state = "survey";

							// We have everything we need from the last survey, create it and move on to the next one
							//
							makeImportedSurvey(retVal, newSurvey, questions, answers);
							questions.clear();
							answers.clear();

							// Start a new survey to read
							//
							newSurvey = instantiator.get();
						}
						else
						{
							Answer newAnswer = new Answer();
	
							if ( newAnswer.fromCSV(currLine) )
							{
								int questionId = newAnswer.getQuestion(false).getSer();
								List<Answer> currAnswers = answers.get(questionId);
								if ( currAnswers == null )
								{
									currAnswers = new ArrayList<>();
									answers.put(questionId, currAnswers);
								}
								currAnswers.add(newAnswer);
							}
							else
							{
								ReturnType<Survey> notImported = new ReturnType<>();
								notImported.put("importErrors", new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.invalidCSV", "Could not import item due to invalid CSV format at line $lineNo", new Object[] {"lineNo", lineNo})));
	
								retVal.add(notImported);
								stopped = true;
							}
						}
					}
				}
	
			} while ( currLine != null && !stopped );

			if ( !stopped )
			{
				// Make the last survey in the file before exiting
				//
				makeImportedSurvey(retVal, newSurvey, questions, answers);
			}

			return retVal;
		}
		catch (IOException ioe)
		{
			throw new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.error", "Could not import items", new Object[] { "ex", ioe }, ioe));
		}
	}

	private void makeImportedSurvey(List<ReturnType<Survey>> retVal, Survey newSurvey, Map<Integer, Question> questions, Map<Integer, List<Answer>> answers)
	{
		// We read this survey successfully
		//
		for ( Question question : questions.values() )
		{
			question.setAnswers(answers.get(question.getSer()));
			question.setSurvey(newSurvey);
			question.setSer(0);

			try
			{
				ReturnType<Question> createdQuestion = getQuestionManager().create(question);
			}
			catch (Errors errors)
			{
				ReturnType<Survey> notImported = new ReturnType<>();
				notImported.put("importErrors", new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.invalidCSV", "Could not import item due to database error when creating question $question.question", new Object[] {"question", question}, errors)));
				retVal.add(notImported);

				break;
			}
		}

		// Load all of the questions/answers
		//
		Survey result = getById(newSurvey.getSer());
		ReturnType<Survey> createdSurvey = new ReturnType<>();
		createdSurvey.setPost(result);

		retVal.add(createdSurvey);
	}

	@Override
	public OutputStream exportCSV(Set<Survey> items, OutputStream out)
	{
		try
		{
			DataOutputStream retVal = new DataOutputStream(out);
	
			// This buffer is populated with each item from the list of items in the current thread,
			// the class will block the current thread until its item has been read. As such, the
			// output stream must be emptied (or closed) for this call to become unblocked
			//
			BlockingSingleItemCollection<Survey> itemBuffer = new BlockingSingleItemCollection<>();
	
			// This thread loads each item and feeds it to the stream
			//
			Runnable loadDataTask = ( () ->
			{
				for ( Survey currItem : items )
				{
					// This call will block until the item that was added
					// is consumed
					//
					itemBuffer.add(currItem);
				}
	
				itemBuffer.close();
			});
	
			// Begin adding items
			//
			Thread rawDataThread = new Thread(loadDataTask);
			rawDataThread.start();

			// At this point, there is a thread that is adding items to the collection, the reason
			// Here, we consume each item by exporting it to the output stream one by one
			//
			Iterator<Survey> it = itemBuffer.iterator();
			while ( it.hasNext() )
			{
				// This unblocks the itemBuffer and a subsequent id is added
				// or the stream is closed
				//
				Survey item = it.next();
	
				if ( item != null )
				{
					retVal.writeBytes("SURVEY"); retVal.write('\n');
					retVal.writeBytes(item.toCSV(true)); retVal.writeBytes("\n");
					retVal.writeBytes(item.toCSV(false)); retVal.write('\n');

					boolean printHeader = true;
					retVal.writeBytes("QUESTIONS"); retVal.write('\n');
					for ( Question question : item.getQuestions() )
					{
						if ( printHeader )
						{
							retVal.writeBytes(question.toCSV(true)); retVal.writeBytes("\n");
							printHeader = false;
						}
						retVal.writeBytes(question.toCSV(false)); retVal.writeBytes("\n");
					}

					printHeader = true;
					retVal.writeBytes("ANSWERS"); retVal.write('\n');
					for ( Question question : item.getQuestions() )
					{
						for ( Answer answer : question.getAnswers() )
						{
							if ( printHeader )
							{
								retVal.writeBytes(answer.toCSV(true)); retVal.writeBytes("\n");
								printHeader = false;
							}
							retVal.writeBytes(answer.toCSV(false)); retVal.writeBytes("\n");
						}
					}
				}
			}
	
			return out;
		}
		catch (IOException ioe)
		{
			throw new Errors(ioe);
		}
	}

	@Override
	public SurveySettings getSettings(SingleClient client)
	{
		SurveySettings retVal = new SurveySettings();
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
	public ReturnType<SurveySettings> updateSettings(SurveySettings settings)
	{
		ReturnType<SurveySettings> retVal = new ReturnType<>(settings);

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
	public SurveyRecordDao getDao()
	{
		return (SurveyRecordDao) super.getDao();
	}

	public QuestionManager getQuestionManager()
	{
		return questionManager;
	}

	@Autowired
	public void setQuestionManager(QuestionManager questionManager)
	{
		this.questionManager = questionManager;
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
	public Survey emptyInstance()
	{
		return new Survey();
	}

	@Override
	protected Survey initModelObject(Survey item)
	{
		item.setQuestions(getQuestionManager().getQuestions(item, SimplePaginationFilter.NONE));

		return item;
	}
}
