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
package com.i4one.base.web.controller.user.accesscodes;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.accesscode.AccessCodeManager;
import com.i4one.base.model.accesscode.AccessCodeResponse;
import com.i4one.base.model.accesscode.AccessCodeResponseManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.user.BaseUserViewController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AccessCodeResponseFormController extends BaseUserViewController
{
	private AccessCodeManager accessCodeManager;
	private AccessCodeResponseManager accessCodeResponseManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelAccessCodeResponse )
		{
		}

		return model;
	}

	@RequestMapping(value = "**/base/user/accesscodes/index", method = RequestMethod.GET)
	public Model codeForm(@RequestParam(value = "id", defaultValue = "0") int ser,  @ModelAttribute("accessCodeResponse") WebModelAccessCodeResponse accessCodeResponse, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException
	{
		accessCodeResponse.getAccessCode().setSer(ser);
		Model model = initRequest(request, accessCodeResponse);

		return initResponse(model, response, accessCodeResponse);
	}

	@RequestMapping(value = "**/base/user/accesscodes/index", method = RequestMethod.POST)
	public Model processCode(@ModelAttribute("accessCodeResponse") @Valid WebModelAccessCodeResponse accessCodeResponse, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException
	{
		Model model = initRequest(request, accessCodeResponse);

		// Need to load the pages which loadedVersion() won't do
		//
		accessCodeResponse.setAccessCode(getAccessCodeManager().getById(accessCodeResponse.getAccessCode().getSer()));

		try
		{
			Errors errors = new Errors();
			if ( accessCodeResponse.isCorrect() )
			{
				ReturnType<AccessCodeResponse> processedResponse = getAccessCodeResponseManager().create(accessCodeResponse);
				//response.sendRedirect(model.getBaseURL() + "/" + accessCodeResponse.getAccessCode().getPages().iterator().next().getPage());
				success(model, "msg.base.user.accesscodes.index.successful", processedResponse, SubmitStatus.ModelStatus.SUCCESSFUL);
				getLogger().debug("Successful response");
			}
			else
			{
				getLogger().debug("Invalid access code '"  + accessCodeResponse.getCode() + "' vs '" + accessCodeResponse.getAccessCode().getCode() + "'" );
				errors.addError(new ErrorMessage("accessCodeResponse.code", "msg.base.AccessCode.invalidCode", "Sorry, that's not the correct code.", new Object[]{"item", this}));
				fail(model, "msg.base.user.accesscodes.index.failed", result, errors);
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.base.user.accesscodes.index.failed", result, errors);
		}

		return initResponse(model, response, accessCodeResponse);
	}

	public AccessCodeResponseManager getAccessCodeResponseManager()
	{
		return accessCodeResponseManager;
	}

	@Autowired
	public void setAccessCodeResponseManager(AccessCodeResponseManager accessCodeResponseManager)
	{
		this.accessCodeResponseManager = accessCodeResponseManager;
	}

	public AccessCodeManager getAccessCodeManager()
	{
		return accessCodeManager;
	}

	@Autowired
	public void setAccessCodeManager(AccessCodeManager accessCodeManager)
	{
		this.accessCodeManager = accessCodeManager;
	}
}
