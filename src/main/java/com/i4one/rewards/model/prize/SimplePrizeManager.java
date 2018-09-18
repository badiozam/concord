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
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.rewards.model.PrizeType;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimplePrizeManager extends BaseSimpleManager<PrizeRecord, Prize> implements PrizeManager
{
	private static final int MIN_SEARCH_LENGTH = 3;
	private static final int MAX_ALL_PER_PAGE = 100;

	@Override
	public Set<Prize> search(String title, PaginationFilter pagination)
	{
		boolean viewAll =  Utils.isEmpty(title);
		if ( viewAll || title.length() < MIN_SEARCH_LENGTH )
		{
			// This check is here to prevent a lot of prizes being displayed at once
			// thus bogging down the database
			//
			if ( pagination.getPerPage() == 0 || pagination.getPerPage() > MAX_ALL_PER_PAGE )
			{
				pagination.setPerPage(MAX_ALL_PER_PAGE);
			}
		}

		if ( viewAll )
		{
			return convertDelegates(getDao().getAll(pagination));
		}
		else
		{
			return convertDelegates(getDao().search(title, pagination));
		}
	}

	@Transactional(readOnly = false)
	@Override
	public Prize getPrizeForUpdate(Prize prize)
	{
		PrizeRecord lockedPrizeRecord = getDao().getBySer(prize.getSer(), true);

		Prize retVal = new Prize();
		if ( lockedPrizeRecord != null )
		{
			retVal.setOwnedDelegate(lockedPrizeRecord);
		}

		return retVal;
	}

	@Override
	protected Prize cloneInternal(Prize item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		String currTimeStamp = String.valueOf(Utils.currentDateTime());
		IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");

		Prize prize = new Prize();
		prize.copyFrom(item);

		prize.setSer(0);
		prize.setTitle(workingTitle);

		return prize;
	}

	@Transactional(readOnly = false)
	@Override
	protected ReturnType<Prize> createInternal(Prize item)
	{
		// Since we're creating our current inventory has to be the same as
		// our initial inventory
		//
		item.setCurrentInventory(item.getInitialInventory());

		return super.createInternal(item);
	}

	@Transactional(readOnly = false)
	@Override
	protected ReturnType<Prize> updateInternal(PrizeRecord lockedRecord, Prize item)
	{
		// Override the inventory values since these can only be updated through
		// the increment methods
		//
		item.setInitialInventory(lockedRecord.getInitinventory());
		item.setCurrentInventory(lockedRecord.getCurrinventory());

		return super.updateInternal(lockedRecord, item);
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Prize> incrementTotalInventory(Prize prize, int amount)
	{
		PrizeRecord prizeRecord = getDao().getBySer(prize.getSer(), true);

		if ( prizeRecord == null )
		{
			throw new Errors(getInterfaceName() + ".incrementTotalInventory", new ErrorMessage("msg." + getInterfaceName() + ".incrementTotalInventory.dne", "This prize no longer exists", new Object[] { "item", prize, "amount", amount }));
		}
		else if ( prizeRecord.getInitinventory() + amount < 0 || prizeRecord.getCurrinventory() + amount < 0 )
		{
			Prize dbPrize = new Prize(prizeRecord);

			throw new Errors(getInterfaceName() + ".incrementTotalInventory", new ErrorMessage("msg." + getInterfaceName() + ".incrementTotalInventory.nsf", "Reducing this item's inventory count by $amount will result in a negative inventory", new Object[] { "item", dbPrize, "amount", amount }));
		}
		else
		{
			// The order we increment these matters because we want to preserve
			// the valid inventory constraint of having the current inventory always
			// less than or equal to the initial inventory
			//
			if ( amount > 0 )
			{
				getDao().incrementInitinventory(prizeRecord.getSer(), amount);
				getDao().incrementCurrinventory(prizeRecord.getSer(), amount);
			}
			else
			{
				getDao().incrementCurrinventory(prizeRecord.getSer(), amount);
				getDao().incrementInitinventory(prizeRecord.getSer(), amount);
			}

			prizeRecord.setInitinventory(prizeRecord.getInitinventory() + amount);
			prizeRecord.setCurrinventory(prizeRecord.getCurrinventory() + amount);

			ReturnType<Prize> retVal = new ReturnType<>();
			retVal.setPre(prize);
			retVal.setPost(new Prize(prizeRecord));

			return retVal;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Prize> incrementCurrentInventory(Prize prize, int amount)
	{
		if ( !prize.exists() )
		{
			throw new Errors(getInterfaceName() + ".incrementCurrentInventory", new ErrorMessage("msg." + getInterfaceName() + ".incrementCurrentInventory.dne", "The prize '$item' does not exist", new Object[] { "item", prize, "amount", amount }));
		}
		else if ( amount >= 0 )
		{
			throw new Errors(getInterfaceName() + ".incrementCurrentInventory", new ErrorMessage("msg." + getInterfaceName() + ".incrementCurrentInventory.noincrease", "The current inventory cannot be increased", new Object[] { "item", prize, "amount", amount }));
		}
		else
		{
			PrizeRecord prizeRecord = getDao().getBySer(prize.getSer(), true);

			getDao().incrementCurrinventory(prize.getSer(), amount);
			prizeRecord.setCurrinventory(prizeRecord.getCurrinventory() + amount);

			ReturnType<Prize> retVal = new ReturnType<>();
			retVal.setPre(prize);
			retVal.setPost(new Prize(prizeRecord));

			return retVal;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<Prize> reserveInventory(PrizeType<?> prizeType, int amount)
	{
		ReturnType<Prize> retVal = new ReturnType<>();

		Prize dbPrize = getPrizeForUpdate(prizeType.getPrize());

		if ( dbPrize.getCurrentInventory() - amount < 0 )
		{
			throw new Errors(getInterfaceName() + ".reserveInventory", new ErrorMessage("msg." + getInterfaceName() + ".reserveInventory.nsf", "Reducing this item's inventory count by $amount will result in a negative inventory", new Object[] { "item", dbPrize, "amount", amount }));
		}
		else
		{
			retVal.setPre(prizeType.getPrize());
			retVal.setPost(dbPrize);

			if ( dbPrize.exists() )
			{
				PrizeRecord prizeRecord = dbPrize.getDelegate();

				getDao().incrementCurrinventory(prizeRecord.getSer(), -1 * amount);
				prizeRecord.setCurrinventory(prizeRecord.getCurrinventory() + -1 * amount);
			}
		}

		return retVal;
	}

	@Override
	public PrizeRecordDao getDao()
	{
		return (PrizeRecordDao) super.getDao();
	}

	@Override
	public Prize emptyInstance()
	{
		return new Prize();
	}
}
