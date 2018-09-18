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
package com.i4one.rewards.model;

import com.i4one.base.dao.categorizable.CategorizableRecordTypeDao;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.category.Category;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.manager.categorizable.BaseSimpleCategorizableManager;
import com.i4one.rewards.dao.RewardsRecordDao;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.prize.PrizeManager;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseWinnablePrizeTypeManager<U extends TerminableRewardsClientRecordType, T extends TerminableRewardsClientType<U,V>, V extends Category<?>> extends BaseSimpleCategorizableManager<U, T, V> implements WinnablePrizeTypeManager<U,T,V>
{
	private PrizeManager prizeManager;
	private PrizeManager readOnlyPrizeManager;

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> clone(T item)
	{
		try
		{
			if ( item.exists() )
			{
				initModelObject(item);

				T prizeType = initModelObject(cloneInternal(item));
	
				if ( prizeType.getInitialReserve() > 0 )
				{
					// Using the reserve system, which means that the prize is reusable.
					// We add 1 to the inventory and reserve it for this clone.
					//
					ReturnType<Prize> incremented = getPrizeManager().incrementTotalInventory(prizeType.getPrize(false), 1);
					ReturnType<T> createdPrizeType = create(prizeType);
					ReturnType<Prize> reserved = getPrizeManager().reserveInventory(prizeType, 1);

					createdPrizeType.addChain(getPrizeManager(), "incrementTotalInventory", incremented);
					createdPrizeType.addChain(getPrizeManager(), "reserveInventory", reserved);

					return createdPrizeType;
				}
				else
				{
					// If there's no reserve being used and the prize has run out of inventory the cloned item
					// will not select a winner
					//
					return create(prizeType);
				}
			}
			else
			{
				throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.dne", "You are attempting to clone a non-existent item: $item", new Object[] { "item", item }));
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
		{
			Errors errors = new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));

			throw errors;
		}
	}

	@Override
	protected T cloneInternal(T item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		long currTimeMillis = getRequestState().getRequest().getTimeInMillis();
		SingleClient client = getRequestState().getSingleClient();

		Calendar currCal = client.getCalendar();
		currCal.setTimeInMillis(currTimeMillis);

		String currTimeStamp = client.getDateFormat().format(currCal.getTime());
		IString workingTitle = new IString(item.getTitle()).appendAll(" [CLONED @ " + currTimeStamp + "]");

		T prizeType = super.cloneInternal(item);

		prizeType.setTitle(workingTitle);

		// The purchase start time begins after the same amount of time has passed 
		// as the item being cloned. The end time is calculated based off of the
		// duration that the prize is purchasable
		//
		int purchaseAfterSeconds = Math.abs(item.getPurchaseStartTimeSeconds() - item.getStartTimeSeconds());
		int purchaseDurationSeconds = Math.abs(item.getPurchaseEndTimeSeconds() - item.getPurchaseStartTimeSeconds());

		prizeType.setPurchaseStartTimeSeconds(prizeType.getStartTimeSeconds() + purchaseAfterSeconds);
		prizeType.setPurchaseEndTimeSeconds(prizeType.getPurchaseStartTimeSeconds() + purchaseDurationSeconds);

		return prizeType;
	}

	@Transactional(readOnly = false)
	@Override
	public void incrementCurrentReserve(T item, int amount)
	{
		// We have to lock to ensure the inventory is available and also
		// to make sure we have the correct reserve mechanism in place
		//
		U lockedRecord = lock(item);
		T lockedItem = emptyInstance();
		lockedItem.setOwnedDelegate(lockedRecord);

		if ( lockedItem.getInitialReserve() > 0 )
		{
			if ( lockedItem.getCurrentReserve() + amount < 0 )
			{
				throw new Errors(getInterfaceName() + ".incrementCurrentReserve", new ErrorMessage("msg." + getInterfaceName() + ".incrementCurrentReserve.soldout", "We do not have $amount of this #IString($item.title) in stock", new Object[] { "item", lockedItem, "amount", amount}, null));
			}
			else
			{
				// Going off of reserve inventory
				//
				getDao().incrementCurrreserve(lockedItem.getSer(), amount);
			}
		}
		else
		{
			Prize lockedPrize = getPrizeManager().getPrizeForUpdate(lockedItem.getPrize(false));

			if ( lockedPrize.getCurrentInventory() + amount < 0 )
			{
				throw new Errors(getInterfaceName() + ".incrementCurrentReserve", new ErrorMessage("msg." + getInterfaceName() + ".incrementCurrentReserve.soldout", "We do not have $amount of this #IString($item.title) in stock", new Object[] { "item", lockedItem, "amount", amount}, null));
			}
			else
			{
				// Going off of prize inventory
				//
				getPrizeManager().incrementCurrentInventory(item.getPrize(false), amount);
			}
		}
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> incrementTotalReserve(T item, int amount)
	{
		U lockedRecord = lock(item);
		T lockedItem = emptyInstance();
		lockedItem.setOwnedDelegate(lockedRecord);

		ReturnType<T> retVal = new ReturnType<>();
		retVal.setPre(lockedItem);

		if ( lockedItem.getInitialReserve() + amount < 0 )
		{
			throw new Errors(getInterfaceName() + ".incrementTotalReserve", new ErrorMessage("msg." + getInterfaceName() + ".incrementTotalReserve.nsf", "Can't increment the reserve by $amount since there are only $item.initialReserve of this #IString($item.title) on reserve.", new Object[] { "item", lockedItem, "amount", amount}, null));
		}
		else
		{
			int itemId = item.getSer();
	
			// NOTE: A total reserve of 0 indicates that the item item
			// is going off of prize inventory directly. If the item
			// item had an initial reserve, then purchases were made causing
			// the current reserve to be reduced below the initial reserve, the
			// item would be prevented from reducing its total reserve
			// down to zero since that would render the current reserve into
			// negative territory.
			//
			// The order we increment these matters because we want to preserve
			// the valid reserve constraint of having the current reserve always
			// less than or equal to the initial reserve.
			//
			if ( amount > 0 )
			{
				getDao().incrementInitreserve(itemId, amount);
				getDao().incrementCurrreserve(itemId, amount);
			}
			else
			{
				getDao().incrementCurrreserve(itemId, amount);
				getDao().incrementInitreserve(itemId, amount);
			}
	
			ReturnType<Prize> prizeUpdate = getPrizeManager().reserveInventory(item, amount);
			retVal.addChain(getPrizeManager(), "reserveInventory", prizeUpdate);
	
			// We could update the lockedItem which we own, but since this method
			// is not expected to be under heavy load, we load the database to
			// ensure consistency
			//
			T post = emptyInstance();
			post.setSer(itemId);
			post.loadedVersion();
	
			retVal.setPost(post);
	
			return retVal;
		}
	}

	@Override
	protected T initModelObject(T item)
	{
		// If we have permission to read a winnable type, we have permission read its prize.
		// This is necessary because many model interceptors rely on validating a raffle/shopping
		// category which involves loading the live items within that category. Loading those
		// involves loading the prize associated, which normally would go through the
		// PrivilegedPrizeManager somewhere along the chain. The PrivilegedPrizeManager then checks
		// the client being accessed against the item being returned. But getting the client means
		// calling on the request state which is what's being initialized. The readonly prize
		// manager skips the privilege check and breaks the circular dependency.
		//
		item.setPrize(getReadOnlyPrizeManager().getById(item.getPrize(false).getSer()));

		return item;
	}

	@Override
	public RewardsRecordDao<U> getDao()
	{
		CategorizableRecordTypeDao<U> baseDao = super.getDao();
		if ( baseDao instanceof RewardsRecordDao )
		{
			RewardsRecordDao<U> rewardsDao = (RewardsRecordDao<U>) baseDao;
			return rewardsDao;
		}
		else
		{
			return null;
		}
	}

	public PrizeManager getPrizeManager()
	{
		return prizeManager;
	}

	@Autowired
	public void setPrizeManager(PrizeManager prizeManager)
	{
		this.prizeManager = prizeManager;
	}

	public PrizeManager getReadOnlyPrizeManager()
	{
		return readOnlyPrizeManager;
	}

	@Autowired
	public void setReadOnlyPrizeManager(PrizeManager readOnlyPrizeManager)
	{
		this.readOnlyPrizeManager = readOnlyPrizeManager;
	}
}
