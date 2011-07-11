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

package de.escidoc.core.common.util.service;

import java.io.Serializable;

/**
 * Class that contains one Key-Value-Pair. Used to convert a object that implements Map into KeyValuePair [].
 *
 * @author Roland Werner (Accenture)
 */
public class KeyValuePair implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private String value;

    /**
     * Standard Constructor.
     */
    public KeyValuePair() {
    }

    /**
     * Constructor. Generates a new object of type KeyValuePair.
     *
     * @param key   The key.
     * @param value The value.
     */
    public KeyValuePair(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return Returns the key.
     */
    public final String getKey() {
        return this.key;
    }

    /**
     * @param key The key to set.
     */
    public final void setKey(final String key) {
        this.key = key;
    }

    /**
     * @return Returns the value.
     */
    public final String getValue() {
        return this.value;
    }

    /**
     * @param value The value to set.
     */
    public final void setValue(final String value) {
        this.value = value;
    }
}
