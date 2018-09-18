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
package com.i4one.base.model.manager.categorizable;

import com.i4one.base.dao.categorizable.CategorizableTerminableSiteGroupRecordType;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.manager.terminable.BaseTerminableSiteGroupType;
import com.i4one.base.model.category.Category;
import com.i4one.base.model.client.SingleClient;

/**
 * A categorizable terminable type for category site groups
 * 
 * @author Hamid Badiozamani
 * 
 * @param <T> The underlying record type
 * @param <U> The specific category type to which the records can belong to
 */
public abstract class BaseCategorizableTerminableSiteGroupType<T extends CategorizableTerminableSiteGroupRecordType, U extends Category<?>> extends BaseTerminableSiteGroupType<T> implements CategorizableTerminableClientType<T,U>
{
	private transient U category;

	public BaseCategorizableTerminableSiteGroupType(T delegate)
	{
		super(delegate);
	}

	/**
	 * Initialize this item's particular category type. This method is used
	 * to instantiate the category type of the concrete subclass.
	 * 
	 * @param categoryid The category's id
	 * 
	 * @return The initialized category item
	 */
	protected abstract U initCategory(int categoryid);

	@Override
	protected void init()
	{
		super.init();

		this.category = initCategory(getDelegate().getCategoryid());
	}

	@Override
	public Errors validate()
	{
		Errors retVal = super.validate();

		// We should consider putting this check into its own Categorizable/SimpleCategorizable interface
		// implementation in order to avoid duplicate code in the BaseCategorizableTerminableSingleClientType class
		//
		// NOTE: We need to pull this out into its own variable in order to work around the following
		// JDK 1.8.0_20 bug: https://bugs.openjdk.java.net/browse/JDK-8056984
		//
		SingleClient categoryClient = category.getClient();
		if ( !belongsTo(categoryClient) )
		{
			retVal.addError(new ErrorMessage("category", "msg." + this.getDelegate().getSchemaName() + "." + getClass().getSimpleName() + ".category.clientMismatch", "The category $category does not belong to the same group of sites as this item", new Object[]{"item", this, "category", category}));
		}

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		super.setOverrides();

		getCategory().setOverrides();
	}

	@Override
	public void actualizeRelations()
	{
		super.actualizeRelations();

		setCategory(getCategory());
	}

	@Override
	public U getCategory()
	{
		return getCategory(true);
	}

	public U getCategory(boolean doLoad)
	{
		if ( doLoad )
		{
			category.loadedVersion();
		}

		return category;
	}

	@Override
	public void setCategory(U category)
	{
		this.category = category;
		getDelegate().setCategoryid(category.getSer());
	}

}
