/*
 * Copyright (c) 2003, Orientation in Objects GmbH, www.oio.de
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of Orienation in Objects GmbH nor the names of its 
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * ${file_name} Created on ${date} by ${user}
 *  
 * ${todo}
 * 
 */

package de.oio.sqlrest.xml;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import de.oio.sqlrest.db.DatabaseInfo;
import de.oio.sqlrest.db.DatabaseUtil;
import de.oio.sqlrest.http.HttpUtil;
import de.oio.sqlrest.rest.RestUtil;
import de.oio.sqlrest.xml.xlink.HRefAttr;

/**
 * @author tbayer
 *
 */
public class TableListResponseBuilder extends AbstractResponseBuilder {
	/**
	 * Constructor TableListResponseBuilder.
	 * @param request
	 * @param response
	 */
	public TableListResponseBuilder(HttpServletRequest request, HttpServletResponse response, DatabaseInfo databaseInfo)
		throws IOException {
		super(request, response, databaseInfo);
	}

	/**
	 * @see de.oio.sqlrest.xml.RESTResponseBuilder#generateResponse(OutputStream)
	 */
	public void generate() throws Exception {

		try {

			start("resource");

			Iterator iter = DatabaseUtil.getTableNames().iterator();

			while (iter.hasNext()) {
				String tableName = (String) iter.next();			

				serializer.startElement(
					"",
					tableName+"List",
					tableName+"List",
					new HRefAttr(
						RestUtil.getTableUrl(HttpUtil.getServletUrl(request), tableName)));
				text(tableName);
				serializer.endElement("family");
			}

			end("resource");

		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

}
