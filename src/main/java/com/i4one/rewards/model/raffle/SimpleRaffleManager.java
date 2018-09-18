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

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.rewards.model.BaseWinnablePrizeTypeManager;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.prize.PrizeWinning;
import com.i4one.rewards.model.prize.PrizeWinningManager;
import com.i4one.rewards.model.raffle.category.RaffleCategory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleRaffleManager extends BaseWinnablePrizeTypeManager<RaffleRecord, Raffle, RaffleCategory> implements RaffleManager
{
	private RaffleEntryManager raffleEntryManager;
	private MessageManager messageManager;

	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;
	private PrizeWinningManager prizeWinningManager;

	private static final String NAMESINGLE_KEY = "rewards.raffleManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "rewards.raffleManager.namePlural";
	private static final String DEFAULTINTRO_KEY = "rewards.raffleManager.defaultIntro";
	private static final String DEFAULTOUTRO_KEY = "rewards.raffleManager.defaultOutro";

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<PrizeWinning>> selectWinners(Raffle raffle)
	{
		Set<UserBalance> winners = new HashSet<>();

		boolean byReserve = raffle.getInitialReserve() > 0;

		int count;
		if ( byReserve )
		{
			// Select the remaining reserve to become winners
			//
			count = raffle.getCurrentReserve();
		}
		else
		{
			// Select the remaining inventory to become winners
			//
			count = raffle.getPrize().getCurrentInventory();
		}

		Balance raffleBal = getBalanceManager().getBalance(raffle);

		// We'll try a few times in order to be able to get all the winners
		//
		int NUM_TRIES = 5;
		for ( int i = 0; i < NUM_TRIES; i++)
		{
			Set<UserBalance> winningEntries = getUserBalanceManager().weightedRandomUserBalances(raffleBal, count);

			winners.addAll(winningEntries);
			if ( winners.size() >= count )
			{
				break;
			}
		}

		List<ReturnType<PrizeWinning>> retVal = new ArrayList<>();
		for ( UserBalance currUserBal : winners )
		{
			PrizeWinning prizeWinning = new PrizeWinning();
			prizeWinning.setOwner(raffle);
			prizeWinning.setPrize(raffle.getPrize(false));
			prizeWinning.setQuantity(1);
			prizeWinning.setUser(currUserBal.getUser(false));

			ReturnType<PrizeWinning> createdWinning = getPrizeWinningManager().create(prizeWinning);
			retVal.add(createdWinning);

			if ( byReserve )
			{
				incrementCurrentReserve(raffle, -1);
			}
			else
			{
				getPrizeManager().incrementCurrentInventory(raffle.getPrize(false), -1);
			}
		}

		return retVal;
	}

	@Override
	protected ReturnType<Raffle> createInternal(Raffle item)
	{
		ReturnType<Raffle> retVal;

		Prize prize = item.getPrize();
		if ( prize.exists() )
		{
			retVal = super.createInternal(item);
		}
		else
		{
			// We're creating a new prize
			//
			prize.setClient(item.getClient(false));
			prize.setTitle(item.getTitle());
			prize.setSiteGroup(item.getSiteGroup(false));

			// When we create a prize the initial inventory is still intact
			//
			ReturnType<Prize> createdPrize = getPrizeManager().create(prize);

			// The prize now has a serial number so it can be created
			//
			item.setPrize(createdPrize.getPost());

			// Now that we have a prize id we can create the item
			//
			retVal = super.createInternal(item);
			retVal.addChain(getPrizeManager(), "create", createdPrize);

			// After we create the item, we can set aside a reserve
			//
			if ( item.getInitialReserve() > 0 )
			{
				ReturnType<Prize> reservedPrize = getPrizeManager().reserveInventory(item, item.getInitialReserve());
				retVal.addChain(getPrizeManager(), "reserveInventory", reservedPrize);

				item.setPrize(reservedPrize.getPost());
			}
		}

		RaffleSettings raffleSettings = getSettings(item.getClient());

		Balance raffleBalance = new Balance();
		raffleBalance.setOwner(retVal.getPost());
		raffleBalance.setSingleName(raffleSettings.getBalanceNameSingle());
		raffleBalance.setPluralName(raffleSettings.getBalanceNamePlural());

		ReturnType<Balance> createdBalance = getBalanceManager().create(raffleBalance);
		retVal.addChain(getBalanceManager(), "create", createdBalance);

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	protected ReturnType<Raffle> updateInternal(RaffleRecord lockedRecord, Raffle item)
	{
		Prize prize = item.getPrize();
		if ( !prize.exists() )
		{
			throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".update.noprize", "This item must have an associated prize: $item: $ex.message", new Object[] { "item", item }));
		}
		else
		{
			if ( !Objects.equals(prize.getSer(), lockedRecord.getPrizeid()) )
			{
				// Only able to change the prizes if nobody has bought it
				//
				if ( getRaffleEntryManager().hasPurchases(item) )
				{
					throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".update.haspurchases", "This item cannot be modified since there are users that have already purchased it", new Object[] { "item", item }));
				}
				else
				{
					// Return the inventory back to the prize
					//
					Prize oldPrize = new Prize();
					oldPrize.setSer(lockedRecord.getPrizeid());

					getPrizeManager().incrementTotalInventory(oldPrize, 1);
				}
			}
	
			return super.updateInternal(lockedRecord, item);
		}
	}
	
	@Transactional(readOnly = false)
	@Override
	public Raffle remove(Raffle item)
	{
		Balance raffleBalance = getBalanceManager().getBalance(item);
		getBalanceManager().remove(raffleBalance);

		Raffle retVal = super.remove(item);

		return retVal;
	}

	@Override
	public RaffleSettings getSettings(SingleClient client)
	{
		RaffleSettings retVal = new RaffleSettings();
		retVal.setClient(client);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		List<Message> intros = getMessageManager().getAllMessages(client, DEFAULTINTRO_KEY);
		List<Message> outros = getMessageManager().getAllMessages(client, DEFAULTOUTRO_KEY);
		retVal.setIntroOutro(intros, outros);

		boolean enabled = Utils.defaultIfNaB(getClientOptionManager().getOption(client, getEnabledOptionKey()).getValue(), true);
		retVal.setEnabled(enabled);

		return retVal;
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<RaffleSettings> updateSettings(RaffleSettings settings)
	{
		ReturnType<RaffleSettings> retVal = new ReturnType<>(settings);

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		// XXX: Consolidate this into a single list and create a new method in MessageManager to update
		// a batch of messages so that the historical records are chained together.

		List<Message> singularNames = settings.getNameSingleMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = settings.getNamePluralMessages(client, NAMEPLURAL_KEY);

		List<Message> intros = settings.getDefaultIntroMessages(client, DEFAULTINTRO_KEY);
		List<Message> outros = settings.getDefaultOutroMessages(client, DEFAULTOUTRO_KEY);

		singularNames.forEach( (message) -> { getMessageManager().update(message); } );
		pluralNames.forEach( (message) -> { getMessageManager().update(message); } );

		intros.forEach( (message) -> { getMessageManager().update(message); } );
		outros.forEach( (message) -> { getMessageManager().update(message); } );

		updateOption(client, getEnabledOptionKey(), String.valueOf(settings.isEnabled()), retVal);

		return retVal;
	}

	@Override
	public RaffleRecordDao getDao()
	{
		return (RaffleRecordDao) super.getDao();
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

	public RaffleEntryManager getRaffleEntryManager()
	{
		return raffleEntryManager;
	}

	@Autowired
	public void setRaffleEntryManager(RaffleEntryManager raffleEntryManager)
	{
		this.raffleEntryManager = raffleEntryManager;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

	public PrizeWinningManager getPrizeWinningManager()
	{
		return prizeWinningManager;
	}

	@Autowired
	public void setPrizeWinningManager(PrizeWinningManager prizeWinningManager)
	{
		this.prizeWinningManager = prizeWinningManager;
	}

	@Override
	public Raffle emptyInstance()
	{
		return new Raffle();
	}

	@Override
	public Raffle getAttached(Balance balance)
	{
		if ( balance.exists() && balance.getDelegate().getFeatureid() > 0 )
		{
			return getById(balance.getDelegate().getFeatureid());
		}
		else
		{
			return emptyInstance();
		}
	}
}
