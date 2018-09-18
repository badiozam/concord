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
package com.i4one.base.tests.model.preferences;

import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.preference.Preference;
import com.i4one.base.model.preference.PreferenceAnswer;
import com.i4one.base.model.preference.PreferenceManager;
import com.i4one.base.model.preference.UserPreference;
import com.i4one.base.model.preference.UserPreferenceManager;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.tests.core.BaseUserManagerTest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;


import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Hamid Badiozamani
 */
public class SimplePreferenceManagerTest extends BaseUserManagerTest
{
	private UserManager userManager;
	private PreferenceManager preferenceManager;
	private UserPreferenceManager userPreferenceManager;

	private Preference singleAnswerPreference;
	private Preference multiAnswerPreference;
	private Preference openAnswerPreference;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		singleAnswerPreference = new Preference();
		singleAnswerPreference.getDelegate().setClientid(getFirstClient().getSer());
		singleAnswerPreference.setSiteGroup(getFirstSiteGroup());
		singleAnswerPreference.setTitle(new IString("Single Answer Preference"));

		List<PreferenceAnswer> answers = new ArrayList<>();
		answers.add(new PreferenceAnswer());
		answers.add(new PreferenceAnswer());
		answers.add(new PreferenceAnswer());

		Iterator<PreferenceAnswer> it = answers.iterator();
		it.next().setAnswer(new IString("Yes"));
		it.next().setAnswer(new IString("No"));
		it.next().setAnswer(new IString("Maybe"));

		singleAnswerPreference.setPreferenceAnswers(answers);
		try
		{
			ReturnType<Preference> singleCreate = getPreferenceManager().create(singleAnswerPreference);

			assertTrue(singleCreate.getPost().exists());
			assertEquals(singleCreate.getPost().getPreferenceAnswers().size(), answers.size());
			for ( PreferenceAnswer prefAnswer : singleCreate.getPost().getPreferenceAnswers() )
			{
				assertTrue(prefAnswer.exists());
			}
		}
		catch (Errors errors)
		{
			getLogger().debug("Caught errors while creating", errors);
		}

		multiAnswerPreference = new Preference();
		multiAnswerPreference.getDelegate().setClientid(getFirstClient().getSer());
		multiAnswerPreference.setSiteGroup(getFirstSiteGroup());
		multiAnswerPreference.setTitle(new IString("Multiple Answer Preference"));

		answers = new ArrayList<>();
		answers.add(new PreferenceAnswer());
		answers.add(new PreferenceAnswer());

		it = answers.iterator();
		it.next().setAnswer(new IString("Coffee"));
		it.next().setAnswer(new IString("Tea"));

		multiAnswerPreference.setPreferenceAnswers(answers);
		ReturnType<Preference> multiCreate = getPreferenceManager().create(multiAnswerPreference);
		assertTrue(multiCreate.getPost().exists());
		assertEquals(multiCreate.getPost().getPreferenceAnswers().size(), answers.size());
		for ( PreferenceAnswer prefAnswer : multiCreate.getPost().getPreferenceAnswers() )
		{
			assertTrue(prefAnswer.exists());
		}

		openAnswerPreference = new Preference();
		openAnswerPreference.getDelegate().setClientid(getFirstClient().getSer());
		openAnswerPreference.setSiteGroup(getFirstSiteGroup());
		openAnswerPreference.setTitle(new IString("Open Answer Preference"));
		openAnswerPreference.setValidAnswer("^.{3,4}$");

