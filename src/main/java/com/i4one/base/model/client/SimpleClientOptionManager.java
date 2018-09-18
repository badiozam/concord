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
package com.i4one.base.model.client;

import com.i4one.base.model.ReturnType;
import static com.i4one.base.model.client.SingleClient.getRoot;
import com.i4one.base.model.manager.BaseSimpleManager;
import com.i4one.base.model.manager.pagination.PaginationFilter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
@Service
public class SimpleClientOptionManager extends BaseSimpleManager<ClientOptionRecord,ClientOption> implements ClientOptionManager
{
	private SingleClientManager singleClientManager;

	@Override
	public String getOptionValue(SingleClient client, String keyStr)
	{
		ClientOption option = getOption(client, keyStr);
		if ( option.exists() )
		{
			return option.getValue();
		}
		else
		{
			getLogger().error("Invalid key {} called from:", keyStr, new Throwable());
			throw new IllegalArgumentException("Option with key " + keyStr + " for client " + client + " not found!");
		}
	}

	@Override
	public ClientOption getOption(SingleClient client, String keyStr)
	{
		// Make sure we load the client in order to ensure we have the correct
		// client tree hierarchy
		//
		SingleClient currClient = getSingleClientManager().getClient(client.getSer());

		while ( currClient.exists() )
		{
			ClientOptionRecord optionRecord = getDao().getOption(currClient.getSer(), keyStr);

			if ( optionRecord != null )
			{
				getLogger().trace("Returning '" + keyStr + "' = " + optionRecord);
				ClientOption option = new ClientOption(optionRecord);

				return option;
			}
			else
			{
				currClient = currClient.getParent();
			}
		}

		getLogger().trace("Option " + keyStr + " not found for " + client);

		ClientOption retVal = new ClientOption();
		retVal.setClient(client);
		retVal.setKey(keyStr);

		return retVal;
	}

	@Override
	public List<ClientOption> getOptions(SingleClient client, String startsWithKey, PaginationFilter pagination)
	{
		// Make sure we load the client in order to ensure we have the correct
		// client tree hierarchy
		//
		SingleClient currClient = getSingleClientManager().getClient(client.getSer());

		if ( currClient.exists() )
		{
			// No caching for this method
			//
			List<ClientOptionRecord> optionRecords = getDao().getOptions(currClient.getSer(), startsWithKey, pagination);
			List<ClientOption> retVal = new ArrayList<>(optionRecords.size());
			optionRecords.stream().forEach((record) ->
			{
				retVal.add(new ClientOption(record));
			});

			if ( !currClient.isRoot() )
			{
				getLogger().trace("Getting options that start with " + startsWithKey + " for client " + currClient.toString());
				retVal.addAll(getOptions(currClient.getParent(), startsWithKey, pagination));
			}

			return retVal;
		}
		else
		{
			return new ArrayList<>();
		}
	}

	@Override
	public ClientOptionRecordDao getDao()
	{
		return (ClientOptionRecordDao) super.getDao();
	}

	/**
	 * Updates an option, overriding the parent client's value if necessary
	 *
	 * @param option The option to update
	 */
	@Transactional(readOnly = false)
	@Override
	public ReturnType<ClientOption> update(ClientOption option)
	{
		getLogger().trace("Updating option " + option);

		ReturnType<ClientOption> retVal = new ReturnType<>();
		retVal.setPre(new ClientOption());

		ClientOption currOption = getOption(option.getClient(), option.getKey());
		if ( !currOption.exists() )
		{
			getLogger().trace("Option " + option + " is a brand new key");

			// This is the first time this key is being created, we create the key
			// at the root node so that all clients of the application have access
			// to the key
			//
			option.setClient(getRoot());
			getDao().insert(option.getDelegate());

			retVal.setPost(option);
		}
		else
		{
			getLogger().trace("Option " + option + " is an existing key: " + currOption);
			retVal.setPre(currOption);

			if ( currOption.getClient().equals(option.getClient()) )
			{
				getLogger().trace("Option " + option + " updates current value for client " + option.getClient() + " via " + currOption);

				SingleClient parentClient = option.getClient().getParent();
				ClientOption parentOption = this.getOption(parentClient, option.getKey());

				if (
				        parentOption.exists() &&
				        parentOption.getValue() != null &&
				        parentOption.getValue().equals(option.getValue())
				        )
				{
					// The value is the same as the parent's value, removing to allow
					// inheritance to take over
					//
					remove(currOption);

					retVal.setPost(parentOption);
				}
				else
				{
					// This is a key for the same client, we only need to update the value
					//
					option.setSer(currOption.getSer());
					getDao().updateBySer(option.getDelegate());

					retVal.setPost(option);
				}
			}
			else if ( !currOption.getValue().equals(option.getValue()))
			{
				getLogger().trace("Option " + option + " overrides parent's value for client " + option.getClient() + " via " + currOption);

				// The key is for a different client so we need to create a new key
				//
				option.setSer(0);
				getDao().insert(option.getDelegate());

				retVal.setPost(option);
			}
			else
			{
				getLogger().trace("Option " + option + " has the same value as its parent for client " + option.getClient() + " via " + currOption);

				retVal.setPost(currOption);
			}
		}

		return retVal;
	}

	@Transactional(readOnly = false)
	@Override
	public ClientOption remove(ClientOption option)
	{
		getLogger().trace("Removing option " + option);

		ClientOption currOption = getOption(option.getClient(), option.getKey());
		if ( !currOption.exists() )
		{
			getLogger().trace("Option " + option + " does not specify a serial number");
			return new ClientOption();
		}
		else
		{
			getLogger().trace("Option " + option + " exists, checking client");

			if ( currOption.getClient().getSer().equals(option.getClient().getSer()))
			{
				getLogger().trace("Option " + option + " is a leaf node, removing");

				// This is a key for the same client
				//
				ClientOptionRecord dbRecord = getDao().getBySer(currOption.getSer(), true);
				if ( dbRecord != null )
				{
					getDao().deleteBySer(currOption.getSer());

					return new ClientOption(dbRecord);
				}
				else
				{
					getLogger().trace("Option " + option + " does not exist in the database");
					return new ClientOption();
				}
			}
			else
			{
				getLogger().trace("Option " + option + " belongs to the parent, ignoring remove request");
				return new ClientOption();
			}
		}
	}

	@Override
	public List<String> getAllKeys(PaginationFilter pagination)
	{
		return getDao().getAllKeys(pagination);
	}

	@Override
	public ReturnType<ClientOption> create(ClientOption item)
	{
		return update(item);
	}

	@Override
	public ClientOption emptyInstance()
	{
		return new ClientOption();
	}

	public SingleClientManager getSingleClientManager()
	{
		return singleClientManager;
	}

	@Autowired
	public void setSingleClientManager(SingleClientManager readOnlySingleClientManager)
	{
		this.singleClientManager = readOnlySingleClientManager;
	}

}
