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

import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.accesscode.AccessCode;
import com.i4one.base.model.accesscode.AccessCodeManager;
import com.i4one.base.model.accesscode.AccessCodeRecord;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.admin.BaseTerminableCrudController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class AccessCodeImportExportController extends BaseTerminableCrudController<AccessCodeRecord, AccessCode>
{
	private AccessCodeManager accessCodeManager;

	@RequestMapping(value = "**/base/admin/accesscodes/import", method = RequestMethod.GET)
	public Model importCodes(@ModelAttribute("accessCode") WebModelAccessCodeImport code, HttpServletRequest request, HttpServletResponse response)
	{
		Model retVal = initRequest(request, code);

		return initResponse(retVal, response, code);
	}

	@RequestMapping(value = "**/base/admin/accesscodes/import", method = RequestMethod.POST)
	public Model doImportCodes(@ModelAttribute("accessCode") WebModelAccessCodeImport code, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws FileUploadException, IOException
	{
		Model model = initRequest(request, code);

		// Streaming upload
		//
		ServletFileUpload upload = new ServletFileUpload();

		FileItemIterator it = upload.getItemIterator(request);
		while ( it.hasNext() )
		{
			FileItemStream fileStream = it.next();

			String fieldName = fileStream.getFieldName();
			if ( fileStream.isFormField() )
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream.openStream()));
				String fieldValue = reader.readLine();

				getLogger().debug("{} = {}", fieldName, fieldValue);
			}
			else
			{
				try
				{
					List<ReturnType<AccessCode>> importResults = getAccessCodeManager().importCSV(fileStream.openStream(),
					() ->
					{
						AccessCode newCode = new AccessCode();
						newCode.setClient(code.getClient());
		
						return newCode;
					}, (item) -> { return true; }, (item) -> {});

					List<Errors> errors = new ArrayList<>();
					for ( ReturnType<AccessCode> currStatus : importResults )
					{
						if ( currStatus.containsKey("importErrors"))
						{
							errors.add((Errors) currStatus.get("importErrors"));
						}
					}
		
					if ( errors.isEmpty() )
					{
						success(model, getMessageRoot() + ".import.success");
					}
					else
					{
						success(model, getMessageRoot() + ".import.partial", importResults, SubmitStatus.ModelStatus.PARTIAL);
					}
				}
				catch (Errors errors)
				{
					fail(model, getMessageRoot() + ".import.failure", result, errors);
				}
		
				code.setCsv(fieldName);

				// Note that the file upload element has to be the last form item
				// to be processed.
				// 
				break;
			}
		}

		return initResponse(model, response, code);
	}

	@RequestMapping(value = "**/base/admin/accesscodes/{id}.csv", produces="text/csv")
	public void exportAccessCode(@PathVariable("id") Integer id, OutputStream out) throws IOException
	{
		Set<AccessCode> codes = new HashSet<>();
		codes.add(getAccessCodeManager().getById(id));

		getAccessCodeManager().exportCSV(codes, out);
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.accesscodes";
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

	@Override
	protected Manager<AccessCodeRecord, AccessCode> getManager()
	{
		return getAccessCodeManager();
	}

}
