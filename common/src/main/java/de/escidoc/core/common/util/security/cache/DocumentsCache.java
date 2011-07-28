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
package de.escidoc.core.common.util.security.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;

import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Cache for xml {@code Document} objects.<br> This cache is used to avoid multiple parsing of the same document.
 * It provides an method to retrieve a document with creation of not found documents.
 *
 * @author Michael Hoppe
 */
@Service("security.DocumentsCache")
public class DocumentsCache {

    /**
     * Private constructor to prevent initialization.
     */
    protected DocumentsCache() {
    }

    /**
     * Retrieves the document for the provided document data.
     *
     * @param documentData The object to get the xml document for, or {@code null} in case of an error.
     * @return Returns the xml {@code Document} object representing the provided xml data.
     * @throws IOException                  Thrown in case of an i/o error.
     * @throws ParserConfigurationException Thrown in case of an error in parser configuration
     * @throws SAXException                 Thrown in case of a parse error
     */
    @Cacheable(cacheName = "documentsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Document retrieveDocument(final Object documentData) throws IOException, ParserConfigurationException,
        SAXException {
        final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(((String) documentData).getBytes(XmlUtility.CHARACTER_ENCODING)));
    }
}
