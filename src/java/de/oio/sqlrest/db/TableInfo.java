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

import java.util.Hashtable;
import java.util.Set;

/**
 * @author tbayer
 *
 */
public class TableInfo {

	private String tableName;
	private String pkColumnName;
	private int pkColumnType;
	private Hashtable relations = new Hashtable();

	private Hashtable columns = new Hashtable();

	public TableInfo(String aTableName) {
		tableName = aTableName;
	}

	/**
	 * Method add.
	 * @param relation
	 */
	public void add(Relation relation) {
		relations.put(relation.getFkColumnName(), relation);
	}

	public Relation getRelation( String fkColumnName) {
		return (Relation) relations.get( fkColumnName);
	}

	/**
	 * Method getTableName.
	 * @return Object
	 */
	public String getTableName() {
		return tableName;
	}

	public boolean isFkColumn(String columnName) {

		return null != relations.get(columnName);
	}
	/**
	 * Returns the pkColumnType.
	 * @return String
	 */
	public int getPkColumnType() {
		return pkColumnType;
	}

	/**
	 * Sets the pkColumnType.
	 * @param pkColumnType The pkColumnType to set
	 */
	public void setPkColumnType(int pkColumnType) {
		this.pkColumnType = pkColumnType;
	}

	/**
	 * Returns the pkColumnName.
	 * @return String
	 */
	public String getPkColumnName() {
		return pkColumnName;
	}

	/**
	 * Sets the pkColumnName.
	 * @param pkColumnName The pkColumnName to set
	 */
	public void setPkColumnName(String pkColumnName) {
		this.pkColumnName = pkColumnName;
	}

	public void add( Column column) {
		columns.put( column.getName(), column);
	}	

	public Set getColumnNames() {
		return columns.keySet();
	}
	
	public boolean hasColumn( String name) {
		return columns.containsKey( name);
	}
	
	public Column getColumn( String columnName) {
		return (Column) columns.get( columnName);
	}
}
