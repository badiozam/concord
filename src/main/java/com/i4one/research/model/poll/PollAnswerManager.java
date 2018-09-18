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
import com.i4one.base.model.manager.Manager;
import java.util.List;
import java.util.Set;

/**
 * @author Hamid Badiozamani
 */
public interface PollAnswerManager extends Manager<PollAnswerRecord, PollAnswer> 
{
	public Set<PollAnswer> getAnswers(Poll poll);

	/**
	 * Process a set of poll answers. The following rules are applied to each
	 * element of the set:
	 * <ul>
	 * 	<li>If an answer is empty and exists, it is removed</li>
	 * 	<li>If an answer is empty but doesn't exist, it is ignored</li>
	 * 	<li>If an answer is non-empty and doesn't exist, it is created</li>
	 * 	<li>If an answer is non-empty and does exist, it is updated</li>
	 * </ul>
	 * 
	 * @param pollAnswers The set of answers to process
	 * 
	 * @return The results of each operation for the input set. Ignored answers
	 * 	will have a corresponding empty ReturnType, removed answers will
	 * 	have a ReturnType with an empty post and the removed item as pre.
	 */
	public List<ReturnType<PollAnswer>> processPollAnswers(Set<PollAnswer> pollAnswers);
}
