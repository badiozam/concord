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
package com.i4one.promotion.web.controller.admin.codes;

import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.promotion.model.code.Code;
import com.i4one.promotion.model.code.CodeManager;
import com.i4one.promotion.model.code.CodeRecord;
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
public class CodeCrudController extends BaseTerminableSiteGroupTypeCrudController<CodeRecord, Code>
{
	private CodeManager codeManager;

	@Override
	public Model initRequest(HttpServletRequest request, Code modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelCode )
		{
			WebModelCode code = (WebModelCode)modelAttribute;
			SingleClient client = model.getSingleClient();
		}

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.promotion.admin.codes";
	}

	@Override
	protected Manager<CodeRecord, Code> getManager()
	{
		return getCodeManager();
	}

	@RequestMapping(value = { "**/promotion/admin/codes/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("code") WebModelCode code,
					@RequestParam(value = "id", required = false) Integer codeId,
					HttpServletRequest request, HttpServletResponse response)
	{
		Model model =  createUpdateImpl(code, codeId, request, response);

		if ( !code.exists() )
		{
			code.setOutro(model.buildIMessage("promotion.codeManager.defaultOutro"));
		}

		return model;
	}

	@RequestMapping(value = "**/promotion/admin/codes/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("code") WebModelCode code, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(code, result, request, response);
	}

	@RequestMapping(value = { "**/promotion/admin/codes/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer codeId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(codeId, request, response);
	}

	public CodeManager getCodeManager()
	{
		return codeManager;
	}

	@Autowired
	public void setCodeManager(CodeManager codeManager)
	{
		this.codeManager = codeManager;
	}
}
