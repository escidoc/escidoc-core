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
package de.escidoc.core.common.util.logger;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;

import de.escidoc.core.common.util.string.StringUtility;

/**
 * Repository selector implementation for the eSciDoc base services.<br>
 * This implementation uses the context class loader of the current thread to
 * identify the logging context.<br>
 * As the default logger repository the repository that exists before creating
 * the eSciDoc repository selector is used.
 * 
 * @see RepositorySelector
 * @author TTE
 * @common
 * 
 */
public final class EscidocRepositorySelector implements RepositorySelector {

    private static Map<ClassLoader, LoggerRepository> map =
        new HashMap<ClassLoader, LoggerRepository>();

    private static RepositorySelector repositorySelector;

    private final LoggerRepository defaultRepository;

    /**
     * Constructor to prevent initialization.
     * 
     * @common
     */
    protected EscidocRepositorySelector() {

        super();
        defaultRepository = LogManager.getLoggerRepository();
        LogManager.setRepositorySelector(this, LogManager.getRootLogger());
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.<br>
     * This implementation takes the context class loader of the current thread
     * to identify the logging context. If a logger repository exists for this
     * class loader, it is returned. Otherwise, a new logger repository is
     * created, configured and stored for the class loader, if a log4j XML
     * configuration resource with path escidoc-core-log4j.xml exists. 
     * If resource does not exist, check if a resource WEB-INF/classes/log4j.xml
     * exists (interally located in the ear-file). If creating a new 
     * logger repository fails, the previous logger repository
     * that existed before initializing the eSciDoc repository selector is
     * returned as the default repository.
     * 
     * @return
     * @see org.apache.log4j.spi.RepositorySelector#getLoggerRepository()
     * @common
     */
    public LoggerRepository getLoggerRepository() {

        final ClassLoader contextClassLoader =
            Thread.currentThread().getContextClassLoader();
        LoggerRepository loggerRepository = map.get(contextClassLoader);
        if (loggerRepository == null) {
            InputStream log4jXmlConfigStream =
                contextClassLoader
                    .getResourceAsStream("escidoc-core-log4j.xml");
            if (log4jXmlConfigStream == null) {
                log4jXmlConfigStream =
                    contextClassLoader
                        .getResourceAsStream("WEB-INF/classes/log4j.xml");
            }
            if (log4jXmlConfigStream != null) {
                final LoggerRepository repository =
                    new Hierarchy(new RootLogger(Level.DEBUG));
                new DOMConfigurator().doConfigure(log4jXmlConfigStream,
                    repository);
                map.put(contextClassLoader, repository);
                return repository;
            }
            else {
                return defaultRepository;
            }
        }
        else {
            return loggerRepository;
        }
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Creates and stores the logger repository for the context class loader of
     * the current thread.<br>
     * The logger repository is configured by using the provided input stream
     * that must point to an log4j xml configuration.
     * 
     * @common
     */
    public static synchronized void init() {

        if (repositorySelector == null) {
            try {
                repositorySelector = new EscidocRepositorySelector();
            }
            catch (IllegalArgumentException e) {
                // This is a "Attempted to reset the LoggerFactory
                // without possessing the guard" error. It can happen
                // during a redeployment without restarting the server if the
                // eSciDoc repository selector has been previously initialized.
                AppLogger log =
                    new AppLogger(EscidocRepositorySelector.class.getName());
                StringBuffer msg =
                    StringUtility.concatenate(
                        "Initialization of the eSciDoc repository selector "
                            + "failed.",
                        "\nMaybe a previously initialized repository "
                            + "selector",
                        " exists that still can be used.\nIf a log4j "
                            + "configuration",
                        " has been modified, the selector is out of date.",
                        "\nThe server must be restarted to let the",
                        " modifications take effect!\n\n");
                log.warn(msg);
            }
        }
    }

    /**
     * Removes the logger repository for the context class loader of the current
     * thread.
     * 
     * @common
     */
    public static synchronized void remove() {

        map.remove(Thread.currentThread().getContextClassLoader());
    }

}
