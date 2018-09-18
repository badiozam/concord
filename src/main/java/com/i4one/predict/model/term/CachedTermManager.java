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
package com.i4one.predict.model.term;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.util.Collections;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service("predict.TermManager")
public class CachedTermManager extends SimpleTermManager implements TermManager
{
	@Cacheable(value = "termManager", key = "target.makeKey('getSettings', #client)")
	@Override
	public TermSettings getSettings(SingleClient client)
	{
		return super.getSettings(client);
	}

	@CacheEvict(value = "termManager", allEntries = true)
	@Override
	public ReturnType<TermSettings> updateSettings(TermSettings settings)
	{
		return super.updateSettings(settings);
	}

	@Cacheable(value = "termManager", key = "target.makeKey('getTerm', #client, #title)")
	@Override
	public Term getTerm(SingleClient client, String title)
	{
		return super.getTerm(client, title);
	}

	@Cacheable(value = "termManager", key = "target.makeKey('getLatestTerm', #client)")
	@Override
	public Term getLatestTerm(SingleClient client)
	{
		return super.getLatestTerm(client);
	}

	@Cacheable(value = "termManager", key = "target.makeKey('getLiveTerm', #client)")
	@Override
	public Term getLiveTerm(SingleClient client)
	{
		return super.getLiveTerm(client);
	}

	@Cacheable(value = "termManager", key = "target.makeKey('getLive', #pagination)")
	@Override
	public Set<Term> getLive(TerminablePagination pagination)
	{
		return Collections.unmodifiableSet(super.getLive(pagination));
	}

	@Cacheable(value = "termManager", key = "target.makeKey('getByRange', #pagination)")
	@Override
	public Set<Term> getByRange(TerminablePagination pagination)
	{
		return Collections.unmodifiableSet(super.getByRange(pagination));
	}

	@CacheEvict(value = "termManager", allEntries = true)
	@Override
	public ReturnType<Term> create(Term item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "termManager", allEntries = true)
	@Override
	public ReturnType<Term> update(Term item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "termManager", allEntries = true)
	@Override
	public Term remove(Term item)
	{
		return super.remove(item);
	}
}
