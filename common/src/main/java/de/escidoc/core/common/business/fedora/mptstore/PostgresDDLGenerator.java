/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.business.fedora.mptstore;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the Postgres implementation of the DDL generator for the MPTStore which
 * sets a maximum prefix length for indices.
 *
 * @author SCHE
 */
public class PostgresDDLGenerator extends BasicDDLGenerator {
    /*
     * (non-Javadoc)
     * 
     * @see org.nsdl.mptstore.impl.postgres.PostgresDDLGenerator#getCreateMapTableDDL(java.lang.String)
     */
    @Override
    public List<String> getCreateMapTableDDL(final String table) {

        final List<String> cmds = new ArrayList<String>();

        cmds.add("CREATE TABLE " + table + " (\n"
               + "  pKey SERIAL,\n"
               + "  p TEXT NOT NULL\n"
               + ')');
        cmds.add("CREATE INDEX " + table + "_pKey "
               + " on " + table + " (pKey)");
        cmds.add("CREATE INDEX " + table + "_p "
               + " on " + table + " (substring(p from 1 for "
               + INDEX_PREFIX_LENGTH + "))");
        addSelectGrants(cmds, table);

        return cmds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nsdl.mptstore.impl.postgres.PostgresDDLGenerator#getCreateSOTableDDL(java.lang.String)
     */
    @Override
    public List<String> getCreateSOTableDDL(final String table) {

        final List<String> cmds = new ArrayList<String>();

        cmds.add("CREATE TABLE " + table + " (\n"
               + "  s TEXT NOT NULL,\n"
               + "  o TEXT NOT NULL\n"
               + ')');
        cmds.add("CREATE INDEX " + table + "_s "
               + " on " + table + " (substring(s from 1 for "
               + INDEX_PREFIX_LENGTH + "))");
        cmds.add("CREATE INDEX " + table + "_o "
               + " on " + table + " (substring(o from 1 for "
               + INDEX_PREFIX_LENGTH + "))");
        addSelectGrants(cmds, table);

        return cmds;
    }
}
