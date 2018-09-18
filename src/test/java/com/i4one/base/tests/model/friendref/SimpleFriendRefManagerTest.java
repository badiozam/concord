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
package com.i4one.base.tests.model.friendref;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.friendref.FriendRef;
import com.i4one.base.model.friendref.FriendRefManager;
import com.i4one.base.model.friendref.FriendRefSettings;
import com.i4one.base.model.user.User;
import com.i4one.base.tests.core.BaseUserManagerTest;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SimpleFriendRefManagerTest extends BaseUserManagerTest
{
	private FriendRefManager simpleFriendRefManager;
	private FriendRefSettings settings;

	private FriendRef firstRef;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		settings = getFriendRefManager().getSettings(getFirstClient());
		settings.setEnabled(true);
		settings.setMaxFriends(2);
		settings.setTrickleDuration(2);
		settings.setTricklePercentage(0.10f);

		logAdminIn(getFirstAdmin());
		getFriendRefManager().updateSettings(settings);
		logAdminOut();

		assertNotNull(getFirstUser());
		assertNotNull(getFirstUser().getClient(false));

		firstRef = new FriendRef();
		firstRef.setClient(getFirstClient());
		firstRef.setUser(getFirstUser());
		firstRef.setEmail("test@friendreferral.com");
		firstRef.setFirstName("Tester");
		firstRef.setLastName("von Friendreferral");
	}

	@Test
	public void testSettings() throws Exception
	{
		logAdminIn(getFirstAdmin());
		FriendRefSettings currSettings = getFriendRefManager().getSettings(getFirstClient());

		assertEquals(settings.isEnabled(), currSettings.isEnabled());
		assertEquals(settings.getMaxFriends(), currSettings.getMaxFriends());
		assertEquals(settings.getTrickleDuration(), currSettings.getTrickleDuration());
		assertEquals(settings.getTricklePercentage(), currSettings.getTricklePercentage(), 0.0f);

		currSettings.setEnabled(false);
		getFriendRefManager().updateSettings(currSettings);

		FriendRefSettings newSettings = getFriendRefManager().getSettings(getFirstClient());
		assertEquals(newSettings.isEnabled(), currSettings.isEnabled());

		logAdminOut();
	}

	@Test
	public void testCreate()
	{
		ReturnType<FriendRef> retVal = getFriendRefManager().create(firstRef);
		assertNotNull(retVal);
		FriendRef createdRef = retVal.getPost();
		assertTrue(createdRef.exists());
		assertTrue(createdRef.getTimeStampSeconds() > 0);

		FriendRef dbRef = getFriendRefManager().getReferral(createdRef.getSer(), null);
		assertNotNull(dbRef);
		assertTrue(dbRef.exists());
		assertEquals(firstRef.getEmail(), dbRef.getEmail());

		dbRef = getFriendRefManager().getReferral(0, createdRef.makeOrGetFriend());
		assertNotNull(dbRef);
		assertTrue(dbRef.exists());
		assertEquals(firstRef.getEmail(), dbRef.getEmail());
	}

	@Test
	public void testCreateDuplicateUser()
	{
		FriendRef ref = new FriendRef();
		ref.setUser(getFirstUser());
		ref.setEmail(getSecondUser().getEmail());
		ref.setFirstName("Tester");
		ref.setLastName("von Friendreferral");
		
		try
		{
			ReturnType<FriendRef> retVal = getFriendRefManager().create(ref);
			fail("Collision detection failed");
		}
		catch (Errors errors)
		{
			assertTrue(errors.getHasErrors());

			ErrorMessage message =  errors.getError("friendRefManager.create");
			assertEquals("msg.friendRefManager.create.duplicateUser", message.getMessageKey());
		}
	}

	@Test
	public void testDuplicateReferral()
	{
		FriendRef ref = new FriendRef();
		ref.setClient(getSecondClient());
		ref.setUser(getFirstUser());
		ref.setEmail("test@friendreferral.com");
		ref.setFirstName("Tester");
		ref.setLastName("von Friendreferral");
		
		ReturnType<FriendRef> retVal = getFriendRefManager().create(ref);
		assertNotNull(retVal);
		assertTrue(retVal.getPost().exists());
		assertTrue(retVal.getPost().getTimeStampSeconds() > 0);

		FriendRef dupe = new FriendRef();
		dupe.setClient(getFirstClient());
		dupe.setUser(ref.getUser());
		dupe.setEmail(ref.getEmail());
		dupe.setFirstName(ref.getFirstName());
		dupe.setLastName(ref.getLastName());
		
		try
		{
			getFriendRefManager().create(dupe);
			fail("Collision detection failed!");
		}
		catch (Errors errors)
		{
			assertTrue(errors.getHasErrors());

			ErrorMessage message =  errors.getError("friendRefManager.create");
			assertEquals("msg.friendRefManager.create.duplicateReferral", message.getMessageKey());
		}

	}
	
	@Test
	public void testProcessReferralByEmail() throws NoSuchAlgorithmException
	{
		testCreate();

		User user = new User();
		user.setClient(firstRef.getClient());
		user.setEmail(firstRef.getEmail());
		user.setFirstName("First");
		user.setLastName("Last");

		user.setUsername("firstfriend");
		user.setPassword("password");
		user.setGender("f");
		user.setHomePhone("1234567890");
		user.setCity("San Diego");
		user.setState("CA");
		user.setZipcode("92111");

		ReturnType<User> userRet = getSimpleUserManager().create(user);
		assertNotNull(userRet);
		assertTrue(userRet.getPost().exists());
		assertNotNull(userRet.getChain("friendRefManager.processReferral"));

		ReturnType<FriendRef> processRef = (ReturnType<FriendRef>) userRet.getChain("friendRefManager.processReferral");
		assertNotNull(processRef.getPost());
		assertTrue(processRef.getPost().exists());
	}

	public FriendRefManager getFriendRefManager()
	{
		return getSimpleFriendRefManager();
	}

	public FriendRefManager getSimpleFriendRefManager()
	{
		return simpleFriendRefManager;
	}

	@Autowired
	public void setSimpleFriendRefManager(FriendRefManager simpleFriendRefManager)
	{
		this.simpleFriendRefManager = simpleFriendRefManager;
	}
}
