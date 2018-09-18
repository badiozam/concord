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
package com.i4one.base.model.client.reports;

import com.i4one.base.model.report.classifier.ClassificationReport;
import com.i4one.base.model.report.classifier.classifications.ZipCodeClassification;
import com.i4one.base.model.report.classifier.classifications.ZipCodeSubclassification;
import com.i4one.base.model.user.UserBalanceRecord;
import java.util.Collections;

/**
 * @author Hamid Badiozamani
 */
public class UserMembershipZipClassification extends ClassificationReport<UserBalanceRecord, UserMembershipUsage>
{
	public UserMembershipZipClassification(int numChars)
	{
		super("base.UserMembershipZipClassification");

		ZipCodeClassification zipCodeClass = new ZipCodeClassification(Collections.emptySet());
		add(zipCodeClass);

		setProcessCallback(Collections.emptySet(),
			(UserMembershipUsage usage) ->
			{
				String zip = usage.getUser().getZipcode().toUpperCase();

				if ( zip.length() >= numChars )
				{
					ZipCodeSubclassification zipCodeSub = new ZipCodeSubclassification(zipCodeClass, zip.substring(0, numChars));
					if ( zipCodeClass.add(zipCodeSub) )
					{
						add(zipCodeSub);
						addCombination(zipCodeSub);
					}
				}

				return true;
			});

		addCombinations();
	}

	@Override
	protected UserMembershipUsage convert(UserBalanceRecord record)
	{
		return new UserMembershipUsage(record);
	}
}
