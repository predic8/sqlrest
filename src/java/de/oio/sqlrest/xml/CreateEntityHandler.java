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
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.oio.sqlrest.RESTRequestHandler;
import de.oio.sqlrest.db.DatabaseInfo;
import de.oio.sqlrest.db.RowDAO;
import de.oio.sqlrest.http.HttpUtil;
import de.oio.sqlrest.rest.RestUtil;

/**
 * @author tbayer
 *
 */
public class CreateEntityHandler extends AbstractResponseBuilder {

	static Logger logger = Logger.getLogger(CreateEntityHandler.class);
	
	/**
	 * Constructor for CreateEntityHandler.
	 * @param aRequest
	 * @param aResponse
	 * @param aDatabaseInfo
	 * @throws IOException
	 */
	public CreateEntityHandler(
		HttpServletRequest aRequest,
		HttpServletResponse aResponse,
		DatabaseInfo aDatabaseInfo)
		throws IOException {
		super(aRequest, aResponse, aDatabaseInfo);
	}

	/**
	 * @see de.oio.sqlrest.xml.ResponseBuilder#generate()
	 */
	public void generate() throws Exception {
		InputSource source = new InputSource(request.getInputStream());

		String primaryKey = null;

		try {
			primaryKey = getPrimaryKey();
		} catch (RuntimeException e1) {
			logger.info("No primary key is specified. Assuming primary key is provided in Message");
		}

		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();

			RESTRequestHandler handler = new RESTRequestHandler();
			parser.parse(source, handler);

			RowDAO.insert(
				getTableName(),
				handler.getValuePairs(),
				databaseInfo,
				primaryKey);

			response.setStatus(HttpServletResponse.SC_CREATED);

			try {
				
				if (getPrimaryKey() != null)
					response.addHeader(
						"Location",
						RestUtil.getRowUrl(
							HttpUtil.getServletUrl(request),
							getTableName(),
							getPrimaryKey()));
				else {
					response.addHeader(
						"Location",
						RestUtil.getTableUrl(
							HttpUtil.getServletUrl(request),
							getTableName())
							+ handler.getValuePairs().get(
								databaseInfo
									.getTableInfo(getTableName())
									.getPkColumnName()) + "/");
				}

				
			} catch (Exception e) {
				logger.warn("Error calculation location header URL" + e);				
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			response.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"Error in parser configuration");
		} catch (SAXException e) {
			logger.info("Parser error: " + e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (SQLException e) {
			logger.error("SQL error: " + e);
			response.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"SQL Error");
		} catch (Exception e) {
			logger.error("Error in create entity hander: " + e);
			throw e;
		}

	}

}
