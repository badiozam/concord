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
package com.i4one.predict.model.eventprediction;

import com.i4one.base.model.manager.activity.ActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.user.User;
import com.i4one.predict.model.event.Event;
import com.i4one.predict.model.eventoutcome.EventOutcome;
import com.i4one.predict.model.term.Term;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface EventPredictionManager extends ActivityManager<EventPredictionRecord, EventPrediction, Event>
{
	/**
	 * Retrieves all of a user's predictions for a particular game
	 *
	 * @param event The event to look up
	 * @param user The user whose predictions to retrieve
	 * @param pagination The pagination/sort ordering information
	 *
	 * @return A list of all of the user's predictions for the given event
	 */
	public Set<EventPrediction> getAllPredictions(Event event, User user, PaginationFilter pagination);

	/**
	 * Retrieves all of a user's predictions
	 *
	 * @param user The user whose predictions to retrieve
	 * @param term The term for which to retrieve the predictions
	 * @param pagination The pagination/sort ordering information
	 *
	 * @return A list of all of the user's predictions for the given term
	 */
	public Set<EventPrediction> getAllPredictionsByUser(User user, Term term, PaginationFilter pagination);

	/**
	 * Retrieves all of a user's predictions that are still awaiting results
	 *
	 * @param user The user whose predictions to retrieve
	 * @param pagination The pagination/sort ordering information
	 *
	 * @return A list of all of the pending predictions
	 */
	public Set<EventPrediction> getPendingPredictionsByUser(User user, PaginationFilter pagination);

	/**
	 * Retrieves all of a user's predictions that have had their results posted
	 *
	 * @param user The user whose predictions to retrieve
	 * @param pagination The pagination/sort ordering information
	 *
	 * @return A list of all of the posted predictions
	 */
	public Set<EventPrediction> getPostedPredictionsByUser(User user, PaginationFilter pagination);

	/**
	 * Set the actual outcome for an event and credit/debit all users for
	 * the given prediction game
	 * 
	 * @param actualOutcome The actual outcome of the event
	 */
	public void actualizeOutcomeForAll(EventOutcome actualOutcome);

	public EventPredictionActualizer getEventPredictionActualizer();
	public void setEventPredictionActualizer(EventPredictionActualizer eventPredictionActualizer);
}
