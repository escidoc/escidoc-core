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
        equalsCalc = null;
        hashCodeCalc = false;
    }

    public MIMETypedStream(String MIMEType, byte stream[], Property header[]) {
        equalsCalc = null;
        hashCodeCalc = false;
        this.mimeType = MIMEType;
        this.stream = stream;
        this.header = header;
    }

    public String getMIMEType() {
        return mimeType;
    }

    public void setMIMEType(final String MIMEType) {
        this.mimeType = MIMEType;
    }

    public byte[] getStream() {
        return stream;
    }

    public void setStream(final byte stream[]) {
        this.stream = stream;
    }

    Property[] getHeader() {
        return header;
    }

    public void setHeader(final Property header[]) {
        this.header = header;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof MIMETypedStream)) {
            return false;
        }
        MIMETypedStream other = (MIMETypedStream) obj;
        if (this == obj) {
            return true;
        }
        if (equalsCalc != null) {
            return equalsCalc == obj;
        }
        else {
            equalsCalc = obj;
            boolean _equals = (mimeType == null && other.getMIMEType() == null || mimeType != null
                && mimeType.equals(other.getMIMEType()))
                && (stream == null && other.getStream() == null || stream != null
                    && Arrays.equals(stream, other.getStream()))
                && (header == null && other.getHeader() == null || header != null
                    && Arrays.equals(header, other.getHeader()));
            equalsCalc = null;
            return _equals;
        }
    }

    public int hashCode() {
        if (hashCodeCalc) {
            return 0;
        }
        hashCodeCalc = true;
        int _hashCode = 1;
        if (getMIMEType() != null) {
            _hashCode += getMIMEType().hashCode();
        }
        if (getStream() != null) {
            for (int i = 0; i < Array.getLength(getStream()); i++) {
                Object obj = Array.get(getStream(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getHeader() != null) {
            for (int i = 0; i < Array.getLength(getHeader()); i++) {
                Object obj = Array.get(getHeader(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        hashCodeCalc = false;
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

    private String mimeType;

    private byte stream[];

    private Property header[];

    private Object equalsCalc;

    private boolean hashCodeCalc;
}
