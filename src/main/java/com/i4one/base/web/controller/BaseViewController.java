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
package com.i4one.base.web.controller;

import com.i4one.base.core.Stringifier;
import com.i4one.base.core.Utils;
import com.i4one.base.core.BaseLoggable;
import com.i4one.base.model.ErrorMessage;
import com.i4one.base.model.Errors;
import com.i4one.base.model.RecordTypeDelegator;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.i18n.IString;
import com.i4one.base.model.i18n.IStringifier;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import com.i4one.base.web.ModelManager;
import com.i4one.base.web.RequestState;
import com.i4one.base.web.persistence.Persistence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Hamid Badiozamani
 */
public abstract class BaseViewController extends BaseLoggable implements ViewController
{
	/** Generic status variable to be exposed in the model */
	public static final String MODELSTATUS = "modelStatus";

	private MessageManager messageManager;
	private ModelManager modelManager;
	private RequestToViewNameTranslator viewNameTranslator;

	private RequestState requestState;

	public BaseViewController()
	{
		getLogger().debug("Created controller {}", this);
	}

	/**
	 * Loads a model from the given request
	 * 
	 * @param request The incoming request
	 * @param modelAttribute The model attribute to initialize
	 * 
	 * @return The model object for the given request
	 */
	public Model initRequest(HttpServletRequest request, Object modelAttribute)
	{
		Model model = getRequestState().getModel();

		// Previous status
		//
		SubmitStatus submitStatus = (SubmitStatus)Persistence.getObject(request, SubmitStatus.class);
		if ( submitStatus != null )
		{
			model.put(MODELSTATUS, submitStatus);
		}

		if ( modelAttribute instanceof WebModel )
		{
			WebModel webModelAttribute = (WebModel)modelAttribute;
			webModelAttribute.setModel(model);
		}

		model.put("item", modelAttribute);

		return model;
	}

	public Model initResponse(Model model, HttpServletResponse response, Object modelAttribute)
	{
		HttpServletRequest request = model.getRequest();
		model = getModelManager().initResponseModel(model);

		if ( Persistence.getObject(request, SubmitStatus.class) != null )
		{
			Persistence.putObject(request, response, MODELSTATUS, null);
		}

		if ( modelAttribute != null && modelAttribute instanceof RecordTypeDelegator )
		{
			// We ensure we have the latest version from the database if the item was
			// not already set as having been loaded. This is to allow POST methods 
			// to properly display class members (e.g. title) in cases where the modelAttribute
			// itself was not updated (but its member attributes were).
			//
			RecordTypeDelegator recordTypeDelegator = (RecordTypeDelegator)modelAttribute;
			recordTypeDelegator.loadedVersion();
		}

		return model;
	}

	@Override
	public boolean isAuthRequired()
	{
		return false;
	}
 
	@InitBinder
	public void initBinder(WebDataBinder binder)
	{
	    binder.setAutoGrowNestedPaths(false);
	}

	// Returns all interfaces and subinterfaces that a given object implements
	//
	protected ArrayList<Class<?>> getAllInterfaces(Class<?> obj)
	{
		ArrayList<Class<?>> retVal = new ArrayList<>();

		for ( Class<?> currInterface : obj.getInterfaces() )
		{
			retVal.addAll(getAllInterfaces(currInterface));
			retVal.add(currInterface);
		}

		return retVal;
	}

	/**
	 * Convert our errors object into BindingResult errors
	 *
	 * @param result The binding result to populate
	 * @param errors The errors to convert to the result
	 */
	protected void convertErrors(BindingResult result, Errors errors)
	{
		if ( result != null && errors != null )
		{
			getLogger().debug("Converting errors " + errors);
			errors.getAllErrorValues().stream().forEach( (error) ->
			{
				getLogger().debug("Adding error for " + result.getObjectName() + " w/ key = " + error.getMessageKey());

				convertError(result, error);
			});
		}

	}

	protected void convertError(BindingResult result, ErrorMessage error)
	{
		// If the error message has a field name, append that to the object name
		//
		String fieldName = error.getFieldName();
		if ( Utils.isEmpty(fieldName) || result.getFieldType(fieldName) == null )
		{
			getLogger().debug("The error is a general error for " + result.getObjectName());
			result.addError(new ObjectError(result.getObjectName(), new String[] { error.getMessageKey() }, error.getParams(), error.getDefaultMessage()));
		}
		else
		{
			getLogger().debug("The error is a field error for " + result.getObjectName() + "." + fieldName + " with raw field value = '" + result.getRawFieldValue(fieldName) + "'");
			result.addError(new FieldError(result.getObjectName(), fieldName, result.getRawFieldValue(fieldName), false, new String[] { error.getMessageKey() }, error.getParams(), error.getDefaultMessage()));
		}
	}

	/**
	 * Shorthand for a successful submission. Builds and injects a SubmitStatus
	 * object into the given model using the given message key and no arguments.
	 * 
	 * @param model The model the will contain the failed message(s)
	 * @param titleKey The overall title message key indicating success
	 */
	public void success(Model model, String titleKey)
	{
		success(model, titleKey, new ArrayList<>());
	}

