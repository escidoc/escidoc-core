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

package de.escidoc.core.common.business.fedora;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

public class MIMETypedStream implements Serializable {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -7845383715052874329L;

    public MIMETypedStream() {
        this.equalsCalc = null;
        this.hashCodeCalc = false;
    }

    public MIMETypedStream(final String mimeType, final byte[] stream, final Property[] header) {
        this.equalsCalc = null;
        this.hashCodeCalc = false;
        this.mimeType = mimeType;
        this.stream = stream;
        this.header = header;
    }

    public String getMIMEType() {
        return this.mimeType;
    }

    public void setMIMEType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getStream() {
        return this.stream;
    }

    public void setStream(final byte[] stream) {
        this.stream = stream;
    }

    public Property[] getHeader() {
        return this.header;
    }

    public void setHeader(final Property[] header) {
        this.header = header;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof MIMETypedStream)) {
            return false;
        }
        final MIMETypedStream other = (MIMETypedStream) obj;
        if (this == obj) {
            return true;
        }
        if (this.equalsCalc != null) {
            return this.equalsCalc == obj;
        }
        else {
            this.equalsCalc = obj;
            final boolean equals =
                (this.mimeType == null && other.getMIMEType() == null || this.mimeType != null
                    && mimeType.equals(other.getMIMEType()))
                    && (this.stream == null && other.getStream() == null || this.stream != null
                        && Arrays.equals(this.stream, other.getStream()))
                    && (this.header == null && other.getHeader() == null || this.header != null
                        && Arrays.equals(this.header, other.getHeader()));
            this.equalsCalc = null;
            return equals;
        }
    }

    public int hashCode() {
        if (this.hashCodeCalc) {
            return 0;
        }
        this.hashCodeCalc = true;
        int hashCode = 1;
        if (getMIMEType() != null) {
            hashCode += getMIMEType().hashCode();
        }
        if (getStream() != null) {
            for (int i = 0; i < Array.getLength(getStream()); i++) {
                final Object obj = Array.get(getStream(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    hashCode += obj.hashCode();
                }
            }
        }
        if (getHeader() != null) {
            for (int i = 0; i < Array.getLength(getHeader()); i++) {
                final Object obj = Array.get(getHeader(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    hashCode += obj.hashCode();
                }
            }
        }
        this.hashCodeCalc = false;
        return hashCode;
    }

    static Class _mthclass$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (final ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.toString()); // Ignore FindBugs
        }
    }

    private String mimeType;

    private byte[] stream;

    private Property[] header;

    private Object equalsCalc;

    private boolean hashCodeCalc;
}
