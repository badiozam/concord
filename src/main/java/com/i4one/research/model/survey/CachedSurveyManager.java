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
package com.i4one.research.model.survey;

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
public class CachedSurveyManager extends SimpleSurveyManager implements SurveyManager
{
	@Cacheable(value = "surveyManager", key = "target.makeKey('getLive', #pagination)")
	@Override
	public Set<Survey> getLive(TerminablePagination pagination)
	{
		return Collections.unmodifiableSet(super.getLive(pagination));
	}

	@Cacheable(value = "surveyManager", key = "target.makeKey('getByRange', #pagination)")
	@Override
	public Set<Survey> getByRange(TerminablePagination pagination)
	{
		return Collections.unmodifiableSet(super.getByRange(pagination));
	}

	@CacheEvict(value = "surveyManager", allEntries = true)
	@Override
	public ReturnType<Survey> clone(Survey item)
	{
		return super.clone(item);
	}

	@CacheEvict(value = "surveyManager", allEntries = true)
	@Override
	public ReturnType<Survey> create(Survey item)
	{
		return super.create(item);
	}

	@CacheEvict(value = "surveyManager", allEntries = true)
	@Override
	public ReturnType<Survey> update(Survey item)
	{
		return super.update(item);
	}

	@CacheEvict(value = "surveyManager", allEntries = true)
	@Override
	public Survey remove(Survey item)
	{
		return super.remove(item);
	}
}