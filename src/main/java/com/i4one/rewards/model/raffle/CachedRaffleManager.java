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
package com.i4one.rewards.model.raffle;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import java.util.Collections;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedRaffleManager extends SimpleRaffleManager implements RaffleManager
{
	@Cacheable(value = "raffleManager", key = "target.makeKey('getLive', #pagination)")
	@Override
	public Set<Raffle> getLive(TerminablePagination pagination)
	{
		return Collections.unmodifiableSet(super.getLive(pagination));
	}

	@Cacheable(value = "raffleManager", key = "target.makeKey('getByRange', #pagination)")
	@Override
	public Set<Raffle> getByRange(TerminablePagination pagination)
	{
		return Collections.unmodifiableSet(super.getByRange(pagination));
	}

	@CacheEvict(value = "raffleManager", allEntries = true)
	@Override
	public ReturnType<Raffle> clone(Raffle item)
	{
		return super.clone(item);
	}

	@CacheEvict(value = "raffleManager", allEntries = true)
	@Override
	public ReturnType<Raffle> create(Raffle item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "raffleManager", allEntries = true)
	@Override
	public ReturnType<Raffle> update(Raffle item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "raffleManager", allEntries = true)
	@Override
	public Raffle remove(Raffle item)
	{
		return super.remove(item);
	}
}