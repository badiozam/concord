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
package com.i4one.base.tests.model.admin;

import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.admin.AdminRecord;
import com.i4one.base.model.admin.AdminRecordDao;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilegeRecord;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilegeRecordDao;
import com.i4one.base.model.adminprivilege.Privilege;
import com.i4one.base.model.adminprivilege.PrivilegeManager;
import com.i4one.base.model.adminprivilege.PrivilegeRecord;
import com.i4one.base.model.adminprivilege.PrivilegeRecordDao;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.pagination.SimplePaginationFilter;
import com.i4one.base.tests.core.BaseManagerTest;
import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hamid Badiozamani
 */
public class SimpleAdminPrivilegeManagerTest extends BaseManagerTest
{
	private PrivilegeManager privilegeManager;
	private AdminPrivilegeManager simpleAdminPrivilegeManager;

	private Admin firstPrivAdmin;
	private Privilege firstPrivilege;
	private Privilege secondPrivilege;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		AdminRecord adminRecord = new AdminRecord();
		adminRecord.setUsername("hamid");
		adminRecord.setPassword("password");
		adminRecord.setName("Hamid Badiozamani");
		adminRecord.setEmail("hamid@m2omedia.com");

		getAdminRecordDao().insert(adminRecord);
		firstPrivAdmin = new Admin();
		firstPrivAdmin.setDelegate(adminRecord);

		PrivilegeRecord privilege = new PrivilegeRecord();
		privilege.setFeature("base.testing");
		privilege.setReadWrite(true);

		getPrivilegeRecordDao().insert(privilege);
		firstPrivilege = new Privilege();
		firstPrivilege.setDelegate(privilege);

		privilege = new PrivilegeRecord();
		privilege.setFeature("base.testing");
		privilege.setReadWrite(false);

		getPrivilegeRecordDao().insert(privilege);
		secondPrivilege = new Privilege();
		secondPrivilege.setDelegate(privilege);

		ClientAdminPrivilegeRecord adminPriv = new ClientAdminPrivilegeRecord();
		adminPriv.setAdminid(adminRecord.getSer());
		adminPriv.setPrivid(firstPrivilege.getSer());
		adminPriv.setClientid(getFirstClient().getSer());

		getClientAdminPrivilegeRecordDao().insert(adminPriv);

