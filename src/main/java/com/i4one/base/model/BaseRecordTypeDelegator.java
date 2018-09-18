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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i4one.base.core.BaseLoggable;
import static com.i4one.base.core.Base.getInstance;
import com.i4one.base.core.Utils;
import com.i4one.base.dao.Dao;
import com.i4one.base.dao.RecordType;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * This class represents a model object that is persisted by an underlying database record in a Delegate
 * design pattern. The general contract for all subclasses are such that:
 * <ul>
 * 	<li>The subclass will not persist any data other than through the delegate</li>
 *  <li>If two instances of the subclass have delegates that are equal, then the subclass instances are also equal</li>
 * </ul>
 *
 * @author Hamid Badiozamani
 *
 * @param <T> The underlying database record
 */
public abstract class BaseRecordTypeDelegator<T extends RecordType> extends BaseLoggable implements RecordTypeDelegator<T>
{
	private final transient String modelName;
	private T delegate;

	// A read-only/read-write flag
	//
	private transient boolean modifiable;
	private transient Throwable disabledModTrace;

	private BaseRecordTypeDelegator()
	{
		throw new IllegalArgumentException("Invalid constructor with no delegate called.");
	}

	/**
	 * The constructor takes a delegate as its only argument. The delegate is
	 * owned by this delegator and should not be manipulated externally 
	 *
	 * @param delegate The delegate to use for this delegator type
	 */
	protected BaseRecordTypeDelegator(T delegate)
	{
		setModifiable();
		setOwnedDelegate(delegate);

		modelName = delegate.getSchemaName() + "." + getClass().getSimpleName();
	}

	/**
	 * Copy the contents of the right bean onto this object
	 *
	 * @param right The object whose properties are to be copied over to us
	 * @throws java.lang.NoSuchMethodException
	 *
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@Override
	public final void copyFrom(RecordTypeDelegator<T> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		// Skip self copies
		//
		if ( this != right )
		{
			checkModifiable();

			PropertyUtils.copyProperties(getDelegate(), right.getDelegate());
			copyFromInternal(right);

			init();
		}
	}

	protected void copyFromInternal(RecordTypeDelegator<T> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
	}

	/**
	 * Copy the contents of the right bean over those of this object, ignoring
	 * properties that are null or collections that are empty.
	 *
	 * @param right The object whose properties are to be copied over to us
	 *
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@Override
	public final void copyOver(RecordTypeDelegator<T> right) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		// Skip self copies
		//
		if ( this != right )
		{
			checkModifiable();
	
			// Go thrugh all of the properties and read each one from the right object
			// and only if the return value is not null, copy it over to this object
			//
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(right.getDelegate());
			for ( PropertyDescriptor propertyDescriptor : descriptors )
			{
				// We only check properties we can read
				//
				if ( propertyDescriptor.getReadMethod() != null )
				{
					Object rightValue = propertyDescriptor.getReadMethod().invoke(right.getDelegate());
	
					// We only set properties we can write
					//
					if ( rightValue != null && propertyDescriptor.getWriteMethod() != null )
					{
						getLogger().trace("Copying over {} w/ value {}", propertyDescriptor.getName(), rightValue);
						propertyDescriptor.getWriteMethod().invoke(this.getDelegate(), rightValue);
					}
					else
					{
						getLogger().trace("Null value or no write method for property {}", propertyDescriptor.getName());
					}
				}
				else
				{
						getLogger().trace("No read method for property {}", propertyDescriptor.getName());
				}
			}
	
			copyOverInternal(right);
	
			init();
		}
	}

	protected void copyOverInternal(RecordTypeDelegator<T> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
	}

	private void checkModifiable()
	{
		if ( !modifiable )
		{
			throw new IllegalStateException("Modifications disallowed: " + disabledModTrace, disabledModTrace);
		}
	}

	// Should have a loadFromManager method instead to allow for initModelObject to be called
	//
	protected final void loadFromDAO()
	{
		if ( getDelegate().exists() )
		{
			// Get the DAO from the Base singleton which uses the class name to determine the DAO
			//
			Dao<T> dao = getInstance().getDaoManager().getNewDao(getDelegate());

			T record = dao.getBySer(getDelegate().getSer());
			if ( record != null && record.exists() && record.isLoaded() )
			{
				// Record will go out of scope after this call, which means that
				// this object can safely own it. Note that the current delegate
				// is abandoned.
				//
				setOwnedDelegate( record );
			}
			else
			{
				getLogger().debug("loadFromDAO couldn't find {} w/ ser {}: {}", getClass().getSimpleName(), getDelegate().getSer(), record);

				// Non-existant record in the database
				//
				getDelegate().setSer(0);
			}
		}
	}

	/**
	 * Attempts to ensure that the loaded version of the underlying record
	 * object is being used in the model.
	 */
	@Override
	public final void loadedVersion()
	{
		if ( !getDelegate().isLoaded() )
		{
			loadFromDAO();
		}
	}

