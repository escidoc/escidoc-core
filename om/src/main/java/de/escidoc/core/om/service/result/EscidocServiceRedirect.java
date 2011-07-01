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
package de.escidoc.core.om.service.result;

import de.escidoc.core.common.business.interfaces.EscidocServiceRedirectInterface;

/**
 * Class encapsulating service redirection.
 */
public class EscidocServiceRedirect implements EscidocServiceRedirectInterface {

    private String content;

    private String redirectUrl;

    /* (non-Javadoc)
     * @see de.escidoc.core.om.service.result.EscidocServiceRedirectInterface#getRedirectUrl()
     */
    @Override
    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.om.service.result.EscidocServiceRedirectInterface#setRedirectUrl(java.lang.String)
     */
    @Override
    public void setRedirectUrl(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.om.service.result.EscidocServiceRedirectInterface#getContent()
     */
    @Override
    public String getContent() {
        return this.content;
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.om.service.result.EscidocServiceRedirectInterface#setContent(java.lang.String)
     */
    @Override
    public void setContent(final String content) {
        this.content = content;
    }
}
