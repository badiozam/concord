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
package com.i4one.predict.web.controller.user;

import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.predict.model.eventprediction.EventPrediction;
import com.i4one.predict.model.term.Term;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hamid Badiozamani
 */
public class PredictionHistoryPagination extends SimplePaginationFilter implements PaginationFilter
{
	static final long serialVersionUID = 42L;
	
	private static final int MAXPERPAGE = 250;

	private Term term;
	private List<EventPrediction> predictions;

	private boolean onlyInitialPredictions;

	public PredictionHistoryPagination()
	{
		term = new Term();

		super.setCurrentPage(0);
		super.setPerPage(MAXPERPAGE);

		predictions = new ArrayList<>();
		onlyInitialPredictions = false;
	}

	public PredictionHistoryPagination(PaginationFilter chain)
	{
		super(chain);

		term = new Term();

		super.setCurrentPage(0);
		super.setPerPage(MAXPERPAGE);

		predictions = new ArrayList<>();
		onlyInitialPredictions = false;
	}

	public Term getTerm()
	{
		return term;
	}

	public void setTerm(Term term)
	{
		this.term = term;
	}

	@Override
	public String getOrderBy()
	{
		return "ser";
	}

	@Override
	public void setOrderBy(String orderBy)
	{
		// Can't set the order by, silently ignore
		//
	}

	@Override
	public void setPerPage(int perPage)
	{
		// No more than 25 per page allowed for performance reasons
		//
		if ( perPage > MAXPERPAGE )
		{
			super.setPerPage(MAXPERPAGE);
		}
		else
		{
			super.setPerPage(perPage);
		}
	}

	public List<EventPrediction> getPredictions()
	{
		return predictions;
	}

	public void setPredictions(Set<EventPrediction> predictions)
	{
		// Need to convert to WebModelEventPrediction in order to have
		// access to the isReferenceURL(..) method.
		//
		Set<Integer> firstPreds = new HashSet<>();
		this.predictions = predictions.stream()
			.map( (eventPrediction) ->
			{
				WebModelEventPrediction retVal = new WebModelEventPrediction();
				retVal.consume(eventPrediction);

				return retVal;
			})
			.filter( (eventPrediction) ->
			{
				if ( !firstPreds.contains(eventPrediction.getEvent(false).getSer()))
				{
					firstPreds.add(eventPrediction.getEvent(false).getSer());
					eventPrediction.setInitial(true);
					return true;
				}
				else
				{
					eventPrediction.setInitial(false);
					return (!isOnlyInitialPredictions());
				}
			})
			.collect(Collectors.toCollection(ArrayList::new));

		Collections.reverse(this.predictions);
	}

	public boolean getOnlyInitialPredictions()
	{
		return isOnlyInitialPredictions();
	}

	public boolean isOnlyInitialPredictions()
	{
		return onlyInitialPredictions;
	}

	public void setOnlyInitialPredictions(boolean onlyInitialPredictions)
	{
		this.onlyInitialPredictions = onlyInitialPredictions;
	}

	@Override
	protected String toStringInternal()
	{
		if ( getLogger().isTraceEnabled() )
		{
			return "term: " + term.getSer() + ", " + super.toStringInternal();
		}
		else
		{
			return "term:" + term.getSer() + "," + super.toStringInternal();
		}
	}
}