	/**
	 * Actualizes the 1-1 foreign fields that have been set by setting their
	 * corresponding identification keys to the delegate. Subclasses should
	 * override this method and set the delegate's corresponding foreign key
	 * values to their internal objects. This method is called after validation
	 * but prior to an update or create persists the object to the database.
	 * 
	 * This method differs from setOverrides() in that it is responsible only
	 * for persisting 1-1 foreign fields, whereas setOverrides is responsible
	 * for overriding any incoming values and is called prior to validation.
	 * 
	 * This method typically consists of setXXX(getXXX()) methods since the
	 * setXXX(..) methods should also set the delegate's 1-1 id.
	 */
	@Override
	public void actualizeRelations()
	{
	}

	/**
	 * Initializes the internal state of the model object from the underlying
	 * delegate. This method is called anytime the delegate is loaded.
	 */
	protected abstract void init();

	@JsonIgnore
	@Override
	public final T getDelegate()
	{
		return delegate;
	}

	/**
	 * Sets the delegate object for this class. The input parameter is cloned to avoid
	 * outside reference access to affect the internal state of this object.
	 *
	 * @param delegate The delegate to set
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final void setDelegate(T delegate)
	{
		if ( delegate == null )
		{
			throw new NullPointerException("Attempting to set a null delegate");
		}
		else
		{
			checkModifiable();
			try
			{
				PropertyUtils.copyProperties(this.delegate, delegate);
			}
			catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
			{
				getLogger().debug("", ex);
				throw new Errors(ex);
			}

			init();
		}
	}

	/**
	 * Sets the delegate object for this class, the input parameter now belongs
	 * to this class and should not be modified externally
	 *
	 * @param delegate The delegate to set
	 */
	@Override
	public final void setOwnedDelegate(T delegate)
	{
		if ( delegate == null )
		{
			throw new NullPointerException("Attempting to set a null delegate");
		}
		else
		{
			checkModifiable();

			this.delegate = delegate;

			init();
		}
	}

	/**
	 * Consume a delegator by taking over its delegate. The effect is that the
	 * incoming parameter becomes a fresh instance while this object takes on
	 * its characteristics.
	 * 
	 * @param right The delegator whose delegate this object will take
	 */
	public final void consume(RecordTypeDelegator<T> right)
	{
		T rightDelegate = right.getDelegate();
		right.reset();

		setOwnedDelegate(rightDelegate);
		consumeInternal(right);
	}

	/**
	 * Consume a delegator by taking over its delegate's high level components.
	 * This method is executed immediately after a record's ownership is
	 * transferred from the right to this object. The right at this point
	 * will have a newly reset delegate and only its typically transient
	 * attributes will remain.
	 * 
	 * @param right The item to consume
	 */
	protected void consumeInternal(RecordTypeDelegator<T> right)
	{
		// Typically logic implemented in initModelObject(..) is copied
		// over and removed from the right
		//
	}

	@Override
	public void reset()
	{
		setOwnedDelegate(newDelegate());
	}

