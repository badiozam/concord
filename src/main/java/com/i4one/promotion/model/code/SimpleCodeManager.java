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
package com.i4one.promotion.model.code;

import com.i4one.base.core.Utils;
import com.i4one.base.model.ReturnType;
import com.i4one.base.model.client.SingleClient;
import com.i4one.base.model.manager.terminable.BaseSimpleTerminableSiteGroupTypeManager;
import com.i4one.base.model.manager.terminable.TerminablePagination;
import com.i4one.base.model.message.Message;
import com.i4one.base.model.message.MessageManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hamid Badiozamani
 */
public class SimpleCodeManager extends BaseSimpleTerminableSiteGroupTypeManager<CodeRecord, Code> implements CodeManager
{
	private MessageManager messageManager;

	private static final String NAMESINGLE_KEY = "promotion.codeManager.nameSingle";
	private static final String NAMEPLURAL_KEY = "promotion.codeManager.namePlural";
	private static final String DEFAULTOUTRO_KEY = "promotion.codeManager.defaultOutro";
	private static final String INSTRUCTIONS_KEY = "promotion.codeManager.instructions";


	@Override
	public Set<Code> getLiveCodes(String code, TerminablePagination pagination)
	{
		return convertDelegates(getDao().getLiveCodes(code, pagination));
	}

	@Override
	public Code cloneInternal(Code item) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		return super.cloneInternal(item);
	}

	@Override
	public List<ReturnType<Code>> importCSV(InputStream stream, Supplier<Code> instantiator, Function<Code,Boolean> preprocessor, Consumer<ReturnType<Code>> postprocessor)
	{
		return super.importCSVInternal(stream, instantiator, preprocessor, postprocessor);
	}

	@Override
	public OutputStream exportCSV(Set<Code> items, OutputStream out)
	{
		return exportCSVInternal(items, out);
	}

	@Override
	public CodeSettings getSettings(SingleClient client)
	{
		CodeSettings retVal = new CodeSettings();
		retVal.setClient(client);

		boolean enabled = Utils.defaultIfNaB(getClientOptionManager().getOption(client, getEnabledOptionKey()).getValue(), true);
		retVal.setEnabled(enabled);

		List<Message> singularNames = getMessageManager().getAllMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = getMessageManager().getAllMessages(client, NAMEPLURAL_KEY);
		retVal.setNames(singularNames, pluralNames);

		List<Message> instructions = getMessageManager().getAllMessages(client, INSTRUCTIONS_KEY);
		List<Message> outros = getMessageManager().getAllMessages(client, DEFAULTOUTRO_KEY);

		retVal.setInstructions(instructions);
		retVal.setOutro(outros);

		return retVal;
	}
	
	@Transactional(readOnly = false)
	@Override
	public ReturnType<CodeSettings> updateSettings(CodeSettings settings)
	{
		ReturnType<CodeSettings> retVal = new ReturnType<>(settings);

		SingleClient client = settings.getClient();
		retVal.setPre(getSettings(client));

		// XXX: Consolidate this into a single list and create a new method in MessageManager to update
		// a batch of messages so that the historical records are chained together.

		List<Message> singularNames = settings.getNameSingleMessages(client, NAMESINGLE_KEY);
		List<Message> pluralNames = settings.getNamePluralMessages(client, NAMEPLURAL_KEY);

		List<Message> instructions = settings.getInstructions(client, INSTRUCTIONS_KEY);
		List<Message> outros = settings.getDefaultOutroMessages(client, DEFAULTOUTRO_KEY);

		singularNames.forEach( (message) -> { getMessageManager().update(message); } );
		pluralNames.forEach( (message) -> { getMessageManager().update(message); } );

		instructions.forEach( (message) -> { getMessageManager().update(message); } );
		outros.forEach( (message) -> { getMessageManager().update(message); } );

		updateOption(client, getEnabledOptionKey(), String.valueOf(settings.isEnabled()), retVal);

		return retVal;
	}

	@Override
	public CodeRecordDao getDao()
	{
		return (CodeRecordDao) super.getDao();
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

	@Override
	public Code emptyInstance()
	{
		return new Code();
	}
}
