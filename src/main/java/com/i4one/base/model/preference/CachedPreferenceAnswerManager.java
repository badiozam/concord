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
package com.i4one.base.model.preference;

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
@Service("base.PreferenceAnswerManager")
public class CachedPreferenceAnswerManager extends SimplePreferenceAnswerManager implements PreferenceAnswerManager
{
	@Cacheable(value = "preferenceAnswerManager", key = "target.makeKey('getAnswers', #preference)")
	@Override
	public Set<PreferenceAnswer> getAnswers(Preference preference)
	{
		return Collections.unmodifiableSet(super.getAnswers(preference));
	}

	@CacheEvict(value = "preferenceAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<PreferenceAnswer> clone(PreferenceAnswer item)
	{
		return super.clone(item);
	}

	@CacheEvict(value = "preferenceAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<PreferenceAnswer> create(PreferenceAnswer item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "preferenceAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public ReturnType<PreferenceAnswer> update(PreferenceAnswer item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "preferenceAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public PreferenceAnswer remove(PreferenceAnswer item)
	{
		return super.remove(item);
	}

	@CacheEvict(value = "preferenceAnswerManager", allEntries = true)
	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<PreferenceAnswer>> processPreferenceAnswers(Set<PreferenceAnswer> preferenceAnswers)
	{
		return super.processPreferenceAnswers(preferenceAnswers);
	}
}
