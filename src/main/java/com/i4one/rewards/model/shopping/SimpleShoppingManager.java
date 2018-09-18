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
package com.i4one.rewards.model.shopping;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.rewards.model.BaseWinnablePrizeTypeManager;
import com.i4one.rewards.model.prize.Prize;
import com.i4one.rewards.model.shopping.category.ShoppingCategory;
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
public class SimpleShoppingManager extends BaseWinnablePrizeTypeManager<ShoppingRecord, Shopping, ShoppingCategory> implements ShoppingManager
{
	private MessageManager messageManager;
	private ShoppingPurchaseManager shoppingPurchaseManager;

	private static final String NAMESINGLE_KEY = "rewards.shoppingManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "rewards.shoppingManager.namePlural";
	private static final String DEFAULTINTRO_KEY = "rewards.shoppingManager.defaultIntro";
	private static final String DEFAULTOUTRO_KEY = "rewards.shoppingManager.defaultOutro";

	@Override
	protected ReturnType<Shopping> createInternal(Shopping item)
	{
		// When creating a shopping item, the current reserve is always
		// equal to the initial reserve
		//
		item.setCurrentReserve(item.getInitialReserve());

		ReturnType<Shopping> retVal;

		Prize prize = item.getPrize();
		if ( prize.exists() )
		{
			retVal = super.createInternal(item);

			if ( item.getInitialReserve() > 0 )
			{
				ReturnType<Prize> prizeReserve = getPrizeManager().reserveInventory(retVal.getPost(), item.getInitialReserve());
				retVal.addChain(getPrizeManager(), "reserveInventory", prizeReserve);
			}
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

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	protected ReturnType<Shopping> updateInternal(ShoppingRecord lockedRecord, Shopping item)
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
				if ( getShoppingPurchaseManager().hasPurchases(item) )
				{
					throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".update.haspurchases", "This item cannot be modified since there are users that have already purchased it", new Object[] { "item", item }));
				}
				else
				{
					// Return the inventory back to the prize
					//
					Prize oldPrize = new Prize();
					oldPrize.setSer(lockedRecord.getPrizeid());

					getPrizeManager().incrementTotalInventory(oldPrize, item.getCurrentReserve());

					// Since the prize changed to some other prize, we reset inventories. By default,
					// we're set to draw directly from the inventory of the prize
					//
					item.setInitialReserve(0);
					item.setCurrentReserve(0);
				}
			}
			else
			{
				// Override the reserve values since these can only be updated through
				// the increment methods for the given prize
				//
				item.setInitialReserve(lockedRecord.getInitreserve());
				item.setCurrentReserve(lockedRecord.getCurrreserve());
	
			}
	
			return super.updateInternal(lockedRecord, item);
		}
	}
	
	@Transactional(readOnly = false)
	@Override
	public Shopping remove(Shopping shopping)
	{
		ShoppingRecord lockedShoppingRecord = lock(shopping);
		Shopping lockedShopping = new Shopping(lockedShoppingRecord);

		// XXX: Maybe we should check to see if we have any activity before removing?
		//

		// Return the inventory back to the prize closet
		//
		getPrizeManager().reserveInventory(shopping, -1 * lockedShopping.getCurrentReserve());

		return super.remove(shopping);
	}

	@Override
	public ShoppingSettings getSettings(SingleClient client)
	{
		ShoppingSettings retVal = new ShoppingSettings();
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
	public ReturnType<ShoppingSettings> updateSettings(ShoppingSettings settings)
	{
		ReturnType<ShoppingSettings> retVal = new ReturnType<>(settings);

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		Set<Message> updateMessages = new HashSet<>();
		updateMessages.addAll(settings.getNameSingleMessages(client, NAMESINGLE_KEY));
		updateMessages.addAll(settings.getNamePluralMessages(client, NAMEPLURAL_KEY));

		updateMessages.addAll(settings.getDefaultIntroMessages(client, DEFAULTINTRO_KEY));
		updateMessages.addAll(settings.getDefaultOutroMessages(client, DEFAULTOUTRO_KEY));

		getMessageManager().updateMessages(updateMessages);

		updateOption(client, getEnabledOptionKey(), String.valueOf(settings.isEnabled()), retVal);

		return retVal;
	}

	@Override
	public ShoppingRecordDao getDao()
	{
		return (ShoppingRecordDao) super.getDao();
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

	public ShoppingPurchaseManager getShoppingPurchaseManager()
	{
		return shoppingPurchaseManager;
	}

	@Autowired
	public void setShoppingPurchaseManager(ShoppingPurchaseManager shoppingPurchaseManager)
	{
		this.shoppingPurchaseManager = shoppingPurchaseManager;
	}

	@Override
	public Shopping emptyInstance()
	{
		return new Shopping();
	}
}
