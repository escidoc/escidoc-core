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
package de.escidoc.core.test.sb;

import de.escidoc.core.test.om.context.ContextTestBase;

/**
 * Call the Context-Service.
 *
 * @author Michael Hoppe
 */
public class ContextHelper extends ContextTestBase {

    /**
     * @param xml The item as xml.
     * @return String item-xml
     * @throws Exception e
     */
    public String create(final String xml) throws Exception {
        return super.create(xml);
    }

    /**
     * @param id The item-id.
     * @return String item-xml
     * @throws Exception e
     */
    public String retrieve(final String id) throws Exception {
        return super.retrieve(id);
    }

    /**
     * @param id The item-id.
     * @throws Exception e
     */
    public void delete(final String id) throws Exception {
        super.delete(id);
    }

    /**
     * @param id          The context-id.
     * @param lastModDate The last-modification-date.
     * @return XML result structure with at least last-modification-date
     * @throws Exception e
     */
    public String open(final String id, final String lastModDate) throws Exception {
        return super.open(id, lastModDate);
    }

    /**
     * @param id          The context-id.
     * @param lastModDate The last-modification-date.
     * @return XML result structure with at least last-modification-date
     * @throws Exception e
     */
    public String close(final String id, final String lastModDate) throws Exception {
        return super.close(id, lastModDate);
    }

}
