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
package com.i4one.rewards.web.controller.user.raffles;

import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.rewards.model.raffle.RaffleEntry;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class RaffleEntryFormProcessController extends BaseRaffleEntryFormController
{
	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@RequestMapping(value = "**/rewards/user/raffles/raffle", method = RequestMethod.POST)
	public Model processRaffle(@ModelAttribute("raffleEntry") @Valid WebModelRaffleEntry raffleEntry, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Model model = initRequest(request, raffleEntry);

		try
		{
			ReturnType<RaffleEntry> processedEntry = getRaffleEntryManager().create(raffleEntry);

			// The raffle may have expired between the time the entry was validated and the time it takes
			// to commit to the database
			//
			if ( !processedEntry.getPost().exists() )
			{
				fail(model, "msg.rewards.user.raffles.index.expired", result, new Errors());
			}
			else
			{
				RaffleEntry purchase = processedEntry.getPost();

				success(model, "msg.rewards.user.raffles.index.successful", processedEntry, SubmitStatus.ModelStatus.SUCCESSFUL);
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.rewards.user.raffles.index.failed", result, errors);
		}

		return initResponse(model, response, raffleEntry);
	}

}
