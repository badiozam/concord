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
package com.i4one.research.web.controller.admin.surveys.categories;

import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.categories.CategoryListingController;
import com.i4one.research.model.survey.category.SurveyCategory;
import com.i4one.research.model.survey.category.SurveyCategoryManager;
import com.i4one.research.model.survey.category.SurveyCategoryRecord;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class SurveyCategoryListingController extends CategoryListingController<SurveyCategoryRecord, SurveyCategory>
{
	private SurveyCategoryManager surveyCategoryManager;

	@RequestMapping(value = "**/research/admin/surveys/categories/index", method = RequestMethod.GET)
	public Model getCategoryListing(HttpServletRequest request, HttpServletResponse response)
	{
		return getCategoryListingImpl(request, response);
	}

	@Override
	public SurveyCategoryManager getCategoryManager()
	{
		return getSurveyCategoryManager();
	}

	public SurveyCategoryManager getSurveyCategoryManager()
	{
		return surveyCategoryManager;
	}

	@Autowired
	public void setSurveyCategoryManager(SurveyCategoryManager surveyCategoryManager)
	{
		this.surveyCategoryManager = surveyCategoryManager;
	}

}