	/**
	 * Shorthand for a successful submission. Builds and injects a SubmitStatus
	 * object into the given model using the given message key result.
	 * 
	 * @param model The model the will contain the failed message(s)
	 * @param titleKey The overall title message key indicating success
	 * @param managerResult The result of the operation
	 */
	public void success(Model model, String titleKey, ReturnType<?> managerResult)
	{
		success(model, titleKey, managerResult, SubmitStatus.ModelStatus.SUCCESSFUL);
	}

	/**
	 * Shorthand for a successful submission. Builds and injects a SubmitStatus
	 * object into the given model using the given message key result.
	 * 
	 * @param model The model the will contain the failed message(s)
	 * @param titleKey The overall title message key indicating success
	 * @param managerResult The result of the operation
	 * @param modelStatus The specific status code (i.e. successful, previously played)
	 */
	public void success(Model model, String titleKey, ReturnType<?> managerResult, SubmitStatus.ModelStatus modelStatus)
	{
		List<ReturnType<?>> managerResults = new ArrayList<>();
		managerResults.add(managerResult);

		success(model, titleKey, managerResults, modelStatus);
	}

	/**
	 * Shorthand for a successful submission. Builds and injects a SubmitStatus
	 * object into the given model using the given message key result.
	 * 
	 * @param model The model the will contain the failed message(s)
	 * @param titleKey The overall title message key indicating success
	 * @param managerResults The results of the operation
	 */
	public void success(Model model, String titleKey, List<? extends ReturnType<?>> managerResults)
	{
		success(model, titleKey, managerResults, SubmitStatus.ModelStatus.SUCCESSFUL);
	}

	/**
	 * Shorthand for a successful submission. Builds and injects a SubmitStatus
	 * object into the given model using the given message key result.
	 * 
	 * @param model The model the will contain the failed message(s)
	 * @param titleKey The overall title message key indicating success
	 * @param managerResults The results of the operation
	 * @param modelStatus The specific status code (i.e. successful, previously played)
	 */
	public void success(Model model, String titleKey, List<? extends ReturnType<?>> managerResults, SubmitStatus.ModelStatus modelStatus)
	{
		Message titleMessage = getMessageManager().getMessage(model.getSingleClient(), titleKey, model.getLanguage());

		SubmitStatus status = new SubmitStatus(titleMessage, model, modelStatus);
		status.setManagerResults(managerResults);

		model.put(MODELSTATUS, status);
	}

	/**
	 * Shorthand for a failed submission
	 * 
	 * @param model The model the will contain the failed message(s)
	 * @param titleKey The overall title message key of the error page
	 * @param result The result object with the specific error to field mapping
	 * @param errors The incoming errors from managers or other sources
	 */
	public void fail(Model model, String titleKey, BindingResult result, Errors errors)
	{
		if ( !Utils.isEmpty(titleKey ))
		{
			Message titleMessage = getMessageManager().getMessage(model.getSingleClient(), titleKey, model.getLanguage());
			SubmitStatus status = new SubmitStatus(titleMessage, model, SubmitStatus.ModelStatus.FAILED);

			model.put(MODELSTATUS, status);
		}

		convertErrors(result, errors);
	}

	/**
	 * Shorthand for adding a built message to the model
	 *
	 * @param model The model to add the message to
	 * @param key The key to use in the model when adding the messages
	 * @param msgKey The message's key
	 * @param args The arguments to the message
	 */
	protected void addMessageToModel(Model model, String key, String msgKey, Object... args)
	{
		model.put(key, model.buildMessage(msgKey, args));
	}

	/**
	 * Shorthand for setting the titleStr of a page.
	 * 
	 * @param model The model (which contains the language being used)
	 * @param titleStr  The titleStr to set
	 */
	protected void setTitle(Model model, IString titleStr)
	{
		model.put(Model.TITLE, titleStr.get(model.getLanguage()));
	}

	/**
	 * Return the present form if there are errors, or use one of the redirection inputs to send
	 * the user to. The priority is the session's REDIRTO variable, followed by redirVew, then
	 * redirURL and finally the defaultRedir
	 *
	 * @param model The model to add errors to and send back
	 * @param result The result of the form that would contains any errors
	 * @param redirView The internal view to send upon success
	 * @param redirURL The (potentially external) URL to send upon success
	 * @param defaultRedir The default location to send if neither of the redir values are specified
	 * @param request The HTTP request that contains the REDIRTO value
	 * @param response The HTTP response that may need to be cleared of the REDIRTO value after success
	 *
	 * @return A ModelAndView with the redirection or form view depending on the status
	 */
	protected ModelAndView redirOnSuccess(Model model, BindingResult result, String redirView, String redirURL, String defaultRedir, HttpServletRequest request, HttpServletResponse response)
	{
		if ( result != null && result.hasErrors() )
		{
			// Back to the same page
			//
			getLogger().debug("Returning form with errors");
			result.getAllErrors().stream().forEach((error) ->
			{
				getLogger().debug(error.getCode() + " for '" + error.getObjectName() + "'", error);
			});

			return new ModelAndView().addAllObjects(initResponse(model, response, null)).addAllObjects(result.getModel());
		}
		else
		{
			return redirWithDefault(model, result, redirView, redirURL, defaultRedir, request, response);
		}
	}

