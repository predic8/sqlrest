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

import de.oio.sqlrest.db.Column;
import de.oio.sqlrest.db.DatabaseInfo;
import de.oio.sqlrest.db.DatabaseUtil;
import de.oio.sqlrest.db.Relation;
import de.oio.sqlrest.db.Row;
import de.oio.sqlrest.http.HttpUtil;
import de.oio.sqlrest.rest.RestUtil;
import de.oio.sqlrest.xml.xlink.HRefAttr;


/**
 * @author tbayer
 *
 */
public class TableDetailResponseBuilder extends AbstractResponseBuilder {

	/**
	 * Constructor for TableDetailResponseBuilder.
	 * @param aRequest
	 * @param aResponse
	 * @throws IOException
	 */
	public TableDetailResponseBuilder(HttpServletRequest aRequest, HttpServletResponse aResponse, DatabaseInfo databaseInfo)
		throws IOException {
		super(aRequest, aResponse, databaseInfo);
	}

	/**
	 * @see de.oio.sqlrest.xml.ResponseBuilder#generate()
	 */
	public void generate() throws Exception {
		
		start(getTableName());
		
		Iterator iter = DatabaseUtil.getTableDetails( getTableName(), getPrimaryKey()).iterator();
		
		checkIfThereAreMatches(iter);
		
		while (iter.hasNext()) {
			Row row = (Row) iter.next();
			
			Iterator columnIter = row.getColumns().iterator();
			
			while (columnIter.hasNext()) {
				Column column = (Column) columnIter.next();
				
				if ( databaseInfo.getTableInfo( getTableName()).isFkColumn(column.getName())) {
					
					serializer.startElement( "",
					column.getName(), column.getName(), 
					new HRefAttr(
						RestUtil.getRowUrl(HttpUtil.getServletUrl(request), getPkTableNameFromRelation(column), column.content)));
				} else  {
					serializer.startElement( column.getName(), null);
				}
				
				text( column.content);
				serializer.endElement( column.getName());	
			}
			
		}
			
		end(getTableName());
	}

	private String getPkTableNameFromRelation(Column column) {
		return ((Relation)databaseInfo.getTableInfo( getTableName()).getRelation(column.getName())).getPkTableName();
	}

	private void checkIfThereAreMatches(Iterator iter) throws IOException {
		if (!iter.hasNext()) {			
			response.sendError( HttpServletResponse.SC_NOT_FOUND, "Resource not found");
		}
	}

}