		assertTrue(firstPrivAdmin.exists());
		assertTrue(firstPrivilege.exists());
		assertTrue(secondPrivilege.exists());
	}

	@Test
	public void testLookupPrivilege()
	{
		Privilege lookup = new Privilege();
		lookup.setFeature("base.testing");
		lookup.setReadWrite(true);

		Privilege writePriv = getPrivilegeManager().lookupPrivilege(lookup);
		assertNotNull(writePriv);
		assertTrue(writePriv.exists());
		assertEquals(lookup.getFeature(), writePriv.getFeature());
		assertEquals(lookup.getReadWrite(), writePriv.getReadWrite());

		lookup.setFeature("dne");
		Privilege dnePriv = getPrivilegeManager().lookupPrivilege(lookup);
		assertFalse(dnePriv.exists());
	}

	@Test
	public void testGetAdminPrivileges()
	{
		Set<ClientAdminPrivilege> privs = getAdminPrivilegeManager().getAdminPrivileges(getFirstPrivAdmin(), getFirstClient(), SimplePaginationFilter.NONE);
		assertNotNull(privs);
		assertEquals(1, privs.size());

		ClientAdminPrivilege currPriv = privs.iterator().next();
		assertNotNull(currPriv);
		assertTrue(currPriv.exists());
		assertEquals(getFirstPrivAdmin(), currPriv.getAdmin());
		assertEquals(getFirstClient(), currPriv.getClient());
		assertEquals("base.testing", currPriv.getPrivilege().getFeature());
		assertTrue(currPriv.getPrivilege().getReadWrite());
	}

	@Test
	public void testHasAdminPrivilege()
	{
		ClientAdminPrivilege clientAdminPriv = new ClientAdminPrivilege();
		clientAdminPriv.setAdmin(getFirstPrivAdmin());
		clientAdminPriv.setClient(getFirstClient());

		assertFalse(getAdminPrivilegeManager().hasAdminPrivilege(clientAdminPriv));
		/*
		try
		{
			getAdminPrivilegeManager().hasAdminPrivilege(clientAdminPriv);
			fail("Failed to detect privilege missing error.");
		}
		catch (Errors errors)
		{
			assertTrue(errors.hasErrors());

			ErrorMessage message = errors.getError("msg.adminPrivilegeManager.privilege.dne");
			assertEquals("msg.adminPrivilegeManager.privilege.dne", message.getMessageKey());
		}
		*/

		clientAdminPriv.setPrivilege(getFirstPrivilege());
		clientAdminPriv.setAdmin(new Admin());

		try
		{
			getAdminPrivilegeManager().hasAdminPrivilege(clientAdminPriv);
			fail("Failed to detect admin missing error.");
		}
		catch (Errors errors)
		{
			assertTrue(errors.hasErrors());

			ErrorMessage message = errors.getError("msg.adminPrivilegeManager.admin.dne");
			assertEquals("msg.adminPrivilegeManager.admin.dne", message.getMessageKey());
		}

		clientAdminPriv.setClient(new SingleClient());

		try
		{
			getAdminPrivilegeManager().hasAdminPrivilege(clientAdminPriv);
			fail("Failed to detect client missing error.");
		}
		catch (Errors errors)
		{
			assertTrue(errors.hasErrors());

			ErrorMessage message = errors.getError("msg.adminPrivilegeManager.admin.dne");
			assertEquals("msg.adminPrivilegeManager.admin.dne", message.getMessageKey());

			message = errors.getError("msg.adminPrivilegeManager.client.dne");
			assertEquals("msg.adminPrivilegeManager.client.dne", message.getMessageKey());
		}

		clientAdminPriv = new ClientAdminPrivilege();
		clientAdminPriv.setAdmin(getFirstPrivAdmin());
		clientAdminPriv.setClient(getFirstClient());
		clientAdminPriv.setPrivilege(getFirstPrivilege());

		assertTrue(getAdminPrivilegeManager().hasAdminPrivilege(clientAdminPriv));

		clientAdminPriv.getDelegate().setSer(0);
		clientAdminPriv.setPrivilege(getSecondPrivilege());
		assertFalse(getAdminPrivilegeManager().hasAdminPrivilege(clientAdminPriv));
	}

	@Test
	public void testGrant()
	{
		ClientAdminPrivilege priv = new ClientAdminPrivilege();
		priv.setAdmin(getFirstPrivAdmin());
		priv.setClient(getFirstClient());
		priv.setPrivilege(getSecondPrivilege());

		assertFalse(getAdminPrivilegeManager().hasAdminPrivilege(priv));

		getAdminPrivilegeManager().grant(priv);
		assertTrue(priv.exists());

		assertTrue(getAdminPrivilegeManager().hasAdminPrivilege(priv));
	}

	@Test
	public void testRevoke()
	{
		ClientAdminPrivilege priv = new ClientAdminPrivilege();
		priv.setAdmin(getFirstPrivAdmin());
		priv.setClient(getFirstClient());
		priv.setPrivilege(getFirstPrivilege());

		assertTrue(getAdminPrivilegeManager().hasAdminPrivilege(priv));

		Privilege privilege = getPrivilegeManager().lookupPrivilege(priv.getPrivilege());
		assertTrue(privilege.exists());

		priv.setPrivilege(privilege);
		getAdminPrivilegeManager().revoke(priv);

		assertFalse(getAdminPrivilegeManager().hasAdminPrivilege(priv));
	}

	public AdminPrivilegeManager getAdminPrivilegeManager()
	{
		// We have to ensure that we're always returning the simple version for this test, and since the
		// superclass method is written specifically for other managers rather than this recursive case
		// we need to override the method
		//
		return getSimpleAdminPrivilegeManager();
	}

	public AdminPrivilegeManager getSimpleAdminPrivilegeManager()
	{
		return simpleAdminPrivilegeManager;
	}

	@Autowired
	public void setSimpleAdminPrivilegeManager(AdminPrivilegeManager simpleAdminPrivilegeManager)
	{
		this.simpleAdminPrivilegeManager = simpleAdminPrivilegeManager;
	}

	public PrivilegeManager getPrivilegeManager()
	{
		return privilegeManager;
	}

	@Autowired
	public void setPrivilegeManager(PrivilegeManager privilegeManager)
	{
		this.privilegeManager = privilegeManager;
	}

	public AdminRecordDao getAdminRecordDao()
	{
		return (AdminRecordDao) getDaoManager().getNewDao("base.JdbcAdminRecordDao");
	}

	public PrivilegeRecordDao getPrivilegeRecordDao()
	{
		return (PrivilegeRecordDao) getDaoManager().getNewDao("base.JdbcPrivilegeRecordDao");
	}

	public ClientAdminPrivilegeRecordDao getClientAdminPrivilegeRecordDao()
	{
		return (ClientAdminPrivilegeRecordDao) getDaoManager().getNewDao("base.JdbcClientAdminPrivilegeRecordDao");
	}

	public Admin getFirstPrivAdmin()
	{
		return firstPrivAdmin;
	}

	public Privilege getFirstPrivilege()
	{
		return firstPrivilege;
	}

	public Privilege getSecondPrivilege()
	{
		return secondPrivilege;
	}

}
