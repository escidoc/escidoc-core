/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.db;

import java.util.TreeSet;

/**
 * This class represents a database table containing a list of all its column
 * names and indexes.
 * 
 * @author SCHE
 */
public class Table implements Comparable<Object> {
    private String name = null;

    private TreeSet<String> columns = null;

    private TreeSet<String> foreignKeys = null;

    private TreeSet<String> indexes = null;

    private TreeSet<String> primaryKeys = null;

    /**
     * Constructor for bean deserialization.
     */
    public Table() {
    }

    /**
     * Create a new Table object.
     * 
     * @param name
     *            table name
     * @param columns
     *            list of table columns
     * @param indexes
     *            list of table indexes
     * @param primaryKeys
     *            primary keys defined for the table
     * @param foreignKeys
     *            foreign keys defined for the table
     */
    public Table(final String name, final String[] columns,
        final String[] indexes, final String[] primaryKeys,
        final String[] foreignKeys) {
        setName(name);
        setColumns(columns);
        setForeignKeys(foreignKeys);
        setIndexes(indexes);
        setPrimaryKeys(primaryKeys);
    }

    /**
     * Compares this object with the specified object for order. Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * 
     * @param o
     *            the Object to be compared.
     * 
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     */
    public int compareTo(final Object o) {
        return name.compareTo(((Table) o).getName());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj
     *            the reference object with which to compare.
     * 
     * @return true if this object is the same as the obj argument; false
     *         otherwise.
     */
    public boolean equals(final Object obj) {
        return name.equals(((Table) obj).getName());
    }

    /**
     * Get the list of column names.
     * 
     * @return column names
     */
    public TreeSet<String> getColumns() {
        return columns;
    }

    /**
     * Get the list of foreign keys.
     * 
     * @return foreign keys
     */
    public TreeSet<String> getForeignKeys() {
        return foreignKeys;
    }

    /**
     * Get the list of table indexes.
     * 
     * @return table indexes
     */
    public TreeSet<String> getIndexes() {
        return indexes;
    }

    /**
     * Get the table name.
     * 
     * @return table name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the list of primary keys.
     * 
     * @return primary keys
     */
    public TreeSet<String> getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * Returns a hash code value for the object. This method is supported for
     * the benefit of hashtables such as those provided by java.util.Hashtable.
     * 
     * @return a hash code value for this object.
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Set the list of column names.
     * 
     * @param columns
     *            column names
     */
    public void setColumns(final String[] columns) {
        this.columns = new TreeSet<String>();
        for (String column : columns) {
            this.columns.add(column);
        }
    }

    /**
     * Set the list of column names.
     * 
     * @param columns
     *            column names
     */
    public void setColumns(final TreeSet<String> columns) {
        this.columns = columns;
    }

    /**
     * Set the list of foreign keys.
     * 
     * @param foreignKeys
     *            foreign keys
     */
    public void setForeignKeys(final String[] foreignKeys) {
        this.foreignKeys = new TreeSet<String>();
        for (String foreignKey : foreignKeys) {
            this.foreignKeys.add(foreignKey);
        }
    }

    /**
     * Set the list of foreign keys.
     * 
     * @param foreignKeys
     *            foreign keys
     */
    public void setForeignKeys(final TreeSet<String> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    /**
     * Set the list of table indexes.
     * 
     * @param indexes
     *            table indexes
     */
    public void setIndexes(final String[] indexes) {
//        this.indexes = new TreeSet<String>();
//        if (indexes != null) {
//            for (String index : indexes) {
//                this.indexes.add(index);
//            }
//        }
    }

    /**
     * Set the list of table indexes.
     * 
     * @param indexes
     *            table indexes
     */
    public void setIndexes(final TreeSet<String> indexes) {
//        this.indexes = indexes;
    }

    /**
     * Set the table name.
     * 
     * @param name
     *            table name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Set the list of primary keys.
     * 
     * @param primaryKeys
     *            primary keys
     */
    public void setPrimaryKeys(final String[] primaryKeys) {
        this.primaryKeys = new TreeSet<String>();
        for (String primaryKey : primaryKeys) {
            this.primaryKeys.add(primaryKey);
        }
    }

    /**
     * Set the list of primary keys.
     * 
     * @param primaryKeys
     *            primary keys
     */
    public void setPrimaryKeys(final TreeSet<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }
}
