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
package com.i4one.base.spring;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;

/**
 * This custom configurer appends our macro definitions to the engine before returning it
 * and also provides a helper method for initializing VelocityTools.
 *
 * @author Hamid Badiozamani
 */
public class VelocityConfigurer extends org.springframework.web.servlet.view.velocity.VelocityConfigurer
{
	public static final String I4ONE_MACRO_LIBRARIES = "/en/base/i4one.vm";

	@Override
	protected void postProcessVelocityEngine(VelocityEngine engine)
	{
		super.postProcessVelocityEngine(engine);

		engine.addProperty(VelocityEngine.VM_LIBRARY, I4ONE_MACRO_LIBRARIES);
		engine.addProperty("macro." + VelocityEngine.PROVIDE_SCOPE_CONTROL, true);
		engine.addProperty("evaluate." + VelocityEngine.PROVIDE_SCOPE_CONTROL, true);
		engine.addProperty("define." + VelocityEngine.PROVIDE_SCOPE_CONTROL, true);
	}

	private static Context toolContext = null;
	public static Context getVelocityToolContext()
	{
		if ( toolContext == null)
		{
			ToolManager velocityToolManager = new ToolManager(true, true);

			toolContext = new UnmodifiableContext(velocityToolManager.createContext());
		}

		/*
		Logger logger = LoggerFactory.getLogger(VelocityConfigurer.class);
		logger.debug("getVelocityToolContext returning " + toolContext);
		*/

       		return toolContext;
	}
}
