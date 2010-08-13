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
package de.escidoc.core.test.common.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import de.escidoc.core.test.common.logger.AppLogger;

/**
 * @author MSC
 * 
 */
public class PropertiesProvider {

    private static final AppLogger LOG =
        new AppLogger(PropertiesProvider.class.getName());

    public static final String ESCIDOC_SERVER_NAME = "server.name";

    public static final String ESCIDOC_SERVER_PORT = "server.port";

    public static final String FEDORA_URL = "fedora.url";

    public static final String FEDORA_USER = "fedora.user";

    public static final String FEDORA_PASSWORD = "fedora.passwd";

    public static final String DIGILIB_SCALER_URL = "digilib.scaler";

    public static final String PERFORMANCE_DB_DRIVER_CLASSNAME =
        "escidoc.performance.db.driverClassName";

    public static final String PERFORMANCE_DB_URL =
        "escidoc.performance.db.url";

    public static final String PERFORMANCE_DB_USERNAME =
        "escidoc.performance.db.username";

    public static final String PERFORMANCE_DB_PASSWORD =
        "escidoc.performance.db.password";

    
    private Properties properties = null;

    private final List<String> files;

    private final String[] paths =
        { "./etc", "../etc", "../../etc",
            "/data/dev/escidoc/cruisecontrol/projects/eSciDocCoreTest/etc" };

    /**
     * @throws Exception
     *             Thrown if init of properties failed.
     */
    public PropertiesProvider() throws Exception {

        this.files = new LinkedList<String>();
        addFile("escidoc.properties");
        String currentUser = System.getProperties().getProperty("user.name");
        if (currentUser != null) {
            addFile(currentUser + ".properties");
        }
        addFile("test.properties");
        addFile("load-test.properties");
        init();
    }

    /**
     * Returns the property with the given name or null if property was not
     * found.
     * 
     * @param name
     *            The name of the Property.
     * @return Value of the given Property as String.
     * @common
     */
    public String getProperty(final String name) {

        return properties.getProperty(name);
    }

    /**
     * Returns the property with the given name or the second parameter as
     * default value if property was not found.
     * 
     * @param name
     *            The name of the Property.
     * @param defaultValue
     *            The default vaule if property isn't given.
     * @return Value of the given Property as String.
     * @common
     */
    public String getProperty(final String name, final String defaultValue) {

        return properties.getProperty(name, defaultValue);
    }

    /**
     * 
     * @throws Exception
     *             Thrown if init of properties failed.
     */
    public synchronized void init() throws Exception {

        Properties result = new Properties();
        Iterator<String> fileIter = files.iterator();
        while (fileIter.hasNext()) {
            String next = fileIter.next();
            try {
                Properties prop = loadProperties(next);
                result.putAll(prop);
            }
            catch (IOException e) {
                LOG.debug(e);
            }
        }
        this.properties = result;
    }

    /**
     * Loads the Properties from the possible files. First loads properties from
     * the file escidoc-core.properties.default. Afterwards tries to load
     * specific properties from the file escidoc.properties and merges them with
     * the default properties. If any key is included in default and specific
     * properties, the value of the specific property will overwrite the default
     * property.
     * 
     * @param file
     *            The name of the properties file.
     * @return The properties
     * @throws Exception
     *             If the loading of the default properties (file
     *             escidoc-core.properties.default) fails.
     * 
     * @common
     */
    private synchronized Properties loadProperties(final String file)
        throws Exception {
        Properties result = new Properties();

        boolean failed = true;
        int noOfPaths = paths.length;
        for (int i = 0; i < noOfPaths && failed; ++i) {
            try {
                InputStream fis =
                    ResourceProvider.getFileInputStreamFromFile(paths[i], file);
                result.load(fis);
                fis.close();
                failed = false;
            }
            catch (IOException e) {
                // ignore, try again
            }
        }
        if (failed) {
            throw new IOException("Error loading properties from file '" + file
                + "'!");
        }

        return result;
    }

    /**
     * Add a properties file to the list of properties.
     * 
     * @param name
     *            Name of properties file.
     */
    public synchronized void addFile(final String name) {

        this.files.add(name);
    }

}
