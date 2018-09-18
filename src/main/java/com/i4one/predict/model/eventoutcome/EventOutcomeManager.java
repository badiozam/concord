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
package com.i4one.predict.model.eventoutcome;

import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.predict.model.event.Event;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface EventOutcomeManager extends Manager<EventOutcomeRecord, EventOutcome>
{
	/**
	 * Get all of the possible event outcomes for a given event
	 *
	 * @param e The event
	 * @param pagination The pagination/sort ordering information
	 *
	 * @return The (potentially empty) list of possible event outcomes
	 */
	public Set<EventOutcome> getEventOutcomes(Event e, PaginationFilter pagination);

	/**
	 * Count how many users have selected this outcome as a likely candidate
	 * 
	 * @param eventOutcome The outcome to look up
	 * 
	 * @return The total number of people who have selected this outcome
	 */
	public int getUsageCount(EventOutcome eventOutcome);
}
