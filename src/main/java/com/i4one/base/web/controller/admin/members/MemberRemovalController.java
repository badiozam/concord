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
package com.i4one.base.web.controller.admin.members;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class MemberRemovalController extends BaseAdminViewController
{
	private UserManager userManager;
	private UserBalanceManager userBalanceManager;

	@ModelAttribute("removal")
	public Removal initRemoval()
	{
		return new Removal();
	}

	@RequestMapping(value = "**/admin/members/removal", method = RequestMethod.GET)
	public Model searchForm(@ModelAttribute("removal") Removal removal, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Model model = initRequest(request, removal);

		return initResponse(model, response, removal);
	}

	@RequestMapping(value = "**/admin/members/removal", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("removal") Removal removal, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
	{
		Model model = initRequest(request, removal);

		String[] usernameList = removal.getUsernames().split("\n");
		List<ReturnType<User>> removalResults = new ArrayList<>();
		List<Errors> errors = new ArrayList<>();

		for ( String username : usernameList )
		{
			User searchUser = new User();
			searchUser.setUsername(username);

			User user = getUserManager().lookupUser(searchUser);
			if ( user.exists() )
			{
				User removed = getUserManager().remove(user);

				ReturnType<User> removalStatus = new ReturnType<>();
				removalStatus.setPre(removed);
				removalStatus.setPost(new User());

				removalResults.add(removalStatus);
			}
			else
			{
				ReturnType<User> removalStatus = new ReturnType<>();
				removalStatus.put("errors", new Errors(new ErrorMessage(getMessageRoot() + "removal.notfound", "User $user.username not found", new Object[] {"user", user})));

				// User not found
				//
				removalResults.add(removalStatus);
			}

			if ( errors.isEmpty() )
			{
				success(model, getMessageRoot() + ".removal.success");
			}
			else
			{
				success(model, getMessageRoot() + ".removal.partial", removalResults, SubmitStatus.ModelStatus.PARTIAL);
			}
		}

		return initResponse(model, response, removal);
	}

	protected String getMessageRoot()
	{
		return "msg.base.admin.members";
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
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

}
