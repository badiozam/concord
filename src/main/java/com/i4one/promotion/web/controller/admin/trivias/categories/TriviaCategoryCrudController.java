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
package com.i4one.promotion.web.controller.admin.trivias.categories;

import com.i4one.base.model.category.CategoryManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.admin.categories.CategoryCrudController;
import com.i4one.promotion.model.trivia.category.TriviaCategory;
import com.i4one.promotion.model.trivia.category.TriviaCategoryRecord;
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

/**
 * @author Hamid Badiozamani
 */
@Controller
public class TriviaCategoryCrudController extends CategoryCrudController<TriviaCategoryRecord, TriviaCategory>
{
	private CategoryManager<TriviaCategoryRecord, TriviaCategory> triviaCategoryManager;

	@RequestMapping(value = "**/promotion/admin/trivias/categories/update", method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("category") TriviaCategory category,
					@RequestParam(value = "categoryid", required = false) Integer categoryId,
					HttpServletRequest request, HttpServletResponse response)
	{
		return super.createUpdateImpl(category, categoryId, request, response);
	}

	@RequestMapping(value = "**/promotion/admin/trivias/categories/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("category") TriviaCategory category, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return super.doUpdateImpl(category, result, request, response);
	}

	@Override
	public CategoryManager<TriviaCategoryRecord, TriviaCategory> getCategoryManager()
	{
		return getTriviaCategoryManager();
	}

	public CategoryManager<TriviaCategoryRecord, TriviaCategory> getTriviaCategoryManager()
	{
		return triviaCategoryManager;
	}

	@Autowired
	public void setTriviaCategoryManager(CategoryManager<TriviaCategoryRecord, TriviaCategory> triviaCategoryManager)
	{
		this.triviaCategoryManager = triviaCategoryManager;
	}


}