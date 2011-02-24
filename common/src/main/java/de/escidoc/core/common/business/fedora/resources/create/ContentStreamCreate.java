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

/**
 * ContentStream for create method.
 * 
 * Attention! This is only a helper class for the transition to integrate this
 * functionality into the ContentStream class.
 * 
 * 
 * @author SWA
 * 
 */
public class ContentStreamCreate {

    // private static final AppLogger LOG =
    // new AppLogger(ContentStreamCreate.class.getName());

    private BinaryContent content = null;

    private String name = null;

    private String mimeType = null;

    private String title = null;

    /**
     * Add content to ContentStream.
     * 
     * @param content
     *            New content of ContentStream
     */
    public final void setContent(final BinaryContent content) {

        this.content = content;
    }

    /**
     * Get content of ContentStream.
     * 
     * @return Content of ContentStream
     */
    public final BinaryContent getContent() {

        return this.content;
    }

    /**
     * @param name
     *            the name to set
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param mimeType
     *            the mimeType to set
     */
    public final void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the mimeType
     */
    public final String getMimeType() {
        return mimeType;
    }

    /**
     * @param title
     *            the title to set
     */
    public final void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return the title
     */
    public final String getTitle() {
        return title;
    }

}
