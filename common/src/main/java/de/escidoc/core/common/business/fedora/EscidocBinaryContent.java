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

import de.escidoc.core.common.util.xml.factory.FoXmlProvider;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Class encapsulating binary content.
 * 
 * @common
 */
public class EscidocBinaryContent {

    private final HttpGet getMethod = null;

    @Deprecated
    private InputStream content;
    
    private String fileName;

    private String mimeType;

    private String redirectUrl = null;

    @Deprecated
    private HttpURLConnection conn = null;

    /**
     * @return the redirectUrl
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * @param redirectUrl
     *            the redirectUrl to set
     */
    public void setRedirectUrl(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    /**
     * @return the content
     * @throws IOException
     */
    public InputStream getContent() throws IOException, NullPointerException {
        if (getMethod == null) {
            if (this.content != null) {
                return this.content;
            }
            throw new NullPointerException(
                "GetMethod not set or already released.");
        }
        return this.content;
    }

    

    /**
     * @deprecated A GetMethod should be set. getContent will acquire the
     *             InputStream from that GetMethod.
     * 
     * @param content
     *            the content to set
     */
    @Deprecated
    public void setContent(final InputStream content) {
        this.content = content;
    }

  
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {

        if ((this.mimeType == null) && (this.getMethod != null)) {
            Header ctype = this.getMethod.getFirstHeader("Content-Type");
            if (ctype != null) {
                this.mimeType = ctype.getValue();
            }
            else {
                this.mimeType =
                    FoXmlProvider.MIME_TYPE_APPLICATION_OCTET_STREAM;
            }
        }
        return this.mimeType;
    }

    /**
     * @param mimeType
     *            the mimeType to set
     */
    public void setMimeType(final String mimeType) {
        if (mimeType != null) {
            this.mimeType = mimeType.trim();
        }
        else {
            this.mimeType = null;
        }
    }

    /**
     * 
     * @return The HtpURLconnection.
     */
    @Deprecated
    public HttpURLConnection getConnection() {
        return this.conn;
    }

    /**
     * Set the HttpConnection.
     * 
     * @param connection
     *            The HttpURLConnection to the content.
     */
    @Deprecated
    public void setConnection(final HttpURLConnection connection) {
        this.conn = connection;
    }

  
  
    
}
