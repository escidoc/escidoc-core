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

import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents a database schema containing a list of all its table
 * names.
 *
 * @author SCHE
 */
public class Schema {
    private String name = null;
    private Set<Table> tables = null;

    /**
     * Constructor for bean deserialization.
     */
    public Schema() {
    }

    /**
     * Create a new Schema object.
     *
     * @param name schema name
     * @param tables list of database tables
     */
    public Schema(final String name, final Table[] tables) {
        setName(name);
        setTables(tables);
    }

    /**
     * Get the schema name.
     *
     * @return schema name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the list of table names.
     *
     * @return table names
     */
    public Set<Table> getTables() {
        return tables;
    }

    /**
     * Set the schema name.
     *
     * @param name schema name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Set the list of table names.
     *
     * @param tables table names
     */
    public void setTables(final Table[] tables) {
        this.tables = new TreeSet<Table>();
        for (Table table : tables) {
          this.tables.add(table);
        }
    }

    /**
     * Set the list of table names.
     *
     * @param tables table names
     */
    public void setTables(final Set<Table> tables) {
        this.tables = tables;
    }
}
