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
package com.i4one.base.model.report.classifier;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.dao.RecordType;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * A report that maps classifications to values. A typical use-case would be to
 * request a data point from the ClassificationReport in a string format:
 * 
 * "MembershipReport:3|genders#m" =&gt; 5
 * "MembershipReport:3|ages#18-24" =&gt; 3
 * "MembershipReport:3|ages#18-24|genders#m" =&gt; 2
 * 
 * The caller might also request a set of all unique standalone Classifications and
 * iterate through each of them as necessary.
 * 
 * @param <U> The type of database record that is fed into this report
 * @param <T> The type of object that the database record is converted into before
 * 	being classified.
 * 
 * @author Hamid Badiozamani
 */
public abstract class ClassificationReport<U extends RecordType, T extends Object> extends BaseLoggable
{
	private final Map<Set<? extends Subclassification<T>>, Function<T, Boolean>> callbacks;

	protected final String title;
	protected final Map<Set<? extends Subclassification<T>>, Integer> values;
	protected final Set<Classification<T, ? extends Subclassification<T>>> classifications;

	public ClassificationReport(String title)
	{
		super();

		this.title = title;

		SetComparator<Set<? extends Subclassification<T>>> subclassComparator = new SetComparator<>();

		values = new TreeMap<>(subclassComparator);
		callbacks = new TreeMap<>(subclassComparator);

		classifications = new LinkedHashSet<>();
	}

	public boolean add(Classification<T, ? extends Subclassification<T>> classification)
	{
		boolean added = classifications.add(classification);
		if ( added )
		{
			// We add all of the individual subclassifications to the tree.
			// Note that this is different than adding the set of
			// subclassifications in this classification.
			//
			for ( Subclassification<T> subclass : classification.getSubclassifications() )
			{
				add(subclass);
			}
		}

		return added;
	}

	/**
	 * Add all possible combinations of top-level subclassifications to all
	 * other subclassifications thus producing a two level drill-down. For
	 * example, if the report contains two classifications AgeClassification
	 * and GenderClassification, then for all ages in AgeClassification each
	 * gender subclassification would be appended and then added to the report.
	 * Note that duplicates are automatically removed via the Set structure.
	 */
	public void addCombinations()
	{
		int numPermutations = 0;

		for ( Classification<? extends T, ? extends Subclassification<T>> class1 : classifications )
		{
			for ( Classification<? extends T, ? extends Subclassification<T>> class2 : classifications )
			{
				numPermutations += addCombinations(class1, class2);
			}
		}

		getLogger().debug("{} permutations were reduced to {} unique combinations for subclassifications", numPermutations, values.size());
	}

	protected int addCombinations(Classification<? extends T, ? extends Subclassification<T>> class1, Classification<? extends T, ? extends Subclassification<T>> class2)
	{
		int numPermutations = 0;
		if ( !class1.equals(class2))
		{
			for ( Subclassification<T> subclass1 : class1.getSubclassifications() )
			{
				// E.g. gender#m
				//
				Set<Subclassification<T>> permutation = new HashSet<>();

				for ( Subclassification<T> subclass2 : class2.getSubclassifications() )
				{
					// E.g. gender#m|age#18-24
					//
					permutation.add(subclass1);
					permutation.add(subclass2);
					add(permutation);

					permutation.clear();
					numPermutations++;
				}
			}
		}

		return numPermutations;
	}

	protected void addCombination(Subclassification<T> subclass2)
	{
		for ( Classification<? extends T, ? extends Subclassification<T>> classification : classifications )
		{
			if ( !classification.equals(subclass2.getParent()))
			{
				// E.g. gender#m
				//
				Set<Subclassification<T>> permutation = new HashSet<>();

				for ( Subclassification<T> subclass1 : classification.getSubclassifications() )
				{
					// E.g. gender#m|zipcode#90210
					//
					permutation.add(subclass1);
					permutation.add(subclass2);
					add(permutation);

					permutation.clear();
				}
			}
		}
	}

	public Set<Classification<T, ? extends Subclassification<T>>> getClassifications()
	{
		return Collections.unmodifiableSet(classifications);
	}

	/**
	 * Convenience method for adding a single subclassification. Has the same
	 * effect as adding a single element set of subclassifications.
	 * 
	 * @param subclass The subclass to add
	 * 
	 * @return True if the subclass was successfully added.
	 */
	public boolean add(Subclassification<T> subclass)
	{
		Set<Subclassification<T>> addition = new HashSet<>();
		addition.add(subclass);

		return add(addition);
	}

