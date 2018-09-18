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
package com.i4one.predict.model.player;

import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.predict.model.term.Term;
import java.util.Collections;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Hamid Badiozamani
 */
@Service
public class CachedPlayerManager extends SimplePlayerManager implements PlayerManager
{
	@Cacheable(value = "playerManager", key = "target.makeKey(#term, #pagination)")
	@Override
	public Set<Player> getAllPlayers(Term term, PaginationFilter pagination)
	{
		return Collections.unmodifiableSet(super.getAllPlayers(term, pagination));
	}

	@CacheEvict(value = "playerManager", allEntries = true)
	@Override
	public ReturnType<Player> create(Player player)
	{
		return super.create(player);
	}

	@CacheEvict(value = "playerManager", allEntries = true)
	@Override
	public ReturnType<Player> update(Player player)
	{
		return super.update(player);
	}

	@CacheEvict(value = "playerManager", allEntries = true)
	@Override
	public Player remove(Player player)
	{
		return super.remove(player);
	}
}