	/**
	 * Create a new delegate to be used when resetting this object.
	 * 
	 * @return The new delegate to use when resetting this object
	 */
	protected T newDelegate()
	{
		try
		{
			return (T) delegate.getClass().newInstance();
		}
		catch (InstantiationException | IllegalAccessException ex )
		{
			return null;
		}
	}

	@Override
	public String getModelName()
	{
		return modelName;
	}

	@Override
	public String getFeatureName()
	{
		return delegate.getFullTableName();
	}

	@Override
	public Integer getSer()
	{
		return delegate.getSer();
	}

	@Override
	public final String getGlobalID()
	{
		return getFeatureName() + ":" + getSer();
	}

	@Override
	public void setSer(Integer ser)
	{
		resetDelegateBySer(ser);
	}

	/**
	 * Generates a key that uniquely identifies this object. Note that side
	 * effects are not allowed during key generation.
	 * 
	 * @return The unique key
	 */
	@Override
	public String uniqueKey()
	{
		setUnmodifiable();
		String retVal = Utils.forceEmptyStr(uniqueKeyInternal());
		setModifiable();

		return retVal;
	}

	@Override
	public void setOverrides()
	{
		checkModifiable();
	}

	public final void setUnmodifiable()
	{
		modifiable = false;

		if ( getLogger().isTraceEnabled() )
		{
			// Building a stack trace is a fairly big performance hit,
			// so we only do it when tracing
			//
			disabledModTrace = new Throwable();
		}
	}

	public final void setModifiable()
	{
		modifiable = true;
		disabledModTrace = null;
	}

	/**
	 * Generates a key that uniquely identifies this object other than the
	 * serial number of the object. This method must not modify the object
	 * in any way.
	 * 
	 * @return The unique key
	 */
	protected abstract String uniqueKeyInternal();

	/*
	 * Seems to be unused
	 *
	public String getMD5Id() throws NoSuchAlgorithmException
	{
		return new String(Hex.encode(getMD5Hash(String.valueOf(getDelegate().getSer()))));
	}
	*/

	/**
	 * Resets the delegate's serial number and invalidates its state
	 * so as to force a fresh load from the database
	 *
	 * @param ser The new serial number for the delegate
	 */
	@Override
	public void resetDelegateBySer(int ser)
	{
		// Only reload the delegate if it hasn't been loaded already
		// or if the serial number is different
		//
		if ( !(delegate.isLoaded() && delegate.getSer() == ser))
		{
			checkModifiable();

			delegate.setSer(ser);
			delegate.setLoaded(false);

			/*
			 * Resetting the serial number invalidates the whole delegate
			 * which means the underlying delegate has to be reloaded.
			 * Reloading the delegate calls init already so we're essentially
			 * doubling up by calling init() here. Furthermore, this may
			 * cause infinite loops between parent/child relations where
			 * each holds a reference to the other.
			 *
			if ( ser > 0 )
			{
				// We only need to initialize internal relations
				// if the serial number existed. After all, the
				// object was originally initialized with a call
				// to setOwnedDelegate(..) in the constructor.
				//
				init();
			}
			*/
		}
		else
		{
			getLogger().trace("resetDelegateBySer given the same serial number ({} vs {}) skipping init", ser, delegate.getSer());
		}
	}

	@Override
	public boolean exists()
	{
		return delegate.exists();
	}

	@JsonIgnore
	public boolean getExists()
	{
		return exists();
	}

	@Override
	public Errors validate()
	{
		Errors errors = new Errors();

		return errors;
	}

	@JsonIgnore
	@Override
	public boolean isValid()
	{
		return !validate().hasErrors();
	}

	/**
	 * Compares two objects to determine whether one is less than the other
	 * by comparing their serial numbers. The parameter given may not
	 * necessarily have to be the same type as this object
	 *
	 * @param right The object to compare to, may not necessarily be the same type
	 *	as this object
	 *
	 * @return 0 if equal, 1 if this object is greater than the right, -1 otherwise
	 */
	@Override
	public int compareTo(RecordTypeDelegator<T> right)
	{
		return new UniqueKey(this).compareTo(new UniqueKey(right));
		//return getDelegate().compareTo(right.getDelegate());
	}

