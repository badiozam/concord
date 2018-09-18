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

import com.i4one.base.dao.Dao;
import com.i4one.base.dao.RecordType;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.base.model.report.classifier.ClassificationReport;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The base manager interface for all model objects
 *
 * @author Hamid Badiozamani
 * @param <U> The database record type
 * @param <T> The model type
 */
public interface Manager<U extends RecordType, T extends RecordTypeDelegator<U>>
{
	/**
	 * Initializes the manager
	 */
	public void init();

	/**
	 * Initializes the manager with a given set of options. This method can be
	 * called after instantiation and init() have been called to reconfigure
	 * the manager.
	 * 
	 * @param options The options to use when configuring the manager
	 */
	public void init(Map<String, Object> options);

	/**
	 * Return an empty instance of the BaseRecordTypeDelegator being managed
	 * 
	 * @return A new reference for use by the caller
	 */
	public T emptyInstance();

	/**
	 * Lock a record to prevent other threads from accessing it. The item must
	 * exist in the database
	 * 
	 * @param item The item to lock
	 * 
	 * @return The latest version of the item in the database
	 */
	public U lock(T item);

	/**
	 * Retrieve a single item by its unique database record identifier.
	 * 
	 * @param id The database record identifier
	 * 
	 * @return The item with that unique identifier or an empty object if
	 * 	not found.
	 */
	public T getById(int id);

	/**
	 * Loads a single item by its unique database record identifier.
	 * 
	 * @param item The previously instantiated object to populate
	 * @param id The database record identifier
	 */
	public void loadById(T item, int id);

	/**
	 * Create a given model's record
	 *
	 * @param item The item to create (must have a serial number value of 0)
	 * 
	 * @return Returns the newly created item
	 */
	public ReturnType<T> create(T item);

	/**
	 * Updates a given model's record
	 *
	 * @param item The item to update (must have the correct serial number)
	 *
	 * @return If successful, the return map will contain the following entries:
	 * 	<ul>
 	 *		<li><b>pre:</b> A BaseRecordTypeDelegator representing the record prior to the update taking place</li>
 	 *		<li><b>post:</b> The BaseRecordTypeDelegator representing the record after the update took place</li>
	 * 	</ul>
	 */
	public ReturnType<T> update(T item);

	/**
	 * Removes a given model's record
	 *
	 * @param item The item to remove
	 *
	 * @return If successful, the record that was removed or
	 * 	a non-existent model object if the record did not exist
	 */
	public T remove(T item);

	/**
	 * Clone and save a given model's record
	 * 
	 * @param item The item to be cloned
	 * 
	 * @return The newly cloned item
	 */
	public ReturnType<T> clone(T item);

	/**
	 * Generate a report for items of the given type. 
	 * 
	 * @param item A sample item to use when matching records
	 * @param report An unprocessed report with the parameters needed to load it
	 * @param pagination Sort/ordering information
	 * 
	 * @return the com.i4one.base.model.report.TopLevelReport
	 */
	public TopLevelReport getReport(T item, TopLevelReport report, PaginationFilter pagination);

	/**
	 * Generate a classification report for the items of the given type.
	 * 
	 * @param item A sample item to use when matching records
	 * @param report An unprocessed report with the parameters needed to load it
	 * @param pagination Sort/ordering information
	 * 
	 * @return The populated classification report.
	 * 
	 * @param <R> A type that the classification report will be converting
	 * 	the records it processes into.
	 */
	public <R extends Object> ClassificationReport<U, R> getReport(T item, ClassificationReport<U, R> report, PaginationFilter pagination);

	/**
	 * Import items from an input stream.
	 * 
	 * @param importStream The input stream to import from.
	 * @param instantiator A method that instantiates items prior to being
	 * 	populated by CSV values.
	 * @param preprocessor A method to be executed prior to creating a
	 * 	successfully read item. This method must return true if the value
	 * 	is cleared for import, or false otherwise.
	 * @param postprocessor A method to be executed immediately after creating
	 * 	a successfully read item.
	 * 
	 * @return A (potentially empty) set of the successfully imported items.
	 */
	public List<ReturnType<T>> importCSV(InputStream importStream, Supplier<T> instantiator, Function<T,Boolean> preprocessor, Consumer<ReturnType<T>> postprocessor);

	/**
	 * Export a set of items to an output stream.
	 * 
	 * @param items The items to export
	 * @param out The output stream to write the items to
	 * 
	 * @return The output stream from which the CSV export can be read
	 */
	public OutputStream exportCSV(Set<T> items, OutputStream out);

	/**
	 * The logical name of the interface (e.g. userManager, clientManager, etc.)
	 * 
	 * @return The interface name
	 */
	public String getInterfaceName();

	/**
	 * Get the DAO for this manager
	 * 
	 * @return The DAO for retrieving items using the underlying data storage
	 */
	public Dao<U> getDao();
}
