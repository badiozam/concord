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
package com.i4one.promotion.web.controller.user.clickthrus;

import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balancetrigger.BalanceTriggerManager;
import com.i4one.base.model.balancetrigger.UserBalanceTrigger;
import com.i4one.base.model.balancetrigger.UserBalanceTriggerManager;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.JsonError;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.user.BaseUserViewController;
import com.i4one.promotion.model.clickthru.ClickThruResponse;
import com.i4one.promotion.model.clickthru.ClickThruResponseManager;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class ClickThruResponseFormController extends BaseUserViewController
{
	private BalanceTriggerManager balanceTriggerManager;
	private UserBalanceTriggerManager userBalanceTriggerManager;

	private ClickThruResponseManager clickThruResponseManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof ClickThruResponse )
		{
		}

		return model;
	}

	@RequestMapping(value = "**/promotion/user/clickthrus/{id}")
	public void processClickThru(@PathVariable("id") int id, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException
	{
		Model model = initRequest(request, null);

		User user = model.getUser();

		ClickThruResponse clickThruResponse = new ClickThruResponse();
		clickThruResponse.getClickThru().setSer(id);
		clickThruResponse.setUser(user);


		try
		{
			if ( clickThruResponse.getClickThru().exists() )
			{
				ReturnType<ClickThruResponse> processedClickThru = getClickThruResponseManager().create(clickThruResponse);

				// Regardless of status, we send to the programmed URL
				//
				response.sendRedirect(processedClickThru.getPost().getClickThru().getURL());
			}
			else
			{
				getLogger().debug("No live clickThrus found matching " + clickThruResponse.getClickThru());

				/*
				Errors errors = new Errors(new ErrorMessage("msg.promotion.clickThruResponseManager.create.clickthrudne", "The clickThru '$clickThru.clickThru' is not a valid clickThru", new Object[] { "clickThru", clickThruResponse.getClickThru()}, null));

				fail(model, "msg.promotion.user.clickThrus.index.notfound", null, errors);
				*/
				response.sendRedirect(model.getBaseURL());
			}
		}
		catch (Errors errors)
		{
			getLogger().warn("processClickThru failed", errors);
			response.sendRedirect(model.getBaseURL());
		}
	}

	@RequestMapping(value = "**/promotion/user/clickthrus/{id}", produces = "application/json")
	public @ResponseBody Object processClickThruJSON(@PathVariable("id") int id, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException
	{
		Model model = initRequest(request, null);

		User user = model.getUser();

		ClickThruResponse clickThruResponse = new ClickThruResponse();
		clickThruResponse.getClickThru().setSer(id);
		clickThruResponse.setUser(user);

		try
		{
			ReturnType<ClickThruResponse> processedClickThru = getClickThruResponseManager().create(clickThruResponse);

			List<ReturnType<UserBalanceTrigger>> processedTriggers = (List<ReturnType<UserBalanceTrigger>>) processedClickThru.get("processedTriggers");

			/*
			 * This happens every 5 seconds regardless since that's the cooldown, we need to add
			 * a check to consider the processedTriggers when displaying this message. 
			 *
			if ( processedClickThru.getPre().exists() &&
				processedClickThru.getPost().exists() &&
				processedClickThru.getPre().getQuantity() == processedClickThru.getPost().getQuantity())
			*/
			if ( !processedClickThru.getPost().getClickThru().exists() )
			{
				success(model, "msg.promotion.user.clickthrus.index.wrong",
					processedClickThru, SubmitStatus.ModelStatus.WRONG);
			}
			else if ( processedTriggers == null || processedTriggers.isEmpty() )
			{
				success(model, "msg.promotion.user.clickthrus.index.prevplayed",
					processedClickThru, SubmitStatus.ModelStatus.PREVPLAYED);
			}
			else if ( !processedClickThru.getPost().exists() )
			{
				success(model, "msg.promotion.user.clickthrus.index.expired",
					processedClickThru, SubmitStatus.ModelStatus.EXPIRED);
			}
			else
			{
				success(model, "msg.promotion.user.clickthrus.index.successful",
					processedClickThru, SubmitStatus.ModelStatus.SUCCESSFUL);
			}

			// The model containing the SubmitStatus object is passed to the JSON
			// response to be delivered to the caller
			//
			JsonClickThruResponse retVal = new JsonClickThruResponse(model, processedClickThru);
			retVal.setUserBalanceTriggerManager(getUserBalanceTriggerManager());
			retVal.setBalanceTriggerManager(getBalanceTriggerManager());
			retVal.init();

			return retVal;
		}
		catch (Errors errors)
		{
			return new JsonError(model, errors);
		}
	}

	public ClickThruResponseManager getClickThruResponseManager()
	{
		return clickThruResponseManager;
	}

	@Autowired
	public void setClickThruResponseManager(ClickThruResponseManager clickThruResponseManager)
	{
		this.clickThruResponseManager = clickThruResponseManager;
	}

	public BalanceTriggerManager getBalanceTriggerManager()
	{
		return balanceTriggerManager;
	}

	@Autowired
	public void setBalanceTriggerManager(BalanceTriggerManager balanceTriggerManager)
	{
		this.balanceTriggerManager = balanceTriggerManager;
	}

	public UserBalanceTriggerManager getUserBalanceTriggerManager()
	{
		return userBalanceTriggerManager;
	}

	@Autowired
	public void setUserBalanceTriggerManager(UserBalanceTriggerManager userBalanceTriggerManager)
	{
		this.userBalanceTriggerManager = userBalanceTriggerManager;
	}

}
