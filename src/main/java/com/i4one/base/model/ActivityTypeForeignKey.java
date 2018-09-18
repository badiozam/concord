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
package com.i4one.base.model;

import com.i4one.base.dao.RecordType;
import com.i4one.base.dao.activity.ActivityRecordType;
import java.util.function.Supplier;

/**
 * Maintains an activity type's foreign key relationship. This relationship is defined
 * by a single action item which is referenced by a multitude of activity records.
 * 
 * @author Hamid Badiozamani
 * @param <U> The owner's record type
 * @param <T> The owner's type
 * @param <V> The activity type
 */
public class ActivityTypeForeignKey<U extends RecordType, T extends RecordTypeDelegator<U>, V extends ActivityType<? extends ActivityRecordType, T>> extends ForeignKey<U, T>
{

	public ActivityTypeForeignKey(V owner, Supplier<T> newInstance)
	{
		super(owner,
			() -> { return owner.getDelegate().getItemid(); },
			(ser) -> { owner.getDelegate().setItemid(ser); },
			newInstance);
	}
}
