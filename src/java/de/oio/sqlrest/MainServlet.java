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

package de.oio.sqlrest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import de.oio.sqlrest.db.DBConnection;
import de.oio.sqlrest.db.DatabaseAnalyser;
import de.oio.sqlrest.db.DatabaseInfo;

import de.oio.sqlrest.rest.RestUtil;

/**
 * @author tbayer
 *
 */
public class MainServlet extends HttpServlet {

	static Logger logger = Logger.getLogger(MainServlet.class);
	
	public DatabaseInfo databaseInfo;

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {		

		logRequest(request);
		
		try {
			Dispatcher.dispatchGET(request, response, databaseInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("Error: " + e);
		}

	}

	/**
	 * @param request
	 */
	private void logRequest(HttpServletRequest request) {
		logger.info("Request Info: Host: " + request.getRemoteHost() + "  Method: " +request.getMethod());
		logger.info("URL: " + request.getRequestURL());
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		logRequest(request);

		try {
			Dispatcher.dispatchDELETE(request, response, databaseInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("Error: " + e);
		}

	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		logRequest(request);

		try {
			Dispatcher.dispatchPOST(request, response, databaseInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("Error: " + e);
		}

	}

	/**
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		super.init();

/*		InputSource source =
			new InputSource(
				getServletContext().getRealPath("/WEB-INF/sqlrestconf.xml"));*/
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			RESTRequestHandler handler = new RESTRequestHandler();
			//parser.parse(source, handler);
			parser.parse(new File(getServletContext().getRealPath("/WEB-INF/sqlrestconf.xml")), handler);
			Map valuePairs = handler.getValuePairs();

			RestUtil.xslt = (String) valuePairs.get("xslt");
			RestUtil.grddl = (String) valuePairs.get("grddl");

			Class.forName((String) valuePairs.get("jdbc-driver-class"));
			System.out.println( "test:"+valuePairs );
			DBConnection.init(
				getDatabaserUrl(valuePairs),
				(String) valuePairs.get("user"),
				(String) valuePairs.get("password"));

			databaseInfo = DatabaseAnalyser.getDatabaseInfo();

		} catch (Exception e) {
			System.err.println("Error initializing sqlrest:"+e);
			e.printStackTrace();
		}
		

	}

	/**
	 * Checks if the database URL equals "jdbc:hsqldb:exampledb". In this case 
	 * get the real absolute path of the database. This is only used, wenn the
	 * internal example DB should be used.
	 * 
	 * @param valuePairs with the sqlrestconf name/value pairs
	 * @return database URL
	 */
	private String getDatabaserUrl(Map valuePairs) {
		String url = (String) valuePairs.get("database-url");

		if ("jdbc:hsqldb:exampledb"
			.equalsIgnoreCase((String) valuePairs.get("database-url"))) {
			url =
				"jdbc:hsqldb:"
					+ getServletContext().getRealPath("/WEB-INF/data/")
					+ File.separator
					+ "exampledb";
		}
		return url;
	}

	/**
	 * Method getPrimaryKey.
	 */
	private void getPrimaryKey() {
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		logRequest(request);

		try {
			Dispatcher.dispatchPUT(request, response, databaseInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("Error: " + e);
		}
	}

}
