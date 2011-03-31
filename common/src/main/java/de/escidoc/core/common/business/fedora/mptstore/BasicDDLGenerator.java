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

import org.nsdl.mptstore.impl.postgres.PostgresDDLGenerator;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * This class is the super class for the Postgres and MySQL implementations of the DDL generator for the MPTStore. Both
 * databases have limits when creating an index on large column values so there has to be defined a maximum prefix
 * length for those indices.
 *
 * @author André Schenk
 */
public class BasicDDLGenerator extends PostgresDDLGenerator {

    private static final Pattern SPLIT_PATTERN = Pattern.compile(" +");

    /**
     * Maximum prefix length when creating a database index.
     */
    protected static final int INDEX_PREFIX_LENGTH = 2000;

    private final String[] users;

    private final String[] groups;

    /**
     * Constructor.
     */
    public BasicDDLGenerator() {
        this.users = splitProperty("mptstore.postgres.autoGrantUsers");
        this.groups = splitProperty("mptstore.postgres.autoGrantGroups");
    }

    /**
     * Copied from superclass.
     */
    protected void addSelectGrants(final Collection<String> cmds, final String table) {
        for (final String name : this.users) {
            cmds.add("GRANT SELECT ON TABLE " + table + " TO " + name);
        }
        for (final String name : this.groups) {
            cmds.add("GRANT SELECT ON TABLE " + table + " TO GROUP " + name);
        }
    }

    /**
     * Copied from superclass.
     */
    protected static String[] splitProperty(final String name) {
        final String val = System.getProperty(name);
        return val == null || val.trim().length() == 0 ? new String[0] : SPLIT_PATTERN.split(val.trim());
    }
}
