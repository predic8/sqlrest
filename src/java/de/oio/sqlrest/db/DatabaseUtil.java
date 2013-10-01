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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * @author tbayer
 *
 */
public class DatabaseUtil {

	static Logger logger = Logger.getLogger(DatabaseUtil.class);
	
	/**
	 * A Catalog is a logical database in a dbms. The method returns a list of catalognames.
	 * 
	 * 
	 * @return Collection containing Strings with the names of the database Catalogs.
	 */
	public static Collection getCatalogs() {

		ArrayList catalogs = new ArrayList();

		try {

			Connection connection = DBConnection.getInstance();

			ResultSet resultSet = connection.getMetaData().getCatalogs();

			while (resultSet.next()) {
				catalogs.add(resultSet.getString("TABLE_CAT"));
			}

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}

		return catalogs;

	}

	public static Collection getTableNames() {

		ArrayList tables = new ArrayList();

		try {

			Connection connection = DBConnection.getInstance();

			logger.debug("GetTableNames ! Start");

			ResultSet resultSet = connection.getMetaData().getTables(null, null, null, new String[] { "TABLE" });

			logger.debug("GetTableNames !");

			while (resultSet.next()) {

				logger.debug("GetTableNames ! inside");

				tables.add(resultSet.getString("TABLE_NAME"));
			}

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}

		return tables;

	}

	/**
	 * Method getTable.
	 * @param tableName
	 */
	public static Collection getPrimaryKeyValuesFromTable(String tableName) {

		ArrayList ids = new ArrayList();

		try {

			Connection connection = DBConnection.getInstance();

			ResultSet resultSet = connection.prepareStatement(getPrimaryKeyValuesSelectStatement(tableName)).executeQuery();

			String pkColumnName = getPrimaryKeyColumnName(tableName);

			while (resultSet.next()) {
				ids.add(resultSet.getString(pkColumnName));
			}

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}

		return ids;

	}

	private static String getPrimaryKeyValuesSelectStatement(String tableName) {
		return "select * from " + tableName;
	}

	/**
	 * Method getTableDetails.
	 * @param string
	 * @param i
	 */
	public static ArrayList getTableDetails(String tableName, String id) {
		ArrayList ids = new ArrayList();

		try {

			Connection connection = DBConnection.getInstance();

			PreparedStatement statement =
				connection.prepareStatement(getTableDetailsStatement(tableName, id));

			ResultSet resultSet = statement.executeQuery();

			ResultSetMetaData rsMetaData = resultSet.getMetaData();
			int columnCount = resultSet.getMetaData().getColumnCount();

			while (resultSet.next()) {
				Row row = new Row();
				String erg = "";
				for (int i = 1; i <= columnCount; i++) {
					row.add(new Column(rsMetaData.getColumnName(i), resultSet.getString(i)));
				}
				ids.add(row);
			}

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}

		return ids;

	}

	private static String getTableDetailsStatement(String tableName, String id) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("select * from ");
		queryString.append(tableName);
		queryString.append(" where ");
		queryString.append(getPrimaryKeyColumnName(tableName));
		queryString.append(" = ");

		int type = getColumnType(tableName, getPrimaryKeyColumnName(tableName));

		queryString.append(getSQLParameterSubstring(id, type));

		return queryString.toString();
	}

	public static String getSQLParameterSubstring(String value, int type) {

		if (isNumericType(type)) {
			return value;
		} else {
			return "'" + value + "'";
		}
	}

	public static boolean isNumericType(int type) {
		return Types.BIGINT == type
			|| Types.DECIMAL == type
			|| Types.INTEGER == type
			|| Types.NUMERIC == type
			|| Types.SMALLINT == type
			|| Types.TINYINT == type;
	}

	public static String getPrimaryKeyColumnName(String tableName) {

		try {
			Connection connection = DBConnection.getInstance();

			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet resultSet = metaData.getPrimaryKeys(null, null, tableName);

			resultSet.next();

			return resultSet.getString("COLUMN_NAME");
		} catch (SQLException e) {
			throw new IllegalArgumentException("Wrong tablename : " + tableName);
		}
	}

	/**
	 * Method getColumnType.
	 * @param tableName
	 * @param string
	 * @return String
	 */
	public static int getColumnType(String tableName, String columnName) {
		try {
			Connection connection = DBConnection.getInstance();
			
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet resultSet = metaData.getColumns(null, null, tableName, columnName);

			resultSet.next();

			return resultSet.getInt("DATA_TYPE");
		} catch (SQLException e) {
			throw new IllegalArgumentException(
				"Wrong tablename or columnName: " + tableName + ", " + columnName);
		}

	}

	/**
	 * Method deleteRow.
	 * @param string
	 * @param string1
	 */
	public static void deleteRow(String tableName, String primaryKey) throws SQLException, NoRowsAffectedException {

		Connection connection = DBConnection.getInstance();

		PreparedStatement statement =
			connection.prepareStatement(getRowDeleteStatement(tableName, primaryKey));

		if(!statement.execute())
		{
			if(statement.getUpdateCount()==0){
				connection.close();
				throw new NoRowsAffectedException();
				
			}
		}
		connection.close();
	}

	

	/**
	 * Method getRowDeleteStatement.
	 * @param tableName
	 * @param primaryKey
	 * @return String
	 */
	private static String getRowDeleteStatement(String tableName, String primaryKey) {
		return "delete from "
			+ tableName
			+ " where "
			+ getPrimaryKeyColumnName(tableName)
			+ " = "
			+ getSQLParameterSubstring(
				primaryKey,
				DatabaseUtil.getColumnType(tableName, getPrimaryKeyColumnName(tableName)));
	}
	/**
	 * Method getColumnsMetaData.
	 * @param tableName
	 * @return Collection
	 */
	public static Collection getColumnsMetaData(String tableName) {
		
		ArrayList columns = new ArrayList();

		try {

			Connection connection = DBConnection.getInstance();

			DatabaseMetaData dbMetaData = connection.getMetaData();
			
			ResultSet rs = dbMetaData.getColumns( null, null, tableName, null);

			while( rs.next()) {				
				columns.add( new Column( rs.getString("COLUMN_NAME"), rs.getInt("DATA_TYPE"), rs.getInt("COLUMN_SIZE")));				
			}

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}

		return columns;		
		
	}

}