		ReturnType<Preference> openCreate = getPreferenceManager().create(openAnswerPreference);
		assertTrue(openCreate.getPost().exists());
		assertEquals(0, openCreate.getPost().getPreferenceAnswers().size());
	}

	@Test
	public void testCreateSuccess()
	{
		Set<PreferenceAnswer> answers = getSingleAnswerPreference().getPreferenceAnswers();
		assertTrue(answers.iterator().next().exists());
		assertEquals(new IString("Yes"), answers.iterator().next().getAnswer());

		UserPreference userPreference = new UserPreference();
		userPreference.setUser(getFirstUser());
		userPreference.setPreference(getSingleAnswerPreference());
		userPreference.setPreferenceAnswer(answers.iterator().next());

		try
		{
			ReturnType<UserPreference> createPref = getUserPreferenceManager().create(userPreference);
			assertTrue(createPref.getPost().exists());

			UserPreference dbPreference = getUserPreferenceManager().getUserPreference(getSingleAnswerPreference(), getFirstUser());
			assertTrue(dbPreference.exists());
			assertEquals(createPref.getPost(), dbPreference);

			dbPreference = getUserPreferenceManager().getUserPreference(getSingleAnswerPreference(), getSecondUser());
			assertTrue(!dbPreference.exists());
		}
		catch (Errors errors)
		{
			getLogger().debug("Caught error while creating", errors);
			throw errors;
		}
	}

	@Test
	public void testUpdateSuccess()
	{
		Set<PreferenceAnswer> answers = getSingleAnswerPreference().getPreferenceAnswers();
		assertTrue(answers.iterator().next().exists());
		assertEquals(new IString("Yes"), answers.iterator().next().getAnswer());

		UserPreference userPreference = new UserPreference();
		userPreference.setUser(getFirstUser());
		userPreference.setPreference(getSingleAnswerPreference());
		userPreference.setPreferenceAnswer(answers.iterator().next());

		try
		{
			ReturnType<UserPreference> createPref = getUserPreferenceManager().create(userPreference);
			assertTrue(createPref.getPost().exists());

			UserPreference dbPreference = getUserPreferenceManager().getUserPreference(getSingleAnswerPreference(), getFirstUser());
			assertTrue(dbPreference.exists());
			assertEquals(createPref.getPost(), dbPreference);

			dbPreference = getUserPreferenceManager().getUserPreference(getSingleAnswerPreference(), getSecondUser());
			assertTrue(!dbPreference.exists());
		}
		catch (Errors errors)
		{
			getLogger().debug("Caught error while creating", errors);
			throw errors;
		}

		UserPreference dbPreference = getUserPreferenceManager().getUserPreference(getSingleAnswerPreference(), getFirstUser());

		Iterator<PreferenceAnswer> it = answers.iterator();
		PreferenceAnswer firstPreferenceAnswer = it.next();

		dbPreference.setPreferenceAnswer(it.next());
		assertTrue(dbPreference.getPreferenceAnswer().exists());
		assertEquals(new IString("No"), dbPreference.getPreferenceAnswer().getAnswer());

		try
		{
			ReturnType<UserPreference> updatedPref = getUserPreferenceManager().update(dbPreference);
			assertTrue(updatedPref.getPre().exists());
			assertTrue(updatedPref.getPost().exists());

			UserPreference updatedPreference = getUserPreferenceManager().getUserPreference(getSingleAnswerPreference(), getFirstUser());
			assertTrue(updatedPreference.exists());
			assertEquals(updatedPref.getPost(), updatedPreference);

			assertNotEquals(firstPreferenceAnswer, updatedPreference.getPreferenceAnswer());
			assertEquals(new IString("No"), updatedPreference.getPreferenceAnswer().getAnswer());
		}
		catch (Errors errors)
		{
			getLogger().debug("Caught error while updating", errors);
			throw errors;
		}
	}

	public Preference getSingleAnswerPreference()
	{
		return singleAnswerPreference;
	}

	public void setSingleAnswerPreference(Preference singleAnswerPreference)
	{
		this.singleAnswerPreference = singleAnswerPreference;
	}

	public Preference getMultiAnswerPreference()
	{
		return multiAnswerPreference;
	}

	public void setMultiAnswerPreference(Preference multiAnswerPreference)
	{
		this.multiAnswerPreference = multiAnswerPreference;
	}

	public Preference getOpenAnswerPreference()
	{
		return openAnswerPreference;
	}

	public void setOpenAnswerPreference(Preference openAnswerPreference)
	{
		this.openAnswerPreference = openAnswerPreference;
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	public PreferenceManager getPreferenceManager()
	{
		return preferenceManager;
	}

	@Autowired
	public void setPreferenceManager(PreferenceManager cachedPreferenceManager)
	{
		this.preferenceManager = cachedPreferenceManager;
	}

	public UserPreferenceManager getUserPreferenceManager()
	{
		return userPreferenceManager;
	}

	@Autowired
	public void setUserPreferenceManager(UserPreferenceManager cachedUserPreferenceManager)
	{
		this.userPreferenceManager = cachedUserPreferenceManager;
	}
}