	/**
	 * Add a set of subclassifications that describe a particular data point.
	 * For example, a subclassification for Males 18-24 who live in 90210 would
	 * look like the following set:
	 * 
	 * gender#m|ages#18-24|zipcode#90210
	 * 
	 * Note that each subclassification implicitly includes its parent such that
	 * including "18-24" would imply "Ages" as well. Further note that the order
	 * of the subclassifications do not affect the result due to the transitive
	 * property of the Boolean AND operator. In fact, internally the sets are
	 * sorted for faster lookup.
	 * 
	 * gender#m|ages#18-24|zipcode#90210 =&gt; ages#18-24|gender#m|zipcode#90210
	 * 
	 * @param subclass The subclass chain to add
	 * 
	 * @return True if the subclass was added, false otherwise
	 */
	public boolean add(Set<? extends Subclassification<T>> subclass)
	{
		getLogger().trace("Considering {}", subclass);

		if ( subclass.isEmpty() )
		{
			getLogger().trace("Cannot add the empty set");
			return false;
		}
		else if ( values.containsKey(getStandardizedSet(subclass)))
		{
			getLogger().trace("Values contains key {}", getStandardizedSet(subclass));
			return false;
		}
		else
		{
			if ( subclass.size() == 1 )
			{
				classifications.add(subclass.iterator().next().getParent());
			}

			values.put(getStandardizedSet(subclass), 0);
			return true;
		}
	}

	/**
	 * Get the value for a particular subclassification chain.
	 * 
	 * @param subclassChain The subclass chain
	 * 
	 * @return The value for the given subclass chain or null if not found
	 */
	public Integer get(Set<? extends Subclassification<T>> subclassChain)
	{
		return values.get(getStandardizedSet(subclassChain));
	}

	/**
	 * Process a record by incrementing the value for each registered subclass
	 * chain if the it matches.
	 * 
	 * @param item The item to process.
	 */
	public void processRecord(U item)
	{
		T convertedItem = convert(item);

		Function<T, Boolean> rootCallback = callbacks.get(Collections.emptySet());
		if ( rootCallback == null || rootCallback.apply(convertedItem))
		{
			for (Set<? extends Subclassification<T>> subclassChain : values.keySet() )
			{
				boolean eligible = true;
				for ( Subclassification<T> subclass : subclassChain )
				{
					eligible &= subclass.belongs(convertedItem);
					if ( !eligible )
					{
						break;
					}
				}
	
				if ( eligible )
				{
					Function<T, Boolean> callback = callbacks.get(subclassChain);

					if ( callback == null || callback.apply(convertedItem))
					{
						// We only proceed if the callback returned true
						//
						Integer value = values.get(subclassChain);
						value++;
	
						values.put(subclassChain, value);
					}
				}
			}
		}
	}

	/**
	 * Attach a callback method to be invoked each time a record is processed.
	 * The method is only invoked if the eligibility requirements have been
	 * met and after the record has been processed.
	 * 
	 * @param lineage The subclasses that the record has to be eligible for.
	 * @param processCallback The method to call back after processing. The item
	 * 	being processed is passed to the callback and the count for the lineage
	 * 	is only incremented if the callback returns true.
	 */
	public void setProcessCallback(Set<Subclassification<T>> lineage, Function<T,Boolean> processCallback)
	{
		callbacks.put(getStandardizedSet(lineage), processCallback);
	}

	/**
	 * Convert the given database record into the type the report is classifiying.
	 * 
	 * @param record The record to convert
	 * 
	 * @return The converted object.
	 */
	protected abstract T convert(U record);

	private Set<? extends Subclassification<T>> getStandardizedSet(Set<? extends Subclassification<T>> originalSet)
	{
		TreeSet<Subclassification<T>> retVal = new TreeSet<>(new ExclusiveSubclassificationComparator<>());
		retVal.addAll(originalSet);

		return Collections.unmodifiableSet(retVal);
	}

	public String getTitle()
	{
		return title;
	}

	public Map<Set<? extends Subclassification<T>>, Integer> getValues()
	{
		return Collections.unmodifiableMap(values);
	}

	@Override
	public String toString()
	{
		StringBuilder retVal = new StringBuilder();
		retVal.append(getTitle()).append("\n");
		retVal.append(classifications).append("\n");
		retVal.append(values.toString()).append("\n");

		return retVal.toString();
	}
}