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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.WSDLConstants;
import org.apache.cxf.jaxrs.utils.schemas.SchemaHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSResourceResolver;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * Helper class to change the base-url of imported schemas.
 *
 * @author Michael Hoppe
 */
public class EscidocSchemaHandler extends SchemaHandler {

    /**
     * The LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EscidocSchemaHandler.class);

    private static LSResourceResolver resourceResolver = null;

    private Schema schema = null;

    private Bus bus;

    private List<String> schemaLocations;
    
    public EscidocSchemaHandler() throws WebserverSystemException {
        schemaLocations = new ArrayList(SchemaUtility.SCHEMA_LOCATIONS.values());
    }

    public void setBus(Bus b) {
        bus = b;
    }

    public void setResourceResolver(LSResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    public void setSchemas(List<String> locations) {
    }

    public Schema getSchema() {
        if (schema == null) {
            schema = createSchema(schemaLocations, bus == null ? BusFactory.getThreadDefaultBus() : bus);
        }
        return schema;
    }

    public static Schema createSchema(List<String> locations, Bus bus) {
        SchemaFactory factory = SchemaFactory.newInstance(WSDLConstants.NS_SCHEMA_XSD);
        //set resource resolver to change schema-location-host
        factory.setResourceResolver(resourceResolver);

        Schema s = null;
        try {
            List<Source> sources = new ArrayList<Source>();
            for (String loc : locations) {
                Reader r = null;
                if (loc.startsWith("http")) {
                    URL url = new URL(loc);
                    try {
                        final URLConnection conn = url.openConnection();
                        r = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    }
                    catch (IOException e) {
                        LOGGER.warn("Schema not found : " + loc + "\n" + e.getMessage());
                        continue;
                    }
                }
                else {
                    InputStream in = EscidocSchemaHandler.class.getClassLoader().getResourceAsStream(loc);
                    if (in == null) {
                        LOGGER.warn("Schema not found : " + loc + "\n");
                        continue;
                    }
                    r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                }
                StreamSource source = new StreamSource(r);
                source.setSystemId(loc);
                sources.add(source);
            }
            s = factory.newSchema(sources.toArray(new Source[] {}));
        }
        catch (Exception ex) {
            LOGGER.warn("Validation will be disabled, failed to create schema : " + ex.getMessage());
        }
        return s;

    }
}
