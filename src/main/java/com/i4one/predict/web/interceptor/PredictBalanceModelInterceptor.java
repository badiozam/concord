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
package com.i4one.predict.web.interceptor;

import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.interceptor.model.BaseModelInterceptor;
import com.i4one.base.web.interceptor.model.ModelInterceptor;
import com.i4one.base.web.interceptor.model.UserAdminModelInterceptor;
import com.i4one.predict.model.player.Player;
import com.i4one.predict.model.player.PlayerManager;
import com.i4one.predict.model.term.Term;
import com.i4one.predict.model.term.TermManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class PredictBalanceModelInterceptor extends BaseModelInterceptor implements ModelInterceptor
{
	private TermManager termManager;
	private PlayerManager playerManager;
	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;

	public static final String TERM = "prediction" + Term.class.getSimpleName();
	public static final String PLAYER = "prediction" + Player.class.getSimpleName();
	public static final String USERBALANCE = "prediction" + UserBalance.class.getSimpleName();


	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public Map<String, Object> initRequestModel(Model model)
	{
		HttpServletRequest request = model.getRequest();
		Map<String, Object> retVal = new HashMap<>();

		if ( isUserRequest(request) )
		{
			User user = (User) model.get(UserAdminModelInterceptor.USER);
			SingleClient client = model.getSingleClient();
	
			// Get the current term (if any)
			//
			Term term = getTermManager().getLiveTerm(client);
			if ( term.exists() )
			{
				retVal.put(TERM, term);
			
				// Only add the player-related information if the user exists
				//
				if ( user.exists() )
				{
					Player player = getPlayerManager().getPlayer(term, user);
					retVal.put(PLAYER, player);
	
					Balance predictionBalance = getBalanceManager().getBalance(term);
					UserBalance userBal = getUserBalanceManager().getUserBalance(user, predictionBalance);
					userBal.setBalance(predictionBalance);
	
					retVal.put(USERBALANCE, userBal);
				}
			}
		}

		return retVal;
	}

	@Override
	public Map<String, Object> initResponseModel(Model model)
	{
		return Collections.EMPTY_MAP;
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

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}

	public TermManager getTermManager()
	{
		return termManager;
	}

	@Autowired
	public void setTermManager(TermManager termManager)
	{
		this.termManager = termManager;
	}

	public PlayerManager getPlayerManager()
	{
		return playerManager;
	}

	@Autowired
	public void setPlayerManager(PlayerManager playerManager)
	{
		this.playerManager = playerManager;
	}
}
