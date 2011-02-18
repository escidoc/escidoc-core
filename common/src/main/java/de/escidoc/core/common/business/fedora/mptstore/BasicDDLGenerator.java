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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.fedora.mptstore;

import java.util.List;

/**
 * This class is the super class for the Postgres and MySQL implementations of the
 * DDL generator for the MPTStore. Both databases have limits when creating an
 * index on large column values so there has to be defined a maximum prefix length
 * for those indices.
 *
 * @author SCHE
 */
public class BasicDDLGenerator
    extends org.nsdl.mptstore.impl.postgres.PostgresDDLGenerator {
    /**
     * Maximum prefix length when creating a database index.
     */
    static final int INDEX_PREFIX_LENGTH = 2000;

    private final String[] users;

    private final String[] groups;

    /**
     * Constructor.
     */
    BasicDDLGenerator() {
        users = splitProperty("mptstore.postgres.autoGrantUsers");
        groups = splitProperty("mptstore.postgres.autoGrantGroups");
    }

    /**
     * Copied from superclass.
     */
    final void addSelectGrants(final List<String> cmds, final String table) {
        for (String name : users) {
            cmds.add("GRANT SELECT ON TABLE " + table + " TO " + name);
        }
        for (String name : groups) {
            cmds.add("GRANT SELECT ON TABLE " + table + " TO GROUP " + name);
        }
    }

    /**
     * Copied from superclass.
     */
    private static String[] splitProperty(final String name) {
        String val = System.getProperty(name);
        if (val == null || val.trim().length() == 0) {
            return new String[0];
        }
        else {
            return val.trim().split(" +");
        }
    }
}
