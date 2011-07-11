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
package de.escidoc.core.common.util.xml.cache;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.SchemaBaseResourceResolver;

/**
 * Cache for xml {@code Schema} objects.<br> This cache is used to avoid multiple parsing of the same schema.
 *
 * @author Michael Hoppe
 */
@Service("common.xml.SchemasCache")
public class SchemasCache {

    /**
     * Gets the {@code Schema} from the cache.<br> If none exists for the provided schema URL, it is created and
     * put into the cache.
     *
     * @param schemaUri The schema URI
     * @return Returns the validator for the schema specified by the provided URL.
     * @throws IOException              Thrown in case of an I/O error.
     * @throws WebserverSystemException Thrown if schema can not be parsed.
     */
    @Cacheable(cacheName = "schemasCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Schema getSchema(final String schemaUri) throws IOException, WebserverSystemException {

        final URLConnection conn = new URL(schemaUri).openConnection();
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // set resource resolver to change schema-location-host
        sf.setResourceResolver(new SchemaBaseResourceResolver());

        final Schema schema;
        try {
            schema = sf.newSchema(new SAXSource(new InputSource(conn.getInputStream())));
        }
        catch (final SAXException e) {
            throw new WebserverSystemException("Problem with schema " + schemaUri + ". ", e);
        }
        return schema;
    }

}
