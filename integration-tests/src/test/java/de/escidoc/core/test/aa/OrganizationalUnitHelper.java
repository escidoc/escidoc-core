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
package de.escidoc.core.test.aa;

import de.escidoc.core.test.oum.organizationalunit.OrganizationalUnitTestBase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Call the Item-Service.
 *
 * @author Michael Hoppe
 */
public class OrganizationalUnitHelper extends OrganizationalUnitTestBase {

    /**
     * Creates and insert the parent ous element at the specified position in the provided document. This element
     * contains references to the specified organizational units.
     *
     * @param document         The document for that the element shall be created.
     * @param xpathBefore      The xpath after that the parent-ous element shall be inserted in the document.
     * @param parentValues     The ids and titles of the parent organizational units, e.g. [id1, id2, title1, title2]
     * @param withRestReadOnly Flag indicating if the parent-ous element shall contain the REST specific read only
     *                         attributes.
     * @return Returns the created <code>Element</code> object.
     * @throws Exception Thrown if anything fails.
     */
    public Node insertParentsElement(
        final Document document, final String xpathBefore, final String[] parentValues, final boolean withRestReadOnly)
        throws Exception {
        return super.insertParentsElement(document, xpathBefore, parentValues, withRestReadOnly);
    }

    /**
     * Gets the task param containing the last modification date of the specified object.
     *
     * @param includeComment Flag indicating if the comment shall be additionally included.
     * @param id             The id of the object.
     * @return Returns the created task param xml.
     * @throws Exception Thrown if anything fails.
     */
    public String getTheLastModificationParam(final boolean includeComment, final String id) throws Exception {
        return super.getTheLastModificationParam(includeComment, id);
    }

}
