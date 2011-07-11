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

//CHECKSTYLE:OFF
package de.escidoc.core.common.business.fedora;

import java.io.Serializable;

public class Property implements Serializable {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 2923670166779578404L;

    public Property() {
        this.__equalsCalc = null;
        this.__hashCodeCalc = false;
    }

    public Property(final String name, final String value) {
        this.__equalsCalc = null;
        this.__hashCodeCalc = false;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof Property)) {
            return false;
        }
        final Property other = (Property) obj;
        if (this == obj) {
            return true;
        }
        if (this.__equalsCalc != null) {
            return this.__equalsCalc == obj;
        }
        else {
            this.__equalsCalc = obj;
            final boolean _equals =
                (this.name == null && other.getName() == null || this.name != null && name.equals(other.getName()))
                    && (this.value == null && other.getValue() == null || this.value != null
                        && value.equals(other.getValue()));
            this.__equalsCalc = null;
            return _equals;
        }
    }

    public int hashCode() {
        if (this.__hashCodeCalc) {
            return 0;
        }
        this.__hashCodeCalc = true;
        int _hashCode = 1;
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        this.__hashCodeCalc = false;
        return _hashCode;
    }

    static Class _mthclass$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (final ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.toString()); // Ignore FindBugs
        }
    }

    private String name;

    private String value;

    private transient Object __equalsCalc;

    private transient boolean __hashCodeCalc;
}
//CHECKSTYLE:ON
