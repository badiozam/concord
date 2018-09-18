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
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.Collections;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("base.PrivilegeManager")
public class CachedPrivilegeManager extends SimplePrivilegeManager implements PrivilegeManager
{
	@Cacheable(value = "privilegeManager", key="target.makeKey(#pagination)")
	@Override
	public Set<Privilege> getAllPrivileges(PaginationFilter pagination)
	{
		return Collections.unmodifiableSet(super.getAllPrivileges(pagination));
	}

	@Cacheable(value = "privilegeManager", key="target.makeKey(#featureName, #hasWrite)")
	@Override
	public Privilege lookupPrivilege(String featureName, boolean hasWrite)
	{
		return super.lookupPrivilege(featureName, hasWrite);
	}

	@Cacheable(value = "privilegeManager", key="target.makeKey(#a0)")
	@Override
	public Privilege lookupPrivilege(Privilege privilege)
	{
		return super.lookupPrivilege(privilege);
	}

	@CacheEvict(value = "privilegeManager", allEntries = true )
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Privilege> create(Privilege priv)
	{
		return super.create(priv);
	}

	@CacheEvict(value = "privilegeManager", key = "target.makeKey(#a0)")
	@Transactional(readOnly = false)
	@Override
	public ReturnType<Privilege> update(Privilege priv)
	{
		return super.update(priv);
	}

	@CacheEvict(value = "privilegeManager", key = "target.makeKey(#a0)")
	@Transactional(readOnly = false)
	@Override
	public Privilege remove(Privilege priv)
	{
		return super.remove(priv);
	}
}
