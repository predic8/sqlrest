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

package de.oio.sqlrest.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * @author tbayer
 *
 */
public class DatabaseAnalyser {

	static Logger logger = Logger.getLogger(DatabaseAnalyser.class);
	
	/**
	 * Method getDatabaseInfo.
	 */
	public static DatabaseInfo getDatabaseInfo() {

		DatabaseInfo databaseInfo = new DatabaseInfo();

		Iterator tablesIterator = DatabaseUtil.getTableNames().iterator();

		while (tablesIterator.hasNext()) {
			String tableName = (String) tablesIterator.next();
			
			logger.debug("Anaylser TableName: " + tableName);
			
			TableInfo tableInfo = new TableInfo(tableName);

			retrivePrimaryKey(tableName, tableInfo);
			retriveRelations(tableName, tableInfo);
			
			retriveColumns(tableName, tableInfo);
			
			Column column = tableInfo.getColumn( tableInfo.getPkColumnName());
			column.setPrimaryKey( true);
			
			databaseInfo.add(tableInfo);
		}

		return databaseInfo;
	}

	public static void retriveColumns(String tableName, TableInfo tableInfo) {
		
		Collection columns = DatabaseUtil.getColumnsMetaData( tableName);
		
		Iterator columsIter = columns.iterator();
		
		while (columsIter.hasNext()) {
			Column column = (Column) columsIter.next();			
			
			tableInfo.add( column);
		}
	}

	public static void retrivePrimaryKey(String tableName, TableInfo tableInfo) {
		tableInfo.setPkColumnName(DatabaseUtil.getPrimaryKeyColumnName(tableName));

		tableInfo.setPkColumnType(
			DatabaseUtil.getColumnType(tableName, tableInfo.getPkColumnName()));
	}

	private static void retriveRelations(String tableName, TableInfo tableInfo) {
		try {
			Connection connection = DBConnection.getInstance();

			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet resultSet = metaData.getImportedKeys(null, null, tableName);

			while (resultSet.next()) {
				for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
					tableInfo.add(
						new Relation(
							resultSet.getString("PKTABLE_NAME"),
							resultSet.getString("PKCOLUMN_NAME"),							
							resultSet.getString("FKTABLE_NAME"),
							resultSet.getString("FKCOLUMN_NAME"),
							resultSet.getString("FK_NAME")));

				}
			}

		} catch (SQLException e) {
			System.err.println(e);
			throw new IllegalArgumentException("Wrong tableName : " + tableName);
		}
	}

}
