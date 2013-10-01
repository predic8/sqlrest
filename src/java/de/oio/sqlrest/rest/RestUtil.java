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

package de.oio.sqlrest.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author tbayer
 *
 */
public class RestUtil {

	/**
	 * Method getTableUrl.
	 * @param request
	 * @param tableName
	 * @return String
	 */
	public static String getTableUrl( String servletURL, String tableName) throws UnsupportedEncodingException {
		return servletURL + "/" + URLEncoder.encode(tableName, "UTF-8") + "/";
	}
	
	/**
	 * Method getRowUrl.
	 * @param request
	 * @param tableName
	 * @param id
	 * @return String
	 */
	public static String getRowUrl( String servletURL, String tableName, String id) throws UnsupportedEncodingException {
		return getTableUrl( servletURL, tableName) + URLEncoder.encode(id, "UTF-8") + "/";
	}
	
	public static void startResponse(XMLSerializer xmlSerializer, String resourceName) throws SAXException {
		xmlSerializer.startDocument();
		
		AttributesImpl attr = new AttributesImpl();
		attr.addAttribute("", "xlink", "xmlns:xlink", "NOTATION", "http://www.w3.org/1999/xlink");

		if (xslt != null) {
			/* add processing instruction linking to xslt */
			xmlSerializer.processingInstruction("xml-stylesheet", "type='text/xsl' href='".concat(xslt).concat("'"));
		}

		xmlSerializer.startPrefixMapping("xlink", "http://www.w3.org/1999/xlink");
		if (grddl != null) {
			/* add GRDDL link if needed */
			xmlSerializer.startPrefixMapping("grddl", "http://www.w3.org/2003/g/data-view#");
			attr.addAttribute("", "transformation", "grddl:transformation", "CDATA", grddl);
		}
		
		xmlSerializer.startElement("", resourceName, resourceName, attr);
	}

	/**
	 * URI of XSLT transformation
	 */
	 public static String xslt = null;
	
	/**
	 * URI of GRDDL transformation
	 */
	public static String grddl = null;
	
}
