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
package com.i4one.base.core;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a collection that only holds one element and blocks on
 * add and remove. The purpose of this structure is to allow for one thread to
 * communicate a set of objects with another thread in a simulated stream manner.
 * 
 * Specifically, the View technology (VTL/Freemarker/etc.) receive a reference to
 * this object and use it to traverse the collection. This object then blocks until
 * new content becomes available, thereby blocking the view rendering process also.
 * 
 * When the thread populating this object has exhausted its stream of objects, it
 * calls the close() method which signals all iterators to show the collection as
 * being empty.
 * 
 * The motivation here is to transfer a very large collection of items over the wire
 * without using VM memory as each item is written to the output stream and discarded
 * and replaced with the subsequent element. As far as the view is concerned, however,
 * there's not much difference other than the fact that the collection is single-use.
 * 
 * @author Hamid Badiozamani
 * 
 * @param <T> The data type being collected
 */
public class BlockingSingleItemCollection<T extends Object> implements Collection<T>
{
	private final Logger logger;

	private T item;
	private boolean closed;

	public BlockingSingleItemCollection()
	{
		logger = LoggerFactory.getLogger(getClass());

		item = null;
		closed = false;
	}

	protected Logger getLogger()
	{
		return logger;
	}

	@Override
	public int size()
	{
		return (item == null ? 0 : 1);
	}

	@Override
	public boolean isEmpty()
	{
		return (item == null);
	}

	@Override
	public boolean contains(Object o)
	{
		return Objects.equals(o, item);
	}

	@Override
	public Object[] toArray()
	{
		Object[] retVal = new Object[1];
		return toArray(retVal);
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		synchronized(this)
		{
			a[0] = (T) item;
		}

		return a;
	}

	@Override
	public boolean add(T e)
	{
		synchronized(this)
		{
			getLogger().trace("add called");
			if ( item == null )
			{
				item = e;

				getLogger().trace("add: Buffer filled, notifying addMonitor");
				this.notify();

				return true;
			}
			else
			{
				try
				{
					getLogger().trace("add: Buffer full, waiting");

					// We're waiting for item to become null
					//
					this.wait();
					getLogger().trace("add: Buffer empty, waking up and trying again");

					return add(e);
				}
				catch (InterruptedException ex)
				{
					getLogger().debug("Interrupted in wait", ex);
					return false;
				}
			}
		}
	}

	@Override
	public boolean remove(Object o)
	{
		synchronized(this)
		{
			getLogger().trace("remove called");
			if ( item == null || !Objects.equals(item, o))
			{
				return false;
			}
			else
			{
				// We've become empty so we notify add
				//
				item  = null;

				getLogger().trace("remove: Buffer emptied, notifying");
				this.notify();

				return true;
			}
		}
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		boolean retVal = true;
		synchronized(this)
		{
			for ( Iterator it = c.iterator(); it.hasNext() && retVal; )
			{
				retVal &= contains(it.next());
			}
		}

		return retVal;
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		boolean retVal = false;
		synchronized(this)
		{
			for ( T currItem : c )
			{
				retVal |= add(currItem);
			}
		}

		return retVal;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean retVal = false;

		synchronized(this)
		{
			for ( Iterator it = c.iterator(); it.hasNext(); )
			{
				retVal |= remove(it.next());
			}
		}

		return retVal;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		synchronized(this)
		{
			remove(item);
		}
	}

	class BlockingSingleItemIterator<T> implements Iterator<T>
	{
		private final BlockingSingleItemCollection<T> parent;
		private Boolean hasNext;

		BlockingSingleItemIterator(BlockingSingleItemCollection<T> parent)
		{
			this.parent = parent;
			this.hasNext = null;
		}

		@Override
		public boolean hasNext()
		{
			getLogger().trace("Iterator hasNext called");

			// We ensure we return the same hasNext value each
			// time we're called unless the state has changed.
			//
			if ( hasNext == null )
			{
				hasNext = hasNextInternal();
			}

			return hasNext;
		}

		protected boolean hasNextInternal()
		{
			getLogger().trace("hasNextInternal called");
			if ( !parent.isClosed() || item != null )
			{
				synchronized(parent)
				{
					if ( parent.item == null )
					{
						getLogger().trace("hasNextInternal: waiting for item to be added or stream to be closed");
						try
						{
							parent.wait();
							return hasNextInternal();
						}
						catch (InterruptedException ex)
						{
							getLogger().trace("Interrupted while waiting for add", ex);
							return false;
						}
					}
					else
					{
						getLogger().trace("hasNextInternal: item is available, returning true");
						return true;
					}
				}
			}
			else
			{
				getLogger().trace("hasNextInternal: stream is closed and there are no more items, returning false");
				return false;
			}
		}

		@Override
		public T next()
		{
			getLogger().trace("Iterator next called");

			synchronized(parent)
			{
				T retVal = parent.item;
				parent.remove(parent.item);

				// Force a recomputation of the hasNext value since
				// we modified the parent
				//
				hasNext = null;

				getLogger().trace("Iterator next returning " + retVal);
				return retVal;
			}
		}
	}

	private BlockingSingleItemIterator<T> iterator;

	@Override
	public Iterator<T> iterator()
	{
		if ( iterator == null )
		{
			iterator = new BlockingSingleItemIterator(this);
			return iterator;
		}
		else
		{
			throw new ConcurrentModificationException("Can't have more than one iterator on this collection");
		}
	}

	public boolean isClosed()
	{
		synchronized(this)
		{
			return closed;
		}
	}

	public void setClosed(boolean closed)
	{
		synchronized(this)
		{
			this.closed = closed;
			if ( closed == true )
			{
				this.notify();
			}
		}
	}

	public void close()
	{
		setClosed(true);
	}
}
