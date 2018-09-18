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
package com.i4one.rewards.model.raffle;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.BalanceAttachmentResolver;
import com.i4one.base.model.client.SingleClient;
import com.i4one.rewards.model.WinnablePrizeTypeManager;
import com.i4one.rewards.model.prize.PrizeWinning;
import com.i4one.rewards.model.raffle.category.RaffleCategory;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface RaffleManager extends WinnablePrizeTypeManager<RaffleRecord, Raffle, RaffleCategory>, BalanceAttachmentResolver<RaffleRecord, Raffle>
{
	/**
	 * Select winners for the given raffle.
	 * 
	 * @param raffle The raffle for which to select winners
	 * 
	 * @return A (potentially empty) set of prize winning objects that were
	 * 	created as a result of winner selection.
	 */
	public List<ReturnType<PrizeWinning>> selectWinners(Raffle raffle);

	/**
	 * Get the current settings for the given manager.
	 * 
	 * @param client The client for which to retrieve the settings.
	 * 
	 * @return The current raffle settings
	 */
	public RaffleSettings getSettings(SingleClient client);

	/**
	 * Update raffle settings.
	 * 
	 * @param settings The new raffle settings to update
	 * 
	 * @return The result of the updated settings
	 */
	public ReturnType<RaffleSettings> updateSettings(RaffleSettings settings);
}
