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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i4one.base.core.Utils;
import com.i4one.base.model.client.SingleClient;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is used to store and retrieve multiple errors from interface calls, it
 * is declared as a RuntimeException so that it can be treated as a rollback for
 * Spring MVC Transactional calls
 *
 * @author Hamid Badiozamani
 */
@JsonSerialize(using = ErrorsSerializer.class)
public final class Errors extends RuntimeException
{
	static final long serialVersionUID = 42L;

	/** This object is here for convenience to represent a no-error condition */
	public static final Errors NO_ERRORS = new Errors();

	private final HashMap<String, ErrorMessage> errorMessages;

	// Useful to have for converting the message key to an actual message
	//
	private transient SingleClient singleClient;
	private transient String language;


	public Errors()
	{
		errorMessages = new HashMap<>();
	}

	public Errors(Exception e)
	{
		super(e);
		errorMessages = new HashMap<>();
	}

	public Errors(String key, ErrorMessage error)
	{
		errorMessages = new HashMap<>();
		addError(key, error);
	}

	public Errors(ErrorMessage error)
	{
		errorMessages = new HashMap<>();
		addError(error);
	}

	/**
	 * Whether a certain error message is contained in the error list or not
	 *
	 * @param message The key used to store the error message
	 *
	 * @return True if the error message exists, false otherwise
	 */
	public boolean containsError(String message)
	{
		return errorMessages.containsKey(message);
	}

	/**
	 * Merge another error structure into this one, absorbing all of its values.
	 * Note that the incoming parameter is now owned by this structure.
	 *
	 * @param errors The error structure to absorb
	 */
	public void merge(Errors errors)
	{
		errorMessages.putAll(errors.errorMessages);
		errors.clearErrors();
	}

	/**
	 * Merge another error structure into this one, but use a key prefix for each
	 * key merged. The keys are merged with keys in the form of "keyPrefix.key".
	 * The incoming parameter's keys are owned by this structure and thus cleared
	 * from the original
	 * 
	 * @param keyPrefix The prefix to use when 
	 * @param errors The errors to merge
	 */
	public void merge(String keyPrefix, Errors errors)
	{
		mergeWithParams(keyPrefix, errors);
	}

	/**
	 * Merge another error structure into this one, but use a key prefix for each
	 * key merged and append the specified parameter list. The keys are merged with
	 * keys in the form of "keyPrefix.key". The incoming parameter's keys are owned
	 * by this structure and thus cleared from the original
	 * 
	 * @param keyPrefix The prefix to use when 
	 * @param errors The errors to merge
	 * @param appendParams The parameters to append
	 */
	public void mergeWithParams(String keyPrefix, Errors errors, Object ... appendParams)
	{
		errors.errorMessages.entrySet().forEach( (errorMessagePair) ->
		{
			ErrorMessage errorMessage = errorMessagePair.getValue();
			errorMessage.setMessageArgument("prefix", keyPrefix);

			for ( Object obj : appendParams )
			{
				errorMessage.addParam(obj);
			}

			errorMessage.setFieldName(keyPrefix + "." + errorMessagePair.getKey());
			errorMessages.put(keyPrefix + "." + errorMessagePair.getKey(), errorMessage);
		});

		errors.clearErrors();
	}

	/**
	 * Add an error message to this structure. The key is typically the 
	 * field name that generated the error.
	 * 
	 *
	 * @param key The key to store the message as
	 * @param error The error message to store
	 */
	public void addError(String key, ErrorMessage error)
	{
		if ( error != null )
		{
			errorMessages.put(key, error);
		}
	}

	/**
	 * Add an error message to this structure using the error messages
	 * key value. If the error message is associated with a field, the
	 * field name is used as its reference. Otherwise the message key is used.
	 *
	 * @param error The error message to add to this structure
	 */
	public void addError(ErrorMessage error)
	{
		if ( error != null )
		{
			if ( !Utils.isEmpty(error.getFieldName()))
			{
				errorMessages.put(error.getFieldName(), error);
			}
			else
			{
				errorMessages.put(error.getMessageKey(), error);
			}
		}
	}

	/**
	 * Get an error message using the given key
	 *
	 * @param key The key to look the error message up
	 *
	 * @return The error message for the given key or null if not found
	 */
	public ErrorMessage getError(String key)
	{
		return errorMessages.get(key);
	}

	public void replaceFieldNames(String from, String to)
	{
		errorMessages.forEach( (key, errorMessage) ->
		{
			String fieldName = errorMessage.getFieldName();

			if ( fieldName.contains(from) )
			{
				String toFieldName = fieldName.replace(from, to);
				errorMessage.setFieldName(toFieldName);
			}
		});

		/*
		Map<String, ErrorMessage> newMessages = new HashMap<>();

		Iterator<Map.Entry<String, ErrorMessage>> it = errorMessages.entrySet().iterator();
		while ( it.hasNext() )
		{
			Map.Entry<String, ErrorMessage> currEntry = it.next();
			String key = currEntry.getKey();

			// Remove the key from the error messages for later addition
			//
			if ( key.contains(from))
			{
				String toKey = key.replace(from, to);
				newMessages.put(toKey, errorMessages.get(key));

				it.remove();
			}
		}

		errorMessages.putAll(newMessages);
		*/
	}

	/**
	 * Get all error messages whose keys match the given regular expression.
	 * 
	 * @param regex The regular expression to match
	 * 
	 * @return The (potentially empty) list of messages whose keys match the regex
	 */
	public List<ErrorMessage> getErrorsByKeyRegex(String regex)
	{
		return errorMessages.entrySet().stream()
			.filter( (entry) -> { return entry.getKey().matches(regex); } )
			.map( (entry) -> { return entry.getValue(); } )
			.collect(Collectors.toList());
	}

	/**
	 * Get all error messages in this structure
	 *
	 * @return All error messages in this structure
	 */
	@JsonIgnore
	public Collection<ErrorMessage> getAllErrorValues()
	{
		return errorMessages.values();
	}

	/**
	 * Get all error messages in this structure with their associated keys
	 *
	 * @return The set of all error messages and their associated keys
	 */
	public Set < Map.Entry < String, ErrorMessage >> getAllErrors()
	{
		return errorMessages.entrySet();
	}

	/**
	 * Clear the structure of any errors
	 */
	public void clearErrors()
	{
		errorMessages.clear();
	}

	/**
	 * Tests whether this structure contains any errors or not
	 *
	 * @return True if there are errors, false otherwise
	 */
	public boolean hasErrors()
	{
		return !errorMessages.isEmpty();
	}

	/**
	 * This is a spring compatibility method intended for calls from JSP using EL
	 *
	 * @return hasErrors()
	 */
	@JsonIgnore
	public boolean getHasErrors()
	{
		return hasErrors();
	}

	/**
	 * Returns the list of errors
	 *
	 * @return THe list of errors
	 */
	@Override
	public String toString()
	{
		StringBuilder retVal = new StringBuilder();
		for ( ErrorMessage currMessage : getAllErrorValues() )
		{
			retVal.append(currMessage.toString());
			retVal.append("\n");
		}

		return retVal.toString();
	}

	public SingleClient getSingleClient()
	{
		return singleClient;
	}

	public void setSingleClient(SingleClient singleClient)
	{
		this.singleClient = singleClient;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}
}