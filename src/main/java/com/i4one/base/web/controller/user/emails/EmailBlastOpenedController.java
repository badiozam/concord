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
package com.i4one.base.web.controller.user.emails;

import com.i4one.base.core.Utils;
import com.i4one.base.model.emailblast.EmailBlast;
import com.i4one.base.model.user.User;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hamid Badiozamani
 */
@Controller
public class EmailBlastOpenedController extends BaseUserEmailBlastController
{

	@RequestMapping(value ="**/base/user/emails/{opened}.pngg", produces="image/png")
	public void emailOpened(@PathVariable("opened") String opened, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String[] openedValues = Utils.forceEmptyStr(opened).split("-", 2);

		if ( openedValues.length >= 2 )
		{
			int id = Utils.defaultIfNaN(openedValues[0], 0);
			int userid = Utils.defaultIfNaN(openedValues[1], 0);

			EmailBlast emailBlast = new EmailBlast();
			emailBlast.setSer(id);

			User user = new User();
			user.setSer(userid);

			recordEmailResponse(user, emailBlast);
		}

		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
		ImageIO.write(image, "png", response.getOutputStream());
	}
}