	/**
	 * Redirects the user based on one of several input parameters.
	 *
	 * @param model The model to send to the redirView (only used if redirView is present)
	 * @param result The result containing any errors or messages
	 * @param redirView The internal view to send upon success
	 * @param redirURL The (potentially external) URL to send upon success
	 * @param defaultRedir The default location to send if neither of the redir values are specified
	 * @param request The HTTP request that contains the REDIRTO value
	 * @param response The HTTP response that may need to be cleared of the REDIRTO value after success
	 *
	 * @return A ModelAndView with the redirection or form view depending on the status
	 */
	protected ModelAndView redirWithDefault(Model model, BindingResult result, String redirView, String redirURL, String defaultRedir, HttpServletRequest request, HttpServletResponse response)
	{
		// Where do we send the admin to next (we clear it after we've used it since it's no longer needed)
		//
		String redirToURL = Utils.defaultIfEmpty(redirURL, (String)Persistence.getObject(request, getRedirKey()));
		Persistence.putObject(request, response, getRedirKey(), null);

		// Maybe we don't have a model
		//
		Map<String, Object> resultModel = (result == null) ? Collections.EMPTY_MAP : result.getModel();

		if ( !Utils.isEmpty(redirView) )
		{
			getLogger().debug("Redirecting to redirView '" + redirView + "' with model " + model);
			return new ModelAndView("redirect:" + redirView, model).addAllObjects(resultModel).addAllObjects(model);
		}
		else if ( !Utils.isEmpty(redirToURL))
		{
			getLogger().debug("Redirecting to redirURL '" + redirToURL + "'");
			return new ModelAndView(new RedirectView(redirToURL)).addAllObjects(resultModel).addAllObjects(model);
		}
		else if ( !Utils.isEmpty(defaultRedir) )
		{
			getLogger().debug("Redirecting to default redirURL '" + defaultRedir + "'");
			return new ModelAndView("redirect:" + defaultRedir, model).addAllObjects(resultModel).addAllObjects(model);
		}
		else
		{
			// Back to the same page
			//
			return new ModelAndView().addAllObjects(initResponse(model, response, null)).addAllObjects(resultModel);
		}
	}

	/**
	 * Returns the key that can be used to look up what URL to redirect the user to
	 * from persistence storage. 
	 * 
	 * @return The redirection URL's persistence key.
	 */
	protected abstract String getRedirKey();

	public static <T extends RecordTypeDelegator<?>> Map<Integer, String> toSelectMapping(Collection<T> items, Stringifier<T> stringifier)
	{
		Map<Integer, String> retVal = new LinkedHashMap<>();

		items.stream().forEach((item) ->
		{
			retVal.put(item.getSer(), stringifier.toString(item));
		});

		return retVal;
	}

	public static <T extends RecordTypeDelegator<?>> Map<Integer, String> toSelectMapping(Collection<T> items, IStringifier<T> stringifier, String language)
	{
		Map<Integer, String> retVal = new LinkedHashMap<>();

		if ( !items.isEmpty() )
		{
			// We only add a default "unselected" item if there are items to select from.
			// This is done so the UI is able to decide whether to even display the select
			// element or not
			//
			retVal.put(0, " -- ");
		}

		retVal.putAll(toRadioButtonMapping(items, stringifier, language));

		return retVal;
	}

	public static <T extends RecordTypeDelegator<?>> Map<Integer, String> toRadioButtonMapping(Collection<T> items, IStringifier<T> stringifier, String language)
	{
		Map<Integer, String> retVal = new LinkedHashMap<>();

		items.stream().forEach((item) ->
		{
			retVal.put(item.getSer(), stringifier.toIString(item).get(language));
		});

		return retVal;
	}

	public MessageManager getMessageManager()
	{
		return messageManager;
	}

	@Autowired
	public void setMessageManager(MessageManager messageManager)
	{
		this.messageManager = messageManager;
	}

	public ModelManager getModelManager()
	{
		getLogger().debug(this + " Returning model manager " + modelManager);
		return modelManager;
	}

	@Autowired
	public void setModelManager(ModelManager modelManager)
	{
		this.modelManager = modelManager;
	}

	public RequestToViewNameTranslator getViewNameTranslator()
	{
		return viewNameTranslator;
	}

	@Autowired
	public void setViewNameTranslator(RequestToViewNameTranslator viewNameTranslator)
	{
		this.viewNameTranslator = viewNameTranslator;
	}

	public RequestState getRequestState()
	{
		return requestState;
	}

	@Autowired
	public void setRequestState(RequestState requestState)
	{
		this.requestState = requestState;
	}
}
