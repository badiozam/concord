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
package com.i4one.base.web.controller.admin.accesscodes;

import com.i4one.base.model.accesscode.AccessCode;
import com.i4one.base.model.accesscode.AccessCodeManager;
import com.i4one.base.model.accesscode.AccessCodeRecord;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableCrudController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AccessCodeCrudController extends BaseTerminableCrudController<AccessCodeRecord, AccessCode>
{
	private AccessCodeManager accessCodeManager;

	@Override
	public Model initRequest(HttpServletRequest request, AccessCode modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelAccessCode )
		{
			WebModelAccessCode code = (WebModelAccessCode)modelAttribute;
			SingleClient client = model.getSingleClient();
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.accesscodes";
	}

	@Override
	protected Manager<AccessCodeRecord, AccessCode> getManager()
	{
		return getAccessCodeManager();
	}

	@RequestMapping(value = { "**/base/admin/accesscodes/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("accesscode") WebModelAccessCode accessCode,
					@RequestParam(value = "id", required = false) Integer codeId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model =  createUpdateImpl(accessCode, codeId, request, response);

		if ( !accessCode.exists() )
		{
			accessCode.setDescr(model.buildIMessage("base.accessCodeManager.defaultDescr"));
			accessCode.setAccessDenied(model.buildIMessage("base.accessCodeManager.defaultAccessDenied"));
		}

		return model;
	}

	@RequestMapping(value = "**/base/admin/accesscodes/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("accesscode") WebModelAccessCode accessCode, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(accessCode, result, request, response);
	}

	@RequestMapping(value = { "**/base/admin/accesscodes/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer codeId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(codeId, request, response);
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
