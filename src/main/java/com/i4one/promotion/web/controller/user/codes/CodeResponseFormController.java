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
package com.i4one.promotion.web.controller.user.codes;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.user.User;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.user.BaseUserViewController;
import com.i4one.promotion.model.code.CodeResponse;
import com.i4one.promotion.model.code.CodeResponseManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class CodeResponseFormController extends BaseUserViewController
{
	private CodeResponseManager codeResponseManager;

	@Override
	public boolean isAuthRequired()
	{
		return true;
	}

	@Override
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof CodeResponse )
		{
		}

		return model;
	}

	@RequestMapping(value = "**/promotion/user/codes/index", method = RequestMethod.GET)
	public Model codeForm(@ModelAttribute("codeResponse") WebModelCodeResponse codeResponse, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException
	{
		codeResponse = new WebModelCodeResponse();
		Model model = initRequest(request, codeResponse);

		return initResponse(model, response, codeResponse);
	}

	private void setModelStatus(WebModelCodeResponse codeResponse, List<ReturnType<CodeResponse>> processedCodes, Model model) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		ReturnType<CodeResponse> firstProcessedCode = processedCodes.get(0);

		if ( !firstProcessedCode.getPost().getCode().exists() )
		{
			success(model, "msg.promotion.user.codes.index.wrong",
				processedCodes, SubmitStatus.ModelStatus.WRONG);

		}
		else if ( firstProcessedCode.getPre().exists() )
		{
			success(model, "msg.promotion.user.codes.index.prevplayed",
				processedCodes, SubmitStatus.ModelStatus.PREVPLAYED);

			codeResponse.copyOver(firstProcessedCode.getPost());
		}
		else if ( !firstProcessedCode.getPost().exists() )
		{
			success(model, "msg.promotion.user.codes.index.expired",
				processedCodes, SubmitStatus.ModelStatus.EXPIRED);
		}
		else
		{
			success(model, "msg.promotion.user.codes.index.successful",
				processedCodes, SubmitStatus.ModelStatus.SUCCESSFUL);

			codeResponse.copyOver(firstProcessedCode.getPost());
		}

	}

	@RequestMapping(value = "**/promotion/user/codes/index", produces = "application/json")
	public @ResponseBody Object processCodesJSON(@ModelAttribute("codeResponse") @Valid WebModelCodeResponse codeResponse, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Model model = initRequest(request, codeResponse);

		User user = model.getUser();

		try
		{
			List<ReturnType<CodeResponse>> processedCodes;
			if ( codeResponse.getCode().exists() )
			{
				ReturnType<CodeResponse> processedCode = getCodeResponseManager().create(codeResponse);
				codeResponse.copyOver(processedCode.getPost());

				processedCodes = new ArrayList<>();
				processedCodes.add(processedCode);
			}
			else
			{
				processedCodes = getCodeResponseManager().processCodes(codeResponse.getCode().getCode(),
					user,
					new TerminablePagination(model.getTimeInSeconds(),
						new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE)));
			}

			setModelStatus(codeResponse, processedCodes, model);
			JsonCodeResponse retVal = new JsonCodeResponse(model, processedCodes.get(0));

			return retVal;

			/*
			Errors errors = new Errors(new ErrorMessage("msg.promotion.codeResponseManager.processCodes.codedne", "The code '$code.code' is not a valid code", new Object[] { "code", codeResponse.getCode()}, null));

			return errors;
			*/
		}
		catch (Errors errors)
		{
			return errors;
		}
	}

	@RequestMapping(value = "**/promotion/user/codes/index", method = RequestMethod.POST)
	public Model processCodes(@ModelAttribute("codeResponse") @Valid WebModelCodeResponse codeResponse, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Model model = initRequest(request, codeResponse);

		User user = model.getUser();

		try
		{
			List<ReturnType<CodeResponse>> processedCodes;
			if ( codeResponse.getCode().exists() )
			{
				ReturnType<CodeResponse> processedCode = getCodeResponseManager().create(codeResponse);
				codeResponse.copyOver(processedCode.getPost());

				processedCodes = new ArrayList<>();
				processedCodes.add(processedCode);
			}
			else
			{
				processedCodes = getCodeResponseManager().processCodes(codeResponse.getCode().getCode(),
					user,
					new TerminablePagination(model.getTimeInSeconds(),
						new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE)));
			}

			if ( !processedCodes.isEmpty() )
			{
				setModelStatus(codeResponse, processedCodes, model);
			}
			else
			{
				getLogger().debug("No live codes found matching " + codeResponse.getCode());

				Errors errors = new Errors(new ErrorMessage("msg.promotion.codeResponseManager.processCodes.codedne", "The code '$code.code' is not a valid code", new Object[] { "code", codeResponse.getCode()}, null));

				fail(model, "msg.promotion.user.codes.index.notfound", result, errors);
			}
		}
		catch (Errors errors)
		{
			fail(model, "msg.promotion.user.codes.index.failed", result, errors);
		}

		return initResponse(model, response, codeResponse);
	}

	public CodeResponseManager getCodeResponseManager()
	{
		return codeResponseManager;
	}

	@Autowired
	public void setCodeResponseManager(CodeResponseManager codeResponseManager)
	{
		this.codeResponseManager = codeResponseManager;
	}


}
