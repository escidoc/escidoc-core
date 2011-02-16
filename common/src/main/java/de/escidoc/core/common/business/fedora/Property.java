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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
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
        __equalsCalc = null;
        __hashCodeCalc = false;
    }

    public Property(String name, String value) {
        __equalsCalc = null;
        __hashCodeCalc = false;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Property)) {
            return false;
        }
        Property other = (Property) obj;
        if (this == obj) {
            return true;
        }
        if (__equalsCalc != null) {
            return __equalsCalc == obj;
        }
        else {
            __equalsCalc = obj;
            boolean _equals =
                (name == null && other.getName() == null || name != null
                    && name.equals(other.getName()))
                    && (value == null && other.getValue() == null || value != null
                        && value.equals(other.getValue()));
            __equalsCalc = null;
            return _equals;
        }
    }

    public int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    static Class _mthclass$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.toString());
        }
    }

    private String name;

    private String value;

    private Object __equalsCalc;

    private boolean __hashCodeCalc;
}
//CHECKSTYLE:ON
