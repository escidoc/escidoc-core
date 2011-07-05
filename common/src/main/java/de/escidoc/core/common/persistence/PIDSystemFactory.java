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

package de.escidoc.core.common.persistence;

import de.escidoc.core.common.exceptions.system.PidSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Factory for PID Generator and Management Systems.
 *
 * @author Steffen Wagner
 */
public abstract class PIDSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PIDSystemFactory.class);

    private static final String DEFAULT_FACTORY = "de.escidoc.core.common.persistence.impl.DummyPIDGeneratorFactory";

    private static PIDSystemFactory pidSystemFactory;

    static {
        try {
            createNewInstanceFromConfig();
        }
        catch (final PidSystemException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on creating new instance of PIDSystemFactory.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on creating new instance of PIDSystemFactory.", e);
            }
        }
    }

    /**
     * Protected constructor as getInstance() should be used to create an instance of an PIDSystemFactory.
     */
    protected PIDSystemFactory() {
    }

    /**
     * Get a new instance using the class name specified in escidoc-core.properties with
     * 'escidoc-core.PidGeneratorFactory'.
     *
     * @return An instance of the PIDGeneratorFactory class specified in escidoc-core.properties with
     *         escidoc-core.PidGeneratorFactory.
     * @throws PidSystemException If no instance could be returned
     * @see EscidocConfiguration
     */
    public static PIDSystemFactory getInstance() {
        return pidSystemFactory;
    }

    /**
     * @throws PidSystemException If no instance could be returned
     * @see #getInstance()
     */
    private static void createNewInstanceFromConfig() throws PidSystemException {
        String factoryClassName =
            EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_PID_SYSTEM_FACTORY);
        if (factoryClassName == null) {
            factoryClassName = DEFAULT_FACTORY;
        }
        try {
            final Class<?> factoryClass = Class.forName(factoryClassName);
            pidSystemFactory = (PIDSystemFactory) factoryClass.getConstructor().newInstance();
        }
        catch (final ClassNotFoundException e) {
            throw new PidSystemException(e);
        }
        catch (final InstantiationException e) {
            throw new PidSystemException(e);
        }
        catch (final IllegalAccessException e) {
            throw new PidSystemException(e);
        }
        catch (NoSuchMethodException e) {
            throw new PidSystemException(e);
        }
        catch (InvocationTargetException e) {
            throw new PidSystemException(e);
        }
    }

    /**
     * Return a PIDSystem using the underlying object model determined when the PIDSystemFactory was instantiated.
     *
     * @return An instance of a PIDSystem.
     * @throws PidSystemException If no PIDSystem could be returned
     */
    public abstract PIDSystem getPIDGenerator() throws PidSystemException;
}
