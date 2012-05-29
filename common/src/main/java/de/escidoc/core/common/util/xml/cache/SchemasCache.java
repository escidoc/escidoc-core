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
import java.util.ArrayList;

import javax.xml.validation.Schema;

import org.escidoc.core.util.xml.internal.EscidocSchemaHandler;
import org.springframework.stereotype.Service;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * Cache for xml {@code Schema} objects.<br> This cache is used to avoid multiple parsing of the same schema.
 *
 * @author Michael Hoppe
 */
@Service("common.xml.SchemasCache")
public class SchemasCache {

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected SchemasCache() {
    }

    /**
     * Gets the {@code Schema} from the cache.<br> If none exists for the provided schema URL, it is created and
     * put into the cache.
     *
     * @param schemaUri The schema URI
     * @return Returns the validator for the schema specified by the provided URL.
     * @throws IOException              Thrown in case of an I/O error.
     * @throws WebserverSystemException Thrown if schema can not be parsed.
     */
    @Cacheable(cacheName = "schemasCache", selfPopulating = true, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Schema getSchema(final String schemaUri) throws IOException, WebserverSystemException {

        return EscidocSchemaHandler.createSchema(new ArrayList<String>() {
            {
                add(schemaUri);
            }
        }, null);
    }

}
