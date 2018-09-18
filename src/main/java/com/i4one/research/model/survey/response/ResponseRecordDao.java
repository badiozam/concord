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
package com.i4one.research.model.survey.response;

import com.i4one.base.dao.PaginableRecordTypeDao;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.List;

/**
 * @author Hamid Badiozamani
 */
public interface ResponseRecordDao extends PaginableRecordTypeDao<ResponseRecord>
{
	/**
	 * Get a list of responses for a given survey and user
	 *
	 * @param respondentid The respondent
	 * @param pagination The qualifier/sorting info
	 *
	 * @return The (potentially empty) list of the respondent's responses
	 */
	public List<ResponseRecord> getAllResponses(int respondentid, PaginationFilter pagination);

	/**
	 * Get the list of respondents who responded to a given question
	 *
	 * @param questionid The question
	 * @param pagination The qualifier/sorting info
	 *
	 * @return The (potentially empty) list of the responses to the given question
	 */
	public List<ResponseRecord> getAllResponsesByQuestionid(int questionid, PaginationFilter pagination);

	/**
	 * Get the list of respondents who selected a given answer
	 *
	 * @param answerid The answer
	 * @param pagination The qualifier/sorting info
	 *
	 * @return The (potentially empty) list of the responses by answer
	 */
	public List<ResponseRecord> getAllResponsesByAnswerid(int answerid, PaginationFilter pagination);

	/**
	 * Get all of a particular respondent's responses to a given question
	 * 
	 * @param questionid The question
	 * @param respondentid The respondent
	 * 
	 * @return The (potentially empty) list of the respondent's responses to the question
	 */
	public List<ResponseRecord> getResponses(int questionid, int respondentid);

	/**
	 * Determines whether a record matching the answerid and respondentid exists or not.
	 * 
	 * @param answerid The answerid to test
	 * @param respondentid The respondentid to test
	 * 
	 * @return True if the record exists.
	 */
	public boolean hasResponded(int answerid, int respondentid);
}
