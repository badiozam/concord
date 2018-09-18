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
package com.i4one.base.web.controller.admin.members;

import com.i4one.base.core.Utils;
import com.i4one.base.model.Errors;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.balance.Balance;
import com.i4one.base.model.balance.BalanceManager;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.HistoricalManager;
import com.i4one.base.model.user.User;
import com.i4one.base.model.user.UserBalance;
import com.i4one.base.model.user.UserBalanceManager;
import com.i4one.base.model.user.UserManager;
import com.i4one.base.web.controller.Model;
import com.i4one.base.web.controller.SubmitStatus;
import com.i4one.base.web.controller.admin.BaseAdminViewController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class MemberImportExportController extends BaseAdminViewController
{
	private UserManager userManager;
	private BalanceManager balanceManager;
	private UserBalanceManager userBalanceManager;

	@RequestMapping(value = "**/base/admin/members/import", method = RequestMethod.GET)
	public Model importCodes(@ModelAttribute("userimport") WebModelUserImport userImport, HttpServletRequest request, HttpServletResponse response)
	{
		Model retVal = initRequest(request, userImport);

		return initResponse(retVal, response, userImport);
	}

	@RequestMapping(value = "**/base/admin/members/import", method = RequestMethod.POST)
	public Model doImportMembers(@ModelAttribute("userimport") WebModelUserImport userImport, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws FileUploadException, IOException
	{
		Model model = initRequest(request, userImport);

		// Streaming upload
		//
		ServletFileUpload upload = new ServletFileUpload();

		// Needed in the lambda for pre-processing later on
		//
		Calendar cal = Calendar.getInstance();
		int currYear = cal.get(Calendar.YEAR);
		int currTime = getRequestState().getRequest().getTimeInSeconds();

		FileItemIterator it = upload.getItemIterator(request);
		while ( it.hasNext() )
		{
			FileItemStream fileStream = it.next();

			String fieldName = fileStream.getFieldName();
			if ( fileStream.isFormField() )
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream.openStream()));
				String fieldValue = reader.readLine();

				getLogger().debug("{} = {}", fieldName, fieldValue);
			}
			else
			{
				SingleClient client = model.getSingleClient();
				Balance defaultBalance = getBalanceManager().getDefaultBalance(client);

				try
				{
					getRequestState().set(HistoricalManager.ATTR_RECORDHISTORYENABLED, false);
					List<ReturnType<User>> importResults = getUserManager().importCSV(fileStream.openStream(),
					() ->
					{
						User newUser = new ImportableUser();
						newUser.setClient(client);
		
						return newUser;
					},
					(item) ->
					{
						ImportableUser importableUser = (ImportableUser)item;

						boolean retVal = true;
						if ( Utils.isEmpty(importableUser.getCellPhone()) ||
							importableUser.getCellPhone().equals(importableUser.getHomePhone()))
						{
							User byCellphone = new User();
							byCellphone.setCellPhone(importableUser.getHomePhone());

							User duplicateUser = getUserManager().lookupUser(byCellphone);
							if ( duplicateUser.exists() )
							{
								getLogger().debug("Found a duplicate user of cellphone {}", byCellphone.getCellPhone());

								// Two people are sharing a home phone. We create a bogus
								// cell phone so the import can continue
								//
								retVal = false;
								for (int i = 0; i < 100; i++ )
								{
									byCellphone.setCellPhone( "" + (Utils.currentTimeSeconds() + i));
									if ( !getUserManager().lookupUser(byCellphone).exists() )
									{
										importableUser.setCellPhone(byCellphone.getCellPhone());
										retVal = true;

										getLogger().debug("User wound up with generated cellphone {}", importableUser.getCellPhone());
										break;
									}
								}
							}
							else
							{
								importableUser.setCellPhone(byCellphone.getCellPhone());
							}
						}
						else
						{
							// Cellphone and homephone aren't the same and cellphone isn't empty, we have to check
							// to see if there's someone with the cellphone
							//
							User byCellphone = new User();
							byCellphone.setCellPhone(importableUser.getCellPhone());

							User duplicateUser = getUserManager().lookupUser(byCellphone);
							if ( duplicateUser.exists() )
							{
								// The user's cellphone is used by someone else, try setting
								// the home phone which should be unique
								//
								byCellphone.setCellPhone(importableUser.getHomePhone());
								duplicateUser = getUserManager().lookupUser(byCellphone);

								if ( duplicateUser.exists() )
								{
									getLogger().debug("User exists by home phone in the cell phone field {}", importableUser.getHomePhone());
									return false;
								}
								else
								{
									importableUser.setCellPhone(importableUser.getHomePhone());
								}
							}
							else if ( duplicateUser.exists() )
							{
								// We've found the user by cellphone match, the assumption is that a user
								// can't share a cellphone with a different account
								//
								UserBalance userBalance = new UserBalance();
								userBalance.setUser(duplicateUser);
								userBalance.setBalance(defaultBalance);
								userBalance.setCreateTimeSeconds(currTime);

								userBalance = getUserBalanceManager().getUserBalanceForUpdate(userBalance);
								userBalance.setTotal(importableUser.getPointBalance());
								getUserBalanceManager().increment(userBalance, importableUser.getPointBalance());

								getLogger().debug("User already exists by cellphone, updated user balance for this client to {}", userBalance);
								return false;
							}
							else
							{
								// Non-duplicate
								//
							}
						}

						User byEmail = new User();
						byEmail.setEmail(importableUser.getEmail());

						User duplicateUser = getUserManager().lookupUser(byEmail);
						if ( duplicateUser.exists() )
						{
							// There's a match by e-mail address, the original username is kept
							//
							UserBalance userBalance = new UserBalance();
							userBalance.setUser(duplicateUser);
							userBalance.setBalance(defaultBalance);
							userBalance.setCreateTimeSeconds(currTime);

							userBalance = getUserBalanceManager().getUserBalanceForUpdate(userBalance);
							userBalance.setTotal(importableUser.getPointBalance());
							getUserBalanceManager().increment(userBalance, importableUser.getPointBalance());

							getLogger().debug("User already exists by e-mail, updated user balance for this client to {}", userBalance);
							return false;
						}

						// Correct birth year to fit within constraints
						//
						if ( importableUser.getBirthYYYY() <= (currYear - 111) || importableUser.getBirthYYYY() >= currYear)
						{
							importableUser.setBirthYYYY(currYear - 12);
						}

						// Correct birthdd to fit within constraints
						//
						if ( importableUser.getBirthDD() < 1 || importableUser.getBirthDD() > 31 )
						{
							importableUser.setBirthDD(1);
						}

						// Correct birthmm to fit within constraints
						//
						if ( importableUser.getBirthMM() < 1 || importableUser.getBirthMM() > 12 )
						{
							importableUser.setBirthMM(1);
						}
						return retVal;
					},
					(item) ->
					{
						User currUser = item.getPost();

						if ( currUser.exists() && currUser instanceof ImportableUser )
						{
							ImportableUser importedUser = (ImportableUser)currUser;

							// Get the balance that was imported and create it for this user
							//
							ReturnType<UserBalance> defBal = getUserBalanceManager().increment(
								new UserBalance(importedUser, defaultBalance, getRequestState().getRequest().getTimeInSeconds()),
								importedUser.getPointBalance());

							item.addChain(getUserBalanceManager(), "increment", defBal);
						}
						else
						{
							throw new IllegalArgumentException("Expected an importable user but instead received " + currUser.getClass().getSimpleName());
						}
					});

					List<Errors> errors = new ArrayList<>();
					for ( ReturnType<User> currStatus : importResults )
					{
						if ( currStatus.containsKey("importErrors"))
						{
							errors.add((Errors) currStatus.get("importErrors"));
						}
					}
		
					if ( errors.isEmpty() )
					{
						success(model, getMessageRoot() + ".import.success");
					}
					else
					{
						success(model, getMessageRoot() + ".import.partial", importResults, SubmitStatus.ModelStatus.PARTIAL);
					}
				}
				catch (Errors errors)
				{
					fail(model, getMessageRoot() + ".import.failure", result, errors);
				}
		
				userImport.setCsv(fieldName);

				// Note that the file upload element has to be the last form item
				// to be processed.
				// 
				break;
			}
		}

		return initResponse(model, response, userImport);
	}

	protected String getMessageRoot()
	{
		return "msg.base.admin.members";
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager)
	{
		this.userManager = userManager;
	}

	public BalanceManager getBalanceManager()
	{
		return balanceManager;
	}

	@Autowired
	public void setBalanceManager(BalanceManager balanceManager)
	{
		this.balanceManager = balanceManager;
	}

	public UserBalanceManager getUserBalanceManager()
	{
		return userBalanceManager;
	}

	@Autowired
	public void setUserBalanceManager(UserBalanceManager userBalanceManager)
	{
		this.userBalanceManager = userBalanceManager;
	}
}