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

package de.escidoc.core.common.business.fedora.resources.create;

import de.escidoc.core.common.business.fedora.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * BinaryContent of Component, Content Stream.
 *
 * @author Steffen Wagner
 */
public class BinaryContent {

    /**
     * External Storage URL or External Storage Managed.
     */
    private StorageType storageType;

    private String content;

    private URL dataLocation;

    /**
     * Set the type of storage.
     *
     * @param storageType Type of Storage.
     */
    public void setStorageType(final StorageType storageType) {

        this.storageType = storageType;
    }

    /**
     * Set the storage type via String. (Try to avoid String for storage type.)
     *
     * @param storageType StorageType
     * @throws InvalidContentException Thrown if StorageType is not supported.
     */
    public void setStorageType(final String storageType) throws InvalidContentException {

        if (storageType != null) {
            if (storageType.equals(Constants.STORAGE_EXTERNAL_URL)) {
                this.storageType = StorageType.EXTERNAL_URL;
            }
            else if (storageType.equals(Constants.STORAGE_EXTERNAL_MANAGED)) {
                this.storageType = StorageType.EXTERNAL_MANAGED;
            }
            else if (storageType.equals(Constants.STORAGE_INTERNAL_MANAGED)) {
                this.storageType = StorageType.INTERNAL_MANAGED;
            }
            else {
                throw new InvalidContentException("The component section 'content' with the attribute "
                    + "'storage' set to 'external-url' or " + "'external-managed' may not have an inline content.");
            }
        }
    }

    /**
     * Get type of storage.
     *
     * @return Type of Storage (within Fedora).
     */
    public StorageType getStorageType() {

        return this.storageType;
    }

    /**
     * Set content (payload).
     *
     * @param content Binary Content (payload).
     */
    public void setContent(final String content) {

        this.content = content;
    }

    /**
     * Get Binary Content payload.
     *
     * @return payload of binary content.
     */
    public String getContent() {

        return this.content;
    }

    /**
     * Set location of content via URL.
     *
     * @param url URL of bianry content.
     */
    public void setDataLocation(final URL url) {

        this.dataLocation = url;
    }

    /**
     * Set location of content via String.
     *
     * @param url URL of data location
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    public void setDataLocation(final String url) throws WebserverSystemException {

        try {
            this.dataLocation =
                url == null ? null : url.startsWith("/") ? new URL(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                    + url) : new URL(url);
        }
        catch (final MalformedURLException e) {
            throw new WebserverSystemException("Invalid URL.", e);
        }
        catch (final IOException e) {
            throw new WebserverSystemException("URL not accessible.", e);
        }
    }

    /**
     * Get URL of binary data.
     *
     * @return URL of data location
     */
    public URL getDataLocation() {

        return this.dataLocation;
    }

}
