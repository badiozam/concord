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

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hamid Badiozamani
 */
public class PollResults extends BaseLoggable
{
	private Poll poll;

	private Map<PollAnswer, Integer> tally;
	private Integer count;

	public PollResults()
	{
		tally = new ConcurrentHashMap<>();
		count = 0;
	}

	public void tally(PollResponse response)
	{
		Integer currCount = tally.get(response.getPollAnswer());
		if ( currCount == null )
		{
			currCount = 0;
		}

		currCount++;

		tally.put(response.getPollAnswer(), currCount);
		count++;
	}

	public void reset()
	{
		tally.clear();
		count = 0;
	}

	public Poll getPoll()
	{
		return poll;
	}

	public void setPoll(Poll poll)
	{
		this.poll = poll;

		// We make sure each answer is represented with at least a single
		// entry to make things look more interesting
		//
		for ( PollAnswer answer : poll.getPollAnswers() )
		{
			tally.put(answer, 1);
			count++;
		}
	}

	public Map<PollAnswer, Integer> getTally()
	{
		return tally;
	}

	public void setTally(Map<PollAnswer, Integer> tally)
	{
		this.tally = tally;
	}

	public Integer getCount()
	{
		return count;
	}

	public String toHighChartsJSON(String lang)
	{
		StringBuilder retVal = new StringBuilder();

		retVal.append("{");
			retVal.append("'title':{'text':'").append(Utils.jsEscape(getPoll().getTitle().get(lang))).append("'}").append(",");
			retVal.append("'chart':{'type':'pie'}").append(",");
			retVal.append("'xAxis':{'type':'category'}").append(",");
			retVal.append("'series':[{'data':[");
			boolean isFirst = true;
				for ( PollAnswer answer : getTally().keySet() )
				{
					if ( isFirst ) { isFirst = false; } else { retVal.append(","); }

					retVal.append("{");
						Integer currCount = getTally().get(answer);
						retVal.append("'y':").append(currCount).append(",");
						retVal.append("'name':\'").append(Utils.jsEscape(breakChartAnswer(answer.getAnswer().get(lang)))).append("\'");
					retVal.append("}");
				}
			retVal.append("]").append(",");
			retVal.append("'name':'").append(Utils.jsEscape(getPoll().getTitle().get(lang))).append("'}]");
		retVal.append("}");

		return retVal.toString();
	}

	// Attempt to break the answer at around this number
	//
	private final int POLLANSWER_BREAKPOINT = 30;

	private String breakChartAnswer(String answer)
	{
		return Utils.breakToHTML(answer, POLLANSWER_BREAKPOINT);
	}
}
