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
package com.i4one.base.model.adminprivilege;

import com.i4one.base.model.ReturnType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedAdminPrivilegeManager extends SimpleAdminPrivilegeManager implements AdminPrivilegeManager
{
	@CacheEvict(value = "adminPrivilegeManager", allEntries = true)
	@Override
	public void init()
	{
		super.init();
	}

	@Cacheable(value = "adminPrivilegeManager", key="target.makeKey(#priv)")
	@Override
	public boolean hasAdminPrivilege(ClientAdminPrivilege priv)
	{
		return super.hasAdminPrivilege(priv);
	}

	@CacheEvict(value = "adminPrivilegeManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClientAdminPrivilege> grant(ClientAdminPrivilege priv)
	{
		return super.grant(priv);
	}

	@CacheEvict(value = "adminPrivilegeManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClientAdminPrivilege> revoke(ClientAdminPrivilege priv)
	{
		return super.revoke(priv);
	}
}
