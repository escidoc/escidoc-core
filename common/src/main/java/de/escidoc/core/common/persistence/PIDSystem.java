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

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.PidSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * Interface of a PIDGenerator obtained from PIDGeneratorFactory.
 *
 * @author Frank Schwichtenberg
 */
public interface PIDSystem {

    /**
     * Assign a Persistent Identifier. Create an Identifier and register the values in the configured Persistent
     * Identifier System.
     *
     * @param systemID The system identifier of the resource.
     * @param param    The XML snippet with the values for the PIDManagement System (or Generator system)
     * @return XML snippet with the assigned PID.
     * @throws PidSystemException       Thrown if communication with the PID System fails.
     * @throws MissingMethodParameterException
     *                                  Thrown if parameter is missing or is invalid.
     * @throws WebserverSystemException Thrown if reading of eSciDoc properties failed.
     *                                  <p/>
     *                                  <pre>
     *                                  &lt;param&gt;
     *                                     &lt;pid&gt;protocol:persistent identifier&lt;/pid&gt;
     *                                  &lt;/param&gt;
     *                                  </pre>
     */
    String assignPID(final String systemID, final String param) throws PidSystemException,
        MissingMethodParameterException, WebserverSystemException;

    /**
     * Generate a new PID that is guaranteed to be unique for the object with the given system id.
     *
     * @param systemID The id of the object.
     * @return The generated PID.
     * @throws PidSystemException Thrown if communication with the PID System fails.
     * @throws MissingMethodParameterException
     *                            Thrown if parameter is missing or is invalid.
     */
    String generatePID(String systemID) throws PidSystemException, MissingMethodParameterException;

    /**
     * Register pid to ensure it is never generated.
     *
     * @param pid The persistent identifier.
     */
    void neverGeneratePID(String pid);
}
