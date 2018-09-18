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
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * This interface represents a model object that is persisted by an underlying database record in a Delegate
 * design pattern. The general contract is such that:
 * 
 * <ul>
 * 	<li>The subclass will not persist any data other than through the delegate</li>
 * 	<li>If two instances of the same subclass have delegates that are equal, then the subclass instances are also equal</li>
 * </ul>
 * 
 * @param <T> The storage backing type
 * 
 * @author Hamid Badiozamani
 */
public interface RecordTypeDelegator<T extends RecordType> extends Cloneable, Comparable<RecordTypeDelegator<T>>, Serializable
{
	/**
	 * Copy the contents of the right bean onto this object
	 *
	 * @param right The object whose properties are to be copied over to us
	 * @throws java.lang.NoSuchMethodException
	 *
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void copyFrom(RecordTypeDelegator<T> right) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

	/**
	 * Copy the contents of the right bean over those of this object, ignoring
	 * properties that are null or collections that are empty
	 *
	 * @param right The object whose properties are to be copied over to us
	 * 
	 * @throws java.lang.NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void copyOver(RecordTypeDelegator<T> right) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

	/**
	 * Whether the item exists in storage or not
	 * 
	 * @return True if the item exists, false otherwise
	 */
	public boolean exists();

	/**
	 * Get the storage backing delegate object
	 * 
	 * @return The storage delegate backing object
	 */
	public T getDelegate();

	/**
	 * Get the model name of this object. The model name is the simple package and
	 * class name that can be used for standardized error message keys and object
	 * type identification.
	 * 
	 * @return The model name of this object
	 */
	public String getModelName();

	/**
	 * Get this item's feature name. The feature name combined with the
	 * serial number make up a unique object
	 * 
	 * @return The feature name of this object
	 */
	public String getFeatureName();

	/**
	 * Retrieve the global identifier of this object. This is equivalent to
	 * the feature name combined with the serial number.
	 * 
	 * @return The global id.
	 */
	public String getGlobalID();

	/**
	 * Get the unique identifier of this object
	 * 
	 * @return The unique identifier of the object
	 */
	public Integer getSer();

	/**
	 * Set the unique identifier of this object
	 * 
	 * @param ser The new unique identifier of the object
	 */
	public void setSer(Integer ser);

	/**
	 * Attempts to ensure that the loaded version of the underlying record
	 * object is being used in the model.
	 */
	public void loadedVersion();

	/**
	 * Resets the delegate's serial number and invalidates its state
	 * so as to force a fresh load from the database
	 *
	 * @param ser The new serial number for the delegate
	 */
	public void resetDelegateBySer(int ser);

	/**
	 * Tests two items of the same type for equality.
	 * 
	 * @param right The item to compare to
	 * 
	 * @return True if the two items are equal.
	 */
	public boolean equals(RecordTypeDelegator<T> right);

	/**
	 * Sets the delegate object for this class. The input parameter is cloned to avoid
	 * outside reference access to affect the internal state of this object.
	 *
	 * @param delegate The delegate to set
	 */
	public void setDelegate(T delegate);

	/**
	 * Sets the delegate object for this class, the input parameter now belongs
	 * to this class and should not be modified externally
	 *
	 * @param delegate The delegate to set
	 */
	public void setOwnedDelegate(T delegate);

	/**
	 * Resets the object by creating and initializing it using a new delegate.
	 * The old delegate is abandoned.
	 */
	public void reset();

	/**
	 * Actualizes the 1-1 foreign fields that have been set by setting their
	 * corresponding identification keys to the delegate. Subclasses should
	 * override this method and set the delegate's corresponding foreign key
	 * values to their internal objects. This ensures that even if a mutable
	 * object has been modified externally, its changes will be reflected
	 * when the record is persisted.
	 */
	public void actualizeRelations();

	/**
	 * Forces fields to be certain values. This method is called prior to
	 * a create/update to give the subclass a chance to ensure that
	 * any fields that have been set externally can be overridden with
	 * hard-coded values.
	 */
	public void setOverrides();

	/**
	 * Validate the object
	 * 
	 * @return A (potentially clear) Errors object containing any errors
	 */
	public Errors validate();

	/**
	 * Whether the object is considered to be valid. This method simply
	 * calls the validate method to determine if any errors were generated
	 * or not.
	 * 
	 * @return True if the object is valid, false otherwise
	 */
	public boolean isValid();

	/**
	 * Returns the JSON string version of this model object
	 * 
	 * @return The JSON string representation of this object
	 */
	public String toJSONString();

	/**
	 * Loads this object from a CSV string.
	 * 
	 * @param csv The CSV string to load from
	 * 
	 * @return True if the parsing was successful, false otherwise
	 */
	public boolean fromCSV(String csv);

	/**
	 * Load this object from a list of strings
	 * 
	 * @param csv The list of strings representing this object
	 * 
	 * @return True if the parsing was successful, false otherwise
	 */
	public boolean fromCSVList(List<String> csv);

	/**
	 * Returns the CSV version of this model object. If the header parameter is
	 * set to true, the method first outputs a header line corresponding to each
	 * attribute before outputting the contents.
	 * 
	 * @param header Whether to include the headings on a separate line or not
	 * 
	 * @return A CSV string representation of this object 
	 */
	public String toCSV(boolean header);

	/**
	 * Generates a key that uniquely identifies this object
	 * 
	 * @return The unique key
	 */
	public String uniqueKey();
}