	/**
	 * Determines whether this object is equal to another. The type of the object
	 * given must be the same as or a subclass of ours otherwise the method will always
	 * return false.
	 *
	 * @param right The object to compare to
	 *
	 * @return true if the objects are equal, false otherwise
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object right)
	{
		getLogger().trace( "equals called for {} vs {}", this, right);
		if ( this == right )
		{
			return true;
		}
		else
		{
			if ( getClass().isAssignableFrom(right.getClass()) )
			{
				BaseRecordTypeDelegator<T> rightType = (BaseRecordTypeDelegator<T>)right;

				return equals(rightType);
			}
			else
			{
				getLogger().trace("{} is not assignable from {}", getClass(), right.getClass());
				return false;
			}
		}
	}

	@Override
	public boolean equals(RecordTypeDelegator<T> right)
	{
		if ( right == null )
		{
			return false;
		}
		else if ( delegate.getSer() > 0 && Objects.equals(delegate.getSer(), right.getDelegate().getSer()) )
		{
			return true;
		}
		else
		{
			return equalsInternal(right);
		}
	}

	/**
	 * Internal equality operator between this object and another without regard
	 * to serial numbers. The default implementation compares unique keys which
	 * can be up to 10x slower than a subclass specific implementation. Obviously,
	 * equality should be consistent with the results of comparison.
	 * 
	 * @param right The item to compare to.
	 * 
	 * @return True if the items are equal, false otherwise.
	 */
	protected boolean equalsInternal(RecordTypeDelegator<T> right)
	{
		// The delegate equals method only compares serial numbers. The uniqueKey()
		// method, compares serial numbers if they exist, and other internal parameters
		// if they do not, thus providing a stronger equality comparison
		//
		//return getDelegate().equals(rightType.getDelegate());
		//return uniqueKey().equals(rightType.uniqueKey());
		boolean retVal = new UniqueKey(this).equals(new UniqueKey(right));
		getLogger().trace("{} vs {} => {}", this, right, retVal);

		return retVal;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(uniqueKey());
	}

	/**
	 * Clones this object
	 * 
	 * @return The cloned object (if supported)
	 * 
	 * @throws CloneNotSupportedException If cloning is not supported for this object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		super.clone();
		throw new CloneNotSupportedException("Cloning hasn't been implemented for " + this.getClass().getSimpleName());
	}

	@Override
	public String toString()
	{
		return "[" + getClass().getSimpleName()+ " ... " + getDelegate().toString() + "@" + Integer.toHexString(System.identityHashCode(getDelegate())) + "]";
	}

	@Override
	public String toJSONString()
	{
		return getDelegate().toJSONString();
	}

	@Override
	public String toCSV(boolean header)
	{
		StringBuilder retVal = new StringBuilder();
		if ( header )
		{
			retVal.append("###");
			retVal.append(toCSVInternal(true));

			return retVal.toString();
		}
		else
		{
			return toCSVInternal(false).toString().replaceAll("\r", "").replaceAll("\n", "");
		}
	}

	@Override
	public boolean fromCSV(String csv)
	{
		try
		{
			getLogger().debug("Importing from CSV \"{}\"", csv);
			Iterable<CSVRecord> iterable = CSVParser.parse(csv, CSVFormat.DEFAULT);

			List<String> tokens = new ArrayList<>();
			CSVRecord record = iterable.iterator().next();
			Iterator<String> tokenIterator = record.iterator();

			tokenIterator.forEachRemaining( (token) -> { tokens.add(token); } );

			return fromCSVInternal(tokens);
		}
		catch (IOException | IndexOutOfBoundsException ex)
		{
			return false;
		}
	}

	@Override
	public boolean fromCSVList(List<String> csv)
	{
		return fromCSVInternal(csv);
	}

	protected boolean fromCSVInternal(List<String> csv)
	{
		return !csv.isEmpty();
	}

	protected StringBuilder toCSVInternal(boolean header)
	{
		return new StringBuilder();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		// Initialize our structure after de-serialization
		//
		init();
	}
}