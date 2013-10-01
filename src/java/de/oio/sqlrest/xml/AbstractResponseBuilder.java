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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.oio.sqlrest.db.DatabaseInfo;
import de.oio.sqlrest.rest.RestUtil;

/**
 * @author tbayer
 *
 */
public abstract class AbstractResponseBuilder implements ResponseBuilder {

	HttpServletRequest request;
	HttpServletResponse response;
	ServletOutputStream out;
	DatabaseInfo databaseInfo;

	XMLSerializer serializer;
	
	String[] pathInfo;

	/**
	 * Constructor for AbstractResponseBuilder.
	 */
	public AbstractResponseBuilder(HttpServletRequest aRequest, HttpServletResponse aResponse, DatabaseInfo aDatabaseInfo)
		throws IOException {
		request = aRequest;
		response = aResponse;
		out = aResponse.getOutputStream();

		databaseInfo = aDatabaseInfo;
		serializer = XMLSerializerFactory.getInstance(out);


		if (request.getPathInfo() != null ) {
			pathInfo = request.getPathInfo().split("\\/");
		} 
	}

	public abstract void generate() throws Exception;

	/**
	 * @see de.oio.sqlrest.xml.ResponseBuilder#start()
	 */
	public void start(String resourceName) throws SAXException {
		
		response.setContentType("application/xml");
		
		RestUtil.startResponse(serializer, resourceName);		
	}

	public void end(String resourceName) throws SAXException {
		serializer.endElement(resourceName);
		serializer.endPrefixMapping("xlink");
		serializer.endDocument();
	}

	public void text(String str) throws SAXException {
		if ( null == str) 
			return;
		serializer.characters(str.toCharArray(), 0, str.length());
	}
	
	public String getTableName() {		
		return pathInfo[1];
	}
	
	public String getPrimaryKey() {
		if (pathInfo.length < 3)
			return null;
		return pathInfo[2];
	}
	
}
