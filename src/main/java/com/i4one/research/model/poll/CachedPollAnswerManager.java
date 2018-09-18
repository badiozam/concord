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
package com.i4one.research.model.poll;

import com.i4one.base.model.ReturnType;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service("research.PollAnswerManager")
public class CachedPollAnswerManager extends SimplePollAnswerManager implements PollAnswerManager
{
	@Cacheable(value = "pollAnswerManager", key = "target.makeKey('getAnswers', #poll)")
	@Override
	public Set<PollAnswer> getAnswers(Poll poll)
	{
		return Collections.unmodifiableSet(super.getAnswers(poll));
	}

	@CacheEvict(value = "pollAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<PollAnswer> clone(PollAnswer item)
	{
		return super.clone(item);
	}

	@CacheEvict(value = "pollAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<PollAnswer> create(PollAnswer item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "pollAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<PollAnswer> update(PollAnswer item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "pollAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public PollAnswer remove(PollAnswer item)
	{
		return super.remove(item);
	}

	@CacheEvict(value = "pollAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<PollAnswer>> processPollAnswers(Set<PollAnswer> pollAnswers)
	{
		return super.processPollAnswers(pollAnswers);
	}
}
