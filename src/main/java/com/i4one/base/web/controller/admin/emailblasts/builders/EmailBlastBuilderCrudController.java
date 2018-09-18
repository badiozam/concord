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
package com.i4one.base.web.controller.admin.emailblasts.builders;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.emailblast.builder.EmailBlastBuilder;
import com.i4one.base.model.emailblast.builder.EmailBlastBuilderManager;
import com.i4one.base.model.emailblast.builder.EmailBlastBuilderRecord;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseClientTypeCrudController;
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
public class EmailBlastBuilderCrudController extends BaseClientTypeCrudController<EmailBlastBuilderRecord, EmailBlastBuilder>
{
	private EmailBlastBuilderManager emailBlastBuilderManager;

	@Override
	public Model initRequest(HttpServletRequest request, EmailBlastBuilder modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof EmailBlastBuilder )
		{
			EmailBlastBuilder emailblast = (EmailBlastBuilder)modelAttribute;
			SingleClient client = model.getSingleClient();
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.emailblasts.builders";
	}

	@Override
	protected Manager<EmailBlastBuilderRecord, EmailBlastBuilder> getManager()
	{
		return getEmailBlastBuilderManager();
	}

	@RequestMapping(value = {"**/base/admin/emailblasts/builders/preview", "**/base/admin/emailblasts/builders/build"}, method = RequestMethod.GET)
	public Model preview(@ModelAttribute("builder") WebModelEmailBlastBuilder builder,
					@RequestParam(value = "id", required = false) Integer id,
					HttpServletRequest request, HttpServletResponse response)
	{
		if ( id != null )
		{
			builder.setSer(id);
			builder.loadedVersion();
		}

		Model model = this.initRequest(request, builder);

		return initResponse(model, response, builder);
	}

	@RequestMapping(value = "**/base/admin/emailblasts/builders/build", method = RequestMethod.POST, params = "build")
	public ModelAndView build(@ModelAttribute("builder") WebModelEmailBlastBuilder builder,
					BindingResult result,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, builder);

		try
		{
			ReturnType<EmailBlast> emailBlast = getEmailBlastBuilderManager().build(builder);

			return redirOnSuccess(model, result, null, null, "../update.html?id=" + emailBlast.getPost().getSer(), request, response);
		}
		catch (Errors errors)
		{
			fail(model, getMessageRoot() + ".build.failure", result, errors);
		}

		return new ModelAndView().addAllObjects(initResponse(model, response, builder));
	}

	@RequestMapping(value = "**/base/admin/emailblasts/builders/build", method = RequestMethod.POST, params = "save")
	public Model saveState(@ModelAttribute("builder") WebModelEmailBlastBuilder builder,
					BindingResult result,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model = initRequest(request, builder);

		try
		{
			getEmailBlastBuilderManager().saveState(builder);

			success(model, getMessageRoot() + ".save.success");
		}
		catch (Errors errors)
		{
			fail(model, getMessageRoot() + ".save.failure", result, errors);
		}

		return initResponse(model, response, builder);
	}


	@RequestMapping(value = { "**/base/admin/emailblasts/builders/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("builder") WebModelEmailBlastBuilder builder,
					@RequestParam(value = "id", required = false) Integer builderId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model =  createUpdateImpl(builder, builderId, request, response);

		if ( !builder.exists() )
		{
			//emailblast.setTitle(model.buildIMessage("base.emailBlastBuilderManager.defaultTitle"));
			//emailblast.setBody(model.buildIMessage("base.emailBlastBuilderManager.defaultBody"));
		}

		return model;
	}

	@RequestMapping(value = "**/base/admin/emailblasts/builders/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("builder") WebModelEmailBlastBuilder builder, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(builder, result, request, response);
	}

	@RequestMapping(value = { "**/base/admin/emailblasts/builders/remove" }, method = RequestMethod.GET)
	public ModelAndView remove(@RequestParam(value="id") Integer id, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return removeImpl(id, request, response);
	}

	@RequestMapping(value = { "**/base/admin/emailblasts/builders/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer emailblastId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(emailblastId, request, response);
	}

	public EmailBlastBuilderManager getEmailBlastBuilderManager()
	{
		return emailBlastBuilderManager;
	}

	@Autowired
	public void setEmailBlastBuilderManager(EmailBlastBuilderManager emailBlastBuilderManager)
	{
		this.emailBlastBuilderManager = emailBlastBuilderManager;
	}
}