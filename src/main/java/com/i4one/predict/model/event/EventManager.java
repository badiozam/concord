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
package com.i4one.predict.model.event;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.categorizable.CategorizableTerminableManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface EventManager extends CategorizableTerminableManager<EventRecord, Event>
{
	/**
	 * Get all events that have actualized outcomes after the given time stamp
	 * 
	 * @param client The client that the events belong to
	 * @param starttm The start time to search for all actualized outcomes
	 * @param pagination The pagination/sort ordering information
	 * 
	 * @return A (potentially empty) list of all actualized events
	 */
	public Set<Event> getAllActualized(SingleClient client, int starttm, PaginationFilter pagination);

	/**
	 * Set the actual outcome for the given event. Note that this method only sets
	 * the actual outcome but does not perform any actualization for members who
	 * selected the correct prediction.
	 * 
	 * @see EventPredicitonManager.actualizeOutcomeForAll(..)
	 * 
	 * @param event The event with the internal actualOutcome member set
	 * 
	 * @return The updated event object
	 */
	public ReturnType<Event> setActualOutcome(Event event);
}
