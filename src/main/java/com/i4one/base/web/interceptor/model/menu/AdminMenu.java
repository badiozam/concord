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
package com.i4one.base.web.interceptor.model.menu;

import com.i4one.base.core.BaseLoggable;
import com.i4one.base.core.Utils;
import com.i4one.base.model.admin.Admin;
import com.i4one.base.model.adminprivilege.AdminPrivilegeManager;
import com.i4one.base.model.adminprivilege.ClientAdminPrivilege;
import com.i4one.base.model.adminprivilege.Privilege;
import com.i4one.base.web.controller.Model;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Hamid Badiozamani
 */
public class AdminMenu extends BaseLoggable
{
	private final Map<String, MenuItem> items;

	public AdminMenu()
	{
		items = new LinkedHashMap<>();
	}

	public Object get(String key)
	{
		MenuItem item = items.get(key);
		if ( item != null )
		{
			return item.getValue();
		}
		else
		{
			return null;
		}
	}

	public void put(String key, Object value)
	{
		items.put(key, new MenuItem(key, value, ""));
	}

	public void put(String key, Object value, String permission)
	{
		items.put(key, new MenuItem(key, value, permission));
	}

	public Map<String, Object> buildMenu(Admin admin, Model model, AdminPrivilegeManager adminPrivilegeManager)
	{
		Map<String, Object> retVal = new LinkedHashMap<>();

		Privilege privilege = new Privilege();
		privilege.setReadWrite(false);

		ClientAdminPrivilege priv = new ClientAdminPrivilege();
		priv.setClient(model.getSingleClient());
		priv.setAdmin(admin);
		priv.setPrivilege(privilege);

		for ( Map.Entry<String, MenuItem> item : items.entrySet() )
		{
			privilege.setFeature(item.getValue().getPermission());

			// If the feature is empty, then we skip any permission checking
			//
			getLogger().trace("Testing permissions for {}", priv.getPrivilege());
			if ( !admin.exists() || Utils.isEmpty(privilege.getFeature()) || adminPrivilegeManager.hasAdminPrivilege(priv))
			{
				MenuItem menuItem = item.getValue();
				Object itemValue = menuItem.getValue();

				if ( itemValue instanceof AdminMenu )
				{
					// Build the submenus recursively with the active permissions
					//
					AdminMenu subMenu = (AdminMenu)itemValue;
					retVal.put(item.getKey(), subMenu.buildMenu(admin, model, adminPrivilegeManager));
				}
				else
				{
					retVal.put(item.getKey(), itemValue);
					getLogger().trace("Adding " + item.getKey() + " => " + itemValue);
				}
			}
			else
			{
				getLogger().trace("Skipping addition of " + item.getKey() + " permission needed = " + privilege.getFeature() + " w/ priv = " + priv);
			}
		}

		return retVal;
	}
}
