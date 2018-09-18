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

import com.i4one.base.dao.categorizable.CategorizableRecordTypeDao;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailtemplate.EmailTemplate;
import com.i4one.base.model.manager.BaseEmailNotificationManager;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.rewards.model.prize.PrizeWinning;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("rewards.RaffleManager")
public class EmailNotificationRaffleManager extends BaseEmailNotificationManager<RaffleRecord, Raffle> implements RaffleManager
{
	private BalanceManager balanceManager;
	private RaffleManager raffleManager;

	private static final String WINNEREMAIL_KEY = "rewards.raffleManager.selectWinners";

	@Override
	public void init()
	{
		super.init();

		getBalanceManager().setBalanceAttachmentResolver(this);
	}

	@Override
	public Manager<RaffleRecord, Raffle> getImplementationManager()
	{
		return getRaffleManager();
	}

	@Override
	public List<ReturnType<PrizeWinning>> selectWinners(Raffle raffle)
	{
		List<ReturnType<PrizeWinning>> retVal =  getRaffleManager().selectWinners(raffle);

		// We get the winner e-mail associated with the raffle's client rather than the 
		// request client to allow parent clients to select winners for child nodes and
		// send out the child node's specific e-mail.
		//
		EmailTemplate winnerEmail = getEmailTemplateManager().getEmailTemplate(raffle.getClient(), WINNEREMAIL_KEY);

		Map<String, Object> model = getRequestState().getModel();
		model.put("raffle", raffle);
		model.put("winners", retVal.stream().map(ReturnType::getPost).collect(Collectors.toSet()));

		// Sending the automated e-mail to the master e-mail address of the client
		//
		getEmailManager().sendEmail(getRequestState().getSingleClient().getEmail(), getRequestState().getLanguage(), winnerEmail, model);

		return retVal;
	}

	@Override
	public RaffleSettings getSettings(SingleClient client)
	{
		RaffleSettings retVal = getRaffleManager().getSettings(client);

		retVal.setWinnerEmail(getEmailTemplateManager().getEmailTemplate(client, WINNEREMAIL_KEY));

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<RaffleSettings> updateSettings(RaffleSettings settings)
	{
		ReturnType<RaffleSettings> retVal = getRaffleManager().updateSettings(settings);

		SingleClient client = settings.getClient();

		EmailTemplate forUpdate = settings.getWinnerEmail();
		forUpdate.setKey(WINNEREMAIL_KEY);
		forUpdate.setClient(client);

		EmailTemplate currEmail = getEmailTemplateManager().getEmailTemplate(client, WINNEREMAIL_KEY);
		if ( currEmail.exists() )
		{
			forUpdate.setSer(currEmail.getSer());

			ReturnType<EmailTemplate> updatedWinnerEmail = getEmailTemplateManager().update(forUpdate);
			retVal.addChain(getEmailTemplateManager(), "create", updatedWinnerEmail);
		}
		else
		{
			ReturnType<EmailTemplate> createdWinnerEmail = getEmailTemplateManager().create(settings.getWinnerEmail());
			retVal.addChain(getEmailTemplateManager(), "create", createdWinnerEmail);
		}


		return retVal;
	}

	@Override
	public ReturnType<Raffle> incrementTotalReserve(Raffle item, int amount)
	{
		return getRaffleManager().incrementTotalReserve(item, amount);
	}

	@Override
	public void incrementCurrentReserve(Raffle item, int amount)
	{
		getRaffleManager().incrementCurrentReserve(item, amount);
	}

	@Override
	public CategorizableRecordTypeDao<RaffleRecord> getDao()
	{
		return getRaffleManager().getDao();
	}

	@Override
	public Set<Raffle> getLive(TerminablePagination pagination)
	{
		return getRaffleManager().getLive(pagination);
	}

	@Override
	public Set<Raffle> getByRange(TerminablePagination pagination)
	{
		return getRaffleManager().getByRange(pagination);
	}

	@Override
	public Raffle getAttached(Balance balance)
	{
		return getRaffleManager().getAttached(balance);
	}

	public RaffleManager getRaffleManager()
	{
		return raffleManager;
	}

	@Autowired
	public void setRaffleManager(RaffleManager historicalRaffleManager)
	{
		this.raffleManager = historicalRaffleManager;
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
}
