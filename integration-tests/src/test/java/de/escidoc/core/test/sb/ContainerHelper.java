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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.sb;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.om.container.ContainerTestBase;

/**
 * Call the Container-Service.
 *
 * @author Michael Hoppe
 */
public class ContainerHelper extends ContainerTestBase {

    /**
     * @param xml The container as xml.
     * @return String container-xml
     * @throws Exception e
     */
    @Override
    public String create(final String xml) throws Exception {
        return super.create(xml);
    }

    /**
     * @param id The container-id.
     * @return String container-xml
     * @throws Exception e
     */
    @Override
    public String retrieve(final String id) throws Exception {
        return super.retrieve(id);
    }

    /**
     * @param id The container-id.
     * @throws Exception e
     */
    @Override
    public void delete(final String id) throws Exception {
        super.delete(id);
    }

    /**
     * @param id          The container-id.
     * @param lastModDate The last-modification-date.
     * @return last-modification-date within a XML structure refering result.xsd
     * @throws Exception e
     */
    @Override
    public String submit(final String id, final String lastModDate) throws Exception {
        return super.submit(id, lastModDate);
    }

    /**
     * @param id          The Container-id.
     * @param lastModDate The last-modification-date.
     * @return XML result structure with at least last-modification-date
     * @throws Exception e
     */
    public String revise(final String id, final String lastModDate) throws Exception {
        return super.revise(id, lastModDate);
    }

    /**
     * @param id          The container-id.
     * @param lastModDate The last-modification-date.
     * @return last-modification-date within a XML structure refering result.xsd
     * @throws Exception e
     */
    @Override
    public String release(final String id, final String lastModDate) throws Exception {
        return super.release(id, lastModDate);
    }

    /**
     * @param id          The container-id.
     * @param lastModDate The last-modification-date.
     * @return last-modification-date within a XML structure refering result.xsd
     * @throws Exception e
     */
    @Override
    public String withdraw(final String id, final String lastModDate) throws Exception {
        return super.withdraw(id, lastModDate);
    }

    /**
     * @param containerId The container-id.
     * @param xml         The item-xml.
     * @return last-modification-date within a XML structure refering result.xsd
     * @throws Exception e
     */
    @Override
    public String createItem(final String containerId, final String xml) throws Exception {
        return super.createItem(containerId, xml);
    }

    /**
     * @param id The container-id.
     * @return Returns the assigned version pid in an XML structure.
     * @throws Exception e
     */
    public String assignVersionPid(final String id) throws Exception {

        String pidParam = null;
        try {
            pidParam =
                "<param last-modification-date=\""
                    + getLastModificationDateValue(EscidocAbstractTest.getDocument(retrieve(id))) + "\" >"
                    + "<url>http://escidoc.de</url>" + "</param>";
        }
        catch (final RuntimeException e) {
            EscidocAbstractTest.failException("Failed to retrieve last modification date of item " + id, e);
        }
        return super.assignVersionPid(id, pidParam);
    }

    /**
     * @param xmlName String name of xml file
     * @return String path to container-xmls.
     * @throws Exception e
     */
    public String getTemplateAsString(final String xmlName) throws Exception {
        return EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH, xmlName);
    }

}
