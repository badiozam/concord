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
package com.i4one.rewards.model.prize;

import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.BaseClientTypePrivilegedManager;
import com.i4one.base.model.manager.PrivilegedManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.rewards.model.PrizeType;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PrivilegedPrizeManager extends BaseClientTypePrivilegedManager<PrizeRecord, Prize> implements PrivilegedManager<PrizeRecord, Prize>,PrizeManager
{
	private PrizeReservationManager prizeReservationManager;
	private PrizeManager prizeManager;

	@Override
	public PrizeManager getImplementationManager()
	{
		return getPrizeManager();
	}

	@Override
	public SingleClient getClient(Prize item)
	{
		// This method cannot have side-effects otherwise updating won't work!
		// item.loadedVersion();

		return item.getClient();
	}

	@Override
	public Prize getPrizeForUpdate(Prize prize)
	{
		return getPrizeManager().getPrizeForUpdate(prize);
	}

	@Override
	public ReturnType<Prize> incrementTotalInventory(Prize prize, int amount)
	{
		super.checkWrite(getClient(prize), "incrementTotalInventory");

		return getPrizeManager().incrementTotalInventory(prize, amount);
	}

	@Override
	public ReturnType<Prize> incrementCurrentInventory(Prize prize, int amount)
	{

		return getPrizeManager().incrementCurrentInventory(prize, amount);
	}

	@Override
	public ReturnType<Prize> reserveInventory(PrizeType prizeType, int amount)
	{
		super.checkWrite(getClient(prizeType.getPrize()), "reserveInventory");

		ReturnType<Prize> retVal = getPrizeManager().reserveInventory(prizeType, amount);

		// We're recording the reservation here since it's the first manager where an
		// administrator is available.
		//
		PrizeReservation reservation = new PrizeReservation();
		reservation.setAmount(amount);
		reservation.setPrize(prizeType.getPrize());
		reservation.setAdmin(getAdmin());
		reservation.setOwner(prizeType);
		reservation.setTimeStampSeconds(getRequestState().getRequest().getTimeInSeconds());

		ReturnType<PrizeReservation> reserved = getPrizeReservationManager().create(reservation);
		retVal.addChain(getPrizeReservationManager(), "create", reserved);

		return retVal;
	}

	@Override
	public Set<Prize> search(String title, PaginationFilter pagination)
	{
		return getPrizeManager().search(title, pagination);
	}

	public PrizeManager getPrizeManager()
	{
		return prizeManager;
	}

	@Autowired
	public void setPrizeManager(PrizeManager cachedPrizeManager)
	{
		this.prizeManager = cachedPrizeManager;
	}

	@Override
	public PaginableRecordTypeDao<PrizeRecord> getDao()
	{
		return getPrizeManager().getDao();
	}

	public PrizeReservationManager getPrizeReservationManager()
	{
		return prizeReservationManager;
	}

	@Autowired
	public void setPrizeReservationManager(PrizeReservationManager prizeReservationManager)
	{
		this.prizeReservationManager = prizeReservationManager;
	}
}
