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
package de.escidoc.core.test.st;

//CHECKSTYLE:OFF Generated Java-File.

/**
 * @hibernate.class table="staging_file"
 * @hibernate.mapping schema="st"
 *
 */
public class StagingFile {

    /**
     * identifier field
     */
    private String token;

    /**
     * persistent field
     */
    private long expiryTs;

    /**
     * nullable persistent field
     */
    private String reference;

    /**
     * nullable persistent field
     */
    private String mimeType;

    /**
     * persistent field
     */
    private boolean upload;

    /**
     * full constructor
     */
    public StagingFile(long expiryTs, String reference, String mimeType, boolean upload) {
        this.expiryTs = expiryTs;
        this.reference = reference;
        this.mimeType = mimeType;
        this.upload = upload;
    }

    /**
     * default constructor
     */
    public StagingFile() {
    }

    /**
     * minimal constructor
     */
    public StagingFile(long expiryTs, boolean upload) {
        this.expiryTs = expiryTs;
        this.upload = upload;
    }

    /**
     * @hibernate.id generator-class="de.escidoc.core.st.business.persistence.hibernate.TokenGenerator"
     *               type="java.lang.String" column="token"
     *
     */
    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @hibernate.property column="expiry_ts" length="8" not-null="true"
     *
     */
    public long getExpiryTs() {
        return this.expiryTs;
    }

    public void setExpiryTs(long expiryTs) {
        this.expiryTs = expiryTs;
    }

    /**
     * @hibernate.property column="reference" length="-1"
     *
     */
    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @hibernate.property column="mime_type" length="255"
     *
     */
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @hibernate.property column="upload" length="1" not-null="true"
     *
     */
    public boolean isUpload() {
        return this.upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

}
