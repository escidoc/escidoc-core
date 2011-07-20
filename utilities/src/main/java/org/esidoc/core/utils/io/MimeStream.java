/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
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
package org.esidoc.core.utils.io;

/**
 * @author jhe
 *
 */
public class MimeStream {
    
    private Stream stream;
    
    private String mimeType;

    /**
     * @param stream
     * @param mimet
     */
    public MimeStream(final Stream stream, final String mimeType) {
        super();
        this.stream = stream;
        this.mimeType = mimeType;
    }

    /**
     * @return the stream
     */
    public Stream getStream() {
        return stream;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + ((stream == null) ? 0 : stream.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MimeStream other = (MimeStream) obj;
        if (mimeType == null) {
            if (other.mimeType != null)
                return false;
        }
        else if (!mimeType.equals(other.mimeType))
            return false;
        if (stream == null) {
            if (other.stream != null)
                return false;
        }
        else if (!stream.equals(other.stream))
            return false;
        return true;
    };     

}
