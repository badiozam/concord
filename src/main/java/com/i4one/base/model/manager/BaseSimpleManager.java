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
package com.i4one.base.model.manager;

import com.i4one.base.core.Utils;
import com.i4one.base.dao.Dao;
import com.i4one.base.dao.DaoManager;
import com.i4one.base.dao.RecordType;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.BlockingSingleItemCollection;
import com.i4one.base.model.BaseRecordTypeDelegator;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.UniqueKey;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseSimpleManager<U extends RecordType, T extends RecordTypeDelegator<U>> extends BaseLoggable implements Manager<U, T>
{
	// Used for getting class information when looking up DAOs
	//
	private T emptyT;

	// Used for update operations
	//
	private Dao<U> statelessDao;
	private String interfaceName;

	private DaoManager daoManager;

	public BaseSimpleManager()
	{
		try
		{
			// The interface that is being implemented by the subclass, this will
			// be useful for methods that throw Errors objects as they need to
			// encode the error message key using this value
			//
			Type[] interfaces = getClass().getGenericInterfaces();
			if ( interfaces.length == 0 )
			{
				getLogger().debug("Could not get any generic interfaces for " + getClass().getCanonicalName());
			}
			else
			{
				getLogger().debug("Interfaces retrieved are " + Utils.toCSV(Arrays.asList(interfaces)));
			}

			// The interfaces are going to be FQN so we split them on dots to extract
			// the portion we want
			//
			String[] interfaceNameArray = interfaces[0].toString().split("\\.");
			setInterfaceName(interfaceNameArray[interfaceNameArray.length - 1]);
		}
		catch (RuntimeException e)
		{
			getLogger().debug("Couldn't construct BaseSimpleManager: ", e);
			throw e;
		}

		statelessDao = null;
		getLogger().debug("Created manager " + getClass().getSimpleName() + ": " + this);
	}

	public DaoManager getDaoManager()
	{
		return daoManager;
	}

	@Autowired
	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	@PostConstruct
	@Override
	public void init()
	{
		emptyT = emptyInstance();

		// Generally empty unless a manager has to do cache initialization, etc.
		//
		getLogger().debug(getClass() + " implements interface " + interfaceName);
	}

	@Override
	public void init(Map<String, Object> options)
	{
		// We have no options to configure
		this.init();
	}

	@Override
	public String getInterfaceName()
	{
		return interfaceName;
	}

	public String getFullInterfaceName()
	{
		return emptyT.getDelegate().getSchemaName() + "." + interfaceName;
	}

	public final void setInterfaceName(String interfaceName)
	{
		this.interfaceName = Character.toLowerCase(interfaceName.charAt(0)) + interfaceName.substring(1);
	}

	@Transactional(readOnly = false)
	@Override
	public U lock(T item)
	{
		if ( item.exists() )
		{
			U itemRecord = getDao().getBySer(item.getSer(), true);
			if ( itemRecord == null )
			{
				throw new Errors(getInterfaceName() + ".lock", new ErrorMessage("msg." + getInterfaceName() + ".lock.dne", "Item $item.delegate.fullTableName#**#.$item.ser does not exist", new Object[] { "item", item }));
			}
			else
			{
				return itemRecord;
			}
		}
		else
		{
			throw new Errors(getInterfaceName() + ".lock", new ErrorMessage("msg." + getInterfaceName() + ".lock.dne", "Item $item.delegate.fullTableName#**#.$item.ser does not exist", new Object[] { "item", item }));
		}
	}

	@Override
	public T getById(int id)
	{
		return initModelObject(convertDelegate(getDao().getBySer(id)));
	}

	@Override
	public void loadById(T item, int id)
	{
		U delegate = getDao().getBySer(id);
		if ( delegate == null )
		{
			delegate = emptyInstance().getDelegate();
		}

		item.setOwnedDelegate(delegate);
		initModelObject(item);
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> clone(T item)
	{
		try
		{
			if ( item.exists() )
			{
				initModelObject(item);

				T cloned = cloneInternal(item);
	
				return create(cloned);
			}
			else
			{
				throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.dne", "You are attempting to clone a non-existent item: $item", new Object[] { "item", item }));
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
		{
			Errors errors = new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));

			throw errors;
		}
	}

	/**
	 * Responsible for cloning an item in memory. The serial number of the
	 * cloned item is to be zero and uniquely identifying fields must be
	 * modified to ensure the two items don't cause collisions. A clone of
	 * an item therefore is bound to not be equal to the item off of which
	 * it was cloned.
	 * 
	 * @param item The item to clone.
	 * 
	 * @return A clone of the gvien item (that is potentially to be created
	 * in the database).
	 * 
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException 
	 */
	protected T cloneInternal(T item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		throw new Errors(getInterfaceName() + ".clone", new ErrorMessage("msg." + getInterfaceName() + ".clone.unsupported", "Cloning is not supported for $item", new Object[] { "item", item }));
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> create(T item)
	{
		if ( item.exists() )
		{
			throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".create.oldrecord", "You are attempting to create an entry that already exists: $item", new Object[] { "item", item }));
		}
		else
		{
			item.setOverrides();
			Errors errors = item.validate();
			if ( errors.hasErrors() )
			{
				throw errors;
			}
			else
			{
				ReturnType<T> retVal = createInternal(item);
				retVal.setPost(initModelObject(retVal.getPost()));

				return retVal;
			}
		}
	}

	/**
	 * Create an already validated item.
	 * 
	 * @param item The model object to update
	 * 
	 * @return The result of the update
	 */
	protected ReturnType<T> createInternal(T item)
	{
		try
		{
			item.actualizeRelations();
			getDao(item).insert(item.getDelegate());

			ReturnType<T> retVal = new ReturnType<>(item);
			retVal.setPre(emptyInstance());

			return retVal;
		}
		catch (DataAccessException ex)
		{
			throw new Errors(getInterfaceName() + ".create", new ErrorMessage("msg." + getInterfaceName() + ".create.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));
		}
	}

	@Transactional(readOnly = false)
	@Override
	public ReturnType<T> update(T item)
	{
		item.setOverrides();

		Errors errors = item.validate();
		if ( errors.hasErrors() )
		{
			throw errors;
		}
		else
		{
			// By locking the record we also get the latest info from the database
			//
			U currRecord = lock(item);
	
			ReturnType<T> retVal = updateInternal(currRecord, item);

			// If the item was updated successfully, we need to reinitialize any
			// relations to reflect the contents of the database
			//
			retVal.setPost(initModelObject(retVal.getPost()));

			return retVal;
		}	
	}

	/**
	 * Update an already locked and validated item.
	 * 
	 * @param lockedRecord The locked database record
	 * @param item The model object to update
	 * 
	 * @return The result of the update
	 */
	protected ReturnType<T> updateInternal(U lockedRecord, T item)
	{
		if ( item.getDelegate() == lockedRecord )
		{
			// The locked record can't be modified to be updated, this
			// ensures that the contents are actually being updated with
			// a lockedRecord => item.getDelegate() relationship which
			// guarantees the ReturnType's setPre(..) and setPost(..) are
			// correctly set
			//
			throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".update.invalid", "Invalid update: $item", new Object[] { "item", item }));
		}

		ReturnType<T> retVal = new ReturnType<>();

		// We create a new object and copy the current delegate so we can ensure type-safety
		//
		T preItem = emptyInstance();
		preItem.setOwnedDelegate(lockedRecord);

		retVal.setPre(preItem);

		try
		{
			// Update the item's delegate, overwriting any others that exist
			//
			item.actualizeRelations();
			getDao(item).updateBySer(item.getDelegate());
		}
		catch (DataAccessException ex)
		{
			throw new Errors(getInterfaceName() + ".update", new ErrorMessage("msg." + getInterfaceName() + ".update.collision", "An item with the same qualifiers already exists: $item: $ex.message", new Object[] { "item", item, "ex", ex }, ex));
		}

		retVal.setPost(item);

		return retVal;
	}

	@Transactional(readOnly=false)
	@Override
	public T remove(T item)
	{
		if ( !item.exists() )
		{
			throw new Errors(getInterfaceName() + ".remove", new ErrorMessage("msg." + getInterfaceName() + ".remove.dne", "You are attempting to remove a non-existent entry", new Object[] { "item", item }));
		}
		else
		{
			RecordType dbRecord = getDao(item).getBySer(item.getSer(), true);
			if ( dbRecord != null )
			{
				getDao(item).deleteBySer(item.getSer());

				return initModelObject(item);
			}
			else
			{
				throw new Errors(getInterfaceName() + ".remove", new ErrorMessage("msg." + getInterfaceName() + ".remove.dne", "The entry $item does not exist in the database", new Object[] { "item", item }));
			}
		}
	}

	@Override
	public TopLevelReport getReport(T item, TopLevelReport report, PaginationFilter pagination)
	{
		throw new UnsupportedOperationException("Reporting not supported for " + getInterfaceName());
	}

	@Override
	public <R extends Object> ClassificationReport<U, R> getReport(T item, ClassificationReport<U, R> report, PaginationFilter pagination)
	{
		throw new UnsupportedOperationException("Reporting not supported for " + getInterfaceName());
	}

	@Transactional(readOnly = false)
	@Override
	public List<ReturnType<T>> importCSV(InputStream stream, Supplier<T> instantiator, Function<T,Boolean> preprocessor, Consumer<ReturnType<T>> postprocessor)
	{
		throw new UnsupportedOperationException("No CSV import available for this feature");
	}

	protected List<ReturnType<T>> importCSVInternal(InputStream stream, Supplier<T> instantiator, Function<T, Boolean> preprocessor, Consumer<ReturnType<T>> postprocessor)
	{
		//List<T> items = new ArrayList<>();
		Set<T> items = new LinkedHashSet<>();

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	
			List<ReturnType<T>> retVal = new ArrayList<>();
			String currLine;
			do
			{
				currLine = reader.readLine();
	
				if ( !Utils.isEmpty(currLine) && !currLine.startsWith("###"))
				{
					T newItem = instantiator.get();

					try
					{
						if ( newItem.fromCSV(currLine) )
						{
							// We only create the item if the preprocessor
							// was successful
							//
							if ( preprocessor.apply(newItem) )
							{
								ReturnType<T> created = create(newItem);
								postprocessor.accept(created);
	
								retVal.add(created);
							}
							else
							{
								ReturnType<T> notImported = new ReturnType<>();
								notImported.put("importErrors", new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.rejected", "Could not import item due to rejected value: $item", new Object[] { "item", newItem})));
	
								retVal.add(notImported);
							}
						}
						else
						{
							ReturnType<T> notImported = new ReturnType<>();
							notImported.put("importErrors", new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.invalidCSV", "Could not import item due to invalid CSV format", new Object[] { })));
	
							retVal.add(notImported);
						}
					}
					catch (Errors errors)
					{
						getLogger().debug("Couldn't create item {}", newItem, errors);
	
						ReturnType<T> notAdded = new ReturnType<>();
						notAdded.setPre(newItem);
						notAdded.setPost(emptyInstance());
						notAdded.put("importErrors", errors);
	
						retVal.add(notAdded);
					}
				}
	
			} while ( currLine != null );

			return retVal;
		}
		catch (IOException ioe)
		{
			throw new Errors(getInterfaceName() + ".importCSV", new ErrorMessage("msg." + getInterfaceName() + ".importCSV.error", "Could not import items", new Object[] { "items", items, "ex", ioe }, ioe));
		}
	}

	@Override
	public OutputStream exportCSV(Set<T> items, OutputStream out)
	{
		throw new UnsupportedOperationException("No CSV export available for this feature");
	}

	protected OutputStream exportCSVInternal(Set<T> items, OutputStream out)
	{
		try
		{
			DataOutputStream retVal = new DataOutputStream(out);
	
			// This buffer is populated with each item from the list of items in the current thread,
			// the class will block the current thread until its item has been read. As such, the
			// output stream must be emptied (or closed) for this call to become unblocked
			//
			BlockingSingleItemCollection<T> itemBuffer = new BlockingSingleItemCollection<>();
	
			// This thread loads each item and feeds it to the stream
			//
			Runnable loadDataTask = ( () ->
			{
				for ( T currItem : items )
				{
					// This call will block until the item that was added
					// is consumed
					//
					itemBuffer.add(currItem);
				}
	
				itemBuffer.close();
			});
	
			// Begin adding items
			//
			Thread rawDataThread = new Thread(loadDataTask);
			rawDataThread.start();

			boolean printHeader = true;

			// At this point, there is a thread that is adding items to the collection, the reason
			// Here, we consume each item by exporting it to the output stream one by one
			//
			Iterator<T> it = itemBuffer.iterator();
			while ( it.hasNext() )
			{
				// This unblocks the itemBuffer and a subsequent id is added
				// or the stream is closed
				//
				T item = it.next();
	
				if ( item != null )
				{
					if ( printHeader )
					{
						printHeader = false;
						retVal.writeBytes(item.toCSV(true));
						retVal.write('\n');
					}

					retVal.writeBytes(item.toCSV(false));
					retVal.write('\n');
				}
			}
	
			return out;
		}
		catch (IOException ioe)
		{
			throw new Errors(ioe);
		}
	}
	/**
	 * Convert a list of delegates to model objects with those delegates set.
	 *
	 * @param delegates The delegates to convert
	 *
	 * @return The model objects to return
	 */
	protected Set<T> convertDelegates(List<U> delegates)
	{
		return convertGenericDelegates(delegates, (item) -> { initModelObject(item); }, () -> { return emptyInstance(); });
	}

	/**
	 * Convert a single delegate to a model object.
	 * 
	 * @param delegate The delegate to convert
	 * 
	 * @return The initialized model object with the delegate set
	 */
	protected T convertDelegate(U delegate)
	{
		return convertGenericDelegate(delegate, (item) -> { initModelObject(item); }, () -> { return emptyInstance(); });
	}

	/**
	 * Convert a RecordType to its RecordTypeDelegator counterpart.
	 * 
	 * @param <V> The RecordTypeDelegators that accept the record type
	 * @param <W> The BaseRecordType
	 * @param delegate The set of delegates to convert
	 * @param initMethod The initialization method of the RecordTypeDelegator to be called after the item has been created
	 * @param delegatorFactory The creation method of the RecordTypeDelegator
	 * 
	 * @return A set of RecordTypeDelegators that own the list of delegates
	 */
	public static <W extends RecordType, V extends RecordTypeDelegator<W>> V convertGenericDelegate(W delegate, Consumer<V> initMethod, Supplier<V> delegatorFactory)
	{
		V retVal = delegatorFactory.get();
		if ( delegate != null )
		{
			retVal.setOwnedDelegate(delegate);
		}

		initMethod.accept(retVal);

		return retVal;
	}

	/**
	 * Convert a list of RecordTypes to a set of their RecordTypeDelegator counterparts. If there are any
	 * duplicate delegates, the last delegate is used as the backing object for the RecordTypeDelegator.
	 * 
	 * @param <V> The RecordTypeDelegators that accept the record type
	 * @param <W> The BaseRecordType
	 * @param delegates The set of delegates to convert
	 * @param initMethod The initialization method of the RecordTypeDelegator to be called after each item has been created
	 * @param delegatorFactory The creation method of the RecordTypeDelegator
	 * 
	 * @return A set of RecordTypeDelegators that own the list of delegates
	 */
	public static <W extends RecordType, V extends RecordTypeDelegator<W>> Set<V> convertGenericDelegates(List<W> delegates, Consumer<V> initMethod, Supplier<V> delegatorFactory)
	{
		// Convert these all to delegate types, assumes that T has no constructor arguments
		//
		LinkedHashSet<V> retVal = new LinkedHashSet<>(delegates.size());
		delegates.stream().forEach((currDelegate) -> 
		{
			retVal.add(convertGenericDelegate(currDelegate, initMethod, delegatorFactory));
		});

		return retVal;
	}

	/**
	 * Convert a list of delegates to model objects with those delegates set
	 *
	 * @param delegates The delegates to convert
	 *
	 * @return The model objects to return
	 */
	protected Set<RecordTypeDelegator<?>> convertDelegatesToGenericFeatures(List<RecordType> delegates) 
	{
		// Convert these all to delegate types, assumes that T has no constructor arguments
		//
		LinkedHashSet<RecordTypeDelegator<? extends RecordType>> retVal = new LinkedHashSet<>(delegates.size());
		delegates.stream().forEach( (currDelegate) ->
		{
			RecordTypeDelegator<RecordType> currItem = new GenericFeature(currDelegate);
			currItem.setOwnedDelegate(currDelegate);

			retVal.add(currItem);
		});

		return retVal;
	}

	@Override
	protected void appendKey(Object id, StringBuilder toAppend)
	{
		if (id instanceof BaseRecordTypeDelegator<?>)
		{
			BaseRecordTypeDelegator<?> item = (BaseRecordTypeDelegator<?>)id;
			toAppend.append(new UniqueKey(item));
		}
		else
		{
			toAppend.append(id);
		}
	}

	/**
	 * Initialize a model after it has been loaded but before it is returned
	 * 
	 * @param item The item to initialize
	 * 
	 * @return The input parameter that is now newly
	 */
	protected T initModelObject(T item)
	{
		return item;
	}

	@Override
	public String toString()
	{
		String emptyClass = "null";
		if ( emptyT != null )
		{
			emptyClass = emptyT.getClass().getSimpleName();
		}
		return getClass().getSimpleName() + "<" + emptyClass + ">";
	}

	@Override
	public Dao<U> getDao()
	{
		return getDao(emptyT);
	}

	private Dao<U> getDao(T item)
	{
		if ( statelessDao == null )
		{
			statelessDao = getDaoManager().getNewDao(item.getDelegate());
		}

		return statelessDao;
	}
}
