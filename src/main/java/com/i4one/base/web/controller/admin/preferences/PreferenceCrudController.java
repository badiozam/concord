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
package com.i4one.base.web.controller.admin.preferences;

import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.Manager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.model.preference.Preference;
import com.i4one.base.model.preference.PreferenceAnswerManager;
import com.i4one.base.model.preference.PreferenceManager;
import com.i4one.base.model.preference.PreferenceRecord;
import com.i4one.base.web.controller.admin.BaseSiteGroupTypeCrudController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
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
public class PreferenceCrudController extends BaseSiteGroupTypeCrudController<PreferenceRecord, Preference>
{
	private BalanceManager balanceManager;
	private PreferenceManager preferenceManager;
	private PreferenceAnswerManager preferenceAnswerManager;

	@Override
	public Model initRequest(HttpServletRequest request, Preference modelAttribute)
	{
		Model model = super.initRequest(request, modelAttribute);

		if ( modelAttribute instanceof WebModelPreference )
		{
			WebModelPreference preference = (WebModelPreference)modelAttribute;
			SingleClient client = model.getSingleClient();
		}

		// The type of questions and displays
		//
		// 0 - Single Answer: Radio Buttons
		// 1 - Single Answer: Drop-down
		//
		Map<Integer, String> questions = new LinkedHashMap<>();
		questions.put(Preference.TYPE_SINGLEANSWER_RADIO, model.buildMessage("msg.base.admin.preferences.update.singleanswerradio"));
		questions.put(Preference.TYPE_SINGLEANSWER_SELECT, model.buildMessage("msg.base.admin.preferences.update.singleanswerselect"));
		questions.put(Preference.TYPE_MULTIANSWER_CHECKBOX, model.buildMessage("msg.base.admin.preferences.update.multianswercheckbox"));
		questions.put(Preference.TYPE_OPENANSWER_MULTI, model.buildMessage("msg.base.admin.preferences.update.openanswermulti"));
		questions.put(Preference.TYPE_OPENANSWER_SINGLE, model.buildMessage("msg.base.admin.preferences.update.openanswersingle"));

		model.put("validQuestionType", questions);

		// The types of open answers allowed
		//
		Map<String, String> validAnswerTypes = new LinkedHashMap<>();
		validAnswerTypes.put("^.*$", model.buildMessage("msg.base.admin.preferences.update.validanswer.anyresponse"));
		validAnswerTypes.put("^.+$", model.buildMessage("msg.base.admin.preferences.update.validanswer.notempty"));
		validAnswerTypes.put("^\\d+(\\.\\d{1,})?$", model.buildMessage("msg.base.admin.preferences.update.validanswer.numbers"));

		model.put("validAnswerTypes", validAnswerTypes);

		return model;
	}

	@Override
	protected String getMessageRoot()
	{
		return "msg.base.admin.preferences";
	}

	@Override
	protected Manager<PreferenceRecord, Preference> getManager()
	{
		return getPreferenceManager();
	}

	@RequestMapping(value = { "**/base/admin/preferences/update" }, method = RequestMethod.GET)
	public Model createUpdate(@ModelAttribute("preference") WebModelPreference preference,
					@RequestParam(value = "id", required = false) Integer preferenceId,
					HttpServletRequest request, HttpServletResponse response)
	{
		getLogger().debug("Create update called");

		Model model = createUpdateImpl(preference, preferenceId, request, response);

		// We only set the answers when displaying the update form which is why this isn't in initRequest.
		// The reason for this is that calling setPreferenceAnswers(..) would erase any form elements that
		// were updated during POST submission
		//
		preference.setPreferenceAnswers(getPreferenceAnswerManager().getAnswers(preference));

		if ( !preference.exists() )
		{
		}

		return model;
	}

	@RequestMapping(value = "**/base/admin/preferences/update", method = RequestMethod.POST)
	public Model doUpdate(@ModelAttribute("preference") WebModelPreference preference, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return doUpdateImpl(preference, result, request, response);
	}

	@RequestMapping(value = { "**/base/admin/preferences/remove" }, method = RequestMethod.GET)
	public ModelAndView remove(@RequestParam(value="id") Integer preferenceId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return removeImpl(preferenceId, request, response);
	}

	@RequestMapping(value = { "**/base/admin/preferences/clone" }, method = RequestMethod.GET)
	public ModelAndView clone(@RequestParam(value="id") Integer preferenceId, HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		return cloneImpl(preferenceId, request, response);
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

	public PreferenceManager getPreferenceManager()
	{
		return preferenceManager;
	}

	@Autowired
	public void setPreferenceManager(PreferenceManager preferenceManager)
	{
		this.preferenceManager = preferenceManager;
	}

	public PreferenceAnswerManager getPreferenceAnswerManager()
	{
		return preferenceAnswerManager;
	}

	@Autowired
	public void setPreferenceAnswerManager(PreferenceAnswerManager preferenceAnswerManager)
	{
		this.preferenceAnswerManager = preferenceAnswerManager;
	}
}
