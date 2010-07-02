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
package de.escidoc.core.common.business.fedora.resources.create;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.business.fedora.Constants;

/**
 * BinaryContent of Component, Content Stream.
 * 
 * @author SWA
 * 
 */
public class BinaryContent {

    private static final AppLogger LOG =
        new AppLogger(BinaryContent.class.getName());

    /**
     * External Storage URL or External Storage Managed.
     */
    private StorageType storageType = null;

    private String content = null;

    private URL dataLocation = null;

    /**
     * Set the type of storage.
     * 
     * @param storageType
     *            Type of Storage.
     */
    public void setStorageType(final StorageType storageType) {

        this.storageType = storageType;
    }

    /**
     * Set the storage type via String. (Try to avoid String for storage type.)
     * 
     * @param storageType
     *            StorageType
     * @throws InvalidContentException
     *             Thrown if StorageType is not supported.
     */
    public void setStorageType(final String storageType)
        throws InvalidContentException {

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
                final String message =
                    "The component section 'content' with the attribute "
                        + "'storage' set to 'external-url' or "
                        + "'external-managed' may not have an inline content.";
                LOG.debug(message);
                throw new InvalidContentException(message);
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
     * @param content
     *            Binary Content (payload).
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
     * @param url
     *            URL of bianry content.
     */
    public void setDataLocation(final URL url) {

        this.dataLocation = url;
    }

    /**
     * Set location of content via String.
     * 
     * @param url
     *            URL of data location
     * @throws WebserverSystemException
     * @throws IOException
     *             Thrown if obtaining eSciDoc base url failed.
     */
    public void setDataLocation(final String url)
        throws WebserverSystemException {

        try {
            if (url == null) {
                this.dataLocation = null;
            }
            else {
                if (url.startsWith("/")) {
                    this.dataLocation =
                        new URL(EscidocConfiguration.getInstance().get(
                            EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                            + url);
                }
                else {
                    this.dataLocation = new URL(url);
                }
            }
        }
        catch (MalformedURLException e) {
            throw new WebserverSystemException("Invalid URL.", e);
        }
        catch (IOException e) {
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
