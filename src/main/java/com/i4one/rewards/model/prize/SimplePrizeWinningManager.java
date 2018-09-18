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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.activity.BaseActivityManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("rewards.PrizeWinningManager")
public class SimplePrizeWinningManager extends BaseActivityManager<PrizeWinningRecord, PrizeWinning, Prize> implements PrizeWinningManager
{
	private PrizeFulfillmentManager prizeFulfillmentManager;

	@Override
	public Set<PrizeWinning> getAllPrizeWinnings(User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByUser(user.getSer(), pagination));
	}

	@Override
	public Set<PrizeWinning> getAllPrizeWinnings(Prize prize, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getByItem(prize.getSer(), pagination));
	}

	@Override
	public Set<PrizeWinning> getPrizeWinnings(Prize prize, User user, PaginationFilter pagination)
	{
		return convertDelegates(getDao().getAll(prize.getSer(), user.getSer(), pagination));
	}

	@Override
	public ReturnType<PrizeWinning> update(PrizeWinning item)
	{
		throw new UnsupportedOperationException("Can't update a prize winning");
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<PrizeFulfillment>> fulfillPrizeWinnings(Set<PrizeWinning> prizeWinnings, PrizeFulfillment fulfillment)
	{
		List<ReturnType<PrizeFulfillment>> retVal = new ArrayList<>();
		getLogger().debug("Fulfilling {} winnings {}", prizeWinnings.size(), Utils.toCSV(prizeWinnings));

		for ( PrizeWinning currWinning : prizeWinnings )
		{
			PrizeWinningRecord currWinningRecord = lock(currWinning);
			PrizeWinning dbWinning = new PrizeWinning(currWinningRecord);

			if ( dbWinning.exists() )
			{
				getLogger().debug("Updating item {}", dbWinning);
	
				int totalFulfilled = getPrizeFulfillmentManager().getTotalFulfilled(dbWinning);
				int quantity = dbWinning.getQuantity() - totalFulfilled;
	
				if ( quantity > 0 )
				{
					PrizeFulfillment currFulfillment = new PrizeFulfillment();
					currFulfillment.setAdmin(fulfillment.getAdmin());
					currFulfillment.setNotes(fulfillment.getNotes());
					currFulfillment.setStatus(fulfillment.getStatus());
					currFulfillment.setTimeStampSeconds(fulfillment.getTimeStampSeconds());
	
					currFulfillment.setQuantity(quantity);
					currFulfillment.setPrizeWinning(dbWinning);
	
					ReturnType<PrizeFulfillment> fulfillmentRet = getPrizeFulfillmentManager().create(currFulfillment);
					retVal.add(fulfillmentRet);
				}
				else
				{
					getLogger().debug("Item {} has already been fulfilled, skipping", dbWinning);
				}
			}
			else
			{
				getLogger().debug("Item {} doesn't exist in the database anymore, skipping", currWinning);
			}
		}

		return retVal;
	}

	@Override
	public PrizeWinningRecordDao getDao()
	{
		return (PrizeWinningRecordDao) super.getDao();
	}

	@Override
	public PrizeWinning emptyInstance()
	{
		return new PrizeWinning();
	}

	@Override
	public PrizeWinning initModelObject(PrizeWinning prizeWinning)
	{
		PrizeWinning retVal = super.initModelObject(prizeWinning);
		retVal.setPrizeFulfillments(getPrizeFulfillmentManager().getAllPrizeFulfillments(prizeWinning, SimplePaginationFilter.NONE));

		return retVal;
	}

	public PrizeFulfillmentManager getPrizeFulfillmentManager()
	{
		return prizeFulfillmentManager;
	}

	@Autowired
	public void setPrizeFulfillmentManager(PrizeFulfillmentManager prizeFulfillmentManager)
	{
		this.prizeFulfillmentManager = prizeFulfillmentManager;
	}
}
