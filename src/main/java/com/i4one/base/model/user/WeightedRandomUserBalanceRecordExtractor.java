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
package com.i4one.base.model.user;

import com.i4one.base.core.Utils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * @author Hamid Badiozamani
 */
public class WeightedRandomUserBalanceRecordExtractor implements ResultSetExtractor<Set<Integer>>
{
	private final Random rand;
	private final int numWinners;
	private final int totalEntries;

	public WeightedRandomUserBalanceRecordExtractor(int numWinners, int totalEntries)
	{
		rand = new Random( Utils.currentTimeSeconds() );

		this.numWinners = numWinners;
		this.totalEntries = totalEntries;
	}

	@Override
	public Set<Integer> extractData(ResultSet res) throws SQLException, DataAccessException
	{
		List<Integer> randomEntryNumbers = new ArrayList<>(numWinners);
		Set<Integer> winners = new HashSet<>(numWinners);

		if ( numWinners <= totalEntries )
		{
			for (int i = 0; i < numWinners; i++ )
			{
				randomEntryNumbers.add(i);
			}
		}
		else
		{
			while ( randomEntryNumbers.size() < numWinners )
			{
				randomEntryNumbers.add(rand.nextInt(totalEntries));
			}
			Collections.sort(randomEntryNumbers);
		}

		// At this point we should have a set of random entry numbers in
		// ascending order, we iterate through all balances and at each
		// point that we pass a mark, we select that user as a winner
		//
		Iterator<Integer> winnerIterator = randomEntryNumbers.iterator();
		Integer currWinningEntry = winnerIterator.next();

		int entryCount = 0;
		while ( res.next() )
		{
			int total = res.getInt("total");
			entryCount += total;

			if ( entryCount >  currWinningEntry )
			{
				winners.add(res.getInt("ser"));

				if ( winnerIterator.hasNext() )
				{
					currWinningEntry = winnerIterator.next();
				}
				else
				{
					break;
				}
			}
		}

		return winners;
	}
}
