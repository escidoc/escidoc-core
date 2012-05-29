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

package org.escidoc.core.util.xml.internal;

import java.io.IOException;
import java.net.URL;

import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.util.XMLCatalogResolver;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Helper class to change the base-url of imported schemas.
 *
 * @author Michael Hoppe
 */
public class SchemaBaseResourceResolver implements LSResourceResolver {

    private XMLCatalogResolver catalogResolver;
    
    public SchemaBaseResourceResolver(XMLCatalogResolver catalogResolver, String[] catalogList) {
        this.catalogResolver = catalogResolver;
        String[] cList = new String[catalogList.length];
        for (int i = 0; i < catalogList.length; i++) {
            URL xmlCatalogUrl = SchemaBaseResourceResolver.class.getClassLoader().getResource(catalogList[i]);
            cList[i] = xmlCatalogUrl.toExternalForm();
        }
        this.catalogResolver.setCatalogList(cList);
    }
    
    /**
     * Replaces base-part of system-id.
     *
     * @param type         String
     * @param namespaceURI String1
     * @param publicId     String2
     * @param systemId     String3
     * @param baseURI      String4
     * @return LSInput LSInput.
     */
    @Override
    public LSInput resolveResource(
        final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
        if (systemId != null) {
            String systemIdLocal;
            try {
                systemIdLocal = catalogResolver.resolveSystem(systemId);
            }
            catch (IOException e) {
                return null;
            }
            return new DOMInputImpl(publicId, systemIdLocal, baseURI);
        }
        else {
            return null;
        }
    }
    
}
