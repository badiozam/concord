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
package com.i4one.base.tests.model.user;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.transaction.Transaction;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Hamid Badiozamani
 */
public class TransactionalUserBalanceManagerTest extends HistoricalUserBalanceManagerTest
{
	private UserBalanceManager transactionalUserBalanceManager;

	@Test
	public void testUpdateTransactional()
	{
		/*
		getHistoricalUserBalanceManager().set(BasePrivilegedManager.ATTR_ADMIN, new Admin());
		getHistoricalUserBalanceManager().set(BaseUserPrivilegedManager.ATTR_USER, getFirstUser());
		*/
		logAdminOut();
		logUserIn(getFirstUser());

		ReturnType<UserBalance> updateStatus = testUpdateImpl();
		assertNotNull(updateStatus.getPre());
		assertNotNull(updateStatus.getPost());

		Transaction transaction = (Transaction) updateStatus.get("transaction");
		assertNotNull(transaction);
		assertTrue(transaction.exists());
	}

	@Override
	public UserBalanceManager getUserBalanceManager()
	{
		return getTransactionalUserBalanceManager();
	}

	public UserBalanceManager getTransactionalUserBalanceManager()
	{
		return transactionalUserBalanceManager;
	}

	@Autowired
	@Qualifier("base.UserBalanceManager")
	public void setTransactionalUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.transactionalUserBalanceManager = userBalanceManager;
	}
}
