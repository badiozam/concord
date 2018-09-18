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
package com.i4one.research.web.controller.admin.surveys;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.model.manager.pagination.SiteGroupPagination;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.admin.BaseTerminableSiteGroupTypeCrudController;
import com.i4one.base.model.category.CategorySelectStringifier;
import com.i4one.research.model.survey.Survey;
import com.i4one.research.model.survey.SurveyManager;
import com.i4one.research.model.survey.SurveyRecord;
import com.i4one.research.model.survey.category.SurveyCategory;
import com.i4one.research.model.survey.category.SurveyCategoryRecord;
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
public class SurveyImportExportController extends BaseTerminableSiteGroupTypeCrudController<SurveyRecord, Survey>
{
	private SurveyManager surveyManager;
	private CategoryManager<SurveyCategoryRecord, SurveyCategory> surveyCategoryManager;

	@Override
	public Model initRequest(HttpServletRequest request, Survey modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelSurvey )
		{
			WebModelSurvey survey = (WebModelSurvey)modelAttribute;
			SingleClient client = model.getSingleClient();

			Set<SurveyCategory> surveyCategories = getSurveyCategoryManager().getAllCategories(new SiteGroupPagination(model.getSiteGroups(), SimplePaginationFilter.NONE));
			model.put("categories", toSelectMapping(surveyCategories, new CategorySelectStringifier(client), model.getLanguage()));
		}

		return model;
	}

	@RequestMapping(value = "**/research/admin/surveys/import", method = RequestMethod.GET)
	public Model importSurveys(@ModelAttribute("survey") WebModelSurveyImport survey, HttpServletRequest request, HttpServletResponse response)
	{
		Model retVal = initRequest(request, survey);

		return initResponse(retVal, response, survey);
	}

	@RequestMapping(value = "**/research/admin/surveys/import", method = RequestMethod.POST)
	public Model doImportSurveys(@ModelAttribute("survey") WebModelSurveyImport survey, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws FileUploadException, IOException
	{
		Model model = initRequest(request, survey);

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
				if ( fieldName.equals("siteGroup"))
				{
					survey.getSiteGroup().resetDelegateBySer(Utils.defaultIfNaN(fieldValue, 0));
				}
				else if (fieldName.equals("category"))
				{
					survey.getCategory().resetDelegateBySer(Utils.defaultIfNaN(fieldValue, 0));
				}
			}
			else
			{
				try
				{
					List<ReturnType<Survey>> importResults = getSurveyManager().importCSV(fileStream.openStream(),
					() ->
					{
						Survey newSurvey = new Survey();
						newSurvey.setClient(survey.getClient());
						newSurvey.setSiteGroup(survey.getSiteGroup());
						newSurvey.setCategory(survey.getCategory());
		
						return newSurvey;
					}, (item) -> { return true; }, (item) -> {});

					List<Errors> errors = new ArrayList<>();
					for ( ReturnType<Survey> currStatus : importResults )
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
		
				survey.setCsv(fieldName);

				// Note that the file upload element has to be the last form item
				// to be processed.
				// 
				break;
			}
		}

		return initResponse(model, response, survey);
	}

	@RequestMapping(value = "**/research/admin/surveys/{id}.csv", produces="text/csv")
	public void exportLive(@PathVariable("id") Integer id, OutputStream out) throws IOException
	{
		Set<Survey> surveys = new HashSet<>();
		surveys.add(getSurveyManager().getById(id));

		getSurveyManager().exportCSV(surveys, out);
	}

	public SurveyManager getSurveyManager()
	{
		return surveyManager;
	}

	@Autowired
	public void setSurveyManager(SurveyManager surveyManager)
	{
		this.surveyManager = surveyManager;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.research.admin.surveys";
	}

	@Override
	protected Manager<SurveyRecord, Survey> getManager()
	{
		return getSurveyManager();
	}

	public CategoryManager<SurveyCategoryRecord, SurveyCategory> getSurveyCategoryManager()
	{
		return surveyCategoryManager;
	}

	@Autowired
	public void setSurveyCategoryManager(CategoryManager<SurveyCategoryRecord, SurveyCategory> surveyCategoryManager)
	{
		this.surveyCategoryManager = surveyCategoryManager;
	}

}
