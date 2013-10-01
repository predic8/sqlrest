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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.oio.sqlrest.db.DatabaseInfo;
import de.oio.sqlrest.xml.CreateEntityHandler;
import de.oio.sqlrest.xml.ModifyEntityHandler;
import de.oio.sqlrest.xml.ResponseBuilder;
import de.oio.sqlrest.xml.RowDeleteResponseBuilder;
import de.oio.sqlrest.xml.TableDetailResponseBuilder;
import de.oio.sqlrest.xml.TableListResponseBuilder;
import de.oio.sqlrest.xml.TableResponseBuilder;

/**
 * @author tbayer
 *
 */
public class Dispatcher {
	
	static Logger logger = Logger.getLogger(Dispatcher.class);

	public static void dispatchGET(
		HttpServletRequest request,
		HttpServletResponse response,
		DatabaseInfo databaseInfo)
		throws IOException, Exception {

		ResponseBuilder responseBuilder = null;

		if (isServletRootRequested(request)) {
			responseBuilder = new TableListResponseBuilder(request, response, databaseInfo);
		} else if (isTableRequested(request)) {
			responseBuilder = new TableResponseBuilder(request, response, databaseInfo);
		} else {
			responseBuilder = new TableDetailResponseBuilder(request, response, databaseInfo);
		}

		responseBuilder.generate();
	}

	/**
	 * Method isTableRequested.
	 * @param request
	 * @return boolean
	 */
	private static boolean isTableRequested(HttpServletRequest request) {
		return request.getPathInfo().split("\\/").length == 2;
	}

	public static boolean isServletRootRequested(HttpServletRequest request) {
		return request.getPathInfo() == null
			|| request.getPathInfo().length() == 0
			|| "/".equals(request.getPathInfo());
	}

	/**
	 * Method dispatchDELETE.
	 * @param request
	 * @param response
	 * @param databaseInfo
	 */
	public static void dispatchDELETE(
		HttpServletRequest request,
		HttpServletResponse response,
		DatabaseInfo databaseInfo)
		throws Exception {

		ResponseBuilder responseBuilder = null;

		if (isServletRootRequested(request)) {
			//		responseBuilder = new DatabaseDeleteResponseBuilder( request, response, databaseInfo);	
		} else if (isTableRequested(request)) {
			//		responseBuilder = new TableResponseBuilder( request, response, databaseInfo);
		} else {
			responseBuilder = new RowDeleteResponseBuilder(request, response, databaseInfo);
		}

		try {
			responseBuilder.generate();
		} catch (Exception e) {
			System.err.println("Can't delete " + e);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Resource could not be deleted");
		}
	}

	/**
	 * Method dispatchPOST.
	 * @param request
	 * @param response
	 * @param databaseInfo
	 */
	public static void dispatchPOST(
		HttpServletRequest request,
		HttpServletResponse response,
		DatabaseInfo databaseInfo)
		throws Exception {

		ResponseBuilder responseBuilder = null;

		if (isServletRootRequested(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Resource could not be created");
		} else if (isTableRequested(request)) {
			responseBuilder = new CreateEntityHandler( request, response, databaseInfo);
		} else {
			responseBuilder = new ModifyEntityHandler( request, response, databaseInfo);
		}

		try {
			responseBuilder.generate();
		} catch (Exception e) {
			System.err.println("Can't create process POST request " + e);
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Resource could not be created");
		}

	}

	/**
	 * Method dispatchPUT.
	 * @param request
	 * @param response
	 * @param databaseInfo
	 */
	public static void dispatchPUT(
		HttpServletRequest request,
		HttpServletResponse response,
		DatabaseInfo databaseInfo) throws Exception {
			
		ResponseBuilder responseBuilder = null;

		if (isServletRootRequested(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Resource could not be created");
		} else if (isTableRequested(request)) {			
			responseBuilder = new CreateEntityHandler( request, response, databaseInfo);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Resource could not be created");
		} else {
			responseBuilder = new CreateEntityHandler( request, response, databaseInfo);			
		}

		try {
			logger.debug("Builder: " + responseBuilder);
			responseBuilder.generate();
		} catch (Exception e) {
			System.err.println("Can't process PUT request " + e);
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Resource could not be created");
		}			
			
		}

}
