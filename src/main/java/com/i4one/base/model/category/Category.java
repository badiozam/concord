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
package com.i4one.base.model.category;

import com.i4one.base.model.BaseSiteGroupType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.i18n.IString;

/**
 * A category
 * 
 * @author Hamid Badiozamani
 * 
 * @param <U> The underlying category record
 */
public abstract class Category<U extends CategoryRecord> extends BaseSiteGroupType<U>
{
	static final long serialVersionUID = 42L;

	protected Category(U delegate)
	{
		super(delegate);
	}

	public IString getTitle()
	{
		return getDelegate().getTitle();
	}

	public void setTitle(IString title)
	{
		getDelegate().setTitle(title);
	}

	public String getThumbnailURL()
	{
		return getDelegate().getThumbnailurl();
	}

	public void setThumbnailURL(String title)
	{
		getDelegate().setThumbnailurl(title);
	}

	public String getDetailPicURL()
	{
		return getDelegate().getDetailpicurl();
	}

	public void setDetailPicURL(String detailpicurl)
	{
		getDelegate().setDetailpicurl(detailpicurl);
	}

	public IString getDescr()
	{
		return getDelegate().getDescr();
	}

	public void setDescr(IString descr)
	{
		getDelegate().setDescr(descr);
	}

	@Override
	public void setClient(SingleClient client)
	{
		setClientInternal(client);
	}

	@Override
	protected String uniqueKeyInternal()
	{
		return getClient().getSer() + getTitle().toString();
	}
}
