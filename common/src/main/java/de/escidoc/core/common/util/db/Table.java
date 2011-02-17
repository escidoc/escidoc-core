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

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents a database table containing a list of all its column
 * names and indexes.
 * 
 * @author SCHE
 */
public class Table implements Comparable<Object> {
    private String name = null;

    private Set<String> columns = null;

    private Set<String> foreignKeys = null;

    private final Set<String> indexes = null;

    private Set<String> primaryKeys = null;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Table table = (Table) o;

        return !(name != null ? !name.equals(table.name) : table.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    /**
     * Get the list of column names.
     * 
     * @return column names
     */
    public Set<String> getColumns() {
        return columns;
    }

    /**
     * Get the list of foreign keys.
     * 
     * @return foreign keys
     */
    public Set<String> getForeignKeys() {
        return foreignKeys;
    }

    /**
     * Get the list of table indexes.
     * 
     * @return table indexes
     */
    public Set<String> getIndexes() {
        return indexes;
    }

    /**
     * Get the table name.
     * 
     * @return table name
     */
    String getName() {
        return name;
    }

    /**
     * Get the list of primary keys.
     * 
     * @return primary keys
     */
    public Set<String> getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * Set the list of column names.
     * 
     * @param columns
     *            column names
     */
    void setColumns(final String[] columns) {
        this.columns = new TreeSet<String>();
        this.columns.addAll(Arrays.asList(columns));
    }

    /**
     * Set the list of column names.
     * 
     * @param columns
     *            column names
     */
    public void setColumns(final Set<String> columns) {
        this.columns = columns;
    }

    /**
     * Set the list of foreign keys.
     * 
     * @param foreignKeys
     *            foreign keys
     */
    void setForeignKeys(final String[] foreignKeys) {
        this.foreignKeys = new TreeSet<String>();
        this.foreignKeys.addAll(Arrays.asList(foreignKeys));
    }

    /**
     * Set the list of foreign keys.
     * 
     * @param foreignKeys
     *            foreign keys
     */
    public void setForeignKeys(final Set<String> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    /**
     * Set the list of table indexes.
     * 
     * @param indexes
     *            table indexes
     */
    void setIndexes(final String[] indexes) {
        // FIXME: reactivate after 1.3 release
        // this.indexes = new Set<String>();
        // if (indexes != null) {
        // for (String index : indexes) {
        // this.indexes.add(index);
        // }
        // }
    }

    /**
     * Set the list of table indexes.
     * 
     * @param indexes
     *            table indexes
     */
    public void setIndexes(final Set<String> indexes) {
        // FIXME: reactivate after 1.3 release
        // this.indexes = indexes;
    }

    /**
     * Set the table name.
     * 
     * @param name
     *            table name
     */
    void setName(final String name) {
        this.name = name;
    }

    /**
     * Set the list of primary keys.
     * 
     * @param primaryKeys
     *            primary keys
     */
    void setPrimaryKeys(final String[] primaryKeys) {
        this.primaryKeys = new TreeSet<String>();
        this.primaryKeys.addAll(Arrays.asList(primaryKeys));
    }

    /**
     * Set the list of primary keys.
     * 
     * @param primaryKeys
     *            primary keys
     */
    public void setPrimaryKeys(final Set<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }
}